package com.example.storesale;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private ArrayList<Product> mData = null;
    private Context context=null;
    private LinearLayout container;
    public RecyclerViewAdapter(ArrayList<Product> data, Context c) {
        mData = data;
        context=c;

    }

    // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.recycler_item, parent, false);
        RecyclerViewAdapter.ViewHolder vh = new RecyclerViewAdapter.ViewHolder(view);
        return vh;
    }
    // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product item = mData.get(position);
        String saled=null;
        RequestOptions options = new RequestOptions().override(200,200).error(R.drawable.file).fallback(R.drawable.file).centerCrop();

        Glide.with(context).load(item.getUrl()).apply(options).into(holder.item_img);//80*80사이즈로 이미지를 가져옴
        holder.tvtitle.setText(item.getName());
        holder.tvstore.setText(item.getStore());
        holder.tvtype.setText(item.getType());
        if (item.getType().equals("1+1")){saled= String.valueOf(Integer.parseInt(item.getProd_price())/2);}
        else{saled= String.valueOf((Integer.parseInt(item.getProd_price())*2)/3);}

        //각 상품 선택시
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(item.getCategory().matches("식품|음료|아이스크림|과자")){
                ((SubActivity)context).changeFrag(item);}
            }
        });



        holder.tvprice.setText("￦"+saled+" (￦"+item.getProd_price()+")");
        holder.tvcat.setText(item.getCategory());
    }
    // getItemCount : 전체 데이터의 개수를 리턴
    @Override
    public int getItemCount() {
        return mData.size();
    }
    // 아이템 뷰를 저장하는 뷰홀더 클래스
    public class ViewHolder extends RecyclerView.ViewHolder {
       ImageView item_img;
       TextView tvstore,tvtitle,tvprice,tvtype,tvcat;
        ViewHolder(View itemView) {
            super(itemView); // 뷰 객체에 대한 참조
           item_img=itemView.findViewById(R.id.item_img);
           tvtitle=itemView.findViewById(R.id.tvtitle);
           tvstore=itemView.findViewById(R.id.tvstore);
           tvprice=itemView.findViewById(R.id.tvprice);
           tvtype=itemView.findViewById(R.id.tvtype);
            tvcat=itemView.findViewById(R.id.tvcat);
            //클릭 리스너를 달기 위한 시스템
            container=itemView.findViewById(R.id.list_item);
        }
    }
}

