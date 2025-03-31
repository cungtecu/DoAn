//package com.example.doan;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.firebase.auth.FirebaseAuth;
//
//public class ForgetPasswordActivity extends AppCompatActivity {
//
//    private EditText edtEmail;
//    private Button btnNext;
//    private FirebaseAuth firebaseAuth;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.forget_password);
//
//        edtEmail = findViewById(R.id.forget_email);
//        btnNext = findViewById(R.id.btn_continue);
//        firebaseAuth = FirebaseAuth.getInstance();
//
//        btnNext.setOnClickListener(v -> {
//            String email = edtEmail.getText().toString().trim();
//
//            if (TextUtils.isEmpty(email)) {
//                Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            // Kiểm tra email có tồn tại không
//            firebaseAuth.fetchSignInMethodsForEmail(email)
//                    .addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            boolean emailExists = !task.getResult().getSignInMethods().isEmpty();
//                            if (emailExists) {
//                                // Gửi email reset mật khẩu
//                                firebaseAuth.sendPasswordResetEmail(email)
//                                        .addOnCompleteListener(resetTask -> {
//                                            if (resetTask.isSuccessful()) {
//                                                Toast.makeText(this, "Đã gửi email đặt lại mật khẩu!", Toast.LENGTH_LONG).show();
//                                            } else {
//                                                Toast.makeText(this, "Không thể gửi email. Thử lại!", Toast.LENGTH_SHORT).show();
//                                            }
//                                        });
//                            } else {
//                                // Email không tồn tại
//                                Intent intent = new Intent(this, EmailNotFoundActivity.class);
//                                startActivity(intent);
//                                finish();
//                            }
//                        } else {
//                            Toast.makeText(this, "Lỗi xảy ra. Thử lại sau!", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//        });
//    }
//}
