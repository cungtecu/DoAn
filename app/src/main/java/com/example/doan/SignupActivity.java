package com.example.doan;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.doan.api.RetrofitClient;
import com.example.doan.models.ApiResponse;
import com.example.doan.models.SignupRequest;
import com.google.gson.Gson;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.io.IOException;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";
    private EditText edtName, edtEmail, edtPhone, edtPass1, edtPass2;
    private Button btnSignup;
    private ImageView eyePass1, eyePass2;
    private TextView txtSignin;
    private boolean isPass1Visible = false, isPass2Visible = false;

    // Regex kiểm tra mật khẩu: ít nhất 7 ký tự
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^.{7,}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        edtName = findViewById(R.id.signup_name);
        edtEmail = findViewById(R.id.signup_email);
        edtPhone = findViewById(R.id.signup_phone);
        edtPass1 = findViewById(R.id.new_pass1);
        edtPass2 = findViewById(R.id.new_pass2);
        btnSignup = findViewById(R.id.btn_signup);
        eyePass1 = findViewById(R.id.icon_newpass1);
        eyePass2 = findViewById(R.id.icon_newpass2);
        txtSignin = findViewById(R.id.txt_signin);

        // Xử lý ẩn/hiện mật khẩu cho Pass1
        eyePass1.setOnClickListener(v -> {
            isPass1Visible = !isPass1Visible;
            if (isPass1Visible) {
                edtPass1.setTransformationMethod(null); // Hiện mật khẩu
                eyePass1.setImageResource(R.drawable.show_password); // Icon mắt mở
            } else {
                edtPass1.setTransformationMethod(PasswordTransformationMethod.getInstance()); // Ẩn mật khẩu
                eyePass1.setImageResource(R.drawable.hide_password); // Icon mắt đóng
            }
            edtPass1.setSelection(edtPass1.getText().length()); // Đặt con trỏ ở cuối
        });

        // Xử lý ẩn/hiện mật khẩu cho Pass2
        eyePass2.setOnClickListener(v -> {
            isPass2Visible = !isPass2Visible;
            if (isPass2Visible) {
                edtPass2.setTransformationMethod(null); // Hiện mật khẩu
                eyePass2.setImageResource(R.drawable.show_password); // Icon mắt mở
            } else {
                edtPass2.setTransformationMethod(PasswordTransformationMethod.getInstance()); // Ẩn mật khẩu
                eyePass2.setImageResource(R.drawable.hide_password); // Icon mắt đóng
            }
            edtPass2.setSelection(edtPass2.getText().length()); // Đặt con trỏ ở cuối
        });

        btnSignup.setOnClickListener(v -> {
            Log.d(TAG, "Nút Đăng Ký được nhấn");

            String name = edtName.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();
            String password = edtPass1.getText().toString().trim();
            String confirmPassword = edtPass2.getText().toString().trim();

            Log.d(TAG, "name: " + name);
            Log.d(TAG, "email: " + email);
            Log.d(TAG, "phone: " + phone);
            Log.d(TAG, "password: " + password);
            Log.d(TAG, "confirmPassword: " + confirmPassword);

            // Kiểm tra ràng buộc
            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Log.d(TAG, "Thông tin không đầy đủ");
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
                Log.d(TAG, "Mật khẩu không hợp lệ");
                Toast.makeText(this, "Mật khẩu phải có ít nhất 7 ký tự!", Toast.LENGTH_LONG).show();
            } else if (!password.equals(confirmPassword)) {
                Log.d(TAG, "Mật khẩu không khớp");
                Toast.makeText(this, "Mật khẩu và xác nhận mật khẩu không khớp!", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "Bắt đầu gửi yêu cầu đăng ký: " + email);
                signupUser(name, email, phone, password, confirmPassword);
            }
        });

        txtSignin.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, SigninActivity.class);
            startActivity(intent);
        });
    }

    private void signupUser(String name, String email, String phone, String password, String confirmPassword) {
        SignupRequest signupRequest = new SignupRequest(name, email, phone, password, confirmPassword);
        Log.d(TAG, "Gửi yêu cầu đăng ký với dữ liệu: " + signupRequest.toString());

        RetrofitClient.getApiService().createUser(signupRequest).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    ApiResponse apiResponse = response.body();
                    Log.d(TAG,  apiResponse.getMessage());
                    Intent intent = new Intent(SignupActivity.this, Notification_2Activity.class);
                    startActivity(intent);
                    finish();

                } else {
                    Log.e(TAG, "Đăng ký thất bại, mã lỗi: " + response.code());
                    try {
                        String errorBody = response.errorBody().string();
                        ApiResponse errorResponse = new Gson().fromJson(errorBody, ApiResponse.class);
                        String errorMessage = errorResponse.getMessage();
                        Log.e(TAG, "Thông báo lỗi từ server: " + errorMessage);
                        Toast.makeText(SignupActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Log.e(TAG, "Lỗi parse phản hồi: " + e.getMessage());
                        Toast.makeText(SignupActivity.this, "Đăng ký thất bại. Mã lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối khi đăng ký: " + t.getMessage());
                Toast.makeText(SignupActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}