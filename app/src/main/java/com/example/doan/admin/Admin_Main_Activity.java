package com.example.doan.admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.doan.R;
import com.example.doan.SigninActivity;

public class Admin_Main_Activity extends AppCompatActivity {
    private LinearLayout adminUser, adminCat, adminPro, adminOrder, adminStatistical, adminPoint;
    private ConstraintLayout adminLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_main);

        // Ánh xạ các thành phần giao diện từ XML
        adminUser = findViewById(R.id.admin_user);
        adminCat = findViewById(R.id.admin_cat);
        adminPro = findViewById(R.id.admin_pro);
        adminOrder = findViewById(R.id.admin_order);
        adminStatistical = findViewById(R.id.admin_statistical);
        adminPoint = findViewById(R.id.admin_point);
        adminLogout = findViewById(R.id.admin_logout);

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
                Intent intent = new Intent(Admin_Main_Activity.this, Admin_SystemConfig_Activity.class);
                startActivity(intent);
            }
        });

        // Xử lý sự kiện click cho nút đăng xuất
        adminLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo AlertDialog để xác nhận
                AlertDialog.Builder builder = new AlertDialog.Builder(Admin_Main_Activity.this);
                builder.setTitle("Xác nhận đăng xuất");
                builder.setMessage("Bạn có chắc muốn đăng xuất không?");
                builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Thực hiện đăng xuất khi nhấn "Đồng ý"
                        Intent intent = new Intent(Admin_Main_Activity.this, SigninActivity.class);
                        startActivity(intent);
                        finish(); // Đóng Admin_Main_Activity
                        Toast.makeText(Admin_Main_Activity.this, "Đã đăng xuất!", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Thoát", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Đóng dialog khi nhấn "Thoát" (không làm gì cả)
                        dialog.dismiss();
                    }
                });
                builder.setCancelable(false); // Không cho phép thoát dialog bằng nút back
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
}