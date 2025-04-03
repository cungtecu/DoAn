package com.example.doan.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan.DatabaseHelper;
import com.example.doan.models.User;

import java.util.List;

public class Admin_User_Activity extends AppCompatActivity implements Admin_User_Adapter.OnUserActionListener {
    private EditText edtSearch, edtNameUser, edtEmailUser;
    private ImageView searchIcon;
    private Button btnAddUser;
    private TextView tvDeleteAll;
    private RecyclerView rcvUser;
    private Admin_User_Adapter userAdapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_user);

        // Ánh xạ giao diện
        edtSearch = findViewById(R.id.edt_search);
        searchIcon = findViewById(R.id.search_icon);
        edtNameUser = findViewById(R.id.edtNameUser);
        edtEmailUser = findViewById(R.id.edtEmailUser);
        btnAddUser = findViewById(R.id.btnAddUser);
        tvDeleteAll = findViewById(R.id.tv_delete_all);
        rcvUser = findViewById(R.id.rcv_user);

        // Khởi tạo cơ sở dữ liệu và adapter
        dbHelper = new DatabaseHelper(this);
        List<User> userList = dbHelper.getAllUsers();
        userAdapter = new Admin_User_Adapter(userList, this);
        rcvUser.setLayoutManager(new LinearLayoutManager(this));
        rcvUser.setAdapter(userAdapter);

        // Sự kiện tìm kiếm
        searchIcon.setOnClickListener(v -> onSearchClicked());

        // Sự kiện thêm người dùng
        btnAddUser.setOnClickListener(v -> onAddUserClicked());

        // Sự kiện xóa tất cả
        tvDeleteAll.setOnClickListener(v -> onDeleteAllClicked());
    }

    private void onSearchClicked() {
        String query = edtSearch.getText().toString().trim();
        if (!query.isEmpty()) {
            List<User> filteredList = dbHelper.searchUsers(query);
            userAdapter.updateList(filteredList);
            Toast.makeText(this, "Đã tìm kiếm: " + query, Toast.LENGTH_SHORT).show();
        } else {
            userAdapter.updateList(dbHelper.getAllUsers());
            Toast.makeText(this, "Vui lòng nhập từ khóa", Toast.LENGTH_SHORT).show();
        }
    }

    private void onAddUserClicked() {
        String name = edtNameUser.getText().toString().trim();
        String email = edtEmailUser.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên và email", Toast.LENGTH_SHORT).show();
        } else {
            User newUser = new User(name, email, null, "default123", 0, "Customer");
            if (dbHelper.addUser(newUser)) {
                userAdapter.updateList(dbHelper.getAllUsers());
                edtNameUser.setText("");
                edtEmailUser.setText("");
                Toast.makeText(this, "Đã thêm: " + name, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Email đã tồn tại", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void onDeleteAllClicked() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận")
                .setMessage("Bạn có chắc muốn xóa tất cả người dùng?")
                .setPositiveButton("Có", (dialog, which) -> {
                    dbHelper.deleteAllUsers();
                    userAdapter.updateList(dbHelper.getAllUsers());
                    Toast.makeText(this, "Đã xóa tất cả", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Không", null)
                .show();
    }

    @Override
    public void onEditUser(User user) {
        // Hiển thị dialog chỉnh sửa
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_user, null);
        EditText edtEditName = dialogView.findViewById(R.id.edt_edit_name);
        EditText edtEditEmail = dialogView.findViewById(R.id.edt_edit_email);

        edtEditName.setText(user.getName());
        edtEditEmail.setText(user.getEmail());

        new AlertDialog.Builder(this)
                .setTitle("Chỉnh sửa người dùng")
                .setView(dialogView)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    user.setName(edtEditName.getText().toString().trim());
                    user.setEmail(edtEditEmail.getText().toString().trim());
                    if (dbHelper.updateUser(user)) {
                        userAdapter.updateList(dbHelper.getAllUsers());
                        Toast.makeText(this, "Đã cập nhật", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onDeleteUser(int userId) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận")
                .setMessage("Bạn có chắc muốn xóa người dùng này?")
                .setPositiveButton("Có", (dialog, which) -> {
                    if (dbHelper.deleteUser(userId)) {
                        userAdapter.updateList(dbHelper.getAllUsers());
                        Toast.makeText(this, "Đã xóa", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Không", null)
                .show();
    }
}
