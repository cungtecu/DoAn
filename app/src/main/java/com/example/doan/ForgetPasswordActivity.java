package com.example.doan;

import android.content.Intent;
import android.os.Bundle;
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
import java.io.IOException;

public class ForgetPasswordActivity extends AppCompatActivity {

    private static final String TAG = "ForgetPasswordActivity";
    private EditText edtEmail;
    private Button btnContinue;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_password);

        edtEmail = findViewById(R.id.forget_email);
        btnContinue = findViewById(R.id.btn_continue);
        progressBar = findViewById(R.id.progressBar);

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        btnContinue.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            Log.d(TAG, "Email nhập vào: " + email);

            if (email.isEmpty()) {
                Log.d(TAG, "Email rỗng");
                Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Log.d(TAG, "Email không đúng định dạng");
                Toast.makeText(this, "Email không đúng định dạng", Toast.LENGTH_SHORT).show();
                return;
            }

            if (progressBar != null) {
                progressBar.setVisibility(View.VISIBLE);
            }
            btnContinue.setEnabled(false);

            sendResetLink(email);
        });
    }

    private void sendResetLink(String email) {
        SendResetLinkRequest request = new SendResetLinkRequest(email);
        Log.d(TAG, "Gửi yêu cầu gửi OTP cho email: " + email);

        Call<String> call = RetrofitClient.getApiServiceForText().sendResetLink(request);
        Log.d(TAG, "URL yêu cầu: " + call.request().url());

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                btnContinue.setEnabled(true);

                if (response.isSuccessful()) {
                    String responseBody = response.body();
                    Log.d(TAG, "Phản hồi thành công: " + responseBody);
                    if ("Mã OTP đã được gửi đến email của bạn".equals(responseBody)) {
                        Toast.makeText(ForgetPasswordActivity.this, "Mã OTP đã được gửi đến email của bạn. Vui lòng kiểm tra!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ForgetPasswordActivity.this, ResetPasswordActivity.class);
                        intent.putExtra("email", email); // Truyền email sang ResetPasswordActivity
                        startActivity(intent);
                        finish();
                    } else {
                        Log.e(TAG, "Phản hồi không mong đợi: " + responseBody);
                        Toast.makeText(ForgetPasswordActivity.this, "Lỗi: Phản hồi từ server không hợp lệ", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Gửi yêu cầu thất bại, mã lỗi: " + response.code());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Không có thông tin lỗi";
                        Log.e(TAG, "Error body: " + errorBody);
                        if ("Email không tồn tại trong hệ thống".equals(errorBody)) {
                            Toast.makeText(ForgetPasswordActivity.this, "Vui lòng nhập đúng email", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ForgetPasswordActivity.this, "Lỗi: " + errorBody, Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Lỗi parse phản hồi: " + e.getMessage());
                        Toast.makeText(ForgetPasswordActivity.this, "Gửi yêu cầu thất bại. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                btnContinue.setEnabled(true);
                Log.e(TAG, "Lỗi kết nối: " + t.getMessage(), t);
                Toast.makeText(ForgetPasswordActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}