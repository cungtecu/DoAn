package com.example.doan.api;

import com.example.doan.models.ApiResponse;
import com.example.doan.models.CartAddRequest;
import com.example.doan.models.CartCheckoutRequest;
import com.example.doan.models.CartDTO;
import com.example.doan.models.CartUpdateRequest;
import com.example.doan.models.Category;
import com.example.doan.models.ChangePasswordRequest;
import com.example.doan.models.CheckoutResponse;
import com.example.doan.models.LoginRequest;
import com.example.doan.models.LoginResponse;
import com.example.doan.models.OrderResponse;
import com.example.doan.models.Product;
import com.example.doan.models.ResetPasswordRequest;
import com.example.doan.models.SendResetLinkRequest;
import com.example.doan.models.SignupRequest;
import com.example.doan.models.SignupResponse;
import com.example.doan.models.UpdateUserRequest;
import com.example.doan.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("/api/users/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @GET("/api/users/me")
    Call<User> getCurrentUser(@Header("Authorization") String token);

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

    @PUT("/api/users/password")
    Call<ApiResponse> changePassword(@Header("Authorization") String token, @Body ChangePasswordRequest request);

    @DELETE("/api/users/profile")
    Call<ApiResponse> deleteProfile(@Header("Authorization") String token);

    @GET("api/categories")
    Call<List<Category>> getCategories();

    @GET("api/products")
    Call<List<Product>> getProducts();

    @GET("api/products/category/{categoryId}")
    Call<List<Product>> getProductsByCategory(@Path("categoryId") int categoryId);

    @GET("api/products/{Id}")
    Call<Product> getProductById(@Path("Id") int productId);

    @GET("api/cart")
    Call<CartDTO> getCartItems();

    @PUT("api/cart/update")
    Call<CartDTO> updateCartItem(@Body CartUpdateRequest request);

    @DELETE("api/cart/remove")
    Call<Void> removeFromCart(@Header("Authorization") String token, @Query("productId") int productId);

    @POST("api/cart/checkout")
    Call<OrderResponse> checkout(@Body CartCheckoutRequest request);

    @POST("api/cart/add")
    Call<CartDTO> addToCart(@Body CartAddRequest request);
}