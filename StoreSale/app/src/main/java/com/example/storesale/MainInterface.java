package com.example.storesale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.QueryMap;

public interface MainInterface
{
    @Headers({"user-token: dbproject2021"})
    @GET("products")
    Call<String> string_call(
            @QueryMap Map<String, String> map
    );
}