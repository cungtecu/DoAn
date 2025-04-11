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
    private String authToken; // Token sẽ được lấy từ SharedPreferences

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_detail_user);

        // Lấy token từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        authToken = "Bearer " + prefs.getString("token", null);
        if (authToken == null) {
            tvName.setText("Không tìm thấy token, vui lòng đăng nhập lại");
            finish(); // Thoát activity nếu không có token
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
        ApiService apiService = RetrofitClient.getApiService();
        Call<Map<String, Object>> call = apiService.getUserDetails(authToken, userId);
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> userDetails = response.body();
                    tvName.setText("Name: " + userDetails.get("name"));
                    tvEmail.setText("Email: " + userDetails.get("email"));
                    tvPhone.setText("Phone: " + (userDetails.get("phone") != null ? userDetails.get("phone") : "N/A"));
                    tvPoints.setText("Points: " + userDetails.get("points"));
                } else {
                    tvName.setText("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                tvName.setText("Error: " + t.getMessage());
            }
        });
    }
}