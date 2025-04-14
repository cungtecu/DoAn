package com.example.doan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doan.api.RetrofitClient;
import com.example.doan.models.ApiResponse;
import com.example.doan.models.UpdateUserRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView titleProfile;
    private Button btnSave;
    private EditText editName, editEmail, editPhone;
    private ProgressBar progressBar;
    private String token;
    private String oldName, oldEmail, oldPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        // Ánh xạ các view
        btnBack = findViewById(R.id.btn_back);
        titleProfile = findViewById(R.id.title_profile);
        btnSave = findViewById(R.id.btn_save);
        editName = findViewById(R.id.edit_name);
        editEmail = findViewById(R.id.edit_email);
        editPhone = findViewById(R.id.edit_phone);
        progressBar = findViewById(R.id.progressBar);

        // Lấy dữ liệu từ Intent
        Intent intent = getIntent();
        token = intent.getStringExtra("token");
        oldName = intent.getStringExtra("name");
        oldEmail = intent.getStringExtra("email");
        oldPhone = intent.getStringExtra("phone");

        // Đảm bảo dữ liệu cũ không null
        oldName = oldName != null ? oldName : "";
        oldEmail = oldEmail != null ? oldEmail : "";
        oldPhone = oldPhone != null ? oldPhone : "";

        // Hiển thị dữ liệu hiện tại
        editName.setText(oldName);
        editEmail.setText(oldEmail);
        editPhone.setText(oldPhone);

        // Xử lý nút Back
        btnBack.setOnClickListener(v -> finish());

        // Xử lý nút Lưu
        btnSave.setOnClickListener(v -> {
            String newName = editName.getText().toString().trim();
            String newEmail = editEmail.getText().toString().trim();
            String newPhone = editPhone.getText().toString().trim();

            // Kiểm tra dữ liệu đầu vào: không bắt buộc nhập tất cả, nhưng nếu nhập thì phải đúng định dạng
            if (!newEmail.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                editEmail.setError("Email không hợp lệ!");
                return;
            }
            if (!newPhone.isEmpty() && !newPhone.matches("^0\\d{9}$")) {
                editPhone.setError("Số điện thoại phải có 10 số và bắt đầu bằng 0!");
                return;
            }

            // Kiểm tra xem có thay đổi nào không
            boolean hasChanges = false;
            if (!newName.equals(oldName)) {
                hasChanges = true;
            }
            if (!newEmail.equals(oldEmail)) {
                hasChanges = true;
            }
            if (!newPhone.equals(oldPhone)) {
                hasChanges = true;
            }

            if (!hasChanges) {
                Toast.makeText(EditProfileActivity.this, "Không có thay đổi để cập nhật!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra dữ liệu gửi lên API
            String nameToUpdate = newName.isEmpty() ? oldName : newName;
            String emailToUpdate = newEmail.isEmpty() ? oldEmail : newEmail;
            String phoneToUpdate = newPhone.isEmpty() ? oldPhone : newPhone;

            // Nếu dữ liệu cũ là "N/A", yêu cầu nhập giá trị mới
            if (nameToUpdate.equals("N/A") || nameToUpdate.isEmpty()) {
                editName.setError("Tên không được để trống!");
                return;
            }
            if (emailToUpdate.equals("N/A") || emailToUpdate.isEmpty()) {
                editEmail.setError("Email không được để trống!");
                return;
            }
            if (phoneToUpdate.equals("N/A") || phoneToUpdate.isEmpty()) {
                editPhone.setError("Số điện thoại không được để trống!");
                return;
            }

            // Kiểm tra định dạng email và phone cho dữ liệu gửi lên
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailToUpdate).matches()) {
                editEmail.setError("Email không hợp lệ!");
                return;
            }
            if (!phoneToUpdate.matches("^0\\d{9}$")) {
                editPhone.setError("Số điện thoại phải có 10 số và bắt đầu bằng 0!");
                return;
            }

            // Kiểm tra token
            if (token == null || token.isEmpty()) {
                Toast.makeText(EditProfileActivity.this, "Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
                // Xóa token cũ khỏi SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("token");
                editor.apply();

                Intent loginIntent = new Intent(EditProfileActivity.this, SigninActivity.class);
                loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginIntent);
                finish();
                return;
            }

            // Hiển thị ProgressBar
            progressBar.setVisibility(View.VISIBLE);
            btnSave.setEnabled(false);

            // In token để debug
            Log.d("EditProfileActivity", "Token: " + token);

            // Tạo request để gửi lên API
            UpdateUserRequest request = new UpdateUserRequest(nameToUpdate, emailToUpdate, phoneToUpdate);

            // Gọi API để cập nhật thông tin (sửa từ POST sang PUT)
            RetrofitClient.getApiService().updateUser("Bearer " + token, request).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    progressBar.setVisibility(View.GONE);
                    btnSave.setEnabled(true);

                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse apiResponse = response.body();
                        if ("success".equals(apiResponse.getStatus())) {
                            Toast.makeText(EditProfileActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            // Trả kết quả về ProfileActivity
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("name", nameToUpdate);
                            resultIntent.putExtra("email", emailToUpdate);
                            resultIntent.putExtra("phone", phoneToUpdate);
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        } else {
                            Toast.makeText(EditProfileActivity.this,
                                    apiResponse.getMessage() != null ? apiResponse.getMessage() : "Lỗi không xác định",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        try {
                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Không có thông tin lỗi";
                            Toast.makeText(EditProfileActivity.this, "Lỗi: " + errorBody, Toast.LENGTH_SHORT).show();
                            if (response.code() == 401) {
                                Toast.makeText(EditProfileActivity.this, "Phiên của bạn đã hết hạn. Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
                                // Xóa token cũ khỏi SharedPreferences
                                SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.remove("token");
                                editor.apply();

                                Intent loginIntent = new Intent(EditProfileActivity.this, SigninActivity.class);
                                loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(loginIntent);
                                finish();
                            }
                        } catch (Exception e) {
                            Toast.makeText(EditProfileActivity.this, "Lỗi xử lý phản hồi!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    btnSave.setEnabled(true);
                    Toast.makeText(EditProfileActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}