package com.example.doan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView; // Add this import for TextView
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
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderActivity extends AppCompatActivity {

    private static final String TAG = "OrderActivity";
    private RecyclerView categoryRecyclerView, productRecyclerView;
    private CategoryAdapter categoryAdapter;
    private ProductAdapter productAdapter;
    private ProgressBar progressBar;
    private List<Category> categoryList;
    private List<Product> productList;

    private String authToken;
    private Call<List<Category>> categoryCall;
    private Call<List<Product>> productByCategoryCall;
    private Call<CartDTO> cartCall; // Add this for cart API call

    private ImageButton btnHome, btnOrder, btnOther;
    private ImageView btnCart;
    private TextView quantityText; // Add this for quantity_text TextView
    private int currentCategoryIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order);

        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        authToken = "Bearer " + prefs.getString("token", null);
        if (authToken == null) {
            Toast.makeText(this, "Không tìm thấy token, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        categoryRecyclerView = findViewById(R.id.category_recycler_view);
        productRecyclerView = findViewById(R.id.product_recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        quantityText = findViewById(R.id.quantity_text); // Initialize quantity_text TextView

        if (categoryRecyclerView == null || productRecyclerView == null || progressBar == null || quantityText == null) {
            Log.e(TAG, "One or more views not found in layout");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // Configure RecyclerViews
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        productRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // Initialize lists
        categoryList = new ArrayList<>();
        productList = new ArrayList<>();

        // Initialize adapters
        categoryAdapter = new CategoryAdapter(this, categoryList, this::loadProductsByCategory);
        productAdapter = new ProductAdapter(this, productList);

        // Set adapters
        categoryRecyclerView.setAdapter(categoryAdapter);
        productRecyclerView.setAdapter(productAdapter);

        // Load categories
        loadCategories();

        // Initialize buttons
        btnHome = findViewById(R.id.btn_home);
        btnOrder = findViewById(R.id.cartIcon);
        btnOther = findViewById(R.id.btn_other);
        btnCart = findViewById(R.id.cart_icon);

        // Set button listeners
        btnHome.setOnClickListener(view -> {
            Intent intent = new Intent(OrderActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
        });

        btnOrder.setOnClickListener(view -> {
            Intent intent = new Intent(OrderActivity.this, CartActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
        });

        btnCart.setOnClickListener(view -> {
            Intent intent = new Intent(OrderActivity.this, CartActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
        });

        btnOther.setOnClickListener(view -> {
            Intent intent = new Intent(OrderActivity.this, OtherActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
        });

        // Load cart quantity
        loadCartQuantity();
    }

    private void loadCategories() {
        categoryCall = RetrofitClient.getApiService(this).getCategories();
        categoryCall.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (!isFinishing()) {
                    if (response.isSuccessful() && response.body() != null) {
                        categoryList.clear();
                        categoryList.addAll(response.body());
                        categoryAdapter.notifyDataSetChanged();
                        Log.d(TAG, "Loaded " + categoryList.size() + " categories");
                        categoryRecyclerView.setVisibility(View.VISIBLE);

                        // Load products of the first category
                        if (!categoryList.isEmpty()) {
                            currentCategoryIndex = 0;
                            loadProductsForNextCategory();
                        } else {
                            Toast.makeText(OrderActivity.this, "Không có danh mục nào để hiển thị", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    } else {
                        handleApiError(response, "Failed to load categories");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                if (!call.isCanceled() && !isFinishing()) {
                    Log.e(TAG, "Error loading categories: " + t.getMessage());
                    Toast.makeText(OrderActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void loadProductsForNextCategory() {
        if (currentCategoryIndex >= categoryList.size()) {
            Toast.makeText(OrderActivity.this, "Hiện tại không có sản phẩm nào để hiển thị", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        int categoryId = categoryList.get(currentCategoryIndex).getId();
        loadProductsByCategory(categoryId);
    }

    private void loadProductsByCategory(int categoryId) {
        productByCategoryCall = RetrofitClient.getApiService(this).getProductsByCategory(categoryId);
        productByCategoryCall.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (!isFinishing()) {
                    if (response.isSuccessful() && response.body() != null) {
                        productList.clear();
                        productList.addAll(response.body());
                        productAdapter.notifyDataSetChanged();
                        Log.d(TAG, "Loaded " + productList.size() + " products for category " + categoryId);
                        checkLoadingComplete();
                    } else {
                        if (response.code() == 404) {
                            productList.clear();
                            productAdapter.notifyDataSetChanged();
                            currentCategoryIndex++;
                            loadProductsForNextCategory();
                        } else {
                            handleApiError(response, "Failed to load products for category");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                if (!call.isCanceled() && !isFinishing()) {
                    Log.e(TAG, "Error loading products by category: " + t.getMessage());
                    Toast.makeText(OrderActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void loadCartQuantity() {
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
                        handleApiError(response, "Failed to load cart items");
                    }
                }
            }

            @Override
            public void onFailure(Call<CartDTO> call, Throwable t) {
                if (!call.isCanceled() && !isFinishing()) {
                    Log.e(TAG, "Error loading cart items: " + t.getMessage());
                    Toast.makeText(OrderActivity.this, "Lỗi kết nối khi tải giỏ hàng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void handleApiError(Response<?> response, String errorPrefix) {
        Log.e(TAG, errorPrefix + ", code: " + response.code() + ", message: " + response.message());
        try {
            Log.e(TAG, "Error body: " + response.errorBody().string());
        } catch (Exception e) {
            Log.e(TAG, "Error parsing error body: " + e.getMessage());
        }
        if (response.code() == 401) {
            Toast.makeText(OrderActivity.this, "Phiên đăng nhập hết hạn, vui lòng đăng nhập lại", Toast.LENGTH_LONG).show();
            SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            prefs.edit().remove("token").apply();
            Intent intent = new Intent(OrderActivity.this, SigninActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(OrderActivity.this, errorPrefix, Toast.LENGTH_SHORT).show();
        }
        progressBar.setVisibility(View.GONE);
    }

    private void checkLoadingComplete() {
        if (!categoryList.isEmpty() && !productList.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            Log.d(TAG, "Loading complete, hiding progress bar");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh cart quantity when returning to the activity
        loadCartQuantity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cancel API calls
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