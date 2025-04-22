package com.example.doan;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class TermsActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView titleTerms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.terms);

        // Ánh xạ các view
        btnBack = findViewById(R.id.btn_back);
        titleTerms = findViewById(R.id.title_terms);

        // Đặt tiêu đề (không cần thiết vì đã có trong XML, nhưng giữ lại để linh hoạt)
        if (titleTerms != null) {
            titleTerms.setText("Điều Khoản Dịch Vụ");
        }

        // Xử lý sự kiện nhấn nút Back
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                finish();
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}