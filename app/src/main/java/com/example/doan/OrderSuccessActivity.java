package com.example.doan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class OrderSuccessActivity extends AppCompatActivity {

    private TextView tvOrderId, tvUsedDrips, tvExpectedDrips;
    private Button btnContinueShopping;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success);

        // Khởi tạo views
        tvOrderId = findViewById(R.id.tv_order_id);
        tvUsedDrips = findViewById(R.id.tv_used_drips);
        tvExpectedDrips = findViewById(R.id.tv_expected_drips);
        btnContinueShopping = findViewById(R.id.btn_continue_shopping);

        // Lấy dữ liệu từ Intent
        String orderId = getIntent().getStringExtra("orderId");
        int usedDrips = getIntent().getIntExtra("usedDrips", 0);
        int expectedDrips = getIntent().getIntExtra("expectedDrips", 0);

        // Hiển thị thông tin
        tvOrderId.setText("Mã đơn hàng: " + orderId);
        tvUsedDrips.setText("Điểm Drips đã sử dụng: " + usedDrips);
        tvExpectedDrips.setText("Điểm Drips dự kiến nhận được: " + expectedDrips);

        // Xử lý nút Tiếp tục mua sắm
        btnContinueShopping.setOnClickListener(v -> {
            Intent intent = new Intent(OrderSuccessActivity.this, OrderActivity.class);
            startActivity(intent);
            finish();
        });
    }
}