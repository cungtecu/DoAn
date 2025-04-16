package com.example.doan.admin;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.doan.models.PriceHistoryAdapter; // Sửa tên package nếu cần
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

public class Admin_HistoryPrice_Activity extends AppCompatActivity {

    private TextView tvOldPrice, tvNewPrice, tvDateChange;
    private RecyclerView rcvHistoryPrice;
    private PriceHistoryAdapter priceHistoryAdapter;
    private List<PriceHistory> priceHistoryList;
    private Button btnBack;
    private String authToken;
    private int productId;
    private String productName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_history_price);

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
        priceHistoryAdapter = new PriceHistoryAdapter(this, priceHistoryList, productName);
        rcvHistoryPrice.setLayoutManager(new LinearLayoutManager(this));
        rcvHistoryPrice.setAdapter(priceHistoryAdapter);

        // Sự kiện nút Trở lại
        btnBack.setOnClickListener(v -> finish());

        // Tải dữ liệu
        loadProduct();
        loadPriceHistory();
    }

    private void loadProduct() {
        ApiService apiService = RetrofitClient.getApiService(this); // Thêm this
        Call<Product> call = apiService.getProductById(authToken, productId); // Sửa tham số
        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Product product = response.body();
                    DecimalFormat df = new DecimalFormat("#,##0 VNĐ");
                    tvNewPrice.setText(df.format(product.getPrice()));
                    // tvOldPrice và tvDateChange sẽ được cập nhật từ lịch sử giá
                } else {
                    Toast.makeText(Admin_HistoryPrice_Activity.this, "Lỗi tải sản phẩm: " + response.code(), Toast.LENGTH_SHORT).show();
                    tvNewPrice.setText("N/A");
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Toast.makeText(Admin_HistoryPrice_Activity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                tvNewPrice.setText("N/A");
            }
        });
    }

    private void loadPriceHistory() {
        ApiService apiService = RetrofitClient.getApiService(this); // Thêm this
        Call<List<PriceHistory>> call = apiService.getPriceHistory(authToken, productId);
        call.enqueue(new Callback<List<PriceHistory>>() {
            @Override
            public void onResponse(Call<List<PriceHistory>> call, Response<List<PriceHistory>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    priceHistoryList.clear();
                    priceHistoryList.addAll(response.body());
                    priceHistoryAdapter.updatePriceHistory(priceHistoryList);

                    // Hiển thị bản ghi mới nhất ở TextView
                    if (!priceHistoryList.isEmpty()) {
                        PriceHistory latestHistory = priceHistoryList.get(0); // Giả sử danh sách được sắp xếp giảm dần theo thời gian
                        DecimalFormat df = new DecimalFormat("#,##0 VNĐ");
                        tvOldPrice.setText(latestHistory.getOldPrice() != null ? df.format(latestHistory.getOldPrice()) : "N/A");
                        tvDateChange.setText(latestHistory.getChangedAt() != null ? formatDate(latestHistory.getChangedAt()) : "N/A");
                    } else {
                        tvOldPrice.setText("N/A");
                        tvDateChange.setText("N/A");
                        Toast.makeText(Admin_HistoryPrice_Activity.this, "Không có lịch sử thay đổi giá", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    tvOldPrice.setText("N/A");
                    tvDateChange.setText("N/A");
                    Toast.makeText(Admin_HistoryPrice_Activity.this, "Lỗi tải lịch sử giá: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PriceHistory>> call, Throwable t) {
                tvOldPrice.setText("N/A");
                tvDateChange.setText("N/A");
                Toast.makeText(Admin_HistoryPrice_Activity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Định dạng ngày từ ISO sang dạng dễ đọc
    private String formatDate(String isoDate) {
        try {
            return isoDate.replace("T", " ").substring(0, 19);
        } catch (Exception e) {
            return isoDate;
        }
    }
}