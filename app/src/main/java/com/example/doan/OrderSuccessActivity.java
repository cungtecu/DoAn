package com.example.doan;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class OrderSuccessActivity extends AppCompatActivity {

    private static final String TAG = "OrderSuccessActivity";
    private Button btnContinueShopping;
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
            tvOrderId = findViewById(R.id.tv_order_id);
            tvUsedDrips = findViewById(R.id.tv_used_drips);
            tvExpectedDrips = findViewById(R.id.tv_expected_drips);
            Log.d(TAG, "Views initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views: " + e.getMessage());
            return;
        }

        // Nhận dữ liệu từ Intent
        double totalPrice = getIntent().getDoubleExtra("total_price", 0);
        int usedDrips = getIntent().getIntExtra("used_drips", 0);
        // Tính điểm Drips dự kiến (1 VNĐ = 1 Drip)
        int expectedDrips = (int) totalPrice;

        // Điền dữ liệu
        try {
            tvOrderId.setText("Mã đơn hàng: DH12345"); // Thay DH12345 bằng mã đơn hàng thực tế nếu có API
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