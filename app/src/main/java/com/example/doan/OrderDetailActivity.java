package com.example.doan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.doan.api.RetrofitClient;
import com.example.doan.models.OrderDetailResponse;
import com.example.doan.models.OrderResponse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity {

    private static final String TAG = "OrderDetailActivity";
    private TextView tvOrderId, tvOrderDate, tvTotalPrice, tvDripsPoints, tvUsedPoints, tvProductList;
    private ImageView btnBack;
    private Call<OrderResponse> orderCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.his_order_detail);

        tvOrderId = findViewById(R.id.tv_order_id);
        tvOrderDate = findViewById(R.id.tv_order_date);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        tvDripsPoints = findViewById(R.id.drips_points);
        tvUsedPoints = findViewById(R.id.tv_used_points);
        tvProductList = findViewById(R.id.tv_product_list);
        btnBack = findViewById(R.id.btn_back);

        int orderId = getIntent().getIntExtra("orderId", 0);
        if (orderId == 0) {
            Toast.makeText(this, "Không tìm thấy đơn hàng!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        if (token == null) {
            Toast.makeText(this, "Token không hợp lệ. Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, SigninActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        fetchOrderDetails(orderId, token);

        btnBack.setOnClickListener(v -> finish());
    }

    private void fetchOrderDetails(int orderId, String token) {
        Log.d(TAG, "Fetching order details for orderId: " + orderId);
        orderCall = RetrofitClient.getApiService(this).getOrderById("Bearer " + token, orderId);
        orderCall.enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (!isFinishing()) {
                    if (response.isSuccessful() && response.body() != null) {
                        OrderResponse order = response.body();
                        Log.d(TAG, "Order ID from API: " + order.getId());
                        Log.d(TAG, "Order Total Price from API: " + order.getTotalPrice());
                        Log.d(TAG, "Order Earned Points from API: " + order.getEarnedPoints());
                        Log.d(TAG, "Order Used Points from API: " + order.getUsedPoints());
                        displayOrderDetails(order);
                    } else {
                        Log.e(TAG, "Failed to fetch order details, code: " + response.code());
                        Toast.makeText(OrderDetailActivity.this, "Không thể tải chi tiết đơn hàng!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                if (!call.isCanceled() && !isFinishing()) {
                    Log.e(TAG, "Error fetching order details: " + t.getMessage());
                    Toast.makeText(OrderDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void displayOrderDetails(OrderResponse order) {
        tvOrderId.setText(String.valueOf(order.getId()));

        // Định dạng lại ngày đặt hàng
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
            Date date = inputFormat.parse(order.getOrderDate());
            tvOrderDate.setText(outputFormat.format(date));
        } catch (Exception e) {
            Log.e(TAG, "Error parsing date: " + e.getMessage());
            tvOrderDate.setText(order.getOrderDate());
        }

        tvTotalPrice.setText(String.format("%,d VNĐ", (long) order.getTotalPrice()));

        // Tính điểm Drips (1,000 VNĐ = 1 điểm)
        double totalPrice = order.getTotalPrice();
        int dripsPoints = (int) (totalPrice / 1000); // 110,000 / 1,000 = 110 điểm
        tvDripsPoints.setText("+" + dripsPoints);

        // Điểm đã sử dụng (nếu không có từ API, mặc định là 0)
        int usedPoints = order.getUsedPoints() != null ? order.getUsedPoints() : 0;
        tvUsedPoints.setText(String.valueOf(usedPoints));

        // Hiển thị danh sách sản phẩm trong TextView
        List<OrderDetailResponse> details = order.getOrderDetails();
        if (details != null && !details.isEmpty()) {
            StringBuilder productList = new StringBuilder();
            for (OrderDetailResponse detail : details) {
                productList.append(detail.getProductName())
                        .append(" x")
                        .append(detail.getQuantity())
                        .append(", ");
            }
            if (productList.length() > 2) {
                productList.setLength(productList.length() - 2);
            }
            tvProductList.setText(productList.toString());
        } else {
            tvProductList.setText("Không có sản phẩm");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (orderCall != null && !orderCall.isCanceled()) {
            orderCall.cancel();
        }
    }
}