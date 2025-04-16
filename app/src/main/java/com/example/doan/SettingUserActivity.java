package com.example.doan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.doan.api.RetrofitClient;
import com.example.doan.models.ApiResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingUserActivity extends AppCompatActivity {

    private static final String TAG = "SettingUserActivity";
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

        // Lấy token từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        token = sharedPreferences.getString("token", null);

        if (token == null) {
            Toast.makeText(this, "Vui lòng đăng nhập lại!", Toast.LENGTH_LONG).show();
            Intent signinIntent = new Intent(SettingUserActivity.this, SigninActivity.class);
            startActivity(signinIntent);
            finish();
            return;
        }

        // Xử lý sự kiện nhấn nút Back
        btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });

        // Xử lý sự kiện nhấn nút Xoá tài khoản
        btnDeleteAccount.setOnClickListener(v -> showDeleteConfirmationDialog());

        // Xử lý sự kiện nhấn nút Đổi mật khẩu
        btnChangePassword.setOnClickListener(v -> {
            Intent changePasswordIntent = new Intent(SettingUserActivity.this, ChangePasswordActivity.class);
            changePasswordIntent.putExtra("token", token);
            startActivity(changePasswordIntent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });
    }

    private void showDeleteConfirmationDialog() {
        DeleteAccountDialogFragment dialogFragment = new DeleteAccountDialogFragment();
        dialogFragment.setOnDeleteAccountListener(this::deleteAccount);
        dialogFragment.show(getSupportFragmentManager(), "DeleteAccountDialogFragment");
    }

    private void deleteAccount() {
        Call<ApiResponse> call = RetrofitClient.getApiService().deleteProfile("Bearer " + token);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    Log.d(TAG, "Phản hồi từ server: status=" + apiResponse.getStatus() + ", message=" + apiResponse.getMessage());
                    if ("success".equals(apiResponse.getStatus()) && "Tài khoản đã được xóa thành công".equals(apiResponse.getMessage())) {
                        Toast.makeText(SettingUserActivity.this,
                                "Tài khoản đã được xóa! Đang chuyển hướng đến trang đăng nhập...",
                                Toast.LENGTH_LONG).show();

                        // Xóa token khỏi SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove("token");
                        editor.apply();

                        // Chuyển về SigninActivity
                        Intent signinIntent = new Intent(SettingUserActivity.this, SigninActivity.class);
                        signinIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(signinIntent);
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    } else {
                        Log.w(TAG, "Lỗi từ server: " + apiResponse.getMessage());
                        Toast.makeText(SettingUserActivity.this,
                                apiResponse.getMessage() != null ? apiResponse.getMessage() : "Lỗi không xác định",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Không có thông tin lỗi";
                        Log.e(TAG, "Lỗi từ server: " + errorBody + ", mã lỗi: " + response.code());
                        if (response.code() == 401) {
                            Log.w(TAG, "Phiên đăng nhập hết hạn");
                            Toast.makeText(SettingUserActivity.this,
                                    "Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại!",
                                    Toast.LENGTH_SHORT).show();
                            Intent signinIntent = new Intent(SettingUserActivity.this, SigninActivity.class);
                            signinIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(signinIntent);
                        } else {
                            Toast.makeText(SettingUserActivity.this,
                                    "Lỗi: " + errorBody,
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi parse phản hồi: " + e.getMessage());
                        Toast.makeText(SettingUserActivity.this,
                                "Lỗi khi xử lý phản hồi từ server. Vui lòng thử lại!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối: " + t.getMessage());
                Toast.makeText(SettingUserActivity.this,
                        "Lỗi kết nối. Vui lòng kiểm tra mạng và thử lại!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}