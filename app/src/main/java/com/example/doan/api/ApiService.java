package com.example.doan.api;

import com.example.doan.models.Category;
import com.example.doan.models.Product;
import com.example.doan.models.ApiResponse;
import com.example.doan.models.LoginRequest;
import com.example.doan.models.SignupRequest;
import retrofit2.Call;
import java.util.List;
import retrofit2.http.GET;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // Endpoint đăng ký (đã có từ trước)
    @POST("api/users/register")
    Call<ApiResponse> createUser(@Body SignupRequest signupRequest);

    // Endpoint đăng nhập (mới thêm)
    @POST("api/users/login")
    Call<ApiResponse> loginUser(@Body LoginRequest loginRequest);

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

    // API cho giỏ hàng
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
}