package com.example.doan;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.doan.models.Product;

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

        // Load dữ liệu sản phẩm từ cơ sở dữ liệu
        product = loadProductFromDatabase(productId);
        if (product == null) {
            Log.e(TAG, "Product not found for ID: " + productId);
            Toast.makeText(this, "Error: Product not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Gán dữ liệu vào giao diện
        productImage.setImageResource(product.getImageResId());
        productName.setText(product.getName());
        productDescription.setText(product.getDescription());
        productPrice.setText(String.format("$%.2f", product.getPrice()));

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
            Toast.makeText(this, "Buying " + quantity + " " + productName.getText() + " (Size: " + selectedSize + ")", Toast.LENGTH_SHORT).show();
        });

        // Cập nhật giao diện ban đầu
        updateSizeButtonStyles();
    }

    // Load sản phẩm từ cơ sở dữ liệu
    private Product loadProductFromDatabase(int productId) {
        Product product = null;
        try {
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT id, name, description, price, image, categoryId, isDeleted FROM products WHERE id = ? AND isDeleted = 0", new String[]{String.valueOf(productId)});
            if (cursor != null && cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
                int imageResId = cursor.getInt(cursor.getColumnIndexOrThrow("image"));
                int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow("categoryId"));
                int isDeleted = cursor.getInt(cursor.getColumnIndexOrThrow("isDeleted"));
                product = new Product(id, name, description, price, imageResId, categoryId, isDeleted);
                cursor.close();
            }
            db.close();
        } catch (Exception e) {
            Log.e(TAG, "Error loading product from database: " + e.getMessage());
        }
        return product;
    }

    // Cập nhật giao diện nút kích thước khi chọn
    private void updateSizeButtonStyles() {
        sizeSButton.setBackgroundResource(selectedSize.equals("S") ? R.drawable.selected_size_button: R.drawable.normal_size_button);
        sizeMButton.setBackgroundResource(selectedSize.equals("M") ? R.drawable.selected_size_button : R.drawable.normal_size_button);
        sizeLButton.setBackgroundResource(selectedSize.equals("L") ? R.drawable.selected_size_button : R.drawable.normal_size_button);
    }
}