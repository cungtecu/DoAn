package com.example.doan.api;

import com.example.doan.models.CartAddRequest;
import com.example.doan.models.CartCheckoutRequest;
import com.example.doan.models.CartDTO;
import com.example.doan.models.CartUpdateRequest;
import com.example.doan.models.Category;
import com.example.doan.models.LoginResponse;
import com.example.doan.models.OrderResponse;
import com.example.doan.models.Product;
import com.example.doan.models.ApiResponse;
import com.example.doan.models.LoginRequest;
import com.example.doan.models.SignupRequest;
import com.example.doan.models.UpdateUserRequest;
import com.example.doan.models.User;
import com.example.doan.models.UserProfileResponse;

import retrofit2.Call;
import java.util.List;

import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @GET("/api/users/me")
    Call<User> getCurrentUser(@Header("Authorization") String token);

    @GET("/api/users/me")
    Call<UserProfileResponse> getUserProfile(@Header("Authorization") String token);

    @PUT("/api/users/profile")
    Call<ApiResponse> updateUser(@Header("Authorization") String token, @Body UpdateUserRequest request);

    @POST("/api/users/register")
    Call<ApiResponse> registerUser(@Body LoginRequest registerRequest);

    @POST("/api/users/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

//    @POST("/api/users/register/initiate")
//    Call<SignupResponse> initiateRegistration(@Body SignupRequest signupRequest);
//
//    @POST("/api/users/register/complete")
//    Call<SignupResponse> completeRegistration(@Body SignupRequest signupRequest, @Query("otp") String otp);

//    @POST("/api/auth/forgot-password")
//    Call<String> sendOtp(@Body SendResetLinkRequest request);
//
//    @POST("/api/auth/reset-password")
//    Call<String> resetPassword(@Body ResetPasswordRequest request);

    // Endpoint đăng ký (đã có từ trước)
    @POST("api/users/register")
    Call<ApiResponse> createUser(@Body SignupRequest signupRequest);

    //Endpoint lấy danh sách danh mục (mới thêm)
    @GET("api/categories")
    Call<List<Category>> getCategories();

    //Endpoint lấy danh sách sản phẩm (mới thêm)
    @GET("api/products")
    Call<List<Product>> getProducts();

    //Endpoint lấy danh sách sản phẩm theo danh mục (mới thêm)
    @GET("api/products/category/{categoryId}")
    Call<List<Product>> getProductsByCategory(@Path("categoryId") int categoryId);

    @GET("api/products/{Id}")
    Call<Product> getProductById(@Path("Id") int productId);

     //API cho giỏ hàng
    @GET("api/cart")
    Call<CartDTO> getCartItems();

    @POST("api/cart/add")
    Call<CartDTO> addToCart(@Body CartAddRequest request);

    @PUT("api/cart/update")
    Call<CartDTO> updateCartItem(@Body CartUpdateRequest request);

    @DELETE("api/cart/remove")
    Call<Void> removeFromCart(@Query("cartItemId") int cartItemId);

    @DELETE("api/cart/clear")
    Call<Void> clearCart();

    @POST("api/cart/checkout")
    Call<OrderResponse> checkout(@Body CartCheckoutRequest request);

    // Phương thức mới cho PaymentActivity
    @GET("order/{orderId}")
    Call<OrderResponse> getOrderDetails(@Path("orderId") String orderId);

//    @GET("vouchers")
//    Call<List<Voucher>> getAvailableVouchers();
//
//    @POST("order/confirm")
//    Call<OrderResponse> confirmOrder(@Body OrderConfirmRequest request);
}