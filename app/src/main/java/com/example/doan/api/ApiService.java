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
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    // Đăng nhập người dùng
    @POST("/api/users/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    // Lấy thông tin người dùng hiện tại
    @GET("/api/users/me")
    Call<User> getCurrentUser(@Header("Authorization") String token);

    // Lấy thông tin hồ sơ người dùng
    @GET("/api/users/me")
    Call<UserProfileResponse> getUserProfile(@Header("Authorization") String token);

    // Cập nhật thông tin người dùng
    @POST("/api/users/update")
    Call<ApiResponse> updateUser(@Header("Authorization") String token, @Body UpdateUserRequest request);

    // Đăng ký người dùng (endpoint cũ của bạn, giữ nguyên)
    @POST("/api/users/register")
    Call<ApiResponse> registerUser(@Body LoginRequest registerRequest);

    // Bắt đầu đăng ký (gửi OTP) - Khớp với Spring Boot
    @POST("/api/users/register/initiate")
    Call<SignupResponse> initiateRegistration(@Body SignupRequest signupRequest);

    // Hoàn tất đăng ký (xác nhận OTP) - Khớp với Spring Boot
    @POST("/api/users/register/complete")
    Call<SignupResponse> completeRegistration(@Body SignupRequest signupRequest, @Query("otp") String otp);

    // Quên mật khẩu (gửi mã OTP hoặc reset link)
    @POST("/api/auth/forgot-password")
    Call<String> sendOtp(@Body SendResetLinkRequest request);

    // Đặt lại mật khẩu
    @POST("/api/auth/reset-password") // Sửa đường dẫn để khớp với Spring Boot
    Call<String> resetPassword(@Body ResetPasswordRequest request);

    // Thay đổi mật khẩu
    @POST("/api/users/change-password")
    Call<ApiResponse> changePassword(@Header("Authorization") String token, @Body ChangePasswordRequest request);
}