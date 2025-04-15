package com.example.doan;

import android.content.Intent;
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
import com.example.doan.models.ResetPasswordRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    private static final String TAG = "ResetPasswordActivity";
    private EditText edtOtp, edtNewPassword, edtConfirmPassword;
    private Button btnConfirm;
    private ProgressBar progressBar;
    private ImageView iconNewPasswordEye, iconConfirmPasswordEye;
    private boolean isNewPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password);

        // Ánh xạ view
        edtOtp = findViewById(R.id.otp);
        edtNewPassword = findViewById(R.id.new_password);
        edtConfirmPassword = findViewById(R.id.confirm_new_password);
        btnConfirm = findViewById(R.id.btn_confirm);
        progressBar = findViewById(R.id.progressBar);
        iconNewPasswordEye = findViewById(R.id.icon_new_password_eye);
        iconConfirmPasswordEye = findViewById(R.id.icon_confirm_new_password_eye);

        // Lấy email từ Intent
        email = getIntent().getStringExtra("email");
        if (email == null) {
            Toast.makeText(this, "Không tìm thấy email. Vui lòng thử lại!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        Log.d(TAG, "Email nhận được: " + email);
        progressBar.setVisibility(View.GONE);

        // Xử lý ẩn/hiện mật khẩu cho new_password
        iconNewPasswordEye.setOnClickListener(v -> {
            isNewPasswordVisible = !isNewPasswordVisible;
            if (isNewPasswordVisible) {
                edtNewPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                iconNewPasswordEye.setImageResource(R.drawable.show_password);
            } else {
                edtNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                iconNewPasswordEye.setImageResource(R.drawable.hide_password);
            }
            edtNewPassword.setSelection(edtNewPassword.getText().length());
        });

        // Xử lý ẩn/hiện mật khẩu cho confirm_new_password
        iconConfirmPasswordEye.setOnClickListener(v -> {
            isConfirmPasswordVisible = !isConfirmPasswordVisible;
            if (isConfirmPasswordVisible) {
                edtConfirmPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                iconConfirmPasswordEye.setImageResource(R.drawable.show_password);
            } else {
                edtConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                iconConfirmPasswordEye.setImageResource(R.drawable.hide_password);
            }
            edtConfirmPassword.setSelection(edtConfirmPassword.getText().length());
        });

        // Xử lý nút "XÁC NHẬN"
        btnConfirm.setOnClickListener(v -> {
            String resetCode = edtOtp.getText().toString().trim();
            String newPassword = edtNewPassword.getText().toString().trim();
            String confirmPassword = edtConfirmPassword.getText().toString().trim();

            // Validate input
            if (resetCode.isEmpty()) {
                edtOtp.setError("Vui lòng nhập mã OTP!");
                edtOtp.requestFocus();
                return;
            }

            if (newPassword.isEmpty()) {
                edtNewPassword.setError("Vui lòng nhập mật khẩu mới!");
                edtNewPassword.requestFocus();
                return;
            }

            if (confirmPassword.isEmpty()) {
                edtConfirmPassword.setError("Vui lòng nhập xác nhận mật khẩu!");
                edtConfirmPassword.requestFocus();
                return;
            }

            if (resetCode.length() != 6) {
                edtOtp.setError("Mã OTP phải có 6 chữ số!");
                edtOtp.requestFocus();
                return;
            }

            if (newPassword.length() < 8) {
                edtNewPassword.setError("Mật khẩu mới phải có ít nhất 8 ký tự!");
                edtNewPassword.requestFocus();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                edtConfirmPassword.setError("Mật khẩu mới và xác nhận mật khẩu không khớp!");
                edtConfirmPassword.requestFocus();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            btnConfirm.setEnabled(false);
            edtOtp.setEnabled(false);
            edtNewPassword.setEnabled(false);
            edtConfirmPassword.setEnabled(false);

            Log.d(TAG, "Gửi yêu cầu đặt lại mật khẩu với: email=" + email + ", OTP=" + resetCode + ", newPassword=" + newPassword);
            resetPassword(email, resetCode, newPassword, confirmPassword);
        });
    }

    private void resetPassword(String email, String resetCode, String newPassword, String confirmPassword) {
        ResetPasswordRequest request = new ResetPasswordRequest(email, resetCode, newPassword, confirmPassword);
        Call<String> call = RetrofitClient.getApiServiceForText(this).resetPassword(request);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                progressBar.setVisibility(View.GONE);
                btnConfirm.setEnabled(true);
                edtOtp.setEnabled(true);
                edtNewPassword.setEnabled(true);
                edtConfirmPassword.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body();
                    Log.d(TAG, "Phản hồi từ server: " + responseBody);

                    if ("Mật khẩu đã được đặt lại thành công".equals(responseBody)) {
                        Toast.makeText(ResetPasswordActivity.this,
                                "Mật khẩu đã được đặt lại thành công! Đang chuyển hướng đến trang đăng nhập...",
                                Toast.LENGTH_LONG).show();

                        // Tự động chuyển hướng sau 2 giây
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            Intent intent = new Intent(ResetPasswordActivity.this, SigninActivity.class);
                            startActivity(intent);
                            finish();
                        }, 2000); // Độ trễ 2000ms (2 giây)
                    } else {
                        // Nhập sai OTP hoặc các lỗi khác từ server
                        if (responseBody.contains("OTP không hợp lệ")) {
                            Toast.makeText(ResetPasswordActivity.this,
                                    "Mã OTP không đúng. Vui lòng kiểm tra lại!",
                                    Toast.LENGTH_LONG).show();
                            edtOtp.setText("");
                            edtOtp.requestFocus();
                        } else if (responseBody.contains("OTP đã hết hạn")) {
                            Toast.makeText(ResetPasswordActivity.this,
                                    "Mã OTP đã hết hạn. Vui lòng yêu cầu mã mới!",
                                    Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(ResetPasswordActivity.this, ForgetPasswordActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(ResetPasswordActivity.this,
                                    responseBody,
                                    Toast.LENGTH_LONG).show();
                            edtOtp.setText(""); // Xóa OTP nếu sai
                            edtOtp.requestFocus();
                        }
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Không có thông tin lỗi";
                        Log.e(TAG, "Lỗi từ server: " + errorBody);
                        if (response.code() == 401) {
                            Toast.makeText(ResetPasswordActivity.this,
                                    "API yêu cầu token xác thực. Kiểm tra server hoặc base URL!",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ResetPasswordActivity.this,
                                    "Lỗi từ server: " + errorBody,
                                    Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi khi xử lý phản hồi: " + e.getMessage());
                        Toast.makeText(ResetPasswordActivity.this,
                                "Lỗi khi xử lý phản hồi: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnConfirm.setEnabled(true);
                edtOtp.setEnabled(true);
                edtNewPassword.setEnabled(true);
                edtConfirmPassword.setEnabled(true);
                Log.e(TAG, "Lỗi kết nối: " + t.getMessage());
                Toast.makeText(ResetPasswordActivity.this,
                        "Lỗi kết nối: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}