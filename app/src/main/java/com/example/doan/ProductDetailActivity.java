package com.example.doan;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.doan.api.RetrofitClient;
import com.example.doan.models.Product;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {

    private static final String TAG = "ProductDetailActivity";
    private ImageView backButton, productImage, quantityDecrement, quantityIncrement;
    private TextView productName, productDescription, productPrice, quantityText;
    private Button sizeSButton, sizeMButton, sizeLButton, buyNowButton;
    private int quantity = 1;
    private String selectedSize = "S"; // Mặc định là S
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_detail);

        // Ánh xạ các thành phần
        backButton = findViewById(R.id.back_button);
        productImage = findViewById(R.id.product_image);
        productName = findViewById(R.id.product_name);
        productDescription = findViewById(R.id.product_description);
        sizeSButton = findViewById(R.id.size_s_button);
        sizeMButton = findViewById(R.id.size_m_button);
        sizeLButton = findViewById(R.id.size_l_button);
        quantityText = findViewById(R.id.quantity_text);
        quantityDecrement = findViewById(R.id.quantity_decrement);
        quantityIncrement = findViewById(R.id.quantity_increment);
        productPrice = findViewById(R.id.product_price);
        buyNowButton = findViewById(R.id.buy_now_button);

        // Lấy productId từ Intent
        int productId = getIntent().getIntExtra("productId", -1);
        if (productId == -1) {
            Log.e(TAG, "Invalid productId received");
            Toast.makeText(this, "Error: Invalid product ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load dữ liệu sản phẩm từ API
        loadProductFromApi(productId);

        // Xử lý sự kiện
        backButton.setOnClickListener(v -> finish());

        // Xử lý chọn kích thước
        sizeSButton.setOnClickListener(v -> {
            selectedSize = "S";
            updateSizeButtonStyles();
        });

        sizeMButton.setOnClickListener(v -> {
            selectedSize = "M";
            updateSizeButtonStyles();
        });

        sizeLButton.setOnClickListener(v -> {
            selectedSize = "L";
            updateSizeButtonStyles();
        });

        // Xử lý tăng giảm số lượng
        quantityDecrement.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                quantityText.setText(String.valueOf(quantity));
            }
        });

        quantityIncrement.setOnClickListener(v -> {
            quantity++;
            quantityText.setText(String.valueOf(quantity));
        });

        // Xử lý nút "Buy Now"
        buyNowButton.setOnClickListener(v -> {
            if (product != null) {
                Toast.makeText(this, "Buying " + quantity + " " + product.getName() + " (Size: " + selectedSize + ")", Toast.LENGTH_SHORT).show();
            }
        });

        // Cập nhật giao diện ban đầu
        updateSizeButtonStyles();
    }

    // Load sản phẩm từ API
    private void loadProductFromApi(int productId) {
        RetrofitClient.getApiService().getProductById(productId).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful() && response.body() != null) {
                    product = response.body();
                    displayProductDetails();
                    Log.d(TAG, "Loaded product: " + product.getName());
                } else {
                    Log.e(TAG, "Failed to load product, code: " + response.code() + ", message: " + response.message());
                    Toast.makeText(ProductDetailActivity.this, "Error: Product not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Log.e(TAG, "Error loading product from API: " + t.getMessage());
                Toast.makeText(ProductDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    // Hiển thị thông tin sản phẩm
    private void displayProductDetails() {
        if (product != null) {
            // Load ảnh từ URL bằng Glide
            if (product.getImage() != null && !product.getImage().isEmpty()) {
                Glide.with(this)
                        .load(product.getImage())
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .error(android.R.drawable.ic_menu_close_clear_cancel)
                        .into(productImage);
            } else {
                productImage.setImageResource(android.R.drawable.ic_menu_gallery);
            }

            productName.setText(product.getName());
            productDescription.setText(product.getDescription());
            productPrice.setText(String.format("$%.2f", product.getPrice()));
            quantityText.setText(String.valueOf(quantity));
        }
    }

    // Cập nhật giao diện nút kích thước khi chọn
    private void updateSizeButtonStyles() {
        sizeSButton.setBackgroundResource(selectedSize.equals("S") ? R.drawable.selected_size_button : R.drawable.normal_size_button);
        sizeMButton.setBackgroundResource(selectedSize.equals("M") ? R.drawable.selected_size_button : R.drawable.normal_size_button);
        sizeLButton.setBackgroundResource(selectedSize.equals("L") ? R.drawable.selected_size_button : R.drawable.normal_size_button);
    }
}