package com.example.doan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doan.admin.Admin_Main_Activity;
import com.example.doan.api.RetrofitClient;
import com.example.doan.models.LoginResponse;
import com.example.doan.models.LoginRequest;
import com.example.doan.models.Users;
import com.google.gson.Gson;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.io.IOException;

public class SigninActivity extends AppCompatActivity {

    private static final String TAG = "SigninActivity";
    private EditText edtPhone, edtPassword;
    private ImageView btnTogglePassword;
    private TextView txtForgetPassword, txtSignup, btnSignin;
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

        // Xử lý hiển thị/ẩn mật khẩu
        btnTogglePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                btnTogglePassword.setImageResource(R.drawable.hide_password);
            } else {
                edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                btnTogglePassword.setImageResource(R.drawable.show_password);
            }
            isPasswordVisible = !isPasswordVisible;
            edtPassword.setSelection(edtPassword.getText().length());
        });

        // Sự kiện quên mật khẩu
        txtForgetPassword.setOnClickListener(v -> {
            Intent intent = new Intent(SigninActivity.this, ForgetPasswordActivity.class);
            startActivity(intent);
        });

        // Sự kiện đăng ký
        txtSignup.setOnClickListener(v -> {
            Intent intent = new Intent(SigninActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        // Sự kiện đăng nhập
        btnSignin.setOnClickListener(v -> {
            Log.d(TAG, "Nút Đăng Nhập được nhấn");
            String phone = edtPhone.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            Log.d(TAG, "phone: " + phone);
            Log.d(TAG, "password: " + password);

            if (phone.isEmpty() || password.isEmpty()) {
                Log.d(TAG, "Thông tin không đầy đủ");
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            loginUser(phone, password);
        });
    }

    private void loginUser(String phone, String password) {
        LoginRequest loginRequest = new LoginRequest(phone, password);
        RetrofitClient.getApiService().loginUser(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getToken();
                    if (token != null) {
                        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("token", token);
                        editor.apply();
                        fetchCurrentUser(token);
                    } else {
                        Toast.makeText(SigninActivity.this, "Token không hợp lệ", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMessage = "Đăng nhập thất bại. Mã lỗi: " + response.code();
                    Toast.makeText(SigninActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(SigninActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchCurrentUser(String token) {
        RetrofitClient.getApiService().getCurrentUser("Bearer " + token).enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Users user = response.body();
                    Intent intent;
                    if ("ADMIN".equals(user.getRole())) {
                        intent = new Intent(SigninActivity.this, Admin_Main_Activity.class);
                    } else {
                        intent = new Intent(SigninActivity.this, MainActivity.class);
                    }
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SigninActivity.this, "Không thể lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                Toast.makeText(SigninActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}