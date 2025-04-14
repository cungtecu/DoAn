package com.example.doan.api;

import com.example.doan.models.ApiResponse;
import com.example.doan.models.ChangePasswordRequest;
import com.example.doan.models.LoginRequest;
import com.example.doan.models.LoginResponse;
import com.example.doan.models.ResetPasswordRequest;
import com.example.doan.models.SendResetLinkRequest;
import com.example.doan.models.SignupRequest;
import com.example.doan.models.SignupResponse;
import com.example.doan.models.UpdateUserRequest;
import com.example.doan.models.User;
import com.example.doan.models.UserProfileResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ApiService {

    @POST("/api/users/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @GET("/api/users/me")
    Call<User> getCurrentUser(@Header("Authorization") String token);

    @GET("/api/users/me")
    Call<UserProfileResponse> getUserProfile(@Header("Authorization") String token);

    @PUT("/api/users/profile")
    Call<ApiResponse> updateUser(@Header("Authorization") String token, @Body UpdateUserRequest request);

    @POST("/api/users/register")
    Call<ApiResponse> registerUser(@Body LoginRequest registerRequest);

    @POST("/api/users/register/initiate")
    Call<SignupResponse> initiateRegistration(@Body SignupRequest signupRequest);

    @POST("/api/users/register/complete")
    Call<SignupResponse> completeRegistration(@Body SignupRequest signupRequest, @Query("otp") String otp);

    @POST("/api/auth/forgot-password")
    Call<String> sendOtp(@Body SendResetLinkRequest request);

    @POST("/api/auth/reset-password")
    Call<String> resetPassword(@Body ResetPasswordRequest request);

    // Cập nhật phương thức đổi mật khẩu
    @PUT("/api/users/password")
    Call<ApiResponse> changePassword(@Header("Authorization") String token, @Body ChangePasswordRequest request);

    @DELETE("/api/users/profile")
    Call<ApiResponse> deleteProfile(@Header("Authorization") String token);
}