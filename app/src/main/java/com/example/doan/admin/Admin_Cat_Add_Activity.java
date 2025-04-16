package com.example.doan.admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.doan.CategoryAdapter;
import com.example.doan.R;
import com.example.doan.api.ApiService;
import com.example.doan.api.RetrofitClient;
import com.example.doan.models.Category;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Admin_Cat_Add_Activity extends AppCompatActivity implements CategoryAdapter.OnCategoryActionListener {
    private RecyclerView recyclerView;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList = new ArrayList<>();
    private EditText edtSearch, edtNameCategory;
    private ImageView searchIcon, imgCategory;
    private Button btnAddCategory, btnChooseImageCategory;
    private TextView tvDeleteAll;
    private String authToken;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int EDIT_CATEGORY_REQUEST = 2;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_category_add);

        // Lấy token từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        authToken = "Bearer " + prefs.getString("token", null);
        if (authToken == null) {
            Toast.makeText(this, "Không tìm thấy token, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Khởi tạo các view
        recyclerView = findViewById(R.id.rcv_category);
        edtSearch = findViewById(R.id.edt_search);
        searchIcon = findViewById(R.id.search_icon);
        edtNameCategory = findViewById(R.id.edtNameCategory);
        imgCategory = findViewById(R.id.imgCategory);
        btnAddCategory = findViewById(R.id.btnAddCategory);
        btnChooseImageCategory = findViewById(R.id.btnChooseImageCategory);
        tvDeleteAll = findViewById(R.id.tv_delete_all);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        categoryAdapter = new CategoryAdapter(this, categoryList, this);
        recyclerView.setAdapter(categoryAdapter);

        fetchCategories();

        // Sự kiện tìm kiếm
        searchIcon.setOnClickListener(v -> filterCategories(edtSearch.getText().toString()));

        // Sự kiện chọn ảnh
        btnChooseImageCategory.setOnClickListener(v -> openImagePicker());

        // Sự kiện thêm danh mục
        btnAddCategory.setOnClickListener(v -> addCategory());

        // Sự kiện xóa tất cả (có thể thêm API nếu cần)
        tvDeleteAll.setOnClickListener(v -> showDeleteAllDialog());
    }

    private void fetchCategories() {
        ApiService apiService = RetrofitClient.getApiService(this); // Thêm this
        Call<List<Category>> call = apiService.getAllCategories(authToken);
        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful()) {
                    categoryList.clear();
                    categoryList.addAll(response.body());
                    categoryAdapter.updateList(categoryList);
                } else {
                    Toast.makeText(Admin_Cat_Add_Activity.this, "Không thể tải danh mục: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(Admin_Cat_Add_Activity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterCategories(String query) {
        if (TextUtils.isEmpty(query)) {
            categoryAdapter.updateList(categoryList);
        } else {
            String lowerQuery = query.toLowerCase();
            List<Category> filteredList = new ArrayList<>();
            for (Category category : categoryList) {
                if ((category.getName() != null && category.getName().toLowerCase().contains(lowerQuery)) ||
                        (category.getDescription() != null && category.getDescription().toLowerCase().contains(lowerQuery))) {
                    filteredList.add(category);
                }
            }
            categoryAdapter.updateList(filteredList);
        }
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
        } else if (requestCode == EDIT_CATEGORY_REQUEST && resultCode == RESULT_OK) {
            fetchCategories(); // Tải lại danh sách sau khi chỉnh sửa
        }
    }

    private void addCategory() {
        String name = edtNameCategory.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Vui lòng nhập tên danh mục", Toast.LENGTH_SHORT).show();
            return;
        }

        Category newCategory = new Category();
        newCategory.setName(name);
        // KHÔNG đặt ID
        if (imageUri != null) {
            newCategory.setImage(imageUri.toString());
        }

        ApiService apiService = RetrofitClient.getApiService(this); // Thêm this
        Call<Category> call = apiService.createCategory(authToken, newCategory);
        call.enqueue(new Callback<Category>() {
            @Override
            public void onResponse(Call<Category> call, Response<Category> response) {
                if (response.isSuccessful()) {
                    categoryList.add(response.body());
                    categoryAdapter.updateList(categoryList);
                    clearInputs();
                    Toast.makeText(Admin_Cat_Add_Activity.this, "Thêm danh mục thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Admin_Cat_Add_Activity.this, "Lỗi khi thêm: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Category> call, Throwable t) {
                Toast.makeText(Admin_Cat_Add_Activity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearInputs() {
        edtNameCategory.setText("");
        imgCategory.setImageResource(android.R.drawable.ic_menu_gallery);
        imageUri = null;
    }

    @Override
    public void onEditClick(int categoryId) {
        Intent intent = new Intent(Admin_Cat_Add_Activity.this, Admin_Cat_Edit_Activity.class);
        intent.putExtra("CATEGORY_ID", categoryId);
        startActivityForResult(intent, EDIT_CATEGORY_REQUEST);
    }

    @Override
    public void onDeleteClick(int categoryId) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa danh mục này?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteCategory(categoryId))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteCategory(int categoryId) {
        ApiService apiService = RetrofitClient.getApiService(this); // Thêm this
        Call<Map<String, String>> call = apiService.deleteCategory(authToken, categoryId);
        call.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    categoryList.removeIf(category -> category.getId() == categoryId);
                    categoryAdapter.updateList(categoryList);
                    Toast.makeText(Admin_Cat_Add_Activity.this, "Xóa thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Admin_Cat_Add_Activity.this, "Lỗi khi xóa: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Toast.makeText(Admin_Cat_Add_Activity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteAllDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa tất cả")
                .setMessage("Bạn có chắc muốn xóa tất cả danh mục? Hành động này không thể hoàn tác.")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    Toast.makeText(this, "Chức năng xóa tất cả chưa được triển khai!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}