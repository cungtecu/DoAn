package com.example.doan;

import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    private ImageButton btnHome, btnCart, btnOther;
    private ImageButton currentSelectedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ánh xạ xử lý chuyển trang
        btnHome = findViewById(R.id.btn_home);
        btnCart = findViewById(R.id.cartIcon);
        btnOther = findViewById(R.id.btn_other);

        // Đặt trạng thái mặc định: Home được chọn khi mở app
        currentSelectedButton = btnHome;
        btnHome.setScaleX(1.5f);
        btnHome.setScaleY(1.5f);
        btnCart.setScaleX(1.0f);
        btnCart.setScaleY(1.0f);
        btnOther.setScaleX(1.0f);
        btnOther.setScaleY(1.0f);

        // Load fragment mặc định (Home) khi mở app
        loadFragment(new HomeFragment());

        btnHome.setOnClickListener(v -> {
            if (currentSelectedButton != btnHome) {
                updateButtonState(btnHome);
                loadFragment(new HomeFragment());
            }
        });

        btnCart.setOnClickListener(v -> {
            if (currentSelectedButton != btnCart) {
                updateButtonState(btnCart);
                loadFragment(new CartFragment());
            }
        });

        btnOther.setOnClickListener(v -> {
            if (currentSelectedButton != btnOther) {
                updateButtonState(btnOther);
                loadFragment(new OtherFragment());
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    private void updateButtonState(ImageButton selectedButton) {
        if (currentSelectedButton != null && currentSelectedButton != selectedButton) {
            // Thu nhỏ icon hiện tại với animation
            currentSelectedButton.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(200) // Thời gian animation (200ms)
                    .start();
        }

        // Phóng to icon được chọn với animation
        selectedButton.animate()
                .scaleX(1.5f)
                .scaleY(1.5f)
                .setDuration(200) // Thời gian animation (200ms)
                .start();

        currentSelectedButton = selectedButton;
    }
}