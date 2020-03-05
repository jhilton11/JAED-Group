package com.appify.jaedgroup.services;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PaystackService {

    @GET("{reference}")
    Call<String> getJSONString(@Path("reference") String reference);
}
