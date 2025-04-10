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
import com.example.doan.models.UserProfileResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
            Toast.makeText(this, "Lỗi: Không tìm thấy nút C FacsCài Đặt", Toast.LENGTH_SHORT).show();
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
                try {
                    SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("token");
                    editor.apply();

                    Toast.makeText(OtherActivity.this, "Đã đăng xuất!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(OtherActivity.this, SigninActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                } catch (Exception e) {
                    Log.e(TAG, "Lỗi khi đăng xuất: " + e.getMessage());
                    Toast.makeText(this, "Lỗi: Không thể đăng xuất", Toast.LENGTH_SHORT).show();
                }
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
                // Đã ở trong OtherActivity, không cần chuyển lại
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
            startActivity(signinIntent);
            finish();
            return;
        }

        RetrofitClient.getApiService().getUserProfile("Bearer " + token).enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                Log.d(TAG, "Mã phản hồi: " + response.code());
                if (response.isSuccessful()) {
                    Log.d(TAG, "Phản hồi thành công từ server");
                    UserProfileResponse userProfile = response.body();
                    if (userProfile != null) {
                        Log.d(TAG, "Thông tin người dùng: id=" + userProfile.getId() +
                                ", name=" + userProfile.getName() +
                                ", phone=" + userProfile.getPhone() +
                                ", email=" + userProfile.getEmail() +
                                ", points=" + userProfile.getPoints() +
                                ", role=" + userProfile.getRole());

                        if (userProfile.getName() != null && userProfile.getPhone() != null && userProfile.getEmail() != null) {
                            try {
                                Intent intent = new Intent(OtherActivity.this, ProfileActivity.class);
                                intent.putExtra("name", userProfile.getName());
                                intent.putExtra("phone", userProfile.getPhone());
                                intent.putExtra("email", userProfile.getEmail());
                                intent.putExtra("token", token);
                                intent.putExtra("points", userProfile.getPoints());
                                intent.putExtra("role", userProfile.getRole());
                                startActivity(intent);
                                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                            } catch (Exception e) {
                                Log.e(TAG, "Lỗi khi chuyển sang ProfileActivity: " + e.getMessage());
                                Toast.makeText(OtherActivity.this, "Lỗi: Không thể mở Hồ Sơ", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e(TAG, "Dữ liệu người dùng không đầy đủ: name=" + userProfile.getName() +
                                    ", phone=" + userProfile.getPhone() +
                                    ", email=" + userProfile.getEmail());
                            Toast.makeText(OtherActivity.this, "Lỗi: Dữ liệu người dùng không đầy đủ", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "UserProfileResponse không hợp lệ");
                        Toast.makeText(OtherActivity.this, "Lỗi: Phản hồi từ server không hợp lệ", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Lấy thông tin thất bại, mã lỗi: " + response.code());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Không có thông tin lỗi";
                        Log.e(TAG, "Error body: " + errorBody);
                        if (response.code() == 401) {
                            Toast.makeText(OtherActivity.this, "Token không hợp lệ. Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
                            Intent signinIntent = new Intent(OtherActivity.this, SigninActivity.class);
                            startActivity(signinIntent);
                            finish();
                        } else if (response.code() == 404) {
                            Toast.makeText(OtherActivity.this, "Không tìm thấy API. Vui lòng kiểm tra server!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(OtherActivity.this, "Lỗi: " + errorBody, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi parse phản hồi: " + e.getMessage());
                        Toast.makeText(OtherActivity.this, "Lỗi khi lấy thông tin. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối khi lấy thông tin: " + t.getMessage());
                Toast.makeText(OtherActivity.this, "Lỗi kết nối. Vui lòng kiểm tra mạng và thử lại!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}