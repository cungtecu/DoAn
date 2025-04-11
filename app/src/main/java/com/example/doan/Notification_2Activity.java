package com.example.doan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class Notification_2Activity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_2);

        // Ánh xạ nút "TIẾP TỤC"
        Button btnContinue = findViewById(R.id.btn_continue_notification2);

        // Xử lý sự kiện khi nhấn nút
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang Activity tiếp theo (Notification2Activity)
                Intent intent = new Intent(Notification_2Activity.this, SigninActivity.class);
                startActivity(intent);
                finish(); // Đóng Activity hiện tại
            }
        });
    }
}