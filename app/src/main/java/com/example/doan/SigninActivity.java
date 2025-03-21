package com.example.doan;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class SigninActivity extends AppCompatActivity {

    private EditText edtPhone, edtPassword;
    private ImageView eyeIcon;
    private boolean isPasswordVisible = false;

    private ArrayList<HashMap<String, String>> fakeUsers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);

        edtPhone = findViewById(R.id.signin_phone);
        edtPassword = findViewById(R.id.signin_password);
        eyeIcon = findViewById(R.id.eye_icon);
        TextView txtForgetPassword = findViewById(R.id.txt_forgetpassword);
        Button btnSignIn = findViewById(R.id.btn_signin);
        TextView txtCreateAccount = findViewById(R.id.txt_signup);  // Thêm đúng vị trí

        initFakeUsers();

        // 1. Xử lý ẩn/hiện mật khẩu
        eyeIcon.setOnClickListener(v -> {
            if (isPasswordVisible) {
                edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                eyeIcon.setImageResource(R.drawable.hide_password);
            } else {
                edtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                eyeIcon.setImageResource(R.drawable.show_password);
            }
            edtPassword.setSelection(edtPassword.getText().length());
            isPasswordVisible = !isPasswordVisible;
        });

        // 2. Nhảy qua giao diện quên mật khẩu
        txtForgetPassword.setOnClickListener(v -> {
            Intent intent = new Intent(SigninActivity.this, ForgetPasswordActivity.class);
            startActivity(intent);
        });

        // 3. Nút đăng nhập
        btnSignIn.setOnClickListener(v -> {
            String inputPhone = edtPhone.getText().toString().trim();
            String inputPassword = edtPassword.getText().toString().trim();

            // ========== RÀNG BUỘC DỮ LIỆU ==========
            if (!validatePhone(inputPhone) || !validatePassword(inputPassword)) {
                return; // Không hợp lệ thì không cho đăng nhập
            }

            // ======= KIỂM TRA ĐĂNG NHẬP (fake DB) =======
            boolean isMatch = false;
            for (HashMap<String, String> user : fakeUsers) {
                if (user.get("phone").equals(inputPhone) && user.get("password").equals(inputPassword)) {
                    isMatch = true;
                    break;
                }
            }

            if (isMatch) {
                Toast.makeText(SigninActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                // startActivity(new Intent(SigninActivity.this, MainActivity.class)); // Chuyển sang Main nếu có
            } else {
                Toast.makeText(SigninActivity.this, "Sai số điện thoại hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
            }
        });

        // 4. Nhảy qua giao diện đăng ký (TÁCH RA ĐÚNG CHỖ)
        txtCreateAccount.setOnClickListener(v -> {
            Intent intent = new Intent(SigninActivity.this, SignupActivity.class);
            startActivity(intent);
        });
    }

    // Hàm kiểm tra số điện thoại
    private boolean validatePhone(String phone) {
        if (phone.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số điện thoại!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!phone.matches("^0[0-9]{9}$")) {
            Toast.makeText(this, "Số điện thoại không hợp lệ! Vui lòng nhập đúng 10 số.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // Hàm kiểm tra mật khẩu
    private boolean validatePassword(String password) {
        if (password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập mật khẩu!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // Tạo database giả để test
    private void initFakeUsers() {
        HashMap<String, String> user1 = new HashMap<>();
        user1.put("phone", "0912345678");
        user1.put("password", "password123");

        HashMap<String, String> user2 = new HashMap<>();
        user2.put("phone", "0987654321");
        user2.put("password", "123456");

        HashMap<String, String> user3 = new HashMap<>();
        user3.put("phone", "0909090909");
        user3.put("password", "coffee2024");

        fakeUsers.add(user1);
        fakeUsers.add(user2);
        fakeUsers.add(user3);
    }
}
