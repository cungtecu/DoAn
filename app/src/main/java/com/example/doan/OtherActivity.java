package com.example.doan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.doan.api.RetrofitClient;
import com.example.doan.models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.io.IOException;

public class OtherActivity extends AppCompatActivity {

    private static final String TAG = "OtherActivity";
    private RelativeLayout btnProfile, btnSetting, btnPolicy, btnTerms;
    private Button btnLogout;
    private ImageButton btnHome, cartIcon, btnOther;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);

        // Ánh xạ các thành phần từ layout
        try {
            btnProfile = findViewById(R.id.btn_profile);
            btnSetting = findViewById(R.id.btn_setting);
            btnPolicy = findViewById(R.id.btn_policy);
            btnTerms = findViewById(R.id.btn_terms);
            btnLogout = findViewById(R.id.btn_logout);
            btnHome = findViewById(R.id.btn_home);
            cartIcon = findViewById(R.id.cartIcon);
            btnOther = findViewById(R.id.btn_other);
        } catch (Exception e) {
            Log.e(TAG, "Lỗi ánh xạ view: " + e.getMessage());
            Toast.makeText(this, "Lỗi giao diện. Vui lòng kiểm tra layout!", Toast.LENGTH_LONG).show();
            return;
        }

        // Xử lý sự kiện nhấn btn_profile (Hồ Sơ)
        if (btnProfile != null) {
            btnProfile.setOnClickListener(v -> {
                Log.d(TAG, "Nhấn nút Hồ Sơ");
                fetchUserProfile();
            });
        } else {
            Log.e(TAG, "btn_profile không được tìm thấy trong layout");
            Toast.makeText(this, "Lỗi: Không tìm thấy nút Hồ Sơ", Toast.LENGTH_SHORT).show();
        }

        // Xử lý sự kiện nhấn btn_setting (Cài Đặt)
        if (btnSetting != null) {
            btnSetting.setOnClickListener(v -> {
                Log.d(TAG, "Nhấn nút Cài Đặt");
                try {
                    Intent intent = new Intent(OtherActivity.this, SettingUserActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                } catch (Exception e) {
                    Log.e(TAG, "Lỗi khi chuyển sang SettingUserActivity: " + e.getMessage());
                    Toast.makeText(this, "Lỗi: Không thể mở Cài Đặt", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.e(TAG, "btn_setting không được tìm thấy trong layout");
            Toast.makeText(this, "Lỗi: Không tìm thấy nút Cài Đặt", Toast.LENGTH_SHORT).show();
        }

        // Xử lý sự kiện nhấn btn_policy (Chính sách bảo mật)
        if (btnPolicy != null) {
            btnPolicy.setOnClickListener(v -> {
                Log.d(TAG, "Nhấn nút Chính sách bảo mật");
                try {
                    Intent intent = new Intent(OtherActivity.this, PolicyActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                } catch (Exception e) {
                    Log.e(TAG, "Lỗi khi chuyển sang PolicyActivity: " + e.getMessage());
                    Toast.makeText(this, "Lỗi: Không thể mở Chính sách bảo mật", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.e(TAG, "btn_policy không được tìm thấy trong layout");
            Toast.makeText(this, "Lỗi: Không tìm thấy nút Chính sách bảo mật", Toast.LENGTH_SHORT).show();
        }

        // Xử lý sự kiện nhấn btn_terms (Điều khoản dịch vụ)
        if (btnTerms != null) {
            btnTerms.setOnClickListener(v -> {
                Log.d(TAG, "Nhấn nút Điều khoản dịch vụ");
                try {
                    Intent intent = new Intent(OtherActivity.this, TermsActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                } catch (Exception e) {
                    Log.e(TAG, "Lỗi khi chuyển sang TermsActivity: " + e.getMessage());
                    Toast.makeText(this, "Lỗi: Không thể mở Điều khoản dịch vụ", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.e(TAG, "btn_terms không được tìm thấy trong layout");
            Toast.makeText(this, "Lỗi: Không tìm thấy nút Điều khoản dịch vụ", Toast.LENGTH_SHORT).show();
        }

        // Xử lý sự kiện nhấn btn_logout (Đăng Xuất)
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                Log.d(TAG, "Nhấn nút Đăng Xuất");
                SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("token");
                editor.apply();
                Toast.makeText(OtherActivity.this, "Đã đăng xuất!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(OtherActivity.this, SigninActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            });
        } else {
            Log.e(TAG, "btn_logout không được tìm thấy trong layout");
            Toast.makeText(this, "Lỗi: Không tìm thấy nút Đăng Xuất", Toast.LENGTH_SHORT).show();
        }

        // Xử lý sự kiện nhấn btn_home
        if (btnHome != null) {
            btnHome.setOnClickListener(v -> {
                Log.d(TAG, "Nhấn nút Home");
                try {
                    Intent intent = new Intent(OtherActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    finish();
                } catch (Exception e) {
                    Log.e(TAG, "Lỗi khi chuyển sang MainActivity: " + e.getMessage());
                    Toast.makeText(this, "Lỗi: Không thể mở Trang chủ", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.e(TAG, "btn_home không được tìm thấy trong layout");
            Toast.makeText(this, "Lỗi: Không tìm thấy nút Home", Toast.LENGTH_SHORT).show();
        }

        // Xử lý sự kiện nhấn cartIcon
        if (cartIcon != null) {
            cartIcon.setOnClickListener(v -> {
                Log.d(TAG, "Nhấn nút Giỏ hàng");
                try {
                    Intent intent = new Intent(OtherActivity.this, CartActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                } catch (Exception e) {
                    Log.e(TAG, "Lỗi khi chuyển sang CartActivity: " + e.getMessage());
                    Toast.makeText(this, "Lỗi: Không thể mở Giỏ hàng", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.e(TAG, "cartIcon không được tìm thấy trong layout");
            Toast.makeText(this, "Lỗi: Không tìm thấy nút Giỏ hàng", Toast.LENGTH_SHORT).show();
        }

        // Xử lý sự kiện nhấn btn_other
        if (btnOther != null) {
            btnOther.setOnClickListener(v -> {
                Log.d(TAG, "Nhấn nút Other");
                Toast.makeText(this, "Bạn đang ở trang Khác", Toast.LENGTH_SHORT).show();
            });
        } else {
            Log.e(TAG, "btn_other không được tìm thấy trong layout");
            Toast.makeText(this, "Lỗi: Không tìm thấy nút Khác", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchUserProfile() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);

        Log.d(TAG, "Gửi yêu cầu lấy thông tin người dùng với token: " + token);
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Token không hợp lệ. Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            Intent signinIntent = new Intent(OtherActivity.this, SigninActivity.class);
            signinIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signinIntent);
            finish();
            return;
        }

        RetrofitClient.getApiService(this).getCurrentUser(token).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.d(TAG, "Mã phản hồi: " + response.code() + ", URL: " + call.request().url());
                if (response.isSuccessful()) {
                    User user = response.body();
                    if (user != null) {
                        Log.d(TAG, "Thông tin người dùng: id=" + user.getId() +
                                ", name=" + user.getName() +
                                ", phone=" + user.getPhone() +
                                ", email=" + user.getEmail() +
                                ", points=" + user.getPoints() +
                                ", role=" + user.getRole());
                        if (user.getName() != null && user.getPhone() != null && user.getEmail() != null) {
                            Intent intent = new Intent(OtherActivity.this, ProfileActivity.class);
                            intent.putExtra("name", user.getName());
                            intent.putExtra("phone", user.getPhone());
                            intent.putExtra("email", user.getEmail());
                            intent.putExtra("token", token);
                            intent.putExtra("points", user.getPoints());
                            intent.putExtra("role", user.getRole());
                            startActivity(intent);
                            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        } else {
                            Log.e(TAG, "Dữ liệu người dùng không đầy đủ");
                            Toast.makeText(OtherActivity.this, "Dữ liệu người dùng không đầy đủ", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "Phản hồi từ server không hợp lệ");
                        Toast.makeText(OtherActivity.this, "Phản hồi từ server không hợp lệ", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorBody = "";
                    try {
                        errorBody = response.errorBody() != null ? response.errorBody().string() : "Không có thông tin lỗi";
                    } catch (IOException e) {
                        Log.e(TAG, "Lỗi parse error body: " + e.getMessage());
                    }
                    Log.e(TAG, "Lấy thông tin thất bại, mã lỗi: " + response.code() + ", Error body: " + errorBody);
                    if (response.code() == 401) {
                        Toast.makeText(OtherActivity.this, "Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove("token");
                        editor.apply();
                        Intent signinIntent = new Intent(OtherActivity.this, SigninActivity.class);
                        signinIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(signinIntent);
                        finish();
                    } else {
                        Toast.makeText(OtherActivity.this, "Lỗi: " + errorBody, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối: " + t.getMessage());
                Toast.makeText(OtherActivity.this, "Lỗi kết nối. Vui lòng kiểm tra mạng!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}