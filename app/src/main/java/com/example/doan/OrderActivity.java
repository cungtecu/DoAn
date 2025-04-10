package com.example.doan;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order);

        // Ánh xạ các view
        categoryRecyclerView = findViewById(R.id.category_recycler_view);
        productRecyclerView = findViewById(R.id.product_recycler_view);
        progressBar = findViewById(R.id.progress_bar);

        if (categoryRecyclerView == null || productRecyclerView == null) {
            Log.e(TAG, "RecyclerView not found in layout");
            return;
        }

        // Hiển thị ProgressBar
        progressBar.setVisibility(View.VISIBLE);

        // Khởi tạo RecyclerView
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        productRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // Khởi tạo danh sách rỗng
        categoryList = new ArrayList<>();
        productList = new ArrayList<>();

        // Khởi tạo adapter
        categoryAdapter = new CategoryAdapter(categoryList, categoryId -> {
            Log.d(TAG, "Category clicked: " + categoryId);
            loadProductsByCategory(categoryId);
        });
        productAdapter = new ProductAdapter(this, productList);

        // Gán adapter cho RecyclerView
        categoryRecyclerView.setAdapter(categoryAdapter);
        productRecyclerView.setAdapter(productAdapter);

        // Load dữ liệu từ API
        loadCategories();
        loadProducts();
    }

    private void loadCategories() {
        RetrofitClient.getApiService().getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList.clear();
                    categoryList.addAll(response.body());
                    categoryAdapter.notifyDataSetChanged();
                    Log.d(TAG, "Loaded categories: " + categoryList.size());
                } else {
                    Log.e(TAG, "Failed to load categories, code: " + response.code());
                    Toast.makeText(OrderActivity.this, "Không thể tải danh mục", Toast.LENGTH_SHORT).show();
                }
                checkLoadingComplete();
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.e(TAG, "Error loading categories: " + t.getMessage());
                Toast.makeText(OrderActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                checkLoadingComplete();
            }
        });
    }

    private void loadProducts() {
        RetrofitClient.getApiService().getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList.clear();
                    productList.addAll(response.body());
                    productAdapter.notifyDataSetChanged();
                    Log.d(TAG, "Loaded products: " + productList.size());
                } else {
                    Log.e(TAG, "Failed to load products, code: " + response.code());
                    Toast.makeText(OrderActivity.this, "Không thể tải sản phẩm", Toast.LENGTH_SHORT).show();
                }
                checkLoadingComplete();
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e(TAG, "Error loading products: " + t.getMessage());
                Toast.makeText(OrderActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                checkLoadingComplete();
            }
        });
    }

    private void loadProductsByCategory(int categoryId) {
        RetrofitClient.getApiService().getProductsByCategory(categoryId).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList.clear();
                    productList.addAll(response.body());
                    productAdapter.notifyDataSetChanged();
                    Log.d(TAG, "Loaded products for category " + categoryId + ": " + productList.size());
                } else {
                    Log.e(TAG, "Failed to load products for category, code: " + response.code());
                    Toast.makeText(OrderActivity.this, "Không thể tải sản phẩm theo danh mục", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e(TAG, "Error loading products by category: " + t.getMessage());
                Toast.makeText(OrderActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkLoadingComplete() {
        if (!categoryList.isEmpty() && !productList.isEmpty()) {
            progressBar.setVisibility(View.GONE);
        }
    }
}