package com.example.storesale;

public class Product //상품목록을 가져올 때 객체로 가져오기 위해 따로 구현
{
    private String img_url=null;
    private String prod_name=null;
    private String store=null;
    private String prod_price=null;
    private String sale_type=null;
    private String category=null;
    private int prod_id=0;
    public Product(String url,String name,String store,String price, String type,int id,String category){
        this.img_url=url;
        this.prod_name=name;
        this.prod_price=price;
        this.sale_type=type;
        this.store=store;
        this.prod_id=id;
        this.category=category;
    }
    public String getUrl(){
        return img_url;
    }
    public String getName(){
        return prod_name;
    }
    public String getStore(){
        return store;
    }
    public String getProd_price(){
        return prod_price;
    }
    public String getType(){
        return sale_type;
    }
    public String getCategory(){return category;}

    //상품의 정보를 보여줄 것을 표시 => 이미지, 상품명, 할인 편의점 명, 가격, 할인 타입
    //영양정보를 표시하기 위해 => prod_id
}
