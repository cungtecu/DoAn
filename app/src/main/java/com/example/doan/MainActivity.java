package com.example.doan;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doan.DatabaseHelper;
import com.example.doan.R;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo DatabaseHelper
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // In đường dẫn database để kiểm tra
        String dbPath = db.getPath();
        Log.d("Database Path", "Database stored at: " + dbPath);
    }
}
