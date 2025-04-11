package com.example.doan.api;

import com.example.doan.models.ApiResponse;
import com.example.doan.models.LoginRequest;
import com.example.doan.models.SignupRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

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

    //Endpoint lấy danh sách danh mục (mới thêm)
    @GET("api/categories")
    Call<List<Category>> getCategories();

    //Endpoint lấy danh sách sản phẩm (mới thêm)
    @GET("api/products")
    Call<List<Product>> getProducts();

    //Endpoint lấy danh sách sản phẩm theo danh mục (mới thêm)
    @GET("api/products/category/{categoryId}")
    Call<List<Product>> getProductsByCategory(@Path("categoryId") int categoryId);

    @GET("api/products/{id}")
    Call<Product> getProductById(@Path("id") int productId);
}