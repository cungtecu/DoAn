package com.example.doan.admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.doan.R;
import com.example.doan.api.ApiService;
import com.example.doan.api.RetrofitClient;
import com.example.doan.models.Category;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Admin_Cat_Edit_Activity extends AppCompatActivity {
    private EditText editNameCategory, editDesCategory;
    private ImageView imgCategory;
    private Button btnChooseImageCategory, btnSaveCategory;
    private String authToken;
    private int categoryId;
    private Uri imageUri;
    private Category category; // Biến để lưu danh mục gốc
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_edit_category);

        // Lấy token từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        authToken = "Bearer " + prefs.getString("token", null);
        if (authToken == null) {
            Toast.makeText(this, "Không tìm thấy token, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Khởi tạo các view
        editNameCategory = findViewById(R.id.editNameCategory);
        editDesCategory = findViewById(R.id.editDesCategory);
        imgCategory = findViewById(R.id.imgCategory);
        btnChooseImageCategory = findViewById(R.id.btnChooseImageCategory);
        btnSaveCategory = findViewById(R.id.btnSaveCategory);

        // Lấy categoryId từ Intent
        categoryId = getIntent().getIntExtra("CATEGORY_ID", -1);
        if (categoryId == -1) {
            Toast.makeText(this, "Không tìm thấy ID danh mục", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Tải thông tin danh mục
        fetchCategoryDetails(categoryId);

        // Sự kiện chọn ảnh
        btnChooseImageCategory.setOnClickListener(v -> openImagePicker());

        // Sự kiện lưu danh mục
        btnSaveCategory.setOnClickListener(v -> saveCategory());
    }

    private void fetchCategoryDetails(int categoryId) {
        ApiService apiService = RetrofitClient.getApiService();
        Call<Category> call = apiService.getCategoryById(authToken, categoryId);
        call.enqueue(new Callback<Category>() {
            @Override
            public void onResponse(Call<Category> call, Response<Category> response) {
                if (response.isSuccessful() && response.body() != null) {
                    category = response.body(); // Lưu vào biến thành viên
                    editNameCategory.setText(category.getName());
                    editDesCategory.setText(category.getDescription());
                    if (category.getImage() != null && !category.getImage().isEmpty()) {
                        Glide.with(Admin_Cat_Edit_Activity.this)
                                .load(category.getImage())
                                .placeholder(android.R.drawable.ic_menu_gallery)
                                .error(android.R.drawable.ic_menu_report_image)
                                .into(imgCategory);
                    } else {
                        imgCategory.setImageResource(android.R.drawable.ic_menu_gallery);
                    }
                } else {
                    Toast.makeText(Admin_Cat_Edit_Activity.this, "Không thể tải danh mục: " + response.code(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Category> call, Throwable t) {
                Toast.makeText(Admin_Cat_Edit_Activity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            Glide.with(this).load(imageUri).into(imgCategory);
        }
    }

    private void saveCategory() {
        String name = editNameCategory.getText().toString().trim();
        String description = editDesCategory.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Vui lòng nhập tên danh mục", Toast.LENGTH_SHORT).show();
            return;
        }

        if (category == null) {
            Toast.makeText(this, "Danh mục chưa được tải, vui lòng thử lại", Toast.LENGTH_SHORT).show();
            return;
        }

        Category updatedCategory = new Category();
        updatedCategory.setId(categoryId);
        updatedCategory.setName(name);
        updatedCategory.setDescription(description);
        // Chỉ set image nếu chọn ảnh mới, nếu không giữ nguyên ảnh hiện tại
        if (imageUri != null) {
            updatedCategory.setImage(imageUri.toString());
        } else {
            updatedCategory.setImage(category.getImage());
        }

        ApiService apiService = RetrofitClient.getApiService();
        Call<Category> call = apiService.updateCategory(authToken, categoryId, updatedCategory);
        call.enqueue(new Callback<Category>() {
            @Override
            public void onResponse(Call<Category> call, Response<Category> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(Admin_Cat_Edit_Activity.this, "Cập nhật danh mục thành công!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(Admin_Cat_Edit_Activity.this, "Lỗi khi cập nhật: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Category> call, Throwable t) {
                Toast.makeText(Admin_Cat_Edit_Activity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}