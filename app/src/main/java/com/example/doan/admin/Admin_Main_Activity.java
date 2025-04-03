package com.example.doan.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.doan.R;

public class Admin_Main_Activity extends AppCompatActivity {

    // Khai báo các biến giao diện
    private EditText edtSearch;
    private ImageView searchIcon;
    private LinearLayout adminUser, adminCat, adminPro, adminOrder, adminStatistical, adminPoint;
    private ConstraintLayout adminLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_main);

        // Ánh xạ các thành phần giao diện từ XML
        edtSearch = findViewById(R.id.edt_search);
        searchIcon = findViewById(R.id.search_icon);
        adminUser = findViewById(R.id.admin_user);
        adminCat = findViewById(R.id.admin_cat);
        adminPro = findViewById(R.id.admin_pro);
        adminOrder = findViewById(R.id.admin_order);
        adminStatistical = findViewById(R.id.admin_statistical);
        adminPoint = findViewById(R.id.admin_point);
        adminLogout = findViewById(R.id.admin_logout);

        // Xử lý sự kiện click cho icon tìm kiếm
//        searchIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String searchQuery = edtSearch.getText().toString().trim();
//                if (!searchQuery.isEmpty()) {
//                    // Giả định chuyển sang một Activity tìm kiếm với từ khóa
//                    Intent intent = new Intent(Admin_Main_Activity.this, SearchActivity.class);
//                    intent.putExtra("SEARCH_QUERY", searchQuery);
//                    startActivity(intent);
//                } else {
//                    Toast.makeText(this, "Vui lòng nhập từ khóa tìm kiếm", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

        // Xử lý sự kiện click cho các LinearLayout
        adminUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Admin_Main_Activity.this, Admin_User_Activity.class);
                startActivity(intent);
            }
        });

        adminCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Admin_Main_Activity.this, Admin_Cat_Activity.class);
                startActivity(intent);
            }
        });

        adminPro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Admin_Main_Activity.this, Admin_Product_Activity.class);
                startActivity(intent);
            }
        });

        adminOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Admin_Main_Activity.this, Admin_Order_Activity.class);
                startActivity(intent);
            }
        });

        adminStatistical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Admin_Main_Activity.this, Admin_Sta_Activity.class);
                startActivity(intent);
            }
        });

        adminPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Admin_Main_Activity.this, Admin_Point_Activity.class);
                startActivity(intent);
            }
        });

//        // Xử lý sự kiện click cho nút đăng xuất
//        adminLogout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Giả định đăng xuất và quay lại màn hình đăng nhập
//                Intent intent = new Intent(Admin_Main_Activity.this, SigninActivity.class);
//                startActivity(intent);
//                finish(); // Đóng Admin_Main_Activity
//                Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    // Hàm xử lý khi nhấn icon tìm kiếm
}
