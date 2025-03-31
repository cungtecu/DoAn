package com.example.doan;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.doan.R;

public class OtherActivity extends AppCompatActivity {
    private ImageButton btnHome, btnCart, btnOther;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);

        btnHome = findViewById(R.id.btn_home);
        btnCart = findViewById(R.id.cartIcon);
        btnOther = findViewById(R.id.btn_other);

        highlightCurrentPage();

        btnHome.setOnClickListener(view -> {
            Intent intent = new Intent(OtherActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
        });

        btnCart.setOnClickListener(view -> {
            Intent intent = new Intent(OtherActivity.this, CartActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
        });

        btnOther.setOnClickListener(view -> {
            // Không cần chuyển vì đã ở OtherActivity
        });
    }

    private void highlightCurrentPage() {
        btnOther.setBackgroundColor(Color.GRAY);
        YoYo.with(Techniques.Pulse).duration(500).playOn(btnOther);
    }
}