package com.example.storesale;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;

import java.util.ArrayList;

//상품을 보여주는 테이블
public class searchFragment extends Fragment {
    RecyclerView mRecyclerView = null;
    RecyclerViewAdapter mAdapter = null;
    ArrayList<Product> mList;
    private Spinner spinner;
    private EditText edtSearch;
    private Button btnSearch;
    private String category;
    private ScrollView scrollview;
    private ProgressBar progressbar;
    private int offset = 0, limit = 10;//페이징 처리용 변수
    public searchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View i = inflater.inflate(R.layout.fragment_search, container, false);
        mRecyclerView = i.findViewById(R.id.recycler_view);
        mList = new ArrayList<>();

        mAdapter = new RecyclerViewAdapter(mList,this.getContext());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), RecyclerView.VERTICAL, false));
        scrollview=i.findViewById(R.id.scrollview);
        progressbar=i.findViewById(R.id.progressbar);
        spinner = i.findViewById(R.id.spinner);
        edtSearch = i.findViewById(R.id.edtSearch);
        btnSearch = i.findViewById(R.id.btnSearch);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = parent.getItemAtPosition(position).toString(); //현재 선택된 값을 검색 카테고리로 지정
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getItems();
            }
        });
        //스크롤 부분..
        .setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener()
        {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY)
            {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())
                {
                    page++;
                    progressBar.setVisibility(View.VISIBLE);
                    getData(page, limit);
                }
            }
        });
    }
        return i;
    }

    private void addItem(String url, String name, String store, String price, String type)
    {
        Product item = new Product(url, name, store, price, type);
    }

    private void getItems() {
        String keyword=edtSearch.getText().toString().replaceAll(" ", "");//검색어 지정
        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                Bundle bun = msg.getData();
                String data = bun.getString("login");

                if (data.equals("OK")) {

                } else {
                    Toast.makeText(getContext(),"다시 시도해주세요",Toast.LENGTH_SHORT).show();
                }
            }
        };
        new Thread() {
            public void run() {
                String key = "products";
                String where="http://193.122.126.186:3000/products?";
                if (keyword!=""){
                    if(category !="전체"){
                        where=where+"&"
                    }
                }
                DB db = new DB(where, key, handler);
                db.connectDB("GET");
            }
        }.start();

    }
}