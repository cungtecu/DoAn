package com.example.doan;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.doan.api.RetrofitClient;
import com.example.doan.models.SendResetLinkRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgetPasswordActivity extends AppCompatActivity {

    private static final String TAG = "ForgetPasswordActivity";
    private EditText emailInput;
    private Button btnContinue;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_password);

        // Ánh xạ view
        emailInput = findViewById(R.id.forget_email);
        btnContinue = findViewById(R.id.btn_continue);
        progressBar = findViewById(R.id.progressBar);

        // Xử lý nút "TIẾP TỤC"
        btnContinue.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            Log.d(TAG, "Email nhập vào: " + email);

            if (TextUtils.isEmpty(email)) {
                emailInput.setError("Vui lòng nhập email!");
                emailInput.requestFocus();
                return;
            }

            if (!isValidEmail(email)) {
                emailInput.setError("Email không hợp lệ! Ví dụ: example@domain.com");
                emailInput.requestFocus();
                Log.d(TAG, "Email không hợp lệ: " + email);
                return;
            }

            sendOtp(email);
        });
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) return false;
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void sendOtp(String email) {
        progressBar.setVisibility(View.VISIBLE);
        btnContinue.setEnabled(false);
        emailInput.setEnabled(false);

        SendResetLinkRequest request = new SendResetLinkRequest(email);
        Log.d(TAG, "Gửi yêu cầu tới API: /api/auth/forgot-password với dữ liệu: " + request.toString());
        Call<String> call = RetrofitClient.getApiServiceForText(this).sendOtp(request);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                progressBar.setVisibility(View.GONE);
                btnContinue.setEnabled(true);
                emailInput.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body();
                    Log.d(TAG, "Phản hồi từ server: " + responseBody);

                    if ("Mã OTP đã được gửi đến email của bạn".equals(responseBody)) {
                        Toast.makeText(ForgetPasswordActivity.this,
                                "Mã OTP đã được gửi đến email của bạn!",
                                Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ForgetPasswordActivity.this, ResetPasswordActivity.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(ForgetPasswordActivity.this,
                                responseBody != null ? responseBody : "Phản hồi không hợp lệ từ server",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Không có thông tin lỗi";
                        Log.e(TAG, "Lỗi từ server: " + errorBody);
                        if (response.code() == 401) {
                            Toast.makeText(ForgetPasswordActivity.this,
                                    "API yêu cầu token xác thực. Kiểm tra server hoặc base URL!",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ForgetPasswordActivity.this,
                                    "Lỗi: " + errorBody,
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi parse phản hồi: " + e.getMessage());
                        Toast.makeText(ForgetPasswordActivity.this,
                                "Lỗi khi xử lý phản hồi: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnContinue.setEnabled(true);
                emailInput.setEnabled(true);
                Log.e(TAG, "Lỗi kết nối: " + t.getMessage());
                Toast.makeText(ForgetPasswordActivity.this,
                        "Lỗi kết nối: " + t.getMessage(),
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