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
import com.example.doan.models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private static final int REQUEST_CODE_EDIT_PROFILE = 100;
    private ImageView btnBack;
    private TextView titleProfile;
    private ImageView profileImage;
    private TextView dripsText;
    private TextView dripsPoints;
    private TextView generalInfoTitle;
    private TextView editButton;
    private TextView txtProfileName;
    private TextView txtProfilePhone;
    private TextView txtProfileEmail;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        // Ánh xạ các thành phần từ layout
        btnBack = findViewById(R.id.btn_back);
        titleProfile = findViewById(R.id.title_profile);
        profileImage = findViewById(R.id.profile_image);
        dripsText = findViewById(R.id.drips_text);
        dripsPoints = findViewById(R.id.drips_points);
        generalInfoTitle = findViewById(R.id.general_info_title);
        editButton = findViewById(R.id.edit_button);
        txtProfileName = findViewById(R.id.txtprofile_name);
        txtProfilePhone = findViewById(R.id.txtprofile_phone);
        txtProfileEmail = findViewById(R.id.txtprofile_email);

        // Lấy token từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        token = sharedPreferences.getString("token", null);

        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Token không tồn tại. Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ProfileActivity.this, SigninActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Tải thông tin người dùng từ API
        loadUserProfile();

        // Xử lý sự kiện nút Back
        btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });

        // Xử lý sự kiện nút Edit
        editButton.setOnClickListener(v -> {
            // Kiểm tra lại token trước khi mở EditProfileActivity
            if (token == null || token.isEmpty()) {
                Toast.makeText(ProfileActivity.this, "Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProfileActivity.this, SigninActivity.class);
                startActivity(intent);
                finish();
                return;
            }

            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            intent.putExtra("token", token);
            intent.putExtra("name", txtProfileName.getText().toString());
            intent.putExtra("email", txtProfileEmail.getText().toString());
            intent.putExtra("phone", txtProfilePhone.getText().toString());
            startActivityForResult(intent, REQUEST_CODE_EDIT_PROFILE);
        });
    }

    private void loadUserProfile() {
        RetrofitClient.getApiService(this).getCurrentUser("Bearer " + token).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    txtProfileName.setText(user.getName() != null ? user.getName() : "N/A");
                    txtProfilePhone.setText(user.getPhone() != null ? user.getPhone() : "N/A");
                    txtProfileEmail.setText(user.getEmail() != null ? user.getEmail() : "N/A");
                    dripsPoints.setText(String.valueOf(user.getPoints() != null ? user.getPoints() : 0));
                } else {
                    Log.e(TAG, "Lỗi tải thông tin người dùng: " + response.code());
                    Toast.makeText(ProfileActivity.this, "Không thể tải thông tin người dùng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối: " + t.getMessage());
                Toast.makeText(ProfileActivity.this, "Lỗi kết nối. Vui lòng kiểm tra mạng!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EDIT_PROFILE && resultCode == RESULT_OK && data != null) {
            // Cập nhật giao diện với thông tin mới
            String name = data.getStringExtra("name");
            String email = data.getStringExtra("email");
            String phone = data.getStringExtra("phone");
            txtProfileName.setText(name != null ? name : "N/A");
            txtProfileEmail.setText(email != null ? email : "N/A");
            txtProfilePhone.setText(phone != null ? phone : "N/A");
        }
    }
}