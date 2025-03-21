package com.example.doan;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.example.doan.ApiService;
import com.google.firebase.auth.FirebaseAuth;
import android.content.Intent;
import com.example.doan.EmailCheckResponse;



public class ForgetPasswordActivity extends AppCompatActivity {

    private EditText edtEmail;
    private Button btnSendOtp;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgetpassword);

        edtEmail = findViewById(R.id.forget_email);
        btnSendOtp = findViewById(R.id.btn_signup);

        // Khởi tạo Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://run.mocky.io/v3/")  // URL Mocky của bạn
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        btnSendOtp.setOnClickListener(v -> handleSendOtp());
    }

    private void handleSendOtp() {
        String email = edtEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            showToast("Vui lòng nhập email");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Email không hợp lệ");
        } else {
            checkEmailExists(email);
        }
    }

    private void checkEmailExists(String email) {
        String mockUrl;

        if (email.equals("trananhhuy12a72021@gmail.com")) {
            mockUrl = "https://run.mocky.io/v3/25bbb57c-cd80-4c66-b1de-44e8490a9c1c";
        } else {
            mockUrl = "https://run.mocky.io/v3/0d0b3a52-9f14-43f6-9c9d-25168698a475";
        }

        // Tách base URL và endpoint
        String baseUrl = mockUrl.substring(0, mockUrl.lastIndexOf("/") + 1);
        String endpoint = mockUrl.substring(mockUrl.lastIndexOf("/") + 1);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)  // base URL phải kết thúc bằng /
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<EmailCheckResponse> call = apiService.checkEmail(endpoint);  // endpoint là UUID
        call.enqueue(new Callback<EmailCheckResponse>() {
            @Override
            public void onResponse(Call<EmailCheckResponse> call, Response<EmailCheckResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().exists) {
                        sendResetPasswordLink(email);
                    } else {
                        Intent intent = new Intent(ForgetPasswordActivity.this, EmailNotFoundActivity.class);
                        startActivity(intent);
                    }
                } else {
                    showToast("Lỗi phản hồi Mocky");
                }
            }

            @Override
            public void onFailure(Call<EmailCheckResponse> call, Throwable t) {
                showToast("Lỗi kết nối Mocky");
            }
        });
    }


    private void sendResetPasswordLink(String email) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showToast("Link đặt lại mật khẩu đã được gửi đến " + email);
                    } else {
                        showToast("Gửi link thất bại. Vui lòng thử lại");
                    }
                });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
