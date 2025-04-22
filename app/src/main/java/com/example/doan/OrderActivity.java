package com.example.doan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.doan.api.RetrofitClient;
import com.example.doan.models.CartDTO;
import com.example.doan.models.CartItemDTO;
import com.example.doan.models.Category;
import com.example.doan.models.Product;
import com.example.doan.models.UserProfileDTO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderActivity extends AppCompatActivity {
    private static final String TAG = "OrderActivity";
    private static final String PREFS_NAME = "MyAppPrefs";
    private static final int SIGNIN_REQUEST_CODE = 1001;
    private RecyclerView categoryRecyclerView, productRecyclerView;
    private CategoryAdapter categoryAdapter;
    private ProductAdapter productAdapter;
    private ProgressBar progressBar;
    private TextView quantityText;
    private List<Category> categoryList;
    private List<Product> productList;
    private Call<List<Category>> categoryCall;
    private Call<List<Product>> productByCategoryCall;
    private Call<CartDTO> cartCall;
    private ImageButton btnHome, btnOrder, btnOther;
    private ImageView btnCart;
    private int currentCategoryIndex = 0;
    private boolean isLoading = false;
    private boolean isTokenValid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order);

        categoryRecyclerView = findViewById(R.id.category_recycler_view);
        productRecyclerView = findViewById(R.id.product_recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        quantityText = findViewById(R.id.quantity_text);
        btnHome = findViewById(R.id.btn_home);
        btnOrder = findViewById(R.id.cartIcon);
        btnOther = findViewById(R.id.btn_other);
        btnCart = findViewById(R.id.cart_icon);

        if (categoryRecyclerView == null || productRecyclerView == null || progressBar == null || quantityText == null) {
            Log.e(TAG, "One or more views not found in layout");
            Toast.makeText(this, "Lỗi giao diện, vui lòng thử lại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        productRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        categoryList = new ArrayList<>();
        productList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(this, categoryList, this::loadProductsByCategory);
        productAdapter = new ProductAdapter(this, productList);
        categoryRecyclerView.setAdapter(categoryAdapter);
        productRecyclerView.setAdapter(productAdapter);

        categoryRecyclerView.setVisibility(View.GONE);
        productRecyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        btnHome.setOnClickListener(v -> navigateTo(MainActivity.class));
        btnOrder.setOnClickListener(v -> navigateTo(CartActivity.class));
        btnCart.setOnClickListener(v -> navigateTo(CartActivity.class));
        btnOther.setOnClickListener(v -> navigateTo(OtherActivity.class));

        checkTokenAndLoadData();
    }

    private void checkTokenAndLoadData() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String token = prefs.getString("token", null);
        if (token == null) {
            Toast.makeText(this, "Không tìm thấy token, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            redirectToSignin();
            return;
        }

        RetrofitClient.isTokenValid(this, new Callback<UserProfileDTO>() {
            @Override
            public void onResponse(Call<UserProfileDTO> call, Response<UserProfileDTO> response) {
                if (!isFinishing()) {
                    if (response.isSuccessful()) {
                        isTokenValid = true;
                        loadCategories();
                        loadCartQuantity();
                    } else {
                        isTokenValid = false;
                        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                        prefs.edit().remove("token").apply();
                        Toast.makeText(OrderActivity.this, "Phiên đăng nhập hết hạn, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
                        redirectToSignin();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserProfileDTO> call, Throwable t) {
                if (!isFinishing()) {
                    isTokenValid = false;
                    Toast.makeText(OrderActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    redirectToSignin();
                }
            }
        });
    }

    private void loadCategories() {
        if (isLoading || !isTokenValid) return;
        isLoading = true;
        progressBar.setVisibility(View.VISIBLE);

        categoryCall = RetrofitClient.getApiService(this).getCategories();
        categoryCall.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                isLoading = false;
                if (!isFinishing()) {
                    if (response.isSuccessful() && response.body() != null) {
                        categoryList.clear();
                        categoryList.addAll(response.body());
                        categoryAdapter.notifyDataSetChanged();
                        categoryRecyclerView.setVisibility(View.VISIBLE);
                        Log.d(TAG, "Loaded " + categoryList.size() + " categories");

                        if (!categoryList.isEmpty()) {
                            currentCategoryIndex = 0;
                            categoryAdapter.setSelectedCategory(currentCategoryIndex);
                            loadProductsByCategory(categoryList.get(currentCategoryIndex).getId());
                        } else {
                            Toast.makeText(OrderActivity.this, "Không có danh mục nào để hiển thị", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    } else {
                        handleApiError(response, "Không thể tải danh mục");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                isLoading = false;
                if (!call.isCanceled() && !isFinishing()) {
                    Log.e(TAG, "Lỗi tải danh mục: " + t.getMessage());
                    Toast.makeText(OrderActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void loadProductsByCategory(int categoryId) {
        if (isLoading || !isTokenValid) return;
        isLoading = true;
        progressBar.setVisibility(View.VISIBLE);
        productRecyclerView.setVisibility(View.GONE);

        productByCategoryCall = RetrofitClient.getApiService(this).getProductsByCategory(categoryId);
        productByCategoryCall.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                isLoading = false;
                if (!isFinishing()) {
                    if (response.isSuccessful() && response.body() != null) {
                        productList.clear();
                        productList.addAll(response.body());
                        productAdapter.notifyDataSetChanged();
                        productRecyclerView.setVisibility(View.VISIBLE);
                        Log.d(TAG, "Loaded " + productList.size() + " products for category " + categoryId);

                        if (productList.isEmpty()) {
                            Toast.makeText(OrderActivity.this, "Không có sản phẩm nào trong danh mục này", Toast.LENGTH_SHORT).show();
                            findNextCategoryWithProducts();
                        } else {
                            progressBar.setVisibility(View.GONE);
                            YoYo.with(Techniques.FadeIn)
                                    .duration(700)
                                    .playOn(productRecyclerView);
                        }
                    } else {
                        handleApiError(response, "Không thể tải sản phẩm");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                isLoading = false;
                if (!call.isCanceled() && !isFinishing()) {
                    Log.e(TAG, "Lỗi tải sản phẩm: " + t.getMessage());
                    Toast.makeText(OrderActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    productRecyclerView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void findNextCategoryWithProducts() {
        currentCategoryIndex++;
        while (currentCategoryIndex < categoryList.size()) {
            int nextCategoryId = categoryList.get(currentCategoryIndex).getId();
            productByCategoryCall = RetrofitClient.getApiService(this).getProductsByCategory(nextCategoryId);
            Response<List<Product>> response = null;
            try {
                response = productByCategoryCall.execute();
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    loadProductsByCategory(nextCategoryId);
                    categoryAdapter.setSelectedCategory(currentCategoryIndex);
                    return;
                }
            } catch (Exception e) {
                Log.e(TAG, "Lỗi kiểm tra sản phẩm danh mục tiếp theo: " + e.getMessage());
            }
            currentCategoryIndex++;
        }

        Toast.makeText(OrderActivity.this, "Hiện tại không có sản phẩm nào để hiển thị", Toast.LENGTH_LONG).show();
        productRecyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private void loadCartQuantity() {
        if (!isTokenValid) {
            Toast.makeText(this, "Không thể tải giỏ hàng do chưa đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }
        cartCall = RetrofitClient.getApiService(this).getCartItems();
        cartCall.enqueue(new Callback<CartDTO>() {
            @Override
            public void onResponse(Call<CartDTO> call, Response<CartDTO> response) {
                if (!isFinishing()) {
                    if (response.isSuccessful() && response.body() != null) {
                        int totalQuantity = 0;
                        for (CartItemDTO itemDTO : response.body().getCartItems()) {
                            totalQuantity += itemDTO.getQuantity();
                        }
                        quantityText.setText(String.valueOf(totalQuantity));
                        Log.d(TAG, "Cart quantity updated: " + totalQuantity);
                    } else {
                        handleApiError(response, "Không thể tải giỏ hàng");
                    }
                }
            }

            @Override
            public void onFailure(Call<CartDTO> call, Throwable t) {
                if (!call.isCanceled() && !isFinishing()) {
                    Log.e(TAG, "Lỗi tải giỏ hàng: " + t.getMessage());
                    Toast.makeText(OrderActivity.this, "Lỗi kết nối khi tải giỏ hàng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void handleApiError(Response<?> response, String defaultMessage) {
        try {
            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Không có thông tin lỗi";
            Log.e(TAG, defaultMessage + ", code: " + response.code() + ", body: " + errorBody);
            if (response.code() == 401) {
                isTokenValid = false;
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                prefs.edit().remove("token").apply();
                Toast.makeText(OrderActivity.this, "Phiên đăng nhập hết hạn, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
                redirectToSignin();
            } else {
                Toast.makeText(OrderActivity.this, defaultMessage + ": " + errorBody, Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Log.e(TAG, "Lỗi parse error body: " + e.getMessage());
            Toast.makeText(OrderActivity.this, defaultMessage + ": Lỗi không xác định", Toast.LENGTH_SHORT).show();
        }
        progressBar.setVisibility(View.GONE);
    }

    private void redirectToSignin() {
        Intent intent = new Intent(this, SigninActivity.class);
        startActivityForResult(intent, SIGNIN_REQUEST_CODE);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish(); // Kết thúc OrderActivity để tránh crash
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGNIN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Khởi động lại ứng dụng hoặc chuyển đến màn hình chính
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                finish();
            }
        }
    }

    private void navigateTo(Class<?> targetActivity) {
        Intent intent = new Intent(OrderActivity.this, targetActivity);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isTokenValid) {
            loadCartQuantity();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (categoryCall != null && !categoryCall.isCanceled()) {
            categoryCall.cancel();
        }
        if (productByCategoryCall != null && !productByCategoryCall.isCanceled()) {
            productByCategoryCall.cancel();
        }
        if (cartCall != null && !cartCall.isCanceled()) {
            cartCall.cancel();
        }
    }
}