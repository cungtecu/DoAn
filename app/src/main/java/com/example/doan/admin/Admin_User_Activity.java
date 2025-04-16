package com.example.doan.admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.doan.R;
import com.example.doan.api.RetrofitClient;
import com.example.doan.models.UserAdapter;
import com.example.doan.api.ApiService;
import com.example.doan.models.UserSummaryDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Admin_User_Activity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<UserSummaryDTO> userList = new ArrayList<>();
    private List<UserSummaryDTO> filteredUserList = new ArrayList<>();
    private EditText edtSearch;
    private ImageView searchIcon;
    private String authToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_user);

        // Lấy token từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        authToken = "Bearer " + prefs.getString("token", null);
        if (authToken == null) {
            Toast.makeText(this, "Không tìm thấy token, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        recyclerView = findViewById(R.id.rcv_user);
        edtSearch = findViewById(R.id.edt_search);
        searchIcon = findViewById(R.id.search_icon);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserAdapter(filteredUserList, this::deleteUser, this::showUserDetails);
        recyclerView.setAdapter(userAdapter);

        fetchUsers();
        searchIcon.setOnClickListener(v -> filterUsers(edtSearch.getText().toString()));
    }

    private void showUserDetails(UserSummaryDTO user) {
        // Chuyển sang Admin_User_Detail_Activity
        Intent intent = new Intent(Admin_User_Activity.this, Admin_User_Detail_Activity.class);
        intent.putExtra("USER_ID", user.getId());
        startActivity(intent);
    }

    private void fetchUsers() {
        ApiService apiService = RetrofitClient.getApiService(this); // Thêm this
        Call<List<UserSummaryDTO>> call = apiService.getUsers(authToken);
        call.enqueue(new Callback<List<UserSummaryDTO>>() {
            @Override
            public void onResponse(Call<List<UserSummaryDTO>> call, Response<List<UserSummaryDTO>> response) {
                if (response.isSuccessful()) {
                    userList = response.body();
                    filteredUserList = new ArrayList<>(userList);
                    userAdapter.updateList(filteredUserList);
                } else {
                    Toast.makeText(Admin_User_Activity.this, "Không thể tải dữ liệu: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<UserSummaryDTO>> call, Throwable t) {
                Toast.makeText(Admin_User_Activity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterUsers(String query) {
        if (TextUtils.isEmpty(query)) {
            filteredUserList = new ArrayList<>(userList);
        } else {
            String lowerQuery = query.toLowerCase();
            filteredUserList = userList.stream()
                    .filter(user -> {
                        boolean matchesName = user.getName() != null && user.getName().toLowerCase().contains(lowerQuery);
                        boolean matchesEmail = user.getEmail() != null && user.getEmail().toLowerCase().contains(lowerQuery);
                        boolean matchesPhone = user.getPhone() != null && user.getPhone().toLowerCase().contains(lowerQuery);
                        return matchesName || matchesEmail || matchesPhone;
                    })
                    .collect(Collectors.toList());
        }
        userAdapter.updateList(filteredUserList);
    }

    private void deleteUser(UserSummaryDTO user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Admin_User_Activity.this);
        builder.setTitle("Xác nhận xóa tài khoản");
        builder.setMessage("Bạn có chắc muốn xóa tài khoản " + user.getName() + " không?");
        builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ApiService apiService = RetrofitClient.getApiService(Admin_User_Activity.this); // Thêm this
                Call<Void> call = apiService.deleteUser(authToken, user.getId());
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            userList.remove(user);
                            filteredUserList = new ArrayList<>(userList);
                            userAdapter.updateList(filteredUserList);
                            new AlertDialog.Builder(Admin_User_Activity.this)
                                    .setTitle("Thông báo")
                                    .setMessage("Tài khoản " + user.getName() + " đã được xóa thành công!")
                                    .setPositiveButton("OK", (d, w) -> d.dismiss())
                                    .setCancelable(false)
                                    .show();
                        } else {
                            Toast.makeText(Admin_User_Activity.this, "Không thể xóa tài khoản: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(Admin_User_Activity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.setCancelable(false);
        builder.create().show();
    }

    public void onSearchClicked(View view) {
        filterUsers(edtSearch.getText().toString());
    }
}