//package com.example.doan;//package com.example.doan;
//import android.os.Bundle;
//import android.text.method.HideReturnsTransformationMethod;
//import android.text.method.PasswordTransformationMethod;
//import android.widget.EditText;
//import android.widget.ImageView;
//import androidx.appcompat.app.AppCompatActivity;
//
//public class SigninActivity extends AppCompatActivity {
//    private boolean isPasswordVisible = false;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.signin);
//
//        EditText edtPassword = findViewById(R.id.signin_password);
//        ImageView btnTogglePassword = findViewById(R.id.eye_icon);
//
//        // Xử lý hiển thị / ẩn mật khẩu
//        btnTogglePassword.setOnClickListener(v -> {
//            if (isPasswordVisible) {
//                // Ẩn mậ9t khẩu
//                edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
//                btnTogglePassword.setImageResource(R.drawable.hide_password);
//            } else {
//                // Hiện mật khẩu
//                edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//                btnTogglePassword.setImageResource(R.drawable.show_password);
//            }
//            isPasswordVisible = !isPasswordVisible;
//        });
//    }
//}
