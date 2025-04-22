package com.example.doan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.doan.api.RetrofitClient;
import com.example.doan.models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ImageView userIcon, searchIcon;
    private ImageButton btnHome, btnCart, btnOther;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ánh xạ các view
        userIcon = findViewById(R.id.user_icon);
        searchIcon = findViewById(R.id.search_icon);
        btnHome = findViewById(R.id.btn_home);
        btnCart = findViewById(R.id.cartIcon);
        btnOther = findViewById(R.id.btn_other);

        // Lấy token từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        token = sharedPreferences.getString("token", null);

        if (token == null || token.isEmpty()) {
            Log.e(TAG, "Token không tồn tại hoặc rỗng");
            Toast.makeText(this, "Lỗi: Token không hợp lệ. Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            Intent signinIntent = new Intent(MainActivity.this, SigninActivity.class);
            startActivity(signinIntent);
            finish();
            return;
        }

        // Highlight nút hiện tại (MainActivity)
        highlightCurrentPage();

        // Load HomeFragment ngay khi khởi động
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new HomeFragment());
            transaction.commit();
        }

        // Xử lý sự kiện nhấn vào user_icon
        userIcon.setOnClickListener(v -> {
            Log.d(TAG, "User icon được nhấn");
            fetchUserProfile(token);
        });

        // Xử lý sự kiện nhấn vào search_icon
        searchIcon.setOnClickListener(v -> onSearchClicked(v));

        // Xử lý sự kiện nhấn btn_home
        btnHome.setOnClickListener(v -> {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new HomeFragment());
            transaction.commit();
        });

        // Xử lý sự kiện nhấn btn_cart
        btnCart.setOnClickListener(v -> {
            Intent orderIntent = new Intent(MainActivity.this, OrderActivity.class);
            orderIntent.putExtra("token", token);
            startActivity(orderIntent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
        });

        // Xử lý sự kiện nhấn btn_other
        btnOther.setOnClickListener(v -> {
            Intent otherIntent = new Intent(MainActivity.this, OtherActivity.class);
            startActivity(otherIntent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
        });
    }

    private void fetchUserProfile(String token) {
        Log.d(TAG, "Gửi yêu cầu lấy thông tin người dùng với token: " + token);
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Token không hợp lệ. Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            Intent signinIntent = new Intent(MainActivity.this, SigninActivity.class);
            startActivity(signinIntent);
            finish();
            return;
        }

        // Gọi API để lấy thông tin người dùng
        RetrofitClient.getApiService(this).getCurrentUser(token).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.d(TAG, "Mã phản hồi: " + response.code() + ", URL: " + call.request().url());
                if (response.isSuccessful()) {
                    Log.d(TAG, "Phản hồi thành công từ server");
                    User user = response.body();
                    if (user != null) {
                        // Log thông tin người dùng
                        Log.d(TAG, "Thông tin người dùng: id=" + user.getId() +
                                ", name=" + user.getName() +
                                ", phone=" + user.getPhone() +
                                ", email=" + user.getEmail() +
                                ", points=" + user.getPoints() +
                                ", role=" + user.getRole());

                        // Kiểm tra dữ liệu trước khi chuyển
                        if (user.getName() != null && user.getPhone() != null && user.getEmail() != null) {
                            // Chuyển hướng đến ProfileActivity và truyền dữ liệu
                            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                            intent.putExtra("name", user.getName());
                            intent.putExtra("phone", user.getPhone());
                            intent.putExtra("email", user.getEmail());
                            intent.putExtra("token", token);
                            intent.putExtra("points", user.getPoints());
                            intent.putExtra("role", user.getRole());
                            startActivity(intent);
                            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        } else {
                            Log.e(TAG, "Dữ liệu người dùng không đầy đủ: name=" + user.getName() +
                                    ", phone=" + user.getPhone() +
                                    ", email=" + user.getEmail());
                            Toast.makeText(MainActivity.this, "Lỗi: Dữ liệu người dùng không đầy đủ", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "User không hợp lệ");
                        Toast.makeText(MainActivity.this, "Lỗi: Phản hồi từ server không hợp lệ", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorBody = "";
                    try {
                        errorBody = response.errorBody() != null ? response.errorBody().string() : "Không có thông tin lỗi";
                        Log.e(TAG, "Error body: " + errorBody);
                    } catch (IOException e) {
                        Log.e(TAG, "Lỗi parse error body: " + e.getMessage());
                    }
                    Log.e(TAG, "Lấy thông tin thất bại, mã lỗi: " + response.code());
                    if (response.code() == 401) {
                        Toast.makeText(MainActivity.this, "Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
                        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove("token");
                        editor.apply();
                        Intent signinIntent = new Intent(MainActivity.this, SigninActivity.class);
                        signinIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(signinIntent);
                        finish();
                    } else if (response.code() == 404) {
                        Toast.makeText(MainActivity.this, "Không tìm thấy API. Vui lòng kiểm tra server!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Lỗi: " + errorBody, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối khi lấy thông tin: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Lỗi kết nối. Vui lòng kiểm tra mạng và thử lại!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Phương thức xử lý sự kiện nhấn search_icon
    public void onSearchClicked(View view) {
        Toast.makeText(this, "Chuyển sang màn hình tìm kiếm", Toast.LENGTH_SHORT).show();
        // TODO: Thêm logic chuyển sang màn hình tìm kiếm nếu cần
    }

    private void highlightCurrentPage() {
        btnHome.setBackgroundColor(Color.GRAY);
        YoYo.with(Techniques.Pulse).duration(500).playOn(btnHome);
    }
}