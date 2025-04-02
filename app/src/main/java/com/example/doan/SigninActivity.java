package com.example.doan;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SigninActivity extends AppCompatActivity {

    private boolean isPasswordVisible = false;  // Biến kiểm tra trạng thái mật khẩu

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);  // Đảm bảo có layout activity_signin.xml

        // Ánh xạ các view
        EditText edtPhone = findViewById(R.id.signin_phone);  // Ánh xạ EditText số điện thoại
        EditText edtPassword = findViewById(R.id.signin_password);  // Ánh xạ EditText mật khẩu
        ImageView btnTogglePassword = findViewById(R.id.eye_icon);
        TextView txtForgetPassword = findViewById(R.id.txt_forgetpassword);  // Ánh xạ TextView quên mật khẩu
        TextView txtSignup = findViewById(R.id.txt_signup);  // Ánh xạ TextView tạo tài khoản
        TextView btnSignin = findViewById(R.id.btn_signin); // Nút đăng nhập

        // Xử lý hiển thị/ẩn mật khẩu khi nhấn vào icon
        btnTogglePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                // Ẩn mật khẩu
                edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                btnTogglePassword.setImageResource(R.drawable.hide_password);  // Icon ẩn mật khẩu
            } else {
                // Hiển thị mật khẩu
                edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                btnTogglePassword.setImageResource(R.drawable.show_password);  // Icon hiện mật khẩu
            }
            isPasswordVisible = !isPasswordVisible;  // Đảo ngược trạng thái
        });

        // Sự kiện khi nhấn vào TextView quên mật khẩu
        txtForgetPassword.setOnClickListener(v -> {
            Intent intent = new Intent(SigninActivity.this, ForgetPasswordActivity.class);
            startActivity(intent);  // Chuyển đến ForgetPasswordActivity
        });

        // Xử lý sự kiện nhấn vào "Tạo Một Tài Khoản"
        txtSignup.setOnClickListener(v -> {
            // Chuyển sang màn hình đăng ký
            Intent intent = new Intent(SigninActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        // Xử lý sự kiện nhấn vào nút Đăng nhập
        btnSignin.setOnClickListener(v -> {
            String phone = edtPhone.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (phone.isEmpty() || password.isEmpty()) {
                // Hiển thị thông báo lỗi nếu số điện thoại hoặc mật khẩu trống
                return;
            }

            // Gửi yêu cầu đăng nhập (có thể sử dụng API hoặc gọi trực tiếp vào backend)
            // Thực hiện đăng nhập tại đây (Ví dụ: sử dụng Retrofit để gửi yêu cầu)
            // Sau khi đăng nhập thành công:
            Intent intent = new Intent(SigninActivity.this, MainActivity.class);  // Ví dụ chuyển đến màn hình chính
            startActivity(intent);
            finish();  // Đóng màn hình đăng nhập
        });
    }
}
