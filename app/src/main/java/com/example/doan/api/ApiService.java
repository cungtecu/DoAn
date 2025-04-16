package com.example.doan.api;

import com.example.doan.models.Category;
import com.example.doan.models.LoginResponse;
import com.example.doan.models.LoginRequest;
import com.example.doan.models.PriceHistory;
import com.example.doan.models.Product;
import com.example.doan.models.SignupRequest;
import com.example.doan.models.UserSummaryDTO;
import com.example.doan.models.Users;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
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

public interface ApiService {

    // User-related endpoints
    @POST("api/users/register")
    Call<LoginResponse> createUser(@Body SignupRequest signupRequest);

    @POST("api/users/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @GET("api/users/me")
    Call<Users> getCurrentUser(@Header("Authorization") String token);



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
}