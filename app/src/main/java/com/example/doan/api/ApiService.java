package com.example.doan.api;

import com.example.doan.models.ApiResponse;
import com.example.doan.models.LoginRequest;
import com.example.doan.models.LoginResponse;
import com.example.doan.models.ResetPasswordRequest;
import com.example.doan.models.SendResetLinkRequest;
import com.example.doan.models.UserProfileResponse;
import com.example.doan.models.UsersDTO;
import com.example.doan.models.ChangePasswordRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {

    @POST("api/users/login")
    Call<LoginResponse> loginUser(@Body LoginRequest request);

    @POST("api/users/register")
    Call<ApiResponse> registerUser(@Body UsersDTO request);

    @POST("api/auth/forgot-password")
    Call<String> sendResetLink(@Body SendResetLinkRequest request);

    @POST("api/auth/reset-password") // Đổi sang /api/auth/reset-password
    Call<String> resetPassword(@Body ResetPasswordRequest request); // Đổi từ ApiResponse sang String

    @GET("api/users/me")
    Call<UserProfileResponse> getUserProfile(@Header("Authorization") String token);

    @POST("api/users/change-password")
    Call<ApiResponse> changePassword(@Header("Authorization") String token, @Body ChangePasswordRequest request);
}