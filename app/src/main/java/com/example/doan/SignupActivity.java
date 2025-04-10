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
import com.example.doan.models.ApiResponse;
import com.example.doan.models.UsersDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.io.IOException;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";
    private EditText edtName, edtPhone, edtEmail, edtPassword, edtConfirmPassword;
    private Button btnSignup;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        // Ánh xạ các view
        edtName = findViewById(R.id.signup_name);
        edtPhone = findViewById(R.id.signup_phone);
        edtEmail = findViewById(R.id.signup_email);
        edtPassword = findViewById(R.id.new_pass1);
        edtConfirmPassword = findViewById(R.id.new_pass2);
        btnSignup = findViewById(R.id.btn_signup);
        progressBar = findViewById(R.id.progressBar);

        // Ẩn ProgressBar ban đầu
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        // Xử lý sự kiện nhấn nút Đăng ký
        btnSignup.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            String confirmPassword = edtConfirmPassword.getText().toString().trim();

            // Kiểm tra ràng buộc
            if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Email không đúng định dạng", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!phone.matches("^0\\d{9}$")) {
                Toast.makeText(this, "Số điện thoại phải có 10 số và bắt đầu bằng 0", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 7) {
                Toast.makeText(this, "Mật khẩu phải có ít nhất 7 ký tự", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Mật khẩu và xác nhận mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            // Hiển thị ProgressBar
            if (progressBar != null) {
                progressBar.setVisibility(View.VISIBLE);
            }
            btnSignup.setEnabled(false);

            // Gửi yêu cầu đăng ký
            registerUser(name, phone, email, password, confirmPassword);
        });
    }

    private void registerUser(String name, String phone, String email, String password, String confirmPassword) {
        UsersDTO usersDTO = new UsersDTO(name, phone, email, password, confirmPassword);
        Log.d(TAG, "Gửi yêu cầu đăng ký với dữ liệu: " + usersDTO.toString());

        RetrofitClient.getApiService().registerUser(usersDTO).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                btnSignup.setEnabled(true);

                if (response.isSuccessful()) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse != null) {
                        Log.d(TAG, "Phản hồi: status=" + apiResponse.getStatus() + ", message=" + apiResponse.getMessage());
                        if (apiResponse.isSuccess()) {
                            // Thành công
                            Toast.makeText(SignupActivity.this, apiResponse.getMessage(), Toast.LENGTH_LONG).show();
                            // Chuyển về màn hình đăng nhập
                            Intent intent = new Intent(SignupActivity.this, SigninActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Thất bại (ví dụ: email hoặc số điện thoại đã tồn tại)
                            Toast.makeText(SignupActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "apiResponse là null");
                        Toast.makeText(SignupActivity.this, "Lỗi: Phản hồi từ server không hợp lệ", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Đăng ký thất bại, mã lỗi: " + response.code());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Không có thông tin lỗi";
                        Log.e(TAG, "Error body: " + errorBody);
                        if (response.code() == 400) {
                            // Lỗi validation hoặc email/số điện thoại đã tồn tại
                            if (errorBody.contains("Email đã tồn tại")) {
                                Toast.makeText(SignupActivity.this, "Email đã được sử dụng!", Toast.LENGTH_SHORT).show();
                            } else if (errorBody.contains("Số điện thoại đã được sử dụng")) {
                                Toast.makeText(SignupActivity.this, "Số điện thoại đã được sử dụng!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SignupActivity.this, "Lỗi: " + errorBody, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(SignupActivity.this, "Đăng ký thất bại. Mã lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Lỗi parse phản hồi: " + e.getMessage());
                        Toast.makeText(SignupActivity.this, "Đăng ký thất bại. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                btnSignup.setEnabled(true);
                Log.e(TAG, "Lỗi kết nối: " + t.getMessage());
                Toast.makeText(SignupActivity.this, "Lỗi kết nối. Vui lòng kiểm tra mạng và thử lại!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}