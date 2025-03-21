package com.example.doan;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("/")
    Call<EmailCheckResponse> checkEmail(@Query("") String unused);
}
