package com.example.doan.admin;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.doan.R;
import com.example.doan.api.RetrofitClient;
import com.example.doan.api.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.Map;

public class Admin_User_Detail_Activity extends AppCompatActivity {
    private TextView tvName, tvEmail, tvPhone, tvPoints;
    private Button btnBackUser;
    private String authToken;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_user_detail);

        // Lấy token từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        authToken = "Bearer " + prefs.getString("token", null);
        if (authToken == null) {
            // Khởi tạo tvName trước khi sử dụng
            tvName = findViewById(R.id.tv_NameUser);
            tvName.setText("Không tìm thấy token, vui lòng đăng nhập lại");
            finish();
            return;
        }

        tvName = findViewById(R.id.tv_NameUser);
        tvEmail = findViewById(R.id.tv_EmailUser);
        tvPhone = findViewById(R.id.tv_PhoneUser);
        tvPoints = findViewById(R.id.tv_PointUser);
        btnBackUser = findViewById(R.id.btn_back_user);

        int userId = getIntent().getIntExtra("USER_ID", -1);
        if (userId != -1) {
            fetchUserDetails(userId);
        } else {
            tvName.setText("Không tìm thấy ID người dùng");
        }
        btnBackUser.setOnClickListener(v -> finish());
    }

    private void fetchUserDetails(int userId) {
        ApiService apiService = RetrofitClient.getApiService(this); // Thêm this
        Call<Map<String, Object>> call = apiService.getUserDetails(authToken, userId);
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> userDetails = response.body();
                    tvName.setText("Tên: " + (userDetails.get("name") != null ? userDetails.get("name") : "N/A"));
                    tvEmail.setText("Email: " + (userDetails.get("email") != null ? userDetails.get("email") : "N/A"));
                    tvPhone.setText("SĐT: " + (userDetails.get("phone") != null ? userDetails.get("phone") : "N/A"));
                    tvPoints.setText("Điểm: " + (userDetails.get("points") != null ? userDetails.get("points") : "0"));
                } else {
                    tvName.setText("Lỗi: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                tvName.setText("Lỗi: " + t.getMessage());
            }
        });
    }
}