package com.example.doan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class Notification_8Activity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_8);

        // Ánh xạ nút "TIẾP TỤC"
        Button btnContinue = findViewById(R.id.btn_continue_notification8);
        Button btnCancel = findViewById(R.id.btn_cancel);

        // Xử lý sự kiện khi nhấn nút
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Notification_8Activity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Đóng Activity hiện tại
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Notification_8Activity.this, SignupActivity.class);
                startActivity(intent);
                finish(); // Đóng Activity hiện tại
            }
        });
    }
}

