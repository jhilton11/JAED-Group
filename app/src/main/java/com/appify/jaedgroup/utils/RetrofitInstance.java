package com.appify.jaedgroup.utils;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {
    public static Retrofit retrofit;
    public static String base_url = "https://api.paystack.co/transaction/verify/";

    public static Retrofit getRetrofitInstance() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(base_url)
                    .client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
