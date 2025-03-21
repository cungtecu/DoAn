package com.example.doan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.doan.MainActivity;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class NewPasswordActivity extends AppCompatActivity {
    private EditText edtEmail, edtPassword;
    private Button btnLoginNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);

        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        btnLoginNew = findViewById(R.id.btn_login_new);

        btnLoginNew.setOnClickListener(v -> loginWithNewPassword());
    }

    private void loginWithNewPassword() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Sai email hoặc mật khẩu", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
