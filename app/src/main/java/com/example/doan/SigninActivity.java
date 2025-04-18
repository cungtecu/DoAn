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
import com.example.doan.api.RetrofitClient;
import com.example.doan.models.LoginRequest;
import com.example.doan.models.LoginResponse;
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
                progressBar.setVisibility(View.GONE);
                btnSignin.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    Log.d(TAG, "Đăng nhập thành công: token=" + loginResponse.getToken());
                    Toast.makeText(SigninActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                    // Lưu token vào SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("token", loginResponse.getToken());
                    editor.apply();

                    // Chuyển sang OrderActivity
                    Intent intent = new Intent(SigninActivity.this, OrderActivity.class);
                    intent.putExtra("token", loginResponse.getToken());
                    intent.putExtra("phone", loginResponse.getPhone());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Xóa stack activity
                    startActivity(intent);
                    finish();
                } else {
                    handleLoginError(response);
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