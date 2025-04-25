package com.example.doan;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class OrderSuccessActivity extends AppCompatActivity {

    private static final String TAG = "OrderSuccessActivity";
    private Button btnContinueShopping, btnViewHistory;
    private TextView tvOrderId, tvUsedDrips, tvExpectedDrips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_order_success);
            Log.d(TAG, "Layout activity_order_success loaded successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error loading layout: " + e.getMessage());
            return;
        }

        // Khởi tạo các view
        try {
            btnContinueShopping = findViewById(R.id.btn_continue_shopping);
            btnViewHistory = findViewById(R.id.btn_view_history);
            tvOrderId = findViewById(R.id.tv_order_id);
            tvUsedDrips = findViewById(R.id.tv_used_drips);
            tvExpectedDrips = findViewById(R.id.tv_expected_drips);
            Log.d(TAG, "Views initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views: " + e.getMessage());
            return;
        }

        // Nhận dữ liệu từ Intent
        int orderId = getIntent().getIntExtra("orderId", 0);
        double totalPrice = getIntent().getDoubleExtra("total_price", 0);
        int usedDrips = getIntent().getIntExtra("used_drips", 0);
        // Tính điểm Drips dự kiến (1 VNĐ = 1 Drip)
        int expectedDrips = (int) totalPrice;

        // Điền dữ liệu
        try {
            tvOrderId.setText("Mã đơn hàng: " + orderId);
            tvUsedDrips.setText("Điểm Drips đã sử dụng: " + usedDrips);
            tvExpectedDrips.setText("Điểm Drips dự kiến nhận được: " + expectedDrips);
            Log.d(TAG, "Data set to views successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error setting data to views: " + e.getMessage());
        }

        // Sự kiện nhấn nút "Tiếp tục mua sắm"
        btnContinueShopping.setOnClickListener(v -> {
            goToCartActivity();
        });

        // Sự kiện nhấn nút "Xem lịch sử đơn hàng"
        btnViewHistory.setOnClickListener(v -> {
            Intent intent = new Intent(OrderSuccessActivity.this, HistoryOrderActivity.class);
            startActivity(intent);
        });
    }

    private void goToCartActivity() {
        Intent intent = new Intent(OrderSuccessActivity.this, CartActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Khi nhấn nút Back, cũng quay lại CartActivity
        goToCartActivity();
    }
}