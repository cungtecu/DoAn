package com.example.doan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";
    private ImageButton btnHome, btnCart, btnOther;
    private ImageButton currentSelectedButton;
    private static final String PREF_SELECTED_BUTTON = "selected_button";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        // Ánh xạ các nút Bottom Navigation
        btnHome = findViewById(R.id.btn_home);
        btnCart = findViewById(R.id.cartIcon);
        btnOther = findViewById(R.id.btn_other);

        // Khôi phục trạng thái nút được chọn từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String selectedButtonId = prefs.getString(PREF_SELECTED_BUTTON, "btn_home");

        // Nếu đây là MainActivity, ưu tiên chọn btn_home
        if (this instanceof MainActivity) {
            selectedButtonId = "btn_home";
            // Lưu trạng thái vào SharedPreferences
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(PREF_SELECTED_BUTTON, selectedButtonId);
            editor.apply();
        }

        // Cập nhật trạng thái ban đầu của các nút
        updateInitialButtonState(selectedButtonId);

        // Thiết lập sự kiện nhấn cho các nút
        setupBottomNavigation();
    }

    // Phương thức trừu tượng để các activity con cung cấp layout cụ thể
    protected abstract int getLayoutId();

    private void updateInitialButtonState(String selectedButtonId) {
        // Đặt kích thước mặc định cho tất cả các nút
        btnHome.setScaleX(1.0f);
        btnHome.setScaleY(1.0f);
        btnCart.setScaleX(1.0f);
        btnCart.setScaleY(1.0f);
        btnOther.setScaleX(1.0f);
        btnOther.setScaleY(1.0f);

        // Phóng to nút được chọn
        switch (selectedButtonId) {
            case "btn_home":
                currentSelectedButton = btnHome;
                btnHome.setScaleX(1.5f);
                btnHome.setScaleY(1.5f);
                break;
            case "cartIcon":
                currentSelectedButton = btnCart;
                btnCart.setScaleX(1.5f);
                btnCart.setScaleY(1.5f);
                break;
            case "btn_other":
                currentSelectedButton = btnOther;
                btnOther.setScaleX(1.5f);
                btnOther.setScaleY(1.5f);
                break;
        }
    }

    private void setupBottomNavigation() {
        btnHome.setOnClickListener(v -> navigateToActivity(MainActivity.class, btnHome, "btn_home"));
        btnCart.setOnClickListener(v -> navigateToActivity(OrderActivity.class, btnCart, "cartIcon"));
        btnOther.setOnClickListener(v -> navigateToActivity(OtherActivity.class, btnOther, "btn_other"));
    }

    private void navigateToActivity(Class<?> activityClass, ImageButton selectedButton, String buttonId) {
        // Kiểm tra nếu đang ở activity hiện tại, không làm gì
        if (this.getClass() == activityClass) {
            return;
        }

        // Lưu trạng thái nút được chọn vào SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREF_SELECTED_BUTTON, buttonId);
        editor.apply();

        // Cập nhật trạng thái nút
        updateButtonState(selectedButton);

        // Chuyển sang activity mới
        Intent intent = new Intent(this, activityClass);
        intent.putExtra("token", getSharedPreferences("MyAppPrefs", MODE_PRIVATE).getString("token", null));
        startActivity(intent);
        overridePendingTransition(R.anim.no_change, R.anim.no_change);
        finish();
    }

    private void updateButtonState(ImageButton selectedButton) {
        if (currentSelectedButton != null && currentSelectedButton != selectedButton) {
            // Thu nhỏ nút hiện tại
            currentSelectedButton.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(200)
                    .start();
        }

        // Phóng to nút được chọn
        selectedButton.animate()
                .scaleX(1.5f)
                .scaleY(1.5f)
                .setDuration(200)
                .start();

        currentSelectedButton = selectedButton;
    }
}