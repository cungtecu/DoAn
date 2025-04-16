package com.example.doan.admin;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.doan.models.PriceHistoryAdapter;
import com.example.doan.R;
import com.example.doan.api.ApiService;
import com.example.doan.api.RetrofitClient;
import com.example.doan.models.PriceHistory;
import com.example.doan.models.Product;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Admin_PriceHistory_Activity extends AppCompatActivity {

    private TextView tvOldPrice, tvNewPrice, tvDateChange;
    private RecyclerView rcvHistoryPrice;
    private PriceHistoryAdapter priceHistoryAdapter;
    private List<PriceHistory> priceHistoryList;
    private Button btnBack;
    private String authToken;
    private int productId;
    private String productName;
    private String productImage; // Thêm biến lưu imageUrl

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_pricehistory);

        // Lấy token từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        authToken = "Bearer " + prefs.getString("token", null);
        if (authToken == null) {
            Toast.makeText(this, "Không tìm thấy token, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Lấy productId và productName từ Intent
        productId = getIntent().getIntExtra("PRODUCT_ID", -1);
        productName = getIntent().getStringExtra("PRODUCT_NAME");
        if (productId == -1) {
            Toast.makeText(this, "Không tìm thấy ID sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Khởi tạo views
        tvOldPrice = findViewById(R.id.tv_oldprice);
        tvNewPrice = findViewById(R.id.tv_newprice);
        tvDateChange = findViewById(R.id.tv_datechange);
        rcvHistoryPrice = findViewById(R.id.rcv_historyprice);
        btnBack = findViewById(R.id.btn_back_user);

        // Thiết lập RecyclerView
        priceHistoryList = new ArrayList<>();
        priceHistoryAdapter = new PriceHistoryAdapter(this, priceHistoryList, productName, productImage);
        rcvHistoryPrice.setLayoutManager(new LinearLayoutManager(this));
        rcvHistoryPrice.setAdapter(priceHistoryAdapter);

        // Sự kiện nút Trở lại
        btnBack.setOnClickListener(v -> finish());

        // Tải dữ liệu
        loadProduct();
        loadPriceHistory();
    }

    private void loadProduct() {
        ApiService apiService = RetrofitClient.getApiService(this);
        Call<Product> call = apiService.getProductById(authToken, productId);
        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Product product = response.body();
                    DecimalFormat df = new DecimalFormat("#,##0 VNĐ");
                    tvNewPrice.setText(df.format(product.getPrice()));
                    productImage = product.getImage(); // Lưu imageUrl
                    // Cập nhật adapter nếu cần
                    priceHistoryAdapter = new PriceHistoryAdapter(Admin_PriceHistory_Activity.this,
                            priceHistoryList, productName, productImage);
                    rcvHistoryPrice.setAdapter(priceHistoryAdapter);
                } else {
                    Toast.makeText(Admin_PriceHistory_Activity.this, "Lỗi tải sản phẩm: " + response.code(), Toast.LENGTH_SHORT).show();
                    tvNewPrice.setText("N/A");
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Toast.makeText(Admin_PriceHistory_Activity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                tvNewPrice.setText("N/A");
            }
        });
    }

    private void loadPriceHistory() {
        ApiService apiService = RetrofitClient.getApiService(this);
        Call<List<PriceHistory>> call = apiService.getPriceHistory(authToken, productId);
        call.enqueue(new Callback<List<PriceHistory>>() {
            @Override
            public void onResponse(Call<List<PriceHistory>> call, Response<List<PriceHistory>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    priceHistoryList.clear();
                    priceHistoryList.addAll(response.body());
                    // Sắp xếp priceHistoryList theo changedAt giảm dần (mới nhất ở đầu)
                    priceHistoryList.sort((ph1, ph2) -> ph2.getChangedAt().compareTo(ph1.getChangedAt()));
                    priceHistoryAdapter.updatePriceHistory(priceHistoryList);

                    if (!priceHistoryList.isEmpty()) {
                        PriceHistory latestHistory = priceHistoryList.get(0);
                        DecimalFormat df = new DecimalFormat("#,##0 VNĐ");
                        tvOldPrice.setText(latestHistory.getOldPrice() != null ? df.format(latestHistory.getOldPrice()) : "");
                        tvDateChange.setText(latestHistory.getChangedAt() != null ? formatDate(latestHistory.getChangedAt()) : "");
                    } else {
                        Toast.makeText(Admin_PriceHistory_Activity.this, "Không có lịch sử thay đổi giá", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<PriceHistory>> call, Throwable t) {
                Toast.makeText(Admin_PriceHistory_Activity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatDate(String isoDate) {
        try {
            return isoDate.replace("T", " ").substring(0, 19);
        } catch (Exception e) {
            return isoDate;
        }
    }
}