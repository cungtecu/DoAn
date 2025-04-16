package com.example.doan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

        btnBack = findViewById(R.id.btn_back);
        iconOldPass = findViewById(R.id.icon_oldpass);
        iconNewPass3 = findViewById(R.id.icon_newpass3);
        iconNewPass4 = findViewById(R.id.icon_newpass4);
        oldPass = findViewById(R.id.old_pass);
        newPass3 = findViewById(R.id.new_pass3);
        newPass4 = findViewById(R.id.new_pass4);
        btnChange = findViewById(R.id.btn_change);
        progressBar = findViewById(R.id.progress_bar);

        token = getIntent().getStringExtra("token");
        Log.d(TAG, "Token received: " + token);
        if (token == null || token.isEmpty()) {
            SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            token = sharedPreferences.getString("token", null);
            if (token == null || token.isEmpty()) {
                Log.e(TAG, "Token không hợp lệ, chuyển hướng về SigninActivity");
                Toast.makeText(this, "Lỗi: Token không hợp lệ. Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
                Intent signinIntent = new Intent(ChangePasswordActivity.this, SigninActivity.class);
                startActivity(signinIntent);
                finish();
                return;
            }
        }

        btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });

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

        btnChange.setOnClickListener(v -> {
            String oldPassword = oldPass.getText().toString().trim();
            String newPassword = newPass3.getText().toString().trim();
            String confirmPassword = newPass4.getText().toString().trim();

            if (oldPassword.isEmpty()) {
                oldPass.setError("Vui lòng nhập mật khẩu cũ!");
                oldPass.requestFocus();
                return;
            }

            if (newPassword.isEmpty()) {
                newPass3.setError("Vui lòng nhập mật khẩu mới!");
                newPass3.requestFocus();
                return;
            }

            if (confirmPassword.isEmpty()) {
                newPass4.setError("Vui lòng xác nhận mật khẩu mới!");
                newPass4.requestFocus();
                return;
            }

            if (newPassword.length() < 6) {
                newPass3.setError("Mật khẩu mới phải dài ít nhất 6 ký tự!");
                newPass3.requestFocus();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                newPass4.setError("Mật khẩu xác nhận không khớp với mật khẩu mới!");
                newPass4.requestFocus();
                return;
            }

            changePassword(oldPassword, newPassword, confirmPassword);
        });
    }

    private void changePassword(String oldPassword, String newPassword, String confirmPassword) {
        progressBar.setVisibility(View.VISIBLE);
        btnChange.setEnabled(false);
        oldPass.setEnabled(false);
        newPass3.setEnabled(false);
        newPass4.setEnabled(false);

        ChangePasswordRequest request = new ChangePasswordRequest(oldPassword, newPassword, confirmPassword);
        Log.d(TAG, "Gửi yêu cầu đổi mật khẩu: oldPassword=" + oldPassword + ", newPassword=" + newPassword + ", confirmPassword=" + confirmPassword);
        Call<ApiResponse> call = RetrofitClient.getApiService(this).changePassword("Bearer " + token, request);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                progressBar.setVisibility(View.GONE);
                btnChange.setEnabled(true);
                oldPass.setEnabled(true);
                newPass3.setEnabled(true);
                newPass4.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    Log.d(TAG, "Phản hồi từ server: status=" + apiResponse.getStatus() + ", message=" + apiResponse.getMessage());

                    if ("success".equals(apiResponse.getStatus())) {
                        Toast.makeText(ChangePasswordActivity.this,
                                "Đổi mật khẩu thành công! Đang chuyển hướng đến trang đăng nhập...",
                                Toast.LENGTH_LONG).show();

                        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove("token");
                        editor.apply();

                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            Intent signinIntent = new Intent(ChangePasswordActivity.this, SigninActivity.class);
                            signinIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(signinIntent);
                            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        }, 2000);
                    } else {
                        Log.w(TAG, "Lỗi từ server: " + apiResponse.getMessage());
                        Toast.makeText(ChangePasswordActivity.this,
                                apiResponse.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        if (apiResponse.getMessage().contains("Mật khẩu cũ không đúng")) {
                            oldPass.setText("");
                            oldPass.requestFocus();
                        } else if (apiResponse.getMessage().contains("Mật khẩu mới phải có ít nhất 6 ký tự")) {
                            newPass3.setText("");
                            newPass4.setText("");
                            newPass3.requestFocus();
                        } else if (apiResponse.getMessage().contains("Mật khẩu mới và xác nhận mật khẩu không khớp")) {
                            newPass4.setText("");
                            newPass4.requestFocus();
                        }
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Không có thông tin lỗi";
                        Log.e(TAG, "Lỗi từ server: " + errorBody + ", mã lỗi: " + response.code());
                        if (response.code() == 400) {
                            if (errorBody.contains("Mật khẩu cũ không đúng")) {
                                Toast.makeText(ChangePasswordActivity.this,
                                        "Mật khẩu cũ không đúng!",
                                        Toast.LENGTH_SHORT).show();
                                oldPass.setText("");
                                oldPass.requestFocus();
                            } else if (errorBody.contains("Mật khẩu mới phải có ít nhất 6 ký tự")) {
                                Toast.makeText(ChangePasswordActivity.this,
                                        "Mật khẩu mới phải có ít nhất 6 ký tự!",
                                        Toast.LENGTH_SHORT).show();
                                newPass3.setText("");
                                newPass4.setText("");
                                newPass3.requestFocus();
                            } else if (errorBody.contains("Mật khẩu mới không được để trống")) {
                                Toast.makeText(ChangePasswordActivity.this,
                                        "Mật khẩu mới không được để trống!",
                                        Toast.LENGTH_SHORT).show();
                                newPass3.setText("");
                                newPass4.setText("");
                                newPass3.requestFocus();
                            } else if (errorBody.contains("Mật khẩu mới và xác nhận mật khẩu không khớp")) {
                                Toast.makeText(ChangePasswordActivity.this,
                                        "Mật khẩu xác nhận không khớp với mật khẩu mới!",
                                        Toast.LENGTH_SHORT).show();
                                newPass4.setText("");
                                newPass4.requestFocus();
                            } else {
                                Toast.makeText(ChangePasswordActivity.this,
                                        "Lỗi: " + errorBody,
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else if (response.code() == 401) {
                            Toast.makeText(ChangePasswordActivity.this,
                                    "Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại!",
                                    Toast.LENGTH_SHORT).show();
                            SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.remove("token");
                            editor.apply();
                            Intent signinIntent = new Intent(ChangePasswordActivity.this, SigninActivity.class);
                            signinIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(signinIntent);
                            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        } else if (response.code() == 403) {
                            Toast.makeText(ChangePasswordActivity.this,
                                    "Bạn không có quyền thực hiện thao tác này!",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ChangePasswordActivity.this,
                                    "Lỗi từ server: " + errorBody,
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi parse phản hồi: " + e.getMessage());
                        Toast.makeText(ChangePasswordActivity.this,
                                "Lỗi khi xử lý phản hồi từ server. Vui lòng thử lại!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnChange.setEnabled(true);
                oldPass.setEnabled(true);
                newPass3.setEnabled(true);
                newPass4.setEnabled(true);

                Log.e(TAG, "Lỗi kết nối: " + t.getMessage());
                Toast.makeText(ChangePasswordActivity.this,
                        "Lỗi kết nối. Vui lòng kiểm tra mạng và thử lại!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}