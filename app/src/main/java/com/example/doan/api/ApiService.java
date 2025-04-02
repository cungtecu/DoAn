package com.example.doan.api;

import com.example.doan.models.SignupRequest;
import com.example.doan.models.ApiResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("api/users/register")
    Call<ApiResponse> createUser(@Body SignupRequest signupRequest);
}