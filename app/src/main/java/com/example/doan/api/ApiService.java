package com.example.doan.api;

import com.example.doan.models.Category;
import com.example.doan.models.LoginResponse;
import com.example.doan.models.LoginRequest;
import com.example.doan.models.Product;
import com.example.doan.models.SignupRequest;
import com.example.doan.models.UserSummaryDTO;
import com.example.doan.models.Users;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

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





    //CUSTOMMER-----------------------------Endpoint lấy danh sách danh mục (mới thêm)----------------------
    @GET("api/categories")
    Call<List<Category>> getCategories();

    //ADMIN---------------------------------Endpoint lấy danh mục-----------------------------------------
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







    //Endpoint lấy danh sách sản phẩm (mới thêm)
    @GET("api/products")
    Call<List<Product>> getProducts();

    //Endpoint lấy danh sách sản phẩm theo danh mục (mới thêm)
    @GET("api/products/category/{categoryId}")
    Call<List<Product>> getProductsByCategory(@Path("categoryId") int categoryId);

    @GET("api/products/{id}")
    Call<Product> getProductById(@Path("id") int productId);
}