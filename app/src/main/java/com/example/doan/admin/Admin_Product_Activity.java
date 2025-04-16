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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.doan.ProductAdapter;
import com.example.doan.R;
import com.example.doan.api.ApiService;
import com.example.doan.api.RetrofitClient;
import com.example.doan.models.Category;
import com.example.doan.models.Product;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Admin_Product_Activity extends AppCompatActivity implements ProductAdapter.OnProductClickListener {
    private RecyclerView rcvProduct;
    private ProductAdapter productAdapter;
    private EditText edtSearch;
    private ImageView searchIcon;
    private List<Product> productList;
    private List<Category> categoryList;
    private EditText edtName, edtPrice;
    private Spinner spinnerCategory;
    private ImageView imgProduct;
    private Button btnAddProduct, btnChooseImage;
    private TextView tvDeleteAll;
    private Uri imageUri;
    private String authToken;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int EDIT_PRODUCT_REQUEST = 2;
    private boolean isProcessing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_product);

        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        authToken = "Bearer " + prefs.getString("token", null);
        if (authToken == null) {
            Toast.makeText(this, "Không tìm thấy token, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        rcvProduct = findViewById(R.id.rcv_product);
        edtName = findViewById(R.id.edtNameProduct);
        edtPrice = findViewById(R.id.edtPriceProduct);
        spinnerCategory = findViewById(R.id.spinnerSelectCat);
        imgProduct = findViewById(R.id.imgProduct);
        btnAddProduct = findViewById(R.id.btnAddProduct);
        btnChooseImage = findViewById(R.id.btnChooseImageProduct);
        tvDeleteAll = findViewById(R.id.tv_delete_all);
        edtSearch = findViewById(R.id.edt_search);
        searchIcon = findViewById(R.id.search_icon);

        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(this, productList, this);
        rcvProduct.setLayoutManager(new LinearLayoutManager(this));
        rcvProduct.setAdapter(productAdapter);

        categoryList = new ArrayList<>();
        loadCategories();
        loadProducts();

        searchIcon.setOnClickListener(v -> filterProducts(edtSearch.getText().toString()));
        btnChooseImage.setOnClickListener(v -> openImagePicker());
        btnAddProduct.setOnClickListener(v -> {
            if (!isProcessing) {
                addProduct();
            }
        });
        tvDeleteAll.setOnClickListener(v -> showDeleteAllDialog());
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
        } else if (requestCode == EDIT_PRODUCT_REQUEST && resultCode == RESULT_OK) {
            loadProducts();
        }
    }

    private void filterProducts(String query) {
        if (TextUtils.isEmpty(query)) {
            productAdapter.updateProducts(productList);
        } else {
            String lowerQuery = query.toLowerCase();
            List<Product> filteredList = new ArrayList<>();
            for (Product product : productList) {
                if ((product.getName() != null && product.getName().toLowerCase().contains(lowerQuery)) ||
                        (product.getDescription() != null && product.getDescription().toLowerCase().contains(lowerQuery))) {
                    filteredList.add(product);
                }
            }
            productAdapter.updateProducts(filteredList);
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

    private void loadCategories() {
        ApiService apiService = RetrofitClient.getApiService(this); // Thêm this
        Call<List<Category>> call = apiService.getAllCategories(authToken);
        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful()) {
                    categoryList.clear();
                    categoryList.addAll(response.body());
                    CategoryAdapter adapter = new CategoryAdapter(Admin_Product_Activity.this, categoryList);
                    spinnerCategory.setAdapter(adapter);
                } else {
                    Toast.makeText(Admin_Product_Activity.this, "Lỗi tải danh mục: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(Admin_Product_Activity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProducts() {
        ApiService apiService = RetrofitClient.getApiService(this); // Thêm this
        Call<List<Product>> call = apiService.getAllProducts(authToken);
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful()) {
                    productList.clear();
                    productList.addAll(response.body());
                    productAdapter.updateProducts(productList);
                } else {
                    Toast.makeText(Admin_Product_Activity.this, "Không thể tải danh sách sản phẩm: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(Admin_Product_Activity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addProduct() {
        if (isProcessing) return;
        isProcessing = true;
        btnAddProduct.setEnabled(false);

        String name = edtName.getText().toString().trim();
        String priceStr = edtPrice.getText().toString().trim();
        Category selectedCategory = (Category) spinnerCategory.getSelectedItem();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(priceStr) || selectedCategory == null) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            isProcessing = false;
            btnAddProduct.setEnabled(true);
            return;
        }

        if (selectedCategory.getId() <= 0) {
            Toast.makeText(this, "Danh mục không hợp lệ", Toast.LENGTH_SHORT).show();
            isProcessing = false;
            btnAddProduct.setEnabled(true);
            return;
        }

        BigDecimal price;
        try {
            price = new BigDecimal(priceStr);
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                Toast.makeText(this, "Giá phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                isProcessing = false;
                btnAddProduct.setEnabled(true);
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Giá không hợp lệ", Toast.LENGTH_SHORT).show();
            isProcessing = false;
            btnAddProduct.setEnabled(true);
            return;
        }

        Product newProduct = new Product();
        newProduct.setName(name);
        newProduct.setPrice(price);
        newProduct.setCategories(selectedCategory);

        if (imageUri != null) {
            newProduct.setImage(imageUri.toString());
        }

        ApiService apiService = RetrofitClient.getApiService(this); // Thêm this
        Call<Product> call = apiService.createProduct(authToken, newProduct);
        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Product addedProduct = response.body();
                    if (addedProduct.getId() <= 0) {
                        Toast.makeText(Admin_Product_Activity.this, "Lỗi: ID sản phẩm không hợp lệ từ server", Toast.LENGTH_LONG).show();
                    } else {
                        productList.add(addedProduct);
                        productAdapter.notifyDataSetChanged();
                        clearInputs();
                        Toast.makeText(Admin_Product_Activity.this, "Thêm sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Toast.makeText(Admin_Product_Activity.this, "Lỗi khi thêm: " + errorBody + " (Mã: " + response.code() + ")", Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        Toast.makeText(Admin_Product_Activity.this, "Lỗi khi thêm: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
                isProcessing = false;
                btnAddProduct.setEnabled(true);
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Toast.makeText(Admin_Product_Activity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                isProcessing = false;
                btnAddProduct.setEnabled(true);
            }
        });
    }

    private void deleteProduct(int productId) {
        if (isProcessing || productId <= 0) {
            Toast.makeText(this, "ID sản phẩm không hợp lệ hoặc đang xử lý", Toast.LENGTH_SHORT).show();
            return;
        }
        isProcessing = true;

        ApiService apiService = RetrofitClient.getApiService(this); // Thêm this
        Call<Map<String, String>> call = apiService.deleteProduct(authToken, productId);
        call.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    productList.removeIf(product -> product.getId() == productId);
                    productAdapter.notifyDataSetChanged();
                    Toast.makeText(Admin_Product_Activity.this, "Xóa thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Toast.makeText(Admin_Product_Activity.this, "Lỗi khi xóa: " + errorBody + " (Mã: " + response.code() + ")", Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        Toast.makeText(Admin_Product_Activity.this, "Lỗi khi xóa: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
                isProcessing = false;
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Toast.makeText(Admin_Product_Activity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                isProcessing = false;
            }
        });
    }

    private void showDeleteAllDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa tất cả")
                .setMessage("Bạn có chắc muốn xóa tất cả sản phẩm? Hành động này không thể hoàn tác.")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    Toast.makeText(this, "Chức năng xóa tất cả chưa được triển khai!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onEditClick(Product product) {
        if (product.getId() <= 0) {
            Toast.makeText(this, "ID sản phẩm không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, Admin_Product_Edit_Activity.class);
        intent.putExtra("PRODUCT_ID", product.getId());
        startActivityForResult(intent, EDIT_PRODUCT_REQUEST);
    }

    @Override
    public void onDeleteClick(Product product) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa sản phẩm này?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteProduct(product.getId()))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void clearInputs() {
        edtName.setText("");
        edtPrice.setText("");
        imgProduct.setImageResource(android.R.drawable.ic_menu_gallery);
        imageUri = null;
        if (spinnerCategory.getAdapter() != null && spinnerCategory.getAdapter().getCount() > 0) {
            spinnerCategory.setSelection(0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProducts();
    }
}