package com.example.doan.admin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan.R;

import java.util.ArrayList;
import java.util.List;

public class Admin_Cat_Activity extends AppCompatActivity {

    // Khai báo các biến giao diện
    private EditText edtSearch, edtNameCategory;
    private ImageView searchIcon, imgCategory;
    private Button btnChooseImageCategory, btnAddCategory;
    private TextView tvDeleteAll;
    private RecyclerView rcvCategory;
//    private CategoryAdapter categoryAdapter; // Giả định có một Adapter cho RecyclerView
//    private List<Category> categoryList; // Giả định có một lớp Category để lưu dữ liệu
    private static final int PICK_IMAGE_REQUEST = 1; // Mã yêu cầu chọn ảnh
    private Uri imageUri; // Lưu đường dẫn ảnh được chọn

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_category); // Liên kết với layout XML

        // Ánh xạ các thành phần giao diện từ XML
        edtSearch = findViewById(R.id.edt_search);
        searchIcon = findViewById(R.id.search_icon);
        edtNameCategory = findViewById(R.id.edtNameCategory);
        imgCategory = findViewById(R.id.imgCategory);
        btnChooseImageCategory = findViewById(R.id.btnChooseImageCategory);
        btnAddCategory = findViewById(R.id.btnAddCategory);
        tvDeleteAll = findViewById(R.id.tv_delete_all);
        rcvCategory = findViewById(R.id.rcv_category);

        // Khởi tạo danh sách danh mục và Adapter
//        categoryList = new ArrayList<>();
//        categoryAdapter = new CategoryAdapter(categoryList); // Giả định CategoryAdapter đã được tạo
        rcvCategory.setLayoutManager(new LinearLayoutManager(this));
//        rcvCategory.setAdapter(categoryAdapter);

        // Xử lý sự kiện click cho icon tìm kiếm
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearchClicked(v);
            }
        });

        // Xử lý sự kiện click cho nút "Chọn Ảnh"
        btnChooseImageCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChooseImageClicked(v);
            }
        });

        // Xử lý sự kiện click cho nút "Thêm"
        btnAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddCategoryClicked(v);
            }
        });

        // Xử lý sự kiện click cho "Xoá Tất Cả"
        tvDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeleteAllClicked(v);
            }
        });
    }

    // Hàm xử lý khi nhấn icon tìm kiếm
    public void onSearchClicked(View view) {
        String searchQuery = edtSearch.getText().toString().trim();
        if (!searchQuery.isEmpty()) {
            // Lọc danh sách danh mục dựa trên từ khóa (giả định)
//            List<Category> filteredList = new ArrayList<>();
//            for (Category category : categoryList) {
//                if (category.getName().toLowerCase().contains(searchQuery.toLowerCase())) {
//                    filteredList.add(category);
//                }
//            }
//            categoryAdapter.updateList(filteredList); // Cập nhật RecyclerView
            Toast.makeText(this, "Đã tìm kiếm: " + searchQuery, Toast.LENGTH_SHORT).show();
        } else {
            // Nếu không nhập từ khóa, hiển thị toàn bộ danh sách
//            categoryAdapter.updateList(categoryList);
            Toast.makeText(this, "Vui lòng nhập từ khóa tìm kiếm", Toast.LENGTH_SHORT).show();
        }
    }

    // Hàm xử lý khi nhấn nút "Chọn Ảnh"
    public void onChooseImageClicked(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Xử lý kết quả sau khi chọn ảnh
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imgCategory.setImageURI(imageUri); // Hiển thị ảnh đã chọn
        }
    }

    // Hàm xử lý khi nhấn nút "Thêm"
    public void onAddCategoryClicked(View view) {
        String name = edtNameCategory.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên danh mục", Toast.LENGTH_SHORT).show();
        } else if (imageUri == null) {
            Toast.makeText(this, "Vui lòng chọn ảnh cho danh mục", Toast.LENGTH_SHORT).show();
        } else {
            // Thêm danh mục mới vào danh sách (giả định)
//            Category newCategory = new Category(name, imageUri.toString()); // Giả định lớp Category có constructor này
//            categoryList.add(newCategory);
//            categoryAdapter.notifyDataSetChanged(); // Cập nhật RecyclerView
            // Xóa nội dung trong các ô nhập liệu và ảnh
            edtNameCategory.setText("");
            imgCategory.setImageURI(null);
            imageUri = null;
            Toast.makeText(this, "Đã thêm danh mục: " + name, Toast.LENGTH_SHORT).show();
        }
    }

    // Hàm xử lý khi nhấn "Xoá Tất Cả"
    public void onDeleteAllClicked(View view) {
//        if (categoryList.isEmpty()) {
//            Toast.makeText(this, "Danh sách danh mục trống", Toast.LENGTH_SHORT).show();
//        } else {
//            categoryList.clear(); // Xóa toàn bộ danh sách
////            categoryAdapter.notifyDataSetChanged(); // Cập nhật RecyclerView
//            Toast.makeText(this, "Đã xóa tất cả danh mục", Toast.LENGTH_SHORT).show();
//        }
    }
}


