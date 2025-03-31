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
        TextView txtCreateAccount = findViewById(R.id.txt_signup);  // Nút Tạo Một Tài Khoản

        if (txtCreateAccount == null || btnSignIn == null) {
            Toast.makeText(this, "Lỗi giao diện! Kiểm tra lại layout.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Khi nhấn vào "Tạo Một Tài Khoản", chuyển đến màn hình đăng ký
        txtCreateAccount.setOnClickListener(v -> {
            Toast.makeText(SigninActivity.this, "Đang chuyển sang đăng ký...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SigninActivity.this, SignupActivity.class));
        });

        eyeIcon.setOnClickListener(v -> {
            togglePasswordVisibility();
        });

        txtForgetPassword.setOnClickListener(v -> {
//            startActivity(new Intent(SigninActivity.this, ForgetPasswordActivity.class));
        });

        btnSignIn.setOnClickListener(v -> handleSignIn());
    }


    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            eyeIcon.setImageResource(R.drawable.hide_password);
        } else {
            edtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            eyeIcon.setImageResource(R.drawable.show_password);
        }
        edtPassword.setSelection(edtPassword.getText().length());
        isPasswordVisible = !isPasswordVisible;
    }

    private void handleSignIn() {
        String inputPhone = edtPhone.getText().toString().trim();
        String inputPassword = edtPassword.getText().toString().trim();

        if (!validatePhone(inputPhone) || !validatePassword(inputPassword)) {
            return;
        }

        for (HashMap<String, String> user : fakeUsers) {
            if (user.get("phone").equals(inputPhone) && user.get("password").equals(inputPassword)) {
                Toast.makeText(SigninActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SigninActivity.this, MainActivity.class));
                finish();
                return;
            }
        }
        Toast.makeText(SigninActivity.this, "Sai số điện thoại hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
    }

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

    private void initFakeUsers() {
        fakeUsers.add(createUser("0912345678", "password123"));
        fakeUsers.add(createUser("0987654321", "123456"));
        fakeUsers.add(createUser("0909090909", "coffee2024"));
    }

    private HashMap<String, String> createUser(String phone, String password) {
        HashMap<String, String> user = new HashMap<>();
        user.put("phone", phone);
        user.put("password", password);
        return user;
    }
}
