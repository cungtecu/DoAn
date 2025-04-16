package com.example.doan;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class PolicyActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView titlePolicy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy);

        // Ánh xạ các view
        btnBack = findViewById(R.id.btn_back);
        titlePolicy = findViewById(R.id.title_policy);

        // Đặt tiêu đề
        titlePolicy.setText("Chính Sách Bảo Mật");

        // Xử lý sự kiện nhấn nút Back
        btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}