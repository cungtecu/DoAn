package com.example.doan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.doan.api.RetrofitClient;
import com.example.doan.models.LoginRequest;
import com.example.doan.models.LoginResponse;
import com.example.doan.models.SignupRequest;
import com.example.doan.models.SignupResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.io.IOException;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";
    private EditText edtName, edtPhone, edtEmail, edtPassword, edtConfirmPassword;
    private Button btnSignup;
    private ProgressBar progressBar;
    private ImageView iconNewPass1, iconNewPass2;
    private TextView txtSignin;
    private boolean isPasswordVisible1 = false;
    private boolean isPasswordVisible2 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        // Ánh xạ các view
        edtName = findViewById(R.id.signup_name);
        edtPhone = findViewById(R.id.signup_phone);
        edtEmail = findViewById(R.id.signup_email);
        edtPassword = findViewById(R.id.new_pass1);
        edtConfirmPassword = findViewById(R.id.new_pass2);
        btnSignup = findViewById(R.id.btn_signup);
        progressBar = findViewById(R.id.progressBar);
        iconNewPass1 = findViewById(R.id.icon_newpass1);
        iconNewPass2 = findViewById(R.id.icon_newpass2);
        txtSignin = findViewById(R.id.txt_signin);

        // Ẩn ProgressBar ban đầu
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        // Xử lý sự kiện nhấn nút ẩn/hiện mật khẩu
        iconNewPass1.setOnClickListener(v -> {
            if (isPasswordVisible1) {
                edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                iconNewPass1.setImageResource(R.drawable.hide_password);
                isPasswordVisible1 = false;
            } else {
                edtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                iconNewPass1.setImageResource(R.drawable.show_password);
                isPasswordVisible1 = true;
            }
            edtPassword.setSelection(edtPassword.getText().length());
        });

        iconNewPass2.setOnClickListener(v -> {
            if (isPasswordVisible2) {
                edtConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                iconNewPass2.setImageResource(R.drawable.hide_password);
                isPasswordVisible2 = false;
            } else {
                edtConfirmPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                iconNewPass2.setImageResource(R.drawable.show_password);
                isPasswordVisible2 = true;
            }
            edtConfirmPassword.setSelection(edtConfirmPassword.getText().length());
        });

        // Xử lý sự kiện nhấn nút Đăng ký
        btnSignup.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            String confirmPassword = edtConfirmPassword.getText().toString().trim();

            // Kiểm tra ràng buộc
            if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Email không đúng định dạng", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!phone.matches("^0\\d{9}$")) {
                Toast.makeText(this, "Số điện thoại phải có 10 số và bắt đầu bằng 0", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 7) {
                Toast.makeText(this, "Mật khẩu phải có ít nhất 7 ký tự", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Mật khẩu và xác nhận mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            // Hiển thị ProgressBar
            if (progressBar != null) {
                progressBar.setVisibility(View.VISIBLE);
            }
            btnSignup.setEnabled(false);

            // Gửi yêu cầu đăng ký bước 1
            initiateRegistration(name, phone, email, password, confirmPassword);
        });

        // Xử lý sự kiện nhấn txt_signin
        txtSignin.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, SigninActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void initiateRegistration(String name, String phone, String email, String password, String confirmPassword) {
        SignupRequest signupRequest = new SignupRequest(name, email, phone, password, confirmPassword);
        Log.d(TAG, "Gửi yêu cầu đăng ký tới /api/users/register/initiate với dữ liệu: " + signupRequest.toString());

        RetrofitClient.getApiService().initiateRegistration(signupRequest).enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                Log.d(TAG, "Mã phản hồi: " + response.code());
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                btnSignup.setEnabled(true);

                if (response.isSuccessful()) {
                    SignupResponse signupResponse = response.body();
                    if (signupResponse != null && "success".equals(signupResponse.getStatus())) {
                        Log.d(TAG, "Đăng ký bước 1 thành công: " + signupResponse.getMessage());
                        if (signupResponse.getMessage().contains("OTP đã được gửi")) {
                            Toast.makeText(SignupActivity.this, "Vui lòng kiểm tra email để lấy mã OTP!", Toast.LENGTH_LONG).show();
                            showOtpDialog(signupRequest);
                        } else {
                            Toast.makeText(SignupActivity.this, "Đăng ký thành công! Vui lòng đăng nhập.", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(SignupActivity.this, SigninActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Log.e(TAG, "Phản hồi không hợp lệ: " + (signupResponse != null ? signupResponse.getMessage() : "null"));
                        Toast.makeText(SignupActivity.this, signupResponse != null ? signupResponse.getMessage() : "Lỗi không xác định", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Đăng ký thất bại, mã lỗi: " + response.code());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Không có thông tin lỗi";
                        Log.e(TAG, "Nội dung lỗi: " + errorBody);
                        if (response.code() == 400) {
                            if (errorBody.contains("Email đã được đăng ký")) {
                                Toast.makeText(SignupActivity.this, "Email đã được sử dụng!", Toast.LENGTH_SHORT).show();
                            } else if (errorBody.contains("Số điện thoại đã được đăng ký")) {
                                Toast.makeText(SignupActivity.this, "Số điện thoại đã được sử dụng!", Toast.LENGTH_SHORT).show();
                            } else if (errorBody.contains("Email đã tồn tại trong hệ thống")) {
                                Toast.makeText(SignupActivity.this, "Email đã tồn tại (tài khoản bị xóa). Vui lòng dùng email khác hoặc liên hệ hỗ trợ!", Toast.LENGTH_LONG).show();
                            } else if (errorBody.contains("Số điện thoại đã tồn tại trong hệ thống")) {
                                Toast.makeText(SignupActivity.this, "Số điện thoại đã tồn tại (tài khoản bị xóa). Vui lòng dùng số khác hoặc liên hệ hỗ trợ!", Toast.LENGTH_LONG).show();
                            } else if (errorBody.contains("Email không hợp lệ")) {
                                Toast.makeText(SignupActivity.this, "Email không hợp lệ!", Toast.LENGTH_SHORT).show();
                            } else if (errorBody.contains("Số điện thoại phải có 10 số")) {
                                Toast.makeText(SignupActivity.this, "Số điện thoại phải có 10 số và bắt đầu bằng 0!", Toast.LENGTH_SHORT).show();
                            } else if (errorBody.contains("Mật khẩu và xác nhận mật khẩu không khớp")) {
                                Toast.makeText(SignupActivity.this, "Mật khẩu và xác nhận mật khẩu không khớp!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SignupActivity.this, "Lỗi: " + errorBody, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(SignupActivity.this, "Đăng ký thất bại. Mã lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Lỗi phân tích phản hồi: " + e.getMessage());
                        Toast.makeText(SignupActivity.this, "Đăng ký thất bại. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                btnSignup.setEnabled(true);
                Log.e(TAG, "Lỗi kết nối: " + t.getMessage());
                Toast.makeText(SignupActivity.this, "Lỗi kết nối. Vui lòng kiểm tra mạng và thử lại!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showOtpDialog(SignupRequest signupRequest) {
        Log.d(TAG, "Hiển thị dialog OTP");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác minh OTP");
        builder.setMessage("Một mã OTP đã được gửi đến email của bạn. Vui lòng nhập mã OTP (6 chữ số) để xác minh tài khoản:");

        // Tạo EditText để người dùng nhập OTP
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint("Nhập mã OTP (6 chữ số)");
        input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
        builder.setView(input);

        // Thiết lập nút "Xác nhận" (nhưng chưa gán hành động ngay)
        builder.setPositiveButton("Xác nhận", null);

        // Thiết lập nút "Hủy"
        builder.setNegativeButton("Hủy", (dialog, which) -> {
            Log.d(TAG, "Người dùng hủy dialog OTP");
            dialog.cancel();
        });

        // Tạo và hiển thị dialog
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        // Vô hiệu hóa nút "Xác nhận" ban đầu
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

        // Gán hành động cho nút "Xác nhận" sau khi dialog hiển thị
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String otp = input.getText().toString().trim();
            Log.d(TAG, "Người dùng nhập OTP: " + otp);
            completeRegistration(signupRequest, otp);
        });

        // Theo dõi sự thay đổi trong ô nhập OTP
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String otp = s.toString().trim();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(!otp.isEmpty());
            }
        });

        Log.d(TAG, "Dialog OTP đã được hiển thị");
    }

    private void completeRegistration(SignupRequest signupRequest, String otp) {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        btnSignup.setEnabled(false);

        Log.d(TAG, "Gửi yêu cầu xác nhận OTP tới /api/users/register/complete với OTP: " + otp);
        RetrofitClient.getApiService().completeRegistration(signupRequest, otp).enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                Log.d(TAG, "Mã phản hồi: " + response.code());
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                btnSignup.setEnabled(true);

                if (response.isSuccessful()) {
                    SignupResponse signupResponse = response.body();
                    if (signupResponse != null && "success".equals(signupResponse.getStatus())) {
                        Log.d(TAG, "Xác nhận OTP thành công: " + signupResponse.getMessage());
                        String successMessage = signupResponse.getMessage();
                        if (signupResponse.getUsers() != null) {
                            successMessage += " Chào mừng " + signupResponse.getUsers().getName() + " (ID: " + signupResponse.getUsers().getId() + ")!";
                        }
                        Toast.makeText(SignupActivity.this, successMessage, Toast.LENGTH_LONG).show();

                        // Đăng nhập tự động sau khi xác nhận OTP thành công
                        loginAutomatically(signupRequest.getPhone(), signupRequest.getPassword());
                    } else {
                        Log.e(TAG, "Phản hồi không hợp lệ: " + (signupResponse != null ? signupResponse.getMessage() : "null"));
                        Toast.makeText(SignupActivity.this, signupResponse != null ? signupResponse.getMessage() : "Lỗi không xác định", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Xác nhận OTP thất bại, mã lỗi: " + response.code());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Không có thông tin lỗi";
                        Log.e(TAG, "Nội dung lỗi: " + errorBody);
                        if (response.code() == 400) {
                            if (errorBody.contains("Mã OTP không hợp lệ")) {
                                Toast.makeText(SignupActivity.this, "Mã OTP không hợp lệ! Vui lòng kiểm tra lại.", Toast.LENGTH_SHORT).show();
                            } else if (errorBody.contains("Mã OTP đã hết hạn")) {
                                Toast.makeText(SignupActivity.this, "Mã OTP đã hết hạn! Vui lòng yêu cầu gửi lại mã.", Toast.LENGTH_LONG).show();
                            } else if (errorBody.contains("Tài khoản không cần khôi phục")) {
                                Toast.makeText(SignupActivity.this, "Tài khoản không cần khôi phục!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(SignupActivity.this, "Lỗi: " + errorBody, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(SignupActivity.this, "Xác nhận OTP thất bại. Mã lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Lỗi phân tích phản hồi: " + e.getMessage());
                        Toast.makeText(SignupActivity.this, "Xác nhận OTP thất bại. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                btnSignup.setEnabled(true);
                Log.e(TAG, "Lỗi kết nối: " + t.getMessage());
                Toast.makeText(SignupActivity.this, "Lỗi kết nối. Vui lòng kiểm tra mạng và thử lại!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginAutomatically(String phone, String password) {
        LoginRequest loginRequest = new LoginRequest(phone, password);
        RetrofitClient.getApiService().loginUser(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();
                    if (loginResponse != null && "success".equals(loginResponse.getStatus())) {
                        Log.d(TAG, "Đăng nhập tự động thành công: " + loginResponse.getMessage());
                        // Lưu token vào SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("token", loginResponse.getToken());
                        editor.putString("phone", loginResponse.getPhone());
                        editor.apply();

                        // Chuyển sang MainActivity
                        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.e(TAG, "Đăng nhập tự động thất bại: " + (loginResponse != null ? loginResponse.getMessage() : "null"));
                        Toast.makeText(SignupActivity.this, "Đăng nhập tự động thất bại. Vui lòng đăng nhập thủ công!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(SignupActivity.this, SigninActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Log.e(TAG, "Đăng nhập tự động thất bại, mã lỗi: " + response.code());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Không có thông tin lỗi";
                        Log.e(TAG, "Nội dung lỗi: " + errorBody);
                        Toast.makeText(SignupActivity.this, "Đăng nhập tự động thất bại: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        Log.e(TAG, "Lỗi phân tích phản hồi: " + e.getMessage());
                        Toast.makeText(SignupActivity.this, "Đăng nhập tự động thất bại. Vui lòng đăng nhập thủ công!", Toast.LENGTH_LONG).show();
                    }
                    Intent intent = new Intent(SignupActivity.this, SigninActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối khi đăng nhập tự động: " + t.getMessage());
                Toast.makeText(SignupActivity.this, "Lỗi kết nối. Vui lòng đăng nhập thủ công!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(SignupActivity.this, SigninActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}