package com.example.doan.admin;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.doan.R;
import com.example.doan.api.ApiService;
import com.example.doan.api.RetrofitClient;
import com.example.doan.models.PriceHistory;
import com.example.doan.models.PriceHistoryAdapter;
import com.example.doan.models.Product;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Admin_PriceHistory_All_Activity extends AppCompatActivity {

    private RecyclerView rcvHistoryPrice;
    private PriceHistoryAdapter priceHistoryAdapter;
    private List<PriceHistory> priceHistoryList;
    private Map<Integer, String> productNameMap;
    private Map<Integer, String> productImageMap; // Thêm ánh xạ productId -> imageUrl
    private Button btnBack;
    private String authToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_pricehistory_all);

        // Lấy token từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        authToken = "Bearer " + prefs.getString("token", null);
        if (authToken == null) {
            Toast.makeText(this, "Không tìm thấy token, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Khởi tạo views
        rcvHistoryPrice = findViewById(R.id.rcv_historyprice);
        btnBack = findViewById(R.id.btn_back_user);

        // Thiết lập RecyclerView
        priceHistoryList = new ArrayList<>();
        productNameMap = new HashMap<>();
        productImageMap = new HashMap<>(); // Khởi tạo ánh xạ ảnh
        priceHistoryAdapter = new PriceHistoryAdapter(this, priceHistoryList, productNameMap, productImageMap);
        rcvHistoryPrice.setLayoutManager(new LinearLayoutManager(this));
        rcvHistoryPrice.setAdapter(priceHistoryAdapter);

        // Sự kiện nút Trở lại
        btnBack.setOnClickListener(v -> finish());

        // Tải dữ liệu
        loadAllPriceHistory();
    }

    private void loadAllPriceHistory() {
        ApiService apiService = RetrofitClient.getApiService(this);
        // Bước 1: Lấy danh sách sản phẩm
        Call<List<Product>> productCall = apiService.getAllProducts(authToken);
        productCall.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body();
                    // Lưu ánh xạ productId -> productName và productId -> imageUrl
                    for (Product product : products) {
                        productNameMap.put(product.getId(), product.getName());
                        productImageMap.put(product.getId(), product.getImage());
                    }
                    // Bước 2: Lấy lịch sử giá cho từng sản phẩm
                    fetchPriceHistoryForProducts(apiService, products);
                } else {
                    Toast.makeText(Admin_PriceHistory_All_Activity.this, "Lỗi tải danh sách sản phẩm: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(Admin_PriceHistory_All_Activity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchPriceHistoryForProducts(ApiService apiService, List<Product> products) {
        priceHistoryList.clear();
        int[] completedRequests = {0};
        int totalRequests = products.size();

        if (totalRequests == 0) {
            Toast.makeText(this, "Không có sản phẩm nào", Toast.LENGTH_SHORT).show();
            priceHistoryAdapter.updatePriceHistory(priceHistoryList);
            return;
        }

        for (Product product : products) {
            Call<List<PriceHistory>> historyCall = apiService.getPriceHistory(authToken, product.getId());
            historyCall.enqueue(new Callback<List<PriceHistory>>() {
                @Override
                public void onResponse(Call<List<PriceHistory>> call, Response<List<PriceHistory>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        priceHistoryList.addAll(response.body());
                    }
                    completedRequests[0]++;
                    if (completedRequests[0] == totalRequests) {
                        // Sắp xếp priceHistoryList theo updatedAt giảm dần (mới nhất ở đầu)
                        priceHistoryList.sort((ph1, ph2) -> ph2.getChangedAt().compareTo(ph1.getChangedAt()));
                        priceHistoryAdapter.updatePriceHistory(priceHistoryList);
                        if (priceHistoryList.isEmpty()) {
                            Toast.makeText(Admin_PriceHistory_All_Activity.this, "Không có lịch sử thay đổi giá", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<PriceHistory>> call, Throwable t) {
                    completedRequests[0]++;
                    if (completedRequests[0] == totalRequests) {
                        // Sắp xếp priceHistoryList theo updatedAt giảm dần (mới nhất ở đầu)
                        priceHistoryList.sort((ph1, ph2) -> ph2.getChangedAt().compareTo(ph1.getChangedAt()));
                        priceHistoryAdapter.updatePriceHistory(priceHistoryList);
                        if (priceHistoryList.isEmpty()) {
                            Toast.makeText(Admin_PriceHistory_All_Activity.this, "Không có lịch sử thay đổi giá", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }
}