//package com.example.doan;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.text.method.HideReturnsTransformationMethod;
//import android.text.method.PasswordTransformationMethod;
//import android.util.Log;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//import androidx.appcompat.app.AppCompatActivity;
//import com.example.doan.api.RetrofitClient;
//import com.example.doan.models.LoginRequest;
//import com.example.doan.models.LoginResponse;
//import com.example.doan.models.UserProfileDTO;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import java.io.IOException;
//
//public class SigninActivity extends AppCompatActivity {
//
//    private static final String TAG = "SigninActivity";
//    private static final String PREFS_NAME = "MyAppPrefs";
//    private EditText edtPhone, edtPassword;
//    private ImageView btnTogglePassword;
//    private TextView txtForgetPassword, txtSignup, btnSignin;
//    private ProgressBar progressBar;
//    private boolean isPasswordVisible = false;
//    private Call<LoginResponse> loginCall;
//    private Call<UserProfileDTO> userProfileCall;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.signin);
//
//        // Khởi tạo các view
//        edtPhone = findViewById(R.id.signin_phone);
//        edtPassword = findViewById(R.id.signin_password);
//        btnTogglePassword = findViewById(R.id.eye_icon);
//        txtForgetPassword = findViewById(R.id.txt_forgetpassword);
//        txtSignup = findViewById(R.id.txt_signup);
//        btnSignin = findViewById(R.id.btn_signin);
//        progressBar = findViewById(R.id.progressBar);
//
//        // Kiểm tra các view có tồn tại không
//        if (edtPhone == null || edtPassword == null || btnTogglePassword == null ||
//                txtForgetPassword == null || txtSignup == null || btnSignin == null || progressBar == null) {
//            Log.e(TAG, "One or more views not found in layout");
//            showToast("Lỗi giao diện, vui lòng thử lại");
//            finish();
//            return;
//        }
//
//        // Xử lý sự kiện các nút
//        btnTogglePassword.setOnClickListener(v -> togglePasswordVisibility());
//
//        txtForgetPassword.setOnClickListener(v -> {
//            if (!isFinishing()) {
//                startActivity(new Intent(SigninActivity.this, ForgetPasswordActivity.class));
//            }
//        });
//
//        txtSignup.setOnClickListener(v -> {
//            if (!isFinishing()) {
//                startActivity(new Intent(SigninActivity.this, SignupActivity.class));
//            }
//        });
//
//        btnSignin.setOnClickListener(v -> attemptLogin());
//    }
//
//    private void togglePasswordVisibility() {
//        if (isPasswordVisible) {
//            edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
//            btnTogglePassword.setImageResource(R.drawable.hide_password);
//        } else {
//            edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//            btnTogglePassword.setImageResource(R.drawable.show_password);
//        }
//        isPasswordVisible = !isPasswordVisible;
//        edtPassword.setSelection(edtPassword.getText().length());
//    }
//
//    private void attemptLogin() {
//        String phone = edtPhone.getText().toString().trim();
//        String password = edtPassword.getText().toString().trim();
//
//        if (phone.isEmpty() || password.isEmpty()) {
//            showToast("Vui lòng điền đầy đủ thông tin");
//            return;
//        }
//
//        progressBar.setVisibility(View.VISIBLE);
//        btnSignin.setEnabled(false);
//
//        LoginRequest loginRequest = new LoginRequest(phone, password);
//        loginCall = RetrofitClient.getApiService(this).loginUser(loginRequest);
//        loginCall.enqueue(new Callback<LoginResponse>() {
//            @Override
//            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
//                if (!isFinishing()) {
//                    progressBar.setVisibility(View.GONE);
//                    btnSignin.setEnabled(true);
//
//                    if (response.isSuccessful() && response.body() != null) {
//                        LoginResponse loginResponse = response.body();
//                        Log.d(TAG, "Đăng nhập thành công: token=" + loginResponse.getToken());
//
//                        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
//                        SharedPreferences.Editor editor = sharedPreferences.edit();
//                        editor.putString("token", loginResponse.getToken());
//                        editor.apply();
//
//                        fetchUserId(loginResponse.getToken(), sharedPreferences);
//                    } else {
//                        handleLoginError(response);
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<LoginResponse> call, Throwable t) {
//                if (!call.isCanceled() && !isFinishing()) {
//                    progressBar.setVisibility(View.GONE);
//                    btnSignin.setEnabled(true);
//                    Log.e(TAG, "Lỗi kết nối: " + t.getMessage());
//                    String errorMessage = "Lỗi kết nối: " + t.getMessage();
//                    if (t instanceof IOException) {
//                        errorMessage = "Không thể kết nối đến server. Vui lòng kiểm tra mạng hoặc server!";
//                    }
//                    showToast(errorMessage);
//                }
//            }
//        });
//    }
//
//    private void fetchUserId(String token, SharedPreferences sharedPreferences) {
//        if (isFinishing()) return;
//
//        userProfileCall = RetrofitClient.getApiService(this).getCurrentUser("Bearer " + token);
//        userProfileCall.enqueue(new Callback<UserProfileDTO>() {
//            @Override
//            public void onResponse(Call<UserProfileDTO> call, Response<UserProfileDTO> response) {
//                if (!isFinishing()) {
//                    if (response.isSuccessful() && response.body() != null) {
//                        UserProfileDTO user = response.body();
//                        Integer userId = user.getId();
//                        if (userId == null || userId == 0) {
//                            Log.e(TAG, "userId không hợp lệ hoặc không có trong phản hồi: " + user);
//                            showToast("Lỗi: Không lấy được thông tin người dùng (ID không hợp lệ)");
//                            return;
//                        }
//
//                        SharedPreferences.Editor editor = sharedPreferences.edit();
//                        editor.putInt("userId", userId);
//                        editor.apply();
//                        Log.d(TAG, "Lấy userId thành công: " + userId);
//
//                        navigateToOrderActivity(token, user.getPhone());
//                    } else {
//                        Log.e(TAG, "Lỗi lấy userId, mã: " + response.code());
//                        try {
//                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Không có thông tin lỗi";
//                            Log.e(TAG, "Chi tiết lỗi: " + errorBody);
//                            showToast("Lỗi lấy thông tin người dùng: " + errorBody);
//                        } catch (IOException e) {
//                            Log.e(TAG, "Lỗi parse lỗi: " + e.getMessage());
//                            showToast("Lỗi không xác định khi lấy thông tin người dùng");
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<UserProfileDTO> call, Throwable t) {
//                if (!call.isCanceled() && !isFinishing()) {
//                    Log.e(TAG, "Lỗi kết nối khi lấy userId: " + t.getMessage());
//                    String errorMessage = "Lỗi kết nối: " + t.getMessage();
//                    if (t instanceof IOException) {
//                        errorMessage = "Không thể kết nối đến server khi lấy thông tin người dùng!";
//                    }
//                    showToast(errorMessage);
//                }
//            }
//        });
//    }
//
//    private void navigateToOrderActivity(String token, String phone) {
//        if (isFinishing()) return;
//
//        Intent intent = new Intent(SigninActivity.this, OrderActivity.class);
//        intent.putExtra("token", token);
//        intent.putExtra("phone", phone);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
//        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//
//        // Trì hoãn việc gọi finish() để đảm bảo quá trình chuyển đổi hoàn tất
//        new Handler(Looper.getMainLooper()).postDelayed(this::finish, 300);
//    }
//
//    private void handleLoginError(Response<LoginResponse> response) {
//        if (isFinishing()) return;
//
//        try {
//            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Không có thông tin lỗi";
//            Log.e(TAG, "Đăng nhập thất bại: " + response.code() + " - " + errorBody);
//            if (response.code() == 401) {
//                showToast("Sai số điện thoại hoặc mật khẩu!");
//            } else {
//                showToast("Đăng nhập thất bại: " + errorBody);
//            }
//        } catch (IOException e) {
//            Log.e(TAG, "Lỗi parse lỗi: " + e.getMessage());
//            showToast("Lỗi không xác định. Vui lòng thử lại!");
//        }
//    }
//
//    private void showToast(String message) {
//        if (!isFinishing()) {
//            new Handler(Looper.getMainLooper()).post(() -> {
//                if (!isFinishing()) {
//                    Toast.makeText(SigninActivity.this, message, Toast.LENGTH_LONG).show();
//                }
//            });
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        // Hủy các yêu cầu mạng nếu chúng chưa hoàn tất
//        if (loginCall != null && !loginCall.isCanceled()) {
//            loginCall.cancel();
//            Log.d(TAG, "Hủy yêu cầu đăng nhập trong onDestroy");
//        }
//        if (userProfileCall != null && !userProfileCall.isCanceled()) {
//            userProfileCall.cancel();
//            Log.d(TAG, "Hủy yêu cầu lấy thông tin người dùng trong onDestroy");
//        }
//    }
//}
package com.example.doan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.doan.api.RetrofitClient;
import com.example.doan.models.LoginRequest;
import com.example.doan.models.LoginResponse;
import com.example.doan.models.UserProfileDTO;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SigninActivity extends AppCompatActivity {

    private static final String TAG = "SigninActivity";
    private static final String PREFS_NAME = "MyAppPrefs";
    private EditText edtPhone, edtPassword;
    private ImageView btnTogglePassword;
    private TextView txtForgetPassword, txtSignup, btnSignin;
    private ProgressBar progressBar;
    private boolean isPasswordVisible = false;
    private Call<LoginResponse> loginCall;
    private Call<UserProfileDTO> userProfileCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);

        // Khởi tạo các view
        edtPhone = findViewById(R.id.signin_phone);
        edtPassword = findViewById(R.id.signin_password);
        btnTogglePassword = findViewById(R.id.eye_icon);
        txtForgetPassword = findViewById(R.id.txt_forgetpassword);
        txtSignup = findViewById(R.id.txt_signup);
        btnSignin = findViewById(R.id.btn_signin);
        progressBar = findViewById(R.id.progressBar);

        // Kiểm tra các view có tồn tại không
        if (edtPhone == null || edtPassword == null || btnTogglePassword == null ||
                txtForgetPassword == null || txtSignup == null || btnSignin == null || progressBar == null) {
            Log.e(TAG, "One or more views not found in layout");
            showToast("Lỗi giao diện, vui lòng thử lại");
            finish();
            return;
        }

        // Xử lý sự kiện các nút
        btnTogglePassword.setOnClickListener(v -> togglePasswordVisibility());

        txtForgetPassword.setOnClickListener(v -> {
            if (!isFinishing()) {
                startActivity(new Intent(SigninActivity.this, ForgetPasswordActivity.class));
            }
        });

        txtSignup.setOnClickListener(v -> {
            if (!isFinishing()) {
                startActivity(new Intent(SigninActivity.this, SignupActivity.class));
            }
        });

        btnSignin.setOnClickListener(v -> attemptLogin());
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            btnTogglePassword.setImageResource(R.drawable.hide_password);
        } else {
            edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            btnTogglePassword.setImageResource(R.drawable.show_password);
        }
        isPasswordVisible = !isPasswordVisible;
        edtPassword.setSelection(edtPassword.getText().length());
    }

    private void attemptLogin() {
        String phone = edtPhone.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (phone.isEmpty() || password.isEmpty()) {
            showToast("Vui lòng điền đầy đủ thông tin");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSignin.setEnabled(false);

        LoginRequest loginRequest = new LoginRequest(phone, password);
        loginCall = RetrofitClient.getApiService(this).loginUser(loginRequest);
        loginCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (!isFinishing()) {
                    progressBar.setVisibility(View.GONE);
                    btnSignin.setEnabled(true);

                    if (response.isSuccessful() && response.body() != null) {
                        LoginResponse loginResponse = response.body();
                        Log.d(TAG, "Đăng nhập thành công: token=" + loginResponse.getToken());

                        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("token", loginResponse.getToken());
                        editor.apply();

                        fetchUserId(loginResponse.getToken(), sharedPreferences);
                    } else {
                        handleLoginError(response);
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                if (!call.isCanceled() && !isFinishing()) {
                    progressBar.setVisibility(View.GONE);
                    btnSignin.setEnabled(true);
                    Log.e(TAG, "Lỗi kết nối: " + t.getMessage());
                    String errorMessage = "Lỗi kết nối: " + t.getMessage();
                    if (t instanceof IOException) {
                        errorMessage = "Không thể kết nối đến server. Vui lòng kiểm tra mạng hoặc server!";
                    }
                    showToast(errorMessage);
                }
            }
        });
    }

    private void fetchUserId(String token, SharedPreferences sharedPreferences) {
        if (isFinishing()) return;

        userProfileCall = RetrofitClient.getApiService(this).getCurrentUser("Bearer " + token);
        userProfileCall.enqueue(new Callback<UserProfileDTO>() {
            @Override
            public void onResponse(Call<UserProfileDTO> call, Response<UserProfileDTO> response) {
                if (!isFinishing()) {
                    if (response.isSuccessful() && response.body() != null) {
                        UserProfileDTO user = response.body();
                        Integer userId = user.getId();
                        if (userId == null || userId == 0) {
                            Log.e(TAG, "userId không hợp lệ hoặc không có trong phản hồi: " + user);
                            showToast("Lỗi: Không lấy được thông tin người dùng (ID không hợp lệ)");
                            return;
                        }

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("userId", userId);
                        editor.apply();
                        Log.d(TAG, "Lấy userId thành công: " + userId);

                        navigateToOrderActivity(token, user.getPhone());
                    } else {
                        Log.e(TAG, "Lỗi lấy userId, mã: " + response.code());
                        try {
                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Không có thông tin lỗi";
                            Log.e(TAG, "Chi tiết lỗi: " + errorBody);
                            showToast("Lỗi lấy thông tin người dùng: " + errorBody);
                        } catch (IOException e) {
                            Log.e(TAG, "Lỗi parse lỗi: " + e.getMessage());
                            showToast("Lỗi không xác định khi lấy thông tin người dùng");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<UserProfileDTO> call, Throwable t) {
                if (!call.isCanceled() && !isFinishing()) {
                    Log.e(TAG, "Lỗi kết nối khi lấy userId: " + t.getMessage());
                    String errorMessage = "Lỗi kết nối: " + t.getMessage();
                    if (t instanceof IOException) {
                        errorMessage = "Không thể kết nối đến server khi lấy thông tin người dùng!";
                    }
                    showToast(errorMessage);
                }
            }
        });
    }

    private void navigateToOrderActivity(String token, String phone) {
        if (isFinishing()) return;

        Intent intent = new Intent(SigninActivity.this, OrderActivity.class);
        intent.putExtra("token", token);
        intent.putExtra("phone", phone);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        // Tăng thời gian trì hoãn để đảm bảo quá trình chuyển đổi hoàn tất
        new Handler(Looper.getMainLooper()).postDelayed(this::finish, 500);
    }

    private void handleLoginError(Response<LoginResponse> response) {
        if (isFinishing()) return;

        try {
            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Không có thông tin lỗi";
            Log.e(TAG, "Đăng nhập thất bại: " + response.code() + " - " + errorBody);
            if (response.code() == 401) {
                showToast("Sai số điện thoại hoặc mật khẩu!");
            } else {
                showToast("Đăng nhập thất bại: " + errorBody);
            }
        } catch (IOException e) {
            Log.e(TAG, "Lỗi parse lỗi: " + e.getMessage());
            showToast("Lỗi không xác định. Vui lòng thử lại!");
        }
    }

    private void showToast(String message) {
        if (!isFinishing()) {
            new Handler(Looper.getMainLooper()).post(() -> {
                if (!isFinishing()) {
                    Toast.makeText(SigninActivity.this, message, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Hủy các yêu cầu mạng nếu chúng chưa hoàn tất
        if (loginCall != null && !loginCall.isCanceled()) {
            loginCall.cancel();
            Log.d(TAG, "Hủy yêu cầu đăng nhập trong onDestroy");
        }
        if (userProfileCall != null && !userProfileCall.isCanceled()) {
            userProfileCall.cancel();
            Log.d(TAG, "Hủy yêu cầu lấy thông tin người dùng trong onDestroy");
        }
    }
}