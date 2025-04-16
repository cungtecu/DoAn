package com.example.doan.api;

import com.example.doan.models.ApiResponse;
import com.example.doan.models.Category;
import com.example.doan.models.LoginResponse;
import com.example.doan.models.ChangePasswordRequest;
import com.example.doan.models.LoginRequest;
import com.example.doan.models.PriceHistory;
import com.example.doan.models.Product;
import com.example.doan.models.ResetPasswordRequest;
import com.example.doan.models.SendResetLinkRequest;
import com.example.doan.models.SignupRequest;
import com.example.doan.models.UserSummaryDTO;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import com.example.doan.models.SignupResponse;
import com.example.doan.models.UpdateUserRequest;
import com.example.doan.models.UserProfileResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // User-related endpoints
    @POST("api/users/register")
    Call<LoginResponse> createUser(@Body SignupRequest signupRequest);

    @POST("api/users/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);



    @GET("api/users/list")
    Call<List<UserSummaryDTO>> getUsers(@Header("Authorization") String token);

    @DELETE("api/users/{id}")
    Call<Void> deleteUser(@Header("Authorization") String token, @Path("id") Integer id);

    @GET("api/users/{id}")
    Call<Map<String, Object>> getUserDetails(@Header("Authorization") String token, @Path("id") Integer id);



    // Category-related endpoints (Customer)
    @GET("api/categories")
    Call<List<Category>> getCategories();

    // Category-related endpoints (Admin)
    @GET("/api/categories")
    Call<List<Category>> getAllCategories(@Header("Authorization") String authToken);

    @GET("/api/categories/{id}")
    Call<Category> getCategoryById(@Header("Authorization") String authToken, @Path("id") int id);

    @POST("/api/categories")
    Call<Category> createCategory(@Header("Authorization") String authToken, @Body Category category);

    @PUT("/api/categories/{id}")
    Call<Category> updateCategory(@Header("Authorization") String authToken, @Path("id") int id, @Body Category category);

    @DELETE("/api/categories/{id}")
    Call<Map<String, String>> deleteCategory(@Header("Authorization") String authToken, @Path("id") int id);



    // Product-related endpoints (Customer)
    @GET("api/products")
    Call<List<Product>> getProducts();

    @GET("api/products/category/{categoryId}")
    Call<List<Product>> getProductsByCategory(@Path("categoryId") int categoryId);

    @GET("api/products/{Id}")
    Call<Product> getProductById(@Path("Id") int productId);



    // Product-related endpoints (Admin)
    @GET("/api/products")
    Call<List<Product>> getAllProducts(@Header("Authorization") String authToken);

    @POST("/api/products")
    Call<Product> createProduct(@Header("Authorization") String authToken, @Body Product product);

    @Multipart
    @POST("/api/upload")
    Call<String> uploadImage(@Header("Authorization") String authToken, @Part MultipartBody.Part file);

    @PUT("/api/products/{id}")
    Call<Product> updateProduct(@Header("Authorization") String authToken, @Path("id") int id, @Body Product product);

    @DELETE("/api/products/{id}")
    Call<Map<String, String>> deleteProduct(@Header("Authorization") String authToken, @Path("id") int id);

    @GET("api/price-history/{productId}")
    Call<List<PriceHistory>> getPriceHistory(@Header("Authorization") String authToken, @Path("productId") int productId);

    @GET("api/products/{id}")
    Call<Product> getProductById(@Header("Authorization") String authToken, @Path("id") int id);
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