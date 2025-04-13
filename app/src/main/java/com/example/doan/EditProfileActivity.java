package com.example.doan;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView titleProfile;
    private Button btnSave;
    private EditText editName, editGender, editBirthday;
    private ImageView genderSelector, birthdayPicker;
    private ProgressBar progressBar;
    private String token;
    private static final String DATE_PATTERN = "dd/MM/yyyy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        // Ánh xạ các view
        btnBack = findViewById(R.id.btn_back);
        titleProfile = findViewById(R.id.title_profile);
        btnSave = findViewById(R.id.btn_save);
        editName = findViewById(R.id.edit_name);
        editGender = findViewById(R.id.edit_gender);
        editBirthday = findViewById(R.id.edit_birthday);
        genderSelector = findViewById(R.id.gender_selector);
        birthdayPicker = findViewById(R.id.birthday_picker);
        progressBar = findViewById(R.id.progressBar);

        // Lấy dữ liệu từ Intent
        Intent intent = getIntent();
        token = intent.getStringExtra("token");
        String name = intent.getStringExtra("name");
        String gender = intent.getStringExtra("gender");
        String birthday = intent.getStringExtra("birthday");

        // Hiển thị dữ liệu hiện tại
        editName.setText(name);
        editGender.setText(gender);
        editBirthday.setText(birthday);

        // Xử lý nút Back
        btnBack.setOnClickListener(v -> finish());

        // Xử lý chọn giới tính
        genderSelector.setOnClickListener(v -> {
            String currentGender = editGender.getText().toString();
            if (currentGender.equals("Nam")) {
                editGender.setText("Nữ");
            } else {
                editGender.setText("Nam");
            }
        });

        // Xử lý chọn ngày sinh
        birthdayPicker.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    EditProfileActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        calendar.set(selectedYear, selectedMonth, selectedDay);
                        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN, Locale.getDefault());
                        editBirthday.setText(sdf.format(calendar.getTime()));
                    },
                    year, month, day);
            datePickerDialog.show();
        });

        // Xử lý nút Lưu
        btnSave.setOnClickListener(v -> {
            String newName = editName.getText().toString().trim();
            String newGender = editGender.getText().toString().trim();
            String newBirthday = editBirthday.getText().toString().trim();

            // Kiểm tra dữ liệu đầu vào
            if (newName.isEmpty()) {
                editName.setError("Tên không được để trống!");
                return;
            }
            if (newGender.isEmpty()) {
                editGender.setError("Giới tính không được để trống!");
                return;
            }
            if (newBirthday.isEmpty()) {
                editBirthday.setError("Ngày sinh không được để trống!");
                return;
            }

            // Hiển thị ProgressBar
            progressBar.setVisibility(View.VISIBLE);
            btnSave.setEnabled(false);

            // Tạo request để gửi lên API
            UpdateUserRequest request = new UpdateUserRequest(newName, newGender, newBirthday);

            // Gọi API để cập nhật thông tin
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
                            resultIntent.putExtra("updatedName", newName);
                            resultIntent.putExtra("updatedGender", newGender);
                            resultIntent.putExtra("updatedBirthday", newBirthday);
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        } else {
                            Toast.makeText(EditProfileActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(EditProfileActivity.this, "Lỗi khi cập nhật thông tin!", Toast.LENGTH_SHORT).show();
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