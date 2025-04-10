package com.example.doan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView titleProfile;
    private ImageView profileImage;
    private TextView dripsText;
    private TextView dripsPoints;
    private TextView generalInfoTitle;
    private TextView editButton;
    private TextView txtProfileName;
    private TextView txtProfileGender;
    private TextView txtProfileBirthday;
    private TextView txtProfilePhone;
    private TextView txtProfileEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile); // Khớp với tên file profile.xml

        // Ánh xạ các thành phần từ layout
        btnBack = findViewById(R.id.btn_back);
        titleProfile = findViewById(R.id.title_profile);
        profileImage = findViewById(R.id.profile_image);
        dripsText = findViewById(R.id.drips_text);
        dripsPoints = findViewById(R.id.drips_points);
        generalInfoTitle = findViewById(R.id.general_info_title);
        editButton = findViewById(R.id.edit_button);
        txtProfileName = findViewById(R.id.txtprofile_name);
        txtProfileGender = findViewById(R.id.txtprofile_gender);
        txtProfileBirthday = findViewById(R.id.txtprofile_birthday);
        txtProfilePhone = findViewById(R.id.txtprofile_phone);
        txtProfileEmail = findViewById(R.id.txtprofile_email);

        // Lấy dữ liệu từ Intent
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String phone = intent.getStringExtra("phone");
        String email = intent.getStringExtra("email");
        String token = intent.getStringExtra("token");
        Integer points = intent.getIntExtra("points", 0); // Giá trị mặc định là 0 nếu không có
        String role = intent.getStringExtra("role");

        // Hiển thị dữ liệu lên giao diện
        if (txtProfileName != null) {
            txtProfileName.setText(name != null ? name : "N/A");
        }
        if (txtProfilePhone != null) {
            txtProfilePhone.setText(phone != null ? phone : "N/A");
        }
        if (txtProfileEmail != null) {
            txtProfileEmail.setText(email != null ? email : "N/A");
        }
        if (dripsPoints != null) {
            dripsPoints.setText(String.valueOf(points));
        }
        if (txtProfileGender != null) {
            txtProfileGender.setText("N/A"); // Không có dữ liệu giới tính
        }
        if (txtProfileBirthday != null) {
            txtProfileBirthday.setText("N/A"); // Không có dữ liệu ngày sinh
        }

        // Lưu token vào SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.apply();

        // Xử lý sự kiện nút Back
        if (btnBack != null) {
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish(); // Quay lại MainActivity
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                }
            });
        }

        // Xử lý sự kiện nút Edit (nếu cần)
        if (editButton != null) {
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: Thêm logic để chỉnh sửa thông tin (nếu cần)
                    Toast.makeText(ProfileActivity.this, "Chức năng chỉnh sửa chưa được triển khai", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}