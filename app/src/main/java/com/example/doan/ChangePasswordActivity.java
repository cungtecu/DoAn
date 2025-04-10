package com.example.doan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.doan.api.RetrofitClient;
import com.example.doan.models.ApiResponse;
import com.example.doan.models.ChangePasswordRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    private static final String TAG = "ChangePasswordActivity";
    private ImageView btnBack, iconOldPass, iconNewPass3, iconNewPass4;
    private EditText oldPass, newPass3, newPass4;
    private Button btnChange;
    private ProgressBar progressBar;
    private String token;
    private boolean isOldPassVisible = false;
    private boolean isNewPass3Visible = false;
    private boolean isNewPass4Visible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);

        // Ánh xạ các view
        btnBack = findViewById(R.id.btn_back);
        iconOldPass = findViewById(R.id.icon_oldpass);
        iconNewPass3 = findViewById(R.id.icon_newpass3);
        iconNewPass4 = findViewById(R.id.icon_newpass4);
        oldPass = findViewById(R.id.old_pass);
        newPass3 = findViewById(R.id.new_pass3);
        newPass4 = findViewById(R.id.new_pass4);
        btnChange = findViewById(R.id.btn_change);
        progressBar = findViewById(R.id.progress_bar);

        // Lấy token từ Intent
        token = getIntent().getStringExtra("token");
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Lỗi: Token không hợp lệ. Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            Intent signinIntent = new Intent(ChangePasswordActivity.this, SigninActivity.class);
            startActivity(signinIntent);
            finish();
            return;
        }

        // Xử lý sự kiện nhấn nút Back
        btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });

        // Xử lý hiển thị/ẩn mật khẩu cũ
        iconOldPass.setOnClickListener(v -> {
            isOldPassVisible = !isOldPassVisible;
            if (isOldPassVisible) {
                oldPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                iconOldPass.setImageResource(R.drawable.show_password);
            } else {
                oldPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                iconOldPass.setImageResource(R.drawable.hide_password);
            }
            oldPass.setSelection(oldPass.getText().length());
        });

        // Xử lý hiển thị/ẩn mật khẩu mới
        iconNewPass3.setOnClickListener(v -> {
            isNewPass3Visible = !isNewPass3Visible;
            if (isNewPass3Visible) {
                newPass3.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                iconNewPass3.setImageResource(R.drawable.show_password);
            } else {
                newPass3.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                iconNewPass3.setImageResource(R.drawable.hide_password);
            }
            newPass3.setSelection(newPass3.getText().length());
        });

        // Xử lý hiển thị/ẩn xác nhận mật khẩu mới
        iconNewPass4.setOnClickListener(v -> {
            isNewPass4Visible = !isNewPass4Visible;
            if (isNewPass4Visible) {
                newPass4.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                iconNewPass4.setImageResource(R.drawable.show_password);
            } else {
                newPass4.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                iconNewPass4.setImageResource(R.drawable.hide_password);
            }
            newPass4.setSelection(newPass4.getText().length());
        });

        // Xử lý sự kiện nhấn nút Đổi mật khẩu
        btnChange.setOnClickListener(v -> {
            String oldPassword = oldPass.getText().toString().trim();
            String newPassword = newPass3.getText().toString().trim();
            String confirmPassword = newPass4.getText().toString().trim();

            // Kiểm tra dữ liệu đầu vào
            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (newPassword.length() < 6) {
                Toast.makeText(this, "Mật khẩu mới phải dài ít nhất 6 ký tự!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "Mật khẩu xác nhận không khớp với mật khẩu mới!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gọi API để đổi mật khẩu
            changePassword(oldPassword, newPassword);
        });
    }

    private void changePassword(String oldPassword, String newPassword) {
        // Hiển thị ProgressBar
        progressBar.setVisibility(View.VISIBLE);
        btnChange.setEnabled(false); // Vô hiệu hóa nút trong khi gọi API

        ChangePasswordRequest request = new ChangePasswordRequest(oldPassword, newPassword);
        Call<ApiResponse> call = RetrofitClient.getApiService().changePassword("Bearer " + token, request);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                // Ẩn ProgressBar và kích hoạt lại nút
                progressBar.setVisibility(View.GONE);
                btnChange.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if ("success".equals(apiResponse.getStatus())) {
                        Toast.makeText(ChangePasswordActivity.this, "Đổi mật khẩu thành công! Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();

                        // Xóa token khỏi SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove("token");
                        editor.apply();

                        // Chuyển về SigninActivity và kết thúc tất cả activity trước đó
                        Intent signinIntent = new Intent(ChangePasswordActivity.this, SigninActivity.class);
                        signinIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(signinIntent);
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    } else {
                        Toast.makeText(ChangePasswordActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Không có thông tin lỗi";
                        Log.e(TAG, "Error body: " + errorBody);
                        if (response.code() == 400) {
                            Toast.makeText(ChangePasswordActivity.this, "Mật khẩu cũ không đúng!", Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 401) {
                            Toast.makeText(ChangePasswordActivity.this, "Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
                            Intent signinIntent = new Intent(ChangePasswordActivity.this, SigninActivity.class);
                            signinIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(signinIntent);
                        } else if (response.code() == 404) {
                            Toast.makeText(ChangePasswordActivity.this, "Không tìm thấy API đổi mật khẩu. Vui lòng kiểm tra server!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ChangePasswordActivity.this, "Lỗi từ server: " + errorBody, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi parse phản hồi: " + e.getMessage());
                        Toast.makeText(ChangePasswordActivity.this, "Lỗi khi xử lý phản hồi từ server. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Ẩn ProgressBar và kích hoạt lại nút
                progressBar.setVisibility(View.GONE);
                btnChange.setEnabled(true);

                Log.e(TAG, "Lỗi kết nối: " + t.getMessage());
                Toast.makeText(ChangePasswordActivity.this, "Lỗi kết nối. Vui lòng kiểm tra mạng và thử lại!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Xử lý nút Back trên thiết bị
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}