package com.example.storesale;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

//번들을 통해 전달받은 상품의 id를 이용하여, 데이터를 조회 후 사용자에게 정보를 보여준다.
//UI 구성시, 1회 제공량, 열량, 탄수화물, 당류, 단백질, 지방, 포화지방, 트랜스지방, 콜레스테롤, 나트륨을 보여준다.
//식품 데이터 일 때만 화면을 보여주도록 유도한다.

public class nutritionFragment extends Fragment {
    Button btnback;
    TextView tvName, tvnat, tvtan, tvpro, tvonce, tvsug, tvfat, tvtran, tvfo, tvcol,tvkcal;
    ImageView ivitem;

    public nutritionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View i = inflater.inflate(R.layout.fragment_nutrition, container, false);
        btnback = i.findViewById(R.id.btnback);
        tvName = i.findViewById(R.id.tvName);//상품명
        tvkcal=i.findViewById(R.id.tvkcal);
        tvnat = i.findViewById(R.id.tvnat);//나트륨
        tvtan = i.findViewById(R.id.tvtan);//탄수화물
        tvpro = i.findViewById(R.id.tvpro);//1회 제공량
        tvonce = i.findViewById(R.id.tvonce);
        tvsug = i.findViewById(R.id.tvsug);
        ivitem = i.findViewById(R.id.ivitem);
        tvfat = i.findViewById(R.id.tvfat);
        tvtran = i.findViewById(R.id.tvtran);
        tvfo = i.findViewById(R.id.tvfo);
        tvcol = i.findViewById(R.id.tvcol);
        Bundle bundle = getArguments();
        Product item = (Product) bundle.getSerializable("item");
        tvName.setText(item.getName());
        RequestOptions options = new RequestOptions().override(200, 200).error(R.drawable.file).fallback(R.drawable.file).centerCrop();
        Glide.with(getContext()).load(item.getUrl()).apply(options).into(ivitem);//80*80사이즈로 이미지를 가져옴
        setData(item.getProd_id());

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SubActivity) getContext()).backFrag();
            }
        });


        return i;
    }

    private void setData(int id) {
        Handler handler = new Handler() {
            public void handleMessage(Message msg) {

                Bundle bun = msg.getData();
                String data = bun.getString("data");

                if (!data.equals("None")){
                    try {
                        data=data.replace("[","").replace("]","");
                        JSONObject jsonObject =new JSONObject(data);
                        tvonce.setText(jsonObject.getString("por"));//1회 제공량
                        tvkcal.setText(jsonObject.getString("kcal"));
                        tvtan.setText(jsonObject.getString("tan"));
                        tvsug.setText(jsonObject.getString("sugar"));
                        tvfo.setText(jsonObject.getString("fofat"));
                        tvfat.setText(jsonObject.getString("fat"));
                        tvpro.setText(jsonObject.getString("protein"));
                        tvtran.setText(jsonObject.getString("transfat"));
                        tvcol.setText(jsonObject.getString("coles"));
                        tvnat.setText(jsonObject.getString("nat"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        };
        new Thread() {
            public void run() {
                String key = "data";
                String where = "http://193.122.126.186:3000/nutrition?id="+id;
                DB db = new DB(where, key, handler);
                db.connectDB( "GET");
            }
        }.start();

    }

}
