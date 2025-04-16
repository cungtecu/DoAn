package com.example.doan.admin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.doan.R;
import com.example.doan.api.ApiService;
import com.example.doan.api.RetrofitClient;
import com.example.doan.models.Category;
import com.example.doan.models.Product;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Admin_Product_Edit_Activity extends AppCompatActivity {

    private EditText editName, editDescription, editPrice;
    private Spinner spinnerCategory;
    private ImageView imgProduct;
    private Button btnChooseImage, btnSave, btnHisPrice;
    private List<Category> categoryList;
    private Product product;
    private Uri imageUri;
    private String authToken;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_product_edit);

        // Lấy token từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        authToken = "Bearer " + prefs.getString("token", null);
        if (authToken == null) {
            Toast.makeText(this, "Không tìm thấy token, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Khởi tạo views
        editName = findViewById(R.id.editNameProduct);
        editDescription = findViewById(R.id.editDesProduct);
        editPrice = findViewById(R.id.editPriceProduct);
        spinnerCategory = findViewById(R.id.spinnerSelectCat);
        imgProduct = findViewById(R.id.imgProduct);
        btnChooseImage = findViewById(R.id.btnChooseImageProduct);
        btnSave = findViewById(R.id.btnSaveProduct);
        btnHisPrice = findViewById(R.id.btnHistoryPrice);

        // Lấy ID sản phẩm từ Intent
        int productId = getIntent().getIntExtra("PRODUCT_ID", -1);
        if (productId == -1) {
            Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Khởi tạo category list
        categoryList = new ArrayList<>();

        // Sự kiện chọn ảnh
        btnChooseImage.setOnClickListener(v -> openImagePicker());

        // Sự kiện lưu sản phẩm
        btnSave.setOnClickListener(v -> updateProduct());

        // Sự kiện xem lịch sử giá
        btnHisPrice.setOnClickListener(v -> {
            Intent intent = new Intent(Admin_Product_Edit_Activity.this, Admin_PriceHistory_Activity.class);
            intent.putExtra("PRODUCT_ID", productId);
            intent.putExtra("PRODUCT_NAME", product != null ? product.getName() : "Sản phẩm");
            startActivity(intent);
        });

        // Load danh mục trước, sau đó load sản phẩm
        loadCategories(productId);
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
            Glide.with(this).load(imageUri).into(imgProduct);
        }
    }

    private void loadProduct(int productId) {
        ApiService apiService = RetrofitClient.getApiService(this); // Thêm this
        Call<Product> call = apiService.getProductById(authToken, productId); // Thêm authToken
        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful() && response.body() != null) {
                    product = response.body();
                    // Hiển thị thông tin sản phẩm
                    editName.setText(product.getName());
                    editDescription.setText(product.getDescription() != null ? product.getDescription() : "");
                    editPrice.setText(product.getPrice().toString());
                    if (product.getImage() != null && !product.getImage().isEmpty()) {
                        Glide.with(Admin_Product_Edit_Activity.this)
                                .load(product.getImage())
                                .placeholder(android.R.drawable.ic_menu_gallery)
                                .into(imgProduct);
                    }
                    // Chọn danh mục hiện tại
                    selectCurrentCategory();
                } else {
                    Toast.makeText(Admin_Product_Edit_Activity.this, "Lỗi tải sản phẩm: " + response.code(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Toast.makeText(Admin_Product_Edit_Activity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void selectCurrentCategory() {
        if (product != null && product.getCategories() != null && categoryList != null) {
            for (int i = 0; i < categoryList.size(); i++) {
                if (categoryList.get(i).getId() == product.getCategories().getId()) {
                    spinnerCategory.setSelection(i);
                    break;
                }
            }
        }
    }

    private class CategoryAdapter extends ArrayAdapter<Category> {
        public CategoryAdapter(Context context, List<Category> categories) {
            super(context, android.R.layout.simple_spinner_item, categories);
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getView(position, convertView, parent);
            Category category = getItem(position);
            if (category != null) {
                view.setText(category.getName());
            }
            return view;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getDropDownView(position, convertView, parent);
            Category category = getItem(position);
            if (category != null) {
                view.setText(category.getName());
            }
            return view;
        }
    }

    private void loadCategories(int productId) {
        ApiService apiService = RetrofitClient.getApiService(this); // Thêm this
        Call<List<Category>> call = apiService.getAllCategories(authToken);
        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList.clear();
                    categoryList.addAll(response.body());
                    CategoryAdapter adapter = new CategoryAdapter(Admin_Product_Edit_Activity.this, categoryList);
                    spinnerCategory.setAdapter(adapter);
                    // Sau khi danh mục được tải, load sản phẩm
                    loadProduct(productId);
                } else {
                    Toast.makeText(Admin_Product_Edit_Activity.this, "Lỗi tải danh mục: " + response.code(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(Admin_Product_Edit_Activity.this, "Lỗi tải danh mục: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void updateProduct() {
        String name = editName.getText().toString().trim();
        String description = editDescription.getText().toString().trim();
        String priceStr = editPrice.getText().toString().trim();
        Category selectedCategory = (Category) spinnerCategory.getSelectedItem();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(priceStr) || selectedCategory == null) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        BigDecimal price;
        try {
            price = new BigDecimal(priceStr);
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                Toast.makeText(this, "Giá phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Giá không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (product == null) {
            Toast.makeText(this, "Sản phẩm chưa được tải, vui lòng thử lại", Toast.LENGTH_SHORT).show();
            return;
        }

        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setCategories(selectedCategory);
        if (imageUri != null) {
            product.setImage(imageUri.toString());
        }

        saveProduct();
    }

    private void saveProduct() {
        ApiService apiService = RetrofitClient.getApiService(this);

        // Kiểm tra xem giá có thay đổi không
        BigDecimal oldPrice = product.getPrice();
        BigDecimal newPrice;
        try {
            newPrice = new BigDecimal(editPrice.getText().toString().trim());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Giá không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cập nhật sản phẩm
        Call<Product> productCall = apiService.updateProduct(authToken, product.getId(), product);
        productCall.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful()) {
                    // Nếu giá thay đổi, gọi API cập nhật lịch sử giá
                    if (!oldPrice.equals(newPrice)) {
                        Call<Void> priceHistoryCall = apiService.updatePriceHistory(authToken, product.getId(), newPrice);
                        priceHistoryCall.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (!response.isSuccessful()) {
                                    try {
                                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                                        Toast.makeText(Admin_Product_Edit_Activity.this, "Lỗi cập nhật lịch sử giá: " + errorBody, Toast.LENGTH_LONG).show();
                                    } catch (IOException e) {
                                        Toast.makeText(Admin_Product_Edit_Activity.this, "Lỗi cập nhật lịch sử giá: " + response.code(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(Admin_Product_Edit_Activity.this, "Lỗi cập nhật lịch sử giá: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    Toast.makeText(Admin_Product_Edit_Activity.this, "Cập nhật sản phẩm thành công", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Toast.makeText(Admin_Product_Edit_Activity.this, "Lỗi cập nhật: " + errorBody + " (Mã: " + response.code() + ")", Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        Toast.makeText(Admin_Product_Edit_Activity.this, "Lỗi cập nhật: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Toast.makeText(Admin_Product_Edit_Activity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}