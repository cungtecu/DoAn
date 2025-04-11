package com.example.doan;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.doan.api.RetrofitClient;
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
    private Call<List<Category>> categoryCall;
    private Call<List<Product>> productByCategoryCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order);

        // Ánh xạ
        categoryRecyclerView = findViewById(R.id.category_recycler_view);
        productRecyclerView = findViewById(R.id.product_recycler_view);
        progressBar = findViewById(R.id.progress_bar);

        if (categoryRecyclerView == null || productRecyclerView == null || progressBar == null) {
            Log.e(TAG, "One or more views not found in layout");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // Cấu hình RecyclerView
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        productRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // Khởi tạo danh sách
        categoryList = new ArrayList<>();
        productList = new ArrayList<>();

        // Khởi tạo adapter
        categoryAdapter = new CategoryAdapter(this, categoryList, this::loadProductsByCategory);
        productAdapter = new ProductAdapter(this, productList);

        // Gán adapter
        categoryRecyclerView.setAdapter(categoryAdapter);
        productRecyclerView.setAdapter(productAdapter);

        // Load danh mục (sản phẩm của danh mục đầu tiên sẽ được load tự động sau khi load danh mục)
        loadCategories();
    }

    private void loadCategories() {
        categoryCall = RetrofitClient.getApiService().getCategories();
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

                        // Tự động load sản phẩm của danh mục đầu tiên
                        if (!categoryList.isEmpty()) {
                            int firstCategoryId = categoryList.get(0).getId();
                            loadProductsByCategory(firstCategoryId);
                        }
                    } else {
                        Log.e(TAG, "Failed to load categories, code: " + response.code() + ", message: " + response.message());
                        try {
                            Log.e(TAG, "Error body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing error body: " + e.getMessage());
                        }
                        if (response.code() == 401) {
                            Toast.makeText(OrderActivity.this, "Phiên đăng nhập hết hạn, vui lòng đăng nhập lại", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(OrderActivity.this, SigninActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(OrderActivity.this, "Không thể tải danh mục", Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.GONE);
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

    private void loadProductsByCategory(int categoryId) {
        productByCategoryCall = RetrofitClient.getApiService().getProductsByCategory(categoryId);
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
                        Log.e(TAG, "Failed to load products for category, code: " + response.code() + ", message: " + response.message());
                        try {
                            Log.e(TAG, "Error body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing error body: " + e.getMessage());
                        }
                        if (response.code() == 404) {
                            productList.clear();
                            productAdapter.notifyDataSetChanged();
                            Toast.makeText(OrderActivity.this, "Không có sản phẩm nào trong danh mục này", Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 401) {
                            Toast.makeText(OrderActivity.this, "Phiên đăng nhập hết hạn, vui lòng đăng nhập lại", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(OrderActivity.this, SigninActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(OrderActivity.this, "Không thể tải sản phẩm theo danh mục", Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.GONE);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Hủy các yêu cầu API khi activity bị hủy
        if (categoryCall != null && !categoryCall.isCanceled()) {
            categoryCall.cancel();
        }
        if (productByCategoryCall != null && !productByCategoryCall.isCanceled()) {
            productByCategoryCall.cancel();
        }
    }
}