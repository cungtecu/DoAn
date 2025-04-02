package com.example.doan.api;

import com.example.doan.models.ApiResponse;
import com.example.doan.models.LoginRequest;
import com.example.doan.models.SignupRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    // Endpoint đăng ký (đã có từ trước)
    @POST("api/users/register")
    Call<ApiResponse> createUser(@Body SignupRequest signupRequest);

    // Endpoint đăng nhập (mới thêm)
    @POST("api/users/login")
    Call<ApiResponse> loginUser(@Body LoginRequest loginRequest);
}