package com.example.doan.api;

import com.example.doan.models.LoginRequest;
import com.example.doan.models.LoginResponse;
import com.example.doan.models.SignupRequest;
import com.example.doan.models.Users;
import com.example.doan.models.UserSummaryDTO;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    // Endpoint đăng ký (đã có từ trước)
    @POST("api/users/register")
    Call<LoginResponse> createUser(@Body SignupRequest signupRequest);

    // Endpoint đăng nhập (mới thêm)
    @POST("api/users/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);
    @GET("api/users/me")
    Call<Users> getCurrentUser(@Header("Authorization") String token);

    @GET("api/users/list")
        // Sửa thành /list
    Call<List<UserSummaryDTO>> getUsers(@Header("Authorization") String token);

    @DELETE("api/users/{id}")
    Call<Void> deleteUser(@Header("Authorization") String token, @Path("id") Integer id);
    @GET("api/users/{id}")
    Call<Map<String, Object>> getUserDetails(@Header("Authorization") String token, @Path("id") Integer id);
}


