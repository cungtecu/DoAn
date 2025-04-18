package com.example.doan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan.api.RetrofitClient;
import com.example.doan.models.Category;
import com.example.doan.models.Product;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderActivity extends AppCompatActivity {

    private static final String TAG = "OrderActivity";
    private RecyclerView categoryRecyclerView, productRecyclerView;
    private CategoryAdapter categoryAdapter;
    private ProductAdapter productAdapter;
    private ProgressBar progressBar;
    private EditText searchEditText;
    private List<Category> categoryList;
    private List<Product> productList;
    private List<Product> allProducts;
    private Call<List<Category>> categoryCall;
    private Call<List<Product>> productByCategoryCall;
    private int currentCategoryIndex = 0;
    private ImageButton btnHome, btnCart, btnOther;
    private ImageView btnBack;
    private Button btnGoToMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order);

        categoryRecyclerView = findViewById(R.id.category_recycler_view);
        productRecyclerView = findViewById(R.id.product_recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        searchEditText = findViewById(R.id.search_edit_text);
        btnHome = findViewById(R.id.btn_home);
        btnCart = findViewById(R.id.cartIcon);
        btnOther = findViewById(R.id.btn_other);
        btnBack = findViewById(R.id.btn_back);
        btnGoToMain = findViewById(R.id.btn_go_to_main);

        if (categoryRecyclerView == null || productRecyclerView == null || progressBar == null || searchEditText == null) {
            Log.e(TAG, "One or more views not found in layout");
            return;
        }

        if (btnHome == null || btnCart == null || btnOther == null || btnBack == null || btnGoToMain == null) {
            Log.e(TAG, "One or more navigation buttons not found in layout");
        }

        progressBar.setVisibility(View.VISIBLE);

        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        productRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        categoryList = new ArrayList<>();
        productList = new ArrayList<>();
        allProducts = new ArrayList<>();

        allProducts.clear();

        categoryAdapter = new CategoryAdapter(this, categoryList, this::loadProductsByCategory);
        productAdapter = new ProductAdapter(this, productList);

        categoryRecyclerView.setAdapter(categoryAdapter);
        productRecyclerView.setAdapter(productAdapter);

        setupSearch();

        loadCategories();

        highlightCurrentPage();

        // Sự kiện cho các nút điều hướng
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                Intent intent = new Intent(OrderActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
            });
        }

        if (btnHome != null) {
            btnHome.setOnClickListener(v -> {
                Intent intent = new Intent(OrderActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
            });
        }

        if (btnCart != null) {
            btnCart.setOnClickListener(v -> {
                Intent intent = new Intent(OrderActivity.this, CartActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
            });
        }

        if (btnOther != null) {
            btnOther.setOnClickListener(v -> {
                Intent intent = new Intent(OrderActivity.this, OtherActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
            });
        }

        if (btnGoToMain != null) {
            btnGoToMain.setOnClickListener(v -> {
                Intent intent = new Intent(OrderActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
            });
        }
    }

    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                filterProducts(s.toString());
            }
        });
    }

    private String removeDiacritics(String str) {
        if (str == null) return "";
        String normalized = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("").replace('đ', 'd').replace('Đ', 'D');
    }

    private void filterProducts(String query) {
        if (query == null || query.trim().isEmpty()) {
            productList.clear();
            if (!allProducts.isEmpty() && !categoryList.isEmpty() && currentCategoryIndex >= 0 && currentCategoryIndex < categoryList.size()) {
                int currentCategoryId = categoryList.get(currentCategoryIndex).getId();
                for (Product product : allProducts) {
                    if (product.getCategoryId() == currentCategoryId) {
                        productList.add(product);
                    }
                }
            }
            productAdapter.notifyDataSetChanged();
            return;
        }

        String normalizedQuery = removeDiacritics(query).toLowerCase();
        List<Product> filteredList = new ArrayList<>();
        for (Product product : allProducts) {
            String normalizedProductName = removeDiacritics(product.getName()).toLowerCase();
            if (normalizedProductName.contains(normalizedQuery)) {
                filteredList.add(product);
            }
        }

        productList.clear();
        productList.addAll(filteredList);
        productAdapter.notifyDataSetChanged();

        if (filteredList.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy sản phẩm nào", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadCategories() {
        categoryCall = RetrofitClient.getApiService(this).getCategories();
        categoryCall.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (!isFinishing()) {
                    Log.d(TAG, "API getCategories response code: " + response.code());
                    if (response.isSuccessful() && response.body() != null) {
                        categoryList.clear();
                        categoryList.addAll(response.body());
                        categoryAdapter.notifyDataSetChanged();
                        Log.d(TAG, "Loaded " + categoryList.size() + " categories");
                        categoryRecyclerView.setVisibility(View.VISIBLE);

                        if (!categoryList.isEmpty()) {
                            currentCategoryIndex = 0;
                            loadProductsForNextCategory();
                        } else {
                            Toast.makeText(OrderActivity.this, "Không có danh mục nào để hiển thị", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    } else {
                        Log.e(TAG, "Failed to load categories, code: " + response.code() + ", message: " + response.message());
                        try {
                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                            Log.e(TAG, "Error body: " + errorBody);
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing error body: " + e.getMessage());
                        }
                        if (response.code() == 401) {
                            Toast.makeText(OrderActivity.this, "Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
                            SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                            prefs.edit().remove("token").apply();
                            Intent intent = new Intent(OrderActivity.this, SigninActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(OrderActivity.this, "Không thể tải danh mục", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
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
            progressBar.setVisibility(View.GONE);
            if (allProducts.isEmpty()) {
                Toast.makeText(OrderActivity.this, "Hiện tại không có sản phẩm nào để hiển thị", Toast.LENGTH_LONG).show();
            }
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
                    Log.d(TAG, "API getProductsByCategory response code: " + response.code());
                    if (response.isSuccessful() && response.body() != null) {
                        List<Product> newProducts = response.body();
                        for (Product product : newProducts) {
                            if (!allProducts.contains(product)) {
                                allProducts.add(product);
                            }
                        }
                        Log.d(TAG, "All products after loading category " + categoryId + ": " + allProducts.toString());
                        productList.clear();
                        productList.addAll(newProducts);
                        productAdapter.notifyDataSetChanged();
                        Log.d(TAG, "Loaded " + productList.size() + " products for category " + categoryId);
                        checkLoadingComplete();

                        currentCategoryIndex++;
                        loadProductsForNextCategory();
                    } else {
                        Log.e(TAG, "Failed to load products for category, code: " + response.code() + ", message: " + response.message());
                        try {
                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                            Log.e(TAG, "Error body: " + errorBody);
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing error body: " + e.getMessage());
                        }
                        if (response.code() == 404) {
                            productList.clear();
                            productAdapter.notifyDataSetChanged();
                            currentCategoryIndex++;
                            loadProductsForNextCategory();
                        } else if (response.code() == 401) {
                            Toast.makeText(OrderActivity.this, "Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
                            SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                            prefs.edit().remove("token").apply();
                            Intent intent = new Intent(OrderActivity.this, SigninActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(OrderActivity.this, "Không thể tải sản phẩm theo danh mục", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
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

    private void checkLoadingComplete() {
        if (!categoryList.isEmpty() && !productList.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            Log.d(TAG, "Loading complete, hiding progress bar");
        }
    }

    private void highlightCurrentPage() {
        if (btnHome != null && btnCart != null && btnOther != null) {
            btnHome.setBackgroundColor(Color.TRANSPARENT);
            btnCart.setBackgroundColor(Color.TRANSPARENT);
            btnOther.setBackgroundColor(Color.GRAY);
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
    }
}