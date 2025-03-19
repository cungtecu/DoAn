package com.example.doan;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doan.MainActivity;
import com.example.doan.R;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        VideoView videoView = findViewById(R.id.Intro);

        // Đường dẫn video từ thư mục raw
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.intro;
        Uri uri = Uri.parse(videoPath);

        // Gán video vào VideoView
        videoView.setVideoURI(uri);

        // Bắt đầu phát video
        videoView.start();

        // Khi video kết thúc, chuyển sang MainActivity
        videoView.setOnCompletionListener(mp -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Đóng màn hình loading
        });
    }
}
