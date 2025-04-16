package com.example.doan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doan.admin.Admin_Main_Activity;
import com.example.doan.api.RetrofitClient;
import com.example.doan.models.LoginRequest;
import com.example.doan.models.LoginResponse;
import com.example.doan.models.User;
import com.example.doan.api.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.io.IOException;

public class SigninActivity extends AppCompatActivity {

    private static final String TAG = "SigninActivity";
    private EditText edtPhone, edtPassword;
    private ImageView btnTogglePassword;
    private TextView txtForgetPassword, txtSignup, btnSignin;
    private ProgressBar progressBar;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);

        // Ánh xạ các view
        edtPhone = findViewById(R.id.signin_phone);
        edtPassword = findViewById(R.id.signin_password);
        btnTogglePassword = findViewById(R.id.eye_icon);
        txtForgetPassword = findViewById(R.id.txt_forgetpassword);
        txtSignup = findViewById(R.id.txt_signup);
        btnSignin = findViewById(R.id.btn_signin);
        progressBar = findViewById(R.id.progressBar);

        // Xử lý hiển thị/ẩn mật khẩu
        btnTogglePassword.setOnClickListener(v -> togglePasswordVisibility());

        // Sự kiện quên mật khẩu
        txtForgetPassword.setOnClickListener(v -> {
            startActivity(new Intent(SigninActivity.this, ForgetPasswordActivity.class));
        });

        // Sự kiện tạo tài khoản
        txtSignup.setOnClickListener(v -> {
            startActivity(new Intent(SigninActivity.this, SignupActivity.class));
        });

        // Sự kiện đăng nhập
        btnSignin.setOnClickListener(v -> attemptLogin());
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            btnTogglePassword.setImageResource(R.drawable.hide_password);
        } else {
            edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            btnTogglePassword.setImageResource(R.drawable.show_password);
        }
        isPasswordVisible = !isPasswordVisible;
        edtPassword.setSelection(edtPassword.getText().length());
    }

    private void attemptLogin() {
        String phone = edtPhone.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Hiển thị ProgressBar và vô hiệu hóa nút đăng nhập
        progressBar.setVisibility(View.VISIBLE);
        btnSignin.setEnabled(false);

        LoginRequest loginRequest = new LoginRequest(phone, password);
        RetrofitClient.getApiService(this).loginUser(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    Log.d(TAG, "Đăng nhập thành công: token=" + loginResponse.getToken());

                    // Lưu token vào SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("token", loginResponse.getToken());
                    editor.putString("phone", loginResponse.getPhone());
                    editor.apply();

                    // Kiểm tra vai trò người dùng
                    checkUserRole(loginResponse.getToken());
                } else {
                    handleLoginError(response);
                    progressBar.setVisibility(View.GONE);
                    btnSignin.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnSignin.setEnabled(true);
                Log.e(TAG, "Lỗi kết nối: " + t.getMessage());
                Toast.makeText(SigninActivity.this, "Lỗi kết nối. Vui lòng kiểm tra mạng!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUserRole(String token) {
        String authToken = "Bearer " + token;
        ApiService apiService = RetrofitClient.getApiService(this);
        Call<User> call = apiService.getCurrentUser(authToken);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                progressBar.setVisibility(View.GONE);
                btnSignin.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    Intent intent;
                    if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                        intent = new Intent(SigninActivity.this, Admin_Main_Activity.class);
                    } else {
                        intent = new Intent(SigninActivity.this, MainActivity.class);
                    }
                    intent.putExtra("token", token);
                    intent.putExtra("phone", user.getPhone());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    Toast.makeText(SigninActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Log.e(TAG, "Lỗi tải thông tin người dùng: " + response.code() + " - " + errorBody);
                        Toast.makeText(SigninActivity.this, "Không thể xác minh vai trò: " + response.code(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Log.e(TAG, "Lỗi parse lỗi: " + e.getMessage());
                        Toast.makeText(SigninActivity.this, "Lỗi không xác định", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnSignin.setEnabled(true);
                Log.e(TAG, "Lỗi kết nối khi tải thông tin người dùng: " + t.getMessage());
                Toast.makeText(SigninActivity.this, "Lỗi kết nối khi xác minh vai trò", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleLoginError(Response<LoginResponse> response) {
        try {
            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Không có thông tin lỗi";
            Log.e(TAG, "Đăng nhập thất bại: " + response.code() + " - " + errorBody);
            if (response.code() == 401) {
                Toast.makeText(this, "Sai số điện thoại hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Đăng nhập thất bại: " + errorBody, Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Log.e(TAG, "Lỗi parse lỗi: " + e.getMessage());
            Toast.makeText(this, "Lỗi không xác định. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
        }
    }
}