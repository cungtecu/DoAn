package com.example.doan;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;

public class RegisterActivity extends AppCompatActivity {

    private EditText signupName, signupEmail, signupPhone, signupPassword;
    private Button btnSignup;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Khởi tạo các view
        signupName = findViewById(R.id.signup_name);
        signupEmail = findViewById(R.id.signup_email);
        signupPhone = findViewById(R.id.signup_phone);
        signupPassword = findViewById(R.id.signup_password);
        btnSignup = findViewById(R.id.btn_signup);

        dbHelper = new DatabaseHelper(this);

        // Xử lý sự kiện khi nhấn nút Đăng Ký
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = signupName.getText().toString().trim();
                String email = signupEmail.getText().toString().trim();
                String phone = signupPhone.getText().toString().trim();
                String password = signupPassword.getText().toString().trim();

                // Kiểm tra dữ liệu nhập vào
                if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Thêm người dùng vào cơ sở dữ liệu
//                long userId = dbHelper.addUser(name, email, phone, password);

//                if (userId > 0) {
//                    // Thông báo đăng ký thành công
//                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
//                    // Có thể chuyển hướng sang màn hình đăng nhập hoặc trang chính
//                } else {
//                    // Thông báo lỗi khi đăng ký
//                    Toast.makeText(RegisterActivity.this, "Đã có lỗi xảy ra. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
//                }
            }
        });
    }
}
