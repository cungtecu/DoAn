package com.example.doan;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    private EditText edtName, edtPhone, edtEmail, edtPassword, edtConfirmPassword;
    private ImageView iconNewPass1, iconNewPass2;
    private Button btnSignup, btnContinueGuest;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        dbHelper = new DatabaseHelper(this);

        edtName = findViewById(R.id.signup_name);
        edtPhone = findViewById(R.id.signup_phone);
        edtEmail = findViewById(R.id.signup_email);
        edtPassword = findViewById(R.id.new_pass1);
        edtConfirmPassword = findViewById(R.id.new_pass2);
        iconNewPass1 = findViewById(R.id.icon_newpass1);
        iconNewPass2 = findViewById(R.id.icon_newpass2);
        btnSignup = findViewById(R.id.btn_signup);
        btnContinueGuest = findViewById(R.id.btn_continueguest);

        iconNewPass1.setOnClickListener(v -> togglePasswordVisibility(edtPassword, iconNewPass1));
        iconNewPass2.setOnClickListener(v -> togglePasswordVisibility(edtConfirmPassword, iconNewPass2));

        edtConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkPasswordMatch();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkAllFields();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        edtName.addTextChangedListener(textWatcher);
        edtPhone.addTextChangedListener(textWatcher);
        edtEmail.addTextChangedListener(textWatcher);
        edtPassword.addTextChangedListener(textWatcher);
        edtConfirmPassword.addTextChangedListener(textWatcher);

        btnSignup.setOnClickListener(v -> validateAndRegister());
        btnContinueGuest.setOnClickListener(v -> continueAsGuest());

        checkAllFields();
    }

    private void togglePasswordVisibility(EditText editText, ImageView icon) {
        if (editText.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            icon.setImageResource(R.drawable.show_password);
        } else {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            icon.setImageResource(R.drawable.hide_password);
        }
        editText.setSelection(editText.getText().length());
    }

    private void checkPasswordMatch() {
        if (!edtPassword.getText().toString().equals(edtConfirmPassword.getText().toString())) {
            edtConfirmPassword.setError("Mật khẩu không khớp");
        } else {
            edtConfirmPassword.setError(null);
        }
    }

    private void checkAllFields() {
        boolean isValid = !edtName.getText().toString().trim().isEmpty() &&
                !edtPhone.getText().toString().trim().isEmpty() &&
                !edtEmail.getText().toString().trim().isEmpty() &&
                !edtPassword.getText().toString().isEmpty() &&
                !edtConfirmPassword.getText().toString().isEmpty();
        btnSignup.setEnabled(isValid);
    }

    private void validateAndRegister() {
        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString();
        String confirmPassword = edtConfirmPassword.getText().toString();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Email không hợp lệ");
            return;
        }
        if (!phone.matches("\\d{10,11}")) {
            edtPhone.setError("Số điện thoại không hợp lệ");
            return;
        }
        if (password.length() < 6) {
            edtPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            return;
        }
        if (!password.equals(confirmPassword)) {
            edtConfirmPassword.setError("Mật khẩu không khớp");
            return;
        }

        if (dbHelper.isUserExists(phone)) {
            Toast.makeText(this, "Số điện thoại đã được đăng ký", Toast.LENGTH_SHORT).show();
        } else {
            long result = dbHelper.addUser(name, phone, email, password);
            if (result != -1) {
                Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SignupActivity.this, SigninActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void continueAsGuest() {
        startActivity(new Intent(SignupActivity.this, MainActivity.class));
        finish();
    }
}
