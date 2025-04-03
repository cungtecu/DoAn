//package com.example.doan;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.method.HideReturnsTransformationMethod;
//import android.text.method.PasswordTransformationMethod;
//import android.util.Log;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//import androidx.appcompat.app.AppCompatActivity;
//import com.example.doan.api.RetrofitClient;
//import com.example.doan.models.ApiResponse;
//import com.example.doan.models.LoginRequest;
//import com.google.gson.Gson;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import java.io.IOException;
//
//public class SigninActivity extends AppCompatActivity {
//
//    private static final String TAG = "SigninActivity";
//    private EditText edtPhone, edtPassword;
//    private ImageView btnTogglePassword;
//    private TextView txtForgetPassword, txtSignup, btnSignin;
//    private boolean isPasswordVisible = false;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.signin);
//
//        // Ánh xạ các view
//        edtPhone = findViewById(R.id.signin_phone);
//        edtPassword = findViewById(R.id.signin_password);
//        btnTogglePassword = findViewById(R.id.eye_icon);
//        txtForgetPassword = findViewById(R.id.txt_forgetpassword);
//        txtSignup = findViewById(R.id.txt_signup);
//        btnSignin = findViewById(R.id.btn_signin);
//
//        // Xử lý hiển thị/ẩn mật khẩu khi nhấn vào icon
//        btnTogglePassword.setOnClickListener(v -> {
//            if (isPasswordVisible) {
//                // Ẩn mật khẩu
//                edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
//                btnTogglePassword.setImageResource(R.drawable.hide_password);
//            } else {
//                // Hiển thị mật khẩu
//                edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//                btnTogglePassword.setImageResource(R.drawable.show_password);
//            }
//            isPasswordVisible = !isPasswordVisible;
//            edtPassword.setSelection(edtPassword.getText().length()); // Đặt con trỏ ở cuối
//        });
//
//        // Sự kiện khi nhấn vào TextView quên mật khẩu
//        txtForgetPassword.setOnClickListener(v -> {
//            Intent intent = new Intent(SigninActivity.this, ForgetPasswordActivity.class);
//            startActivity(intent);
//        });
//
//        // Xử lý sự kiện nhấn vào "Tạo Một Tài Khoản"
//        txtSignup.setOnClickListener(v -> {
//            Intent intent = new Intent(SigninActivity.this, SignupActivity.class);
//            startActivity(intent);
//        });
//
//        // Xử lý sự kiện nhấn vào nút Đăng nhập
//        btnSignin.setOnClickListener(v -> {
//            Log.d(TAG, "Nút Đăng Nhập được nhấn");
//
//            String phone = edtPhone.getText().toString().trim();
//            String password = edtPassword.getText().toString().trim();
//
//            Log.d(TAG, "phone: " + phone);
//            Log.d(TAG, "password: " + password);
//
//            // Kiểm tra ràng buộc
//            if (phone.isEmpty() || password.isEmpty()) {
//                Log.d(TAG, "Thông tin không đầy đủ");
//                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            // Gửi yêu cầu đăng nhập
//            loginUser(phone, password);
//        });
//    }
//
//    private void loginUser(String phone, String password) {
//        LoginRequest loginRequest = new LoginRequest(phone, password);
//        Log.d(TAG, "Gửi yêu cầu đăng nhập với dữ liệu: " + loginRequest.toString());
//
//        RetrofitClient.getApiService().loginUser(loginRequest).enqueue(new Callback<ApiResponse>() {
//            @Override
//            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
//                if (response.isSuccessful()) {
//                    ApiResponse apiResponse = response.body();
//                    Log.d(TAG, "Đăng nhập thành công: " + apiResponse.getMessage());
//                    Toast.makeText(SigninActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
//
//                    // Chuyển đến MainActivity
//                    Intent intent = new Intent(SigninActivity.this, MainActivity.class);
//                    startActivity(intent);
//                    finish(); // Đóng SigninActivity để không quay lại
//                } else {
//                    Log.e(TAG, "Đăng nhập thất bại, mã lỗi: " + response.code());
//                    try {
//                        String errorBody = response.errorBody().string();
//                        ApiResponse errorResponse = new Gson().fromJson(errorBody, ApiResponse.class);
//                        String errorMessage = errorResponse.getMessage();
//                        Log.e(TAG, "Thông báo lỗi từ server: " + errorMessage);
//                        Toast.makeText(SigninActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
//                    } catch (IOException e) {
//                        Log.e(TAG, "Lỗi parse phản hồi: " + e.getMessage());
//                        Toast.makeText(SigninActivity.this, "Đăng nhập thất bại. Mã lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ApiResponse> call, Throwable t) {
//                Log.e(TAG, "Lỗi kết nối khi đăng nhập: " + t.getMessage());
//                Toast.makeText(SigninActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//}
package com.example.doan;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.doan.api.RetrofitClient;
import com.example.doan.models.ApiResponse;
import com.example.doan.models.LoginRequest;
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

        // Xử lý hiển thị/ẩn mật khẩu khi nhấn vào icon
        btnTogglePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                // Ẩn mật khẩu
                edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                btnTogglePassword.setImageResource(R.drawable.hide_password);
            } else {
                // Hiển thị mật khẩu
                edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                btnTogglePassword.setImageResource(R.drawable.show_password);
            }
            isPasswordVisible = !isPasswordVisible;
            edtPassword.setSelection(edtPassword.getText().length());
        });

        // Sự kiện khi nhấn vào TextView quên mật khẩu
        txtForgetPassword.setOnClickListener(v -> {
            Intent intent = new Intent(SigninActivity.this, ForgetPasswordActivity.class);
            startActivity(intent);
        });

        // Xử lý sự kiện nhấn vào "Tạo Một Tài Khoản"
        txtSignup.setOnClickListener(v -> {
            Intent intent = new Intent(SigninActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        // Xử lý sự kiện nhấn vào nút Đăng nhập
        btnSignin.setOnClickListener(v -> {
            Log.d(TAG, "Nút Đăng Nhập được nhấn");

            String phone = edtPhone.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            Log.d(TAG, "phone: " + phone);
            Log.d(TAG, "password: " + password);

            // Kiểm tra ràng buộc
            if (phone.isEmpty() || password.isEmpty()) {
                Log.d(TAG, "Thông tin không đầy đủ");
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gửi yêu cầu đăng nhập
            loginUser(phone, password);
        });
    }

    private void loginUser(String phone, String password) {
        LoginRequest loginRequest = new LoginRequest(phone, password);
        Log.d(TAG, "Gửi yêu cầu đăng nhập với dữ liệu: " + loginRequest.toString());

        RetrofitClient.getApiService().loginUser(loginRequest).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    Log.d(TAG, "Đăng nhập thành công: " + apiResponse.getMessage());
                    Toast.makeText(SigninActivity.this, "Đăng nhập thành công, hãy đợi một chút nhé!", Toast.LENGTH_SHORT).show();

                    // Chuyển đến MainActivity
                    Intent intent = new Intent(SigninActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // Đóng SigninActivity
                } else {
                    Log.e(TAG, "Đăng nhập thất bại, mã lỗi: " + response.code());
                    String errorMessage = "Đăng nhập thất bại. Mã lỗi: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            ApiResponse errorResponse = new Gson().fromJson(errorBody, ApiResponse.class);
                            if (errorResponse != null && errorResponse.getMessage() != null) {
                                errorMessage = errorResponse.getMessage();
                            }
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Lỗi parse phản hồi: " + e.getMessage());
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi không xác định: " + e.getMessage());
                    }
                    Toast.makeText(SigninActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối khi đăng nhập: " + t.getMessage());
                Toast.makeText(SigninActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}