package com.example.storesale;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

//상품을 보여주는 테이블
public class searchFragment extends Fragment {
    RecyclerView mRecyclerView = null;
    RecyclerViewAdapter mAdapter = null;
    private ArrayList<Product> mList;
    private Spinner spinner;
    private EditText edtSearch;
    private Button btnSearch;
    private String category;
    private NestedScrollView nscrollview;
    private ProgressBar progressbar;
    private String keyword = null;
    private int offset = 0, limit = 0;//페이징 처리용 변수
    private Boolean check=false;
    private FragmentManager manager;
    public void setValue() {
        offset = 0;
        limit = 20;
    }

    public searchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View i = inflater.inflate(R.layout.fragment_search, container, false);
        mRecyclerView = i.findViewById(R.id.recycler_view);
        mList = new ArrayList<Product>();

        mAdapter = new RecyclerViewAdapter(mList, this.getContext());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), RecyclerView.VERTICAL, false));
        nscrollview = i.findViewById(R.id.nestedscrollview);
        progressbar = i.findViewById(R.id.progressbar);
        spinner = i.findViewById(R.id.spinner);
        edtSearch = i.findViewById(R.id.edtSearch);
        btnSearch = i.findViewById(R.id.btnSearch);
        setValue();
        getData();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = parent.getItemAtPosition(position).toString(); //현재 선택된 값을 검색 카테고리로 지정
                setValue();
                check=false;
                mList.clear();
                getData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyword = edtSearch.getText().toString();
                setValue();
                mList.clear();
                check=false;
                getData();

            }
        });
        //스크롤 부분..
        nscrollview.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {


                if (check==false &&( scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())){
                    edtSearch.clearFocus();
                    offset += (limit + 1);
                    progressbar.setVisibility(View.VISIBLE);
                    getData();
                }else{return;}

            }
        });

        return i;
    }


    private void getData() {
        // 레트로핏 초기화
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://193.122.126.186:3000/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        MainInterface mainInterface = retrofit.create(MainInterface.class);
        Call<String> call = null;
        Map map = new HashMap();
        try {
            if (!keyword.equals("")) {
                map.put("name", keyword);
            }
        } catch (NullPointerException err) {

        }
        try {
            if (!category.equals("")) {
                if (!category.equals("카테고리전체")) map.put("cat", category);
            }
        } catch (NullPointerException err) {

        }
        map.put("offset", String.valueOf(offset));
        map.put("limit", String.valueOf(limit));

        call = mainInterface.string_call(map);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    progressbar.setVisibility(View.GONE);
                    try {
                        JSONArray jsonArray = new JSONArray(response.body());
                        parseResult(jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        try {
                            response.errorBody().string();
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("에러 : ", t.getMessage());
            }
        });
    }

    private void parseResult(JSONArray jsonArray) {
        int size=mList.size();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("prod_name");
                String store = jsonObject.getString("store");
                int id = Integer.parseInt(jsonObject.getString("prod_id"));
                String url = jsonObject.getString("prod_img");
                String type = jsonObject.getString("saletype");
                String price = jsonObject.getString("prod_price");
                String category = jsonObject.getString("prod_category");
                Product data = new Product(url, name, store, price, type, id, category);
                mList.add(data);
                Log.d("data", Arrays.toString(mList.toArray()));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(size==mList.size()){
                check=true;
            }
            mAdapter = new RecyclerViewAdapter(mList, getContext());
            mRecyclerView.setAdapter(mAdapter);
        }
    }
}