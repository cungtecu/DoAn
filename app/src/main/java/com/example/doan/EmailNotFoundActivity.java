package com.example.doan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class EmailNotFoundActivity extends AppCompatActivity {
    private Button btnRetry, btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_not_found);

        btnRetry = findViewById(R.id.btn_retry);
        btnRegister = findViewById(R.id.btn_register);

        btnRetry.setOnClickListener(v -> {
            // Quay lại màn hình nhập email
//            startActivity(new Intent(this, ForgetPasswordActivity.class));
            finish();
        });

        btnRegister.setOnClickListener(v -> {
            // Chuyển sang màn hình đăng ký
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        });
    }
}
