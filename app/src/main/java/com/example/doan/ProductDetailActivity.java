//package com.example.doan;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//import androidx.appcompat.app.AppCompatActivity;
//import com.bumptech.glide.Glide;
//import com.example.doan.api.RetrofitClient;
//import com.example.doan.models.Product;
//
//import java.text.NumberFormat;
//import java.util.Locale;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class ProductDetailActivity extends AppCompatActivity {
//
//    private static final String TAG = "ProductDetailActivity";
//    private ImageView backButton, productImage, quantityDecrement, quantityIncrement;
//    private TextView productName, productDescription, productPrice, quantityText;
//    private Button sizeSButton, sizeMButton, sizeLButton, buyNowButton;
//    private int quantity = 1;
//    private String selectedSize = "S"; // Mặc định là S
//    private Product product;
//    private Call<Product> productCall;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        try {
//            setContentView(R.layout.product_detail);
//        } catch (Exception e) {
//            Log.e(TAG, "Error inflating layout: " + e.getMessage(), e);
//            Toast.makeText(this, "Lỗi tải giao diện sản phẩm", Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
//
//        // Ánh xạ các thành phần
//        try {
//            backButton = findViewById(R.id.back_button);
//            productImage = findViewById(R.id.product_image);
//            productName = findViewById(R.id.product_name);
//            productDescription = findViewById(R.id.product_description);
//            sizeSButton = findViewById(R.id.size_s_button);
//            sizeMButton = findViewById(R.id.size_m_button);
//            sizeLButton = findViewById(R.id.size_l_button);
//            quantityText = findViewById(R.id.quantity_text);
//            quantityDecrement = findViewById(R.id.quantity_decrement);
//            quantityIncrement = findViewById(R.id.quantity_increment);
//            productPrice = findViewById(R.id.product_price);
//            buyNowButton = findViewById(R.id.buy_now_button);
//        } catch (Exception e) {
//            Log.e(TAG, "Error finding views: " + e.getMessage(), e);
//            Toast.makeText(this, "Lỗi khởi tạo giao diện", Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
//
//        // Lấy productId từ Intent
//        int productId = getIntent().getIntExtra("productId", -1);
//        if (productId == -1) {
//            Log.e(TAG, "Invalid productId received");
//            Toast.makeText(this, "Error: Invalid product ID", Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
//
//        // Load dữ liệu sản phẩm từ API
//        loadProductFromApi(productId);
//
//        // Xử lý sự kiện
//        backButton.setOnClickListener(v -> finish());
//
//        // Xử lý chọn kích thước
//        sizeSButton.setOnClickListener(v -> {
//            selectedSize = "S";
//            updateSizeButtonStyles();
//        });
//
//        sizeMButton.setOnClickListener(v -> {
//            selectedSize = "M";
//            updateSizeButtonStyles();
//        });
//
//        sizeLButton.setOnClickListener(v -> {
//            selectedSize = "L";
//            updateSizeButtonStyles();
//        });
//
//        // Xử lý tăng giảm số lượng
//        quantityText.setText(String.valueOf(quantity));
//        quantityDecrement.setOnClickListener(v -> {
//            if (quantity > 1) {
//                quantity--;
//                quantityText.setText(String.valueOf(quantity));
//            }
//        });
//
//        quantityIncrement.setOnClickListener(v -> {
//            quantity++;
//            quantityText.setText(String.valueOf(quantity));
//        });
//
//        // Xử lý nút "Thêm vào giỏ"
//        buyNowButton.setOnClickListener(v -> {
//            if (product != null) {
//                Toast.makeText(this, "Đã thêm " + quantity + " " + product.getName() + " (Kích thước: " + selectedSize + ") vào giỏ hàng", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, "Không thể thêm vào giỏ hàng: Sản phẩm chưa được tải", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        // Cập nhật giao diện ban đầu
//        updateSizeButtonStyles();
//    }
//
//    // Load sản phẩm từ API
//    private void loadProductFromApi(int productId) {
//        productCall = RetrofitClient.getApiService().getProductById(productId);
//        productCall.enqueue(new Callback<Product>() {
//            @Override
//            public void onResponse(Call<Product> call, Response<Product> response) {
//                if (!isFinishing()) {
//                    if (response.isSuccessful() && response.body() != null) {
//                        product = response.body();
//                        displayProductDetails();
//                        Log.d(TAG, "Loaded product: " + product.getName());
//                    } else {
//                        Log.e(TAG, "Failed to load product, code: " + response.code() + ", message: " + response.message());
//                        try {
//                            Log.e(TAG, "Error body: " + response.errorBody().string());
//                        } catch (Exception e) {
//                            Log.e(TAG, "Error parsing error body: " + e.getMessage());
//                        }
//                        if (response.code() == 401) {
//                            Toast.makeText(ProductDetailActivity.this, "Phiên đăng nhập hết hạn, vui lòng đăng nhập lại", Toast.LENGTH_LONG).show();
//                            Intent intent = new Intent(ProductDetailActivity.this, SigninActivity.class);
//                            startActivity(intent);
//                            finish();
//                        } else {
//                            Toast.makeText(ProductDetailActivity.this, "Error: Product not found", Toast.LENGTH_SHORT).show();
//                            finish();
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Product> call, Throwable t) {
//                if (!call.isCanceled() && !isFinishing()) {
//                    Log.e(TAG, "Error loading product from API: " + t.getMessage());
//                    Toast.makeText(ProductDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//                    finish();
//                }
//            }
//        });
//    }
//
//    // Hiển thị thông tin sản phẩm
//    private void displayProductDetails() {
//        if (product != null) {
//            // Load ảnh từ URL bằng Glide
//            if (product.getImage() != null && !product.getImage().isEmpty()) {
//                Glide.with(this)
//                        .load(product.getImage())
//                        .placeholder(android.R.drawable.ic_menu_gallery)
//                        .error(android.R.drawable.ic_menu_close_clear_cancel)
//                        .into(productImage);
//            } else {
//                productImage.setImageResource(android.R.drawable.ic_menu_gallery);
//            }
//
//            productName.setText(product.getName() != null ? product.getName() : "N/A");
//            productDescription.setText(product.getDescription() != null ? product.getDescription() : "Không có mô tả");
//            // Định dạng giá tiền với locale vi_VN
//            try {
//                double price = product.getPrice();
//                NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
//                numberFormat.setMinimumFractionDigits(0); // Không hiển thị số thập phân
//                numberFormat.setMaximumFractionDigits(0);
//                productPrice.setText(numberFormat.format(price) + " VNĐ");
//            } catch (Exception e) {
//                Log.e(TAG, "Error formatting price: " + e.getMessage());
//                productPrice.setText("N/A");
//            }
//            quantityText.setText(String.valueOf(quantity));
//        }
//    }
//
//    // Cập nhật giao diện nút kích thước khi chọn
//    private void updateSizeButtonStyles() {
//        sizeSButton.setBackgroundResource(selectedSize.equals("S") ? R.drawable.selected_size_button : R.drawable.normal_size_button);
//        sizeMButton.setBackgroundResource(selectedSize.equals("M") ? R.drawable.selected_size_button : R.drawable.normal_size_button);
//        sizeLButton.setBackgroundResource(selectedSize.equals("L") ? R.drawable.selected_size_button : R.drawable.normal_size_button);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (productCall != null && !productCall.isCanceled()) {
//            productCall.cancel();
//        }
//    }
//}

package com.example.doan;

import android.content.Intent;
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
import java.text.NumberFormat;
import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {

    private static final String TAG = "ProductDetailActivity";
    private ImageView backButton, productImage, quantityDecrement, quantityIncrement;
    private TextView productName, productDescription, productPrice, quantityText;
    private Button sizeSButton, sizeMButton, sizeLButton, buyNowButton;
    private int quantity = 1;
    private String selectedSize = "S"; // Mặc định là S
    private Product product;
    private Call<Product> productCall;
    private boolean isImageZoomed = false; // Theo dõi trạng thái phóng to
    private double basePrice; // Giá gốc của sản phẩm (size S)
    private double displayPrice; // Giá hiển thị (sẽ thay đổi theo size)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.product_detail);
        } catch (Exception e) {
            Log.e(TAG, "Error inflating layout: " + e.getMessage(), e);
            Toast.makeText(this, "Lỗi tải giao diện sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Ánh xạ các thành phần
        try {
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
        } catch (Exception e) {
            Log.e(TAG, "Error finding views: " + e.getMessage(), e);
            Toast.makeText(this, "Lỗi khởi tạo giao diện", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

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

        // Thêm sự kiện click để phóng to hình ảnh
        productImage.setOnClickListener(v -> {
            if (!isImageZoomed) {
                // Phóng to 4 lần
                productImage.setScaleX(4f);
                productImage.setScaleY(4f);
                isImageZoomed = true;
            } else {
                // Khôi phục kích thước ban đầu
                productImage.setScaleX(1f);
                productImage.setScaleY(1f);
                isImageZoomed = false;
            }
        });

        // Xử lý chọn kích thước
        sizeSButton.setOnClickListener(v -> {
            selectedSize = "S";
            updatePriceAndDisplay();
            updateSizeButtonStyles();
        });

        sizeMButton.setOnClickListener(v -> {
            selectedSize = "M";
            updatePriceAndDisplay();
            updateSizeButtonStyles();
        });

        sizeLButton.setOnClickListener(v -> {
            selectedSize = "L";
            updatePriceAndDisplay();
            updateSizeButtonStyles();
        });

        // Xử lý tăng giảm số lượng
        quantityText.setText(String.valueOf(quantity));
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

        // Xử lý nút "Thêm vào giỏ"
        buyNowButton.setOnClickListener(v -> {
            if (product != null) {
                addToCart();
            } else {
                Toast.makeText(this, "Không thể thêm vào giỏ hàng: Sản phẩm chưa được tải", Toast.LENGTH_SHORT).show();
            }
        });

        // Cập nhật giao diện ban đầu
        updateSizeButtonStyles();
    }

    // Load sản phẩm từ API
    private void loadProductFromApi(int productId) {
        productCall = RetrofitClient.getApiService().getProductById(productId);
        productCall.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (!isFinishing()) {
                    if (response.isSuccessful() && response.body() != null) {
                        product = response.body();
                        // Lưu giá gốc (size S)
                        basePrice = product.getPrice();
                        displayPrice = basePrice; // Giá ban đầu là giá của size S
                        displayProductDetails();
                        Log.d(TAG, "Loaded product: " + product.getName());
                    } else {
                        Log.e(TAG, "Failed to load product, code: " + response.code() + ", message: " + response.message());
                        try {
                            Log.e(TAG, "Error body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing error body: " + e.getMessage());
                        }
                        if (response.code() == 401) {
                            Toast.makeText(ProductDetailActivity.this, "Phiên đăng nhập hết hạn, vui lòng đăng nhập lại", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(ProductDetailActivity.this, SigninActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(ProductDetailActivity.this, "Error: Product not found", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                if (!call.isCanceled() && !isFinishing()) {
                    Log.e(TAG, "Error loading product from API: " + t.getMessage());
                    Toast.makeText(ProductDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    // Cập nhật giá dựa trên size và hiển thị
    private void updatePriceAndDisplay() {
        if (product == null) return;

        // Tính giá dựa trên size
        displayPrice = basePrice; // Giá gốc (size S)
        if (selectedSize.equals("M")) {
            displayPrice = basePrice + 5000; // Size M: +5,000 VNĐ
        } else if (selectedSize.equals("L")) {
            displayPrice = basePrice + 10000; // Size L: +10,000 VNĐ
        }

        // Cập nhật giá hiển thị
        NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        numberFormat.setMinimumFractionDigits(0);
        numberFormat.setMaximumFractionDigits(0);
        productPrice.setText(numberFormat.format(displayPrice) + " VNĐ");
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

            productName.setText(product.getName() != null ? product.getName() : "N/A");
            productDescription.setText(product.getDescription() != null ? product.getDescription() : "Không có mô tả");
            // Hiển thị giá tiền (sẽ được cập nhật lại khi chọn size)
            updatePriceAndDisplay();
            quantityText.setText(String.valueOf(quantity));
        }
    }

    // Cập nhật giao diện nút kích thước khi chọn
    private void updateSizeButtonStyles() {
        sizeSButton.setBackgroundResource(selectedSize.equals("S") ? R.drawable.selected_size_button : R.drawable.normal_size_button);
        sizeMButton.setBackgroundResource(selectedSize.equals("M") ? R.drawable.selected_size_button : R.drawable.normal_size_button);
        sizeLButton.setBackgroundResource(selectedSize.equals("L") ? R.drawable.selected_size_button : R.drawable.normal_size_button);
    }

    // Thêm sản phẩm vào giỏ hàng
    private void addToCart() {
        // Tạo đối tượng để gửi lên server
        // Giả sử bạn có một API để thêm vào giỏ hàng: POST /api/cart/add
        // Dữ liệu cần gửi: productId, size, quantity, price
        int productId = product.getId();
        String size = selectedSize;
        int qty = quantity;
        double price = displayPrice; // Giá đã điều chỉnh theo size

        // Gửi yêu cầu lên server (ví dụ)
        Toast.makeText(this, "Đã thêm " + quantity + " " + product.getName() + " (Kích thước: " + selectedSize + ", Giá: " + NumberFormat.getNumberInstance(new Locale("vi", "VN")).format(displayPrice) + " VNĐ) vào giỏ hàng", Toast.LENGTH_LONG).show();

        // Gọi API để thêm vào giỏ hàng (bạn cần tạo API này trong backend)
        // Ví dụ:
        /*
        RetrofitClient.getApiService(this).addToCart(productId, size, qty, price).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProductDetailActivity.this, "Đã thêm vào giỏ hàng thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Failed to add to cart, code: " + response.code());
                    try {
                        Log.e(TAG, "Error body: " + response.errorBody().string());
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error body: " + e.getMessage());
                    }
                    Toast.makeText(ProductDetailActivity.this, "Không thể thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error adding to cart: " + t.getMessage());
                Toast.makeText(ProductDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        */
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (productCall != null && !productCall.isCanceled()) {
            productCall.cancel();
        }
    }
}