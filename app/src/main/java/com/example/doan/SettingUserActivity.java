package com.example.doan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SettingUserActivity extends AppCompatActivity {

    private ImageView btnBack;
    private RelativeLayout btnDeleteAccount, btnChangePassword;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_user);

        // Ánh xạ các view
        btnBack = findViewById(R.id.btn_back);
        btnDeleteAccount = findViewById(R.id.btn_delete_account);
        btnChangePassword = findViewById(R.id.btn_change_password);

        // Lấy token từ Intent
        token = getIntent().getStringExtra("token");

        // Xử lý sự kiện nhấn nút Back
        btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });

        // Xử lý sự kiện nhấn nút Xoá tài khoản (để trống theo yêu cầu)
        btnDeleteAccount.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng xoá tài khoản chưa được triển khai", Toast.LENGTH_SHORT).show();
        });

        // Xử lý sự kiện nhấn nút Đổi mật khẩu
        btnChangePassword.setOnClickListener(v -> {
            Intent changePasswordIntent = new Intent(SettingUserActivity.this, ChangePasswordActivity.class);
            changePasswordIntent.putExtra("token", token);
            startActivity(changePasswordIntent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });
    }
}