package com.example.doan;

import android.content.Intent;
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
import com.example.doan.models.ResetPasswordRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.io.IOException;

public class ResetPasswordActivity extends AppCompatActivity {

    private static final String TAG = "ResetPasswordActivity";
    private EditText edtOtp, edtNewPassword, edtConfirmPassword;
    private Button btnConfirm;
    private ProgressBar progressBar;
    private ImageView iconNewPasswordEye, iconConfirmPasswordEye;
    private boolean isNewPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;
    private String email; // Lưu email từ ForgetPasswordActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password);

        // Ánh xạ các thành phần từ layout
        edtOtp = findViewById(R.id.otp);
        edtNewPassword = findViewById(R.id.new_password);
        edtConfirmPassword = findViewById(R.id.confirm_new_password);
        btnConfirm = findViewById(R.id.btn_confirm);
        progressBar = findViewById(R.id.progressBar);
        iconNewPasswordEye = findViewById(R.id.icon_new_password_eye);
        iconConfirmPasswordEye = findViewById(R.id.icon_confirm_new_password_eye);

        // Nhận email từ ForgetPasswordActivity
        email = getIntent().getStringExtra("email");
        if (email == null) {
            Toast.makeText(this, "Không tìm thấy email. Vui lòng thử lại!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        // Xử lý ẩn/hiện mật khẩu cho new_password
        iconNewPasswordEye.setOnClickListener(v -> {
            if (isNewPasswordVisible) {
                // Ẩn mật khẩu
                edtNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                iconNewPasswordEye.setImageResource(R.drawable.hide_password);
            } else {
                // Hiện mật khẩu
                edtNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                iconNewPasswordEye.setImageResource(R.drawable.show_password); // Cần có drawable show_password
            }
            isNewPasswordVisible = !isNewPasswordVisible;
            // Di chuyển con trỏ về cuối
            edtNewPassword.setSelection(edtNewPassword.getText().length());
        });

        // Xử lý ẩn/hiện mật khẩu cho confirm_new_password
        iconConfirmPasswordEye.setOnClickListener(v -> {
            if (isConfirmPasswordVisible) {
                // Ẩn mật khẩu
                edtConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                iconConfirmPasswordEye.setImageResource(R.drawable.hide_password);
            } else {
                // Hiện mật khẩu
                edtConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                iconConfirmPasswordEye.setImageResource(R.drawable.show_password);
            }
            isConfirmPasswordVisible = !isConfirmPasswordVisible;
            // Di chuyển con trỏ về cuối
            edtConfirmPassword.setSelection(edtConfirmPassword.getText().length());
        });

        btnConfirm.setOnClickListener(v -> {
            String resetCode = edtOtp.getText().toString().trim();
            String newPassword = edtNewPassword.getText().toString().trim();
            String confirmPassword = edtConfirmPassword.getText().toString().trim();

            Log.d(TAG, "Email: " + email + ", ResetCode: " + resetCode);

            // Validate input
            if (resetCode.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (resetCode.length() != 6) {
                Toast.makeText(this, "Mã OTP phải có 6 chữ số", Toast.LENGTH_SHORT).show();
                return;
            }

            if (newPassword.length() < 8) {
                Toast.makeText(this, "Mật khẩu mới phải có ít nhất 8 ký tự", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "Mật khẩu mới và xác nhận mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            if (progressBar != null) {
                progressBar.setVisibility(View.VISIBLE);
            }
            btnConfirm.setEnabled(false);

            resetPassword(email, resetCode, newPassword, confirmPassword);
        });
    }

    private void resetPassword(String email, String resetCode, String newPassword, String confirmPassword) {
        ResetPasswordRequest request = new ResetPasswordRequest(email, resetCode, newPassword, confirmPassword);
        Log.d(TAG, "Gửi yêu cầu đặt lại mật khẩu cho email: " + email);

        Call<String> call = RetrofitClient.getApiServiceForText().resetPassword(request);
        Log.d(TAG, "URL yêu cầu: " + call.request().url());

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                btnConfirm.setEnabled(true);

                if (response.isSuccessful()) {
                    String responseBody = response.body();
                    Log.d(TAG, "Phản hồi thành công: " + responseBody);
                    if ("Mật khẩu đã được đặt lại thành công".equals(responseBody)) {
                        Toast.makeText(ResetPasswordActivity.this, "Mật khẩu đã được đặt lại thành công!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ResetPasswordActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.e(TAG, "Phản hồi không mong đợi: " + responseBody);
                        Toast.makeText(ResetPasswordActivity.this, "Lỗi: Phản hồi từ server không hợp lệ", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Gửi yêu cầu thất bại, mã lỗi: " + response.code());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Không có thông tin lỗi";
                        Log.e(TAG, "Error body: " + errorBody);
                        if ("Email không đúng định dạng".equals(errorBody)) {
                            Toast.makeText(ResetPasswordActivity.this, "Email không đúng định dạng", Toast.LENGTH_SHORT).show();
                        } else if ("Email hoặc mã OTP không hợp lệ".equals(errorBody)) {
                            Toast.makeText(ResetPasswordActivity.this, "Email hoặc mã OTP không hợp lệ", Toast.LENGTH_SHORT).show();
                        } else if ("Mã OTP đã hết hạn".equals(errorBody)) {
                            Toast.makeText(ResetPasswordActivity.this, "Mã OTP đã hết hạn", Toast.LENGTH_SHORT).show();
                        } else if ("Mật khẩu mới phải có ít nhất 8 ký tự".equals(errorBody)) {
                            Toast.makeText(ResetPasswordActivity.this, "Mật khẩu mới phải có ít nhất 8 ký tự", Toast.LENGTH_SHORT).show();
                        } else if ("Mật khẩu mới và xác nhận mật khẩu không khớp".equals(errorBody)) {
                            Toast.makeText(ResetPasswordActivity.this, "Mật khẩu mới và xác nhận mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ResetPasswordActivity.this, "Lỗi: " + errorBody, Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Lỗi parse phản hồi: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                btnConfirm.setEnabled(true);
                Log.e(TAG, "Lỗi kết nối: " + t.getMessage(), t);
                Toast.makeText(ResetPasswordActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}