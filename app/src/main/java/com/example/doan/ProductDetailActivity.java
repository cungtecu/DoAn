package com.example.doan;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.doan.models.CartAddRequest;
import com.example.doan.models.CartDTO;
import com.example.doan.models.Product;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {

    private static final String TAG = "ProductDetailActivity";
    private static final String PREFS_NAME = "MyAppPrefs";
    private static final int SIGNIN_REQUEST_CODE = 1001;
    private ImageView backButton, productImage, quantityDecrement, quantityIncrement;
    private TextView productName, productDescription, productPrice, quantityText;
    private Button sizeSButton, sizeMButton, sizeLButton, buyNowButton;
    private int quantity = 1;
    private String selectedSize = "S";
    private Product product;

    private String authToken;
    private Call<Product> productCall;
    private Call<CartDTO> addToCartCall;
    private boolean isImageZoomed = false;
    private BigDecimal basePrice;
    private BigDecimal displayPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        authToken = "Bearer " + prefs.getString("token", null);
        if (authToken == null) {
            Toast.makeText(this, "Không tìm thấy token, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        try {
            setContentView(R.layout.product_detail);
        } catch (Exception e) {
            Log.e(TAG, "Error inflating layout: " + e.getMessage(), e);
            Toast.makeText(this, "Lỗi tải giao diện sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

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

        int productId = getIntent().getIntExtra("productId", -1);
        if (productId == -1) {
            Log.e(TAG, "Invalid productId received");
            Toast.makeText(this, "Error: Invalid product ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Kiểm tra đăng nhập trước khi tải sản phẩm
        if (isLoggedIn()) {
            loadProductFromApi(productId);
        } else {
            redirectToSignin();
        }

        backButton.setOnClickListener(v -> finish());

        productImage.setOnClickListener(v -> {
            if (!isImageZoomed) {
                productImage.setScaleX(4f);
                productImage.setScaleY(4f);
                isImageZoomed = true;
            } else {
                productImage.setScaleX(1f);
                productImage.setScaleY(1f);
                isImageZoomed = false;
            }
        });

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

        quantityText.setText(String.valueOf(quantity));
        quantityDecrement.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                quantityText.setText(String.valueOf(quantity));
                updatePriceAndDisplay();
            }
        });

        quantityIncrement.setOnClickListener(v -> {
            quantity++;
            quantityText.setText(String.valueOf(quantity));
            updatePriceAndDisplay();
        });

        buyNowButton.setOnClickListener(v -> {
            if (product != null) {
                if (isLoggedIn()) {
                    addToCart();
                } else {
                    redirectToSignin();
                }
            } else {
                Toast.makeText(this, "Không thể thêm vào giỏ hàng: Sản phẩm chưa được tải", Toast.LENGTH_SHORT).show();
            }
        });

        updateSizeButtonStyles();
    }

    private boolean isLoggedIn() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String token = prefs.getString("token", null);
        return token != null;
    }

    private void redirectToSignin() {
        Intent intent = new Intent(this, SigninActivity.class);
        startActivityForResult(intent, SIGNIN_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGNIN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                int productId = getIntent().getIntExtra("productId", -1);
                if (productId != -1) {
                    loadProductFromApi(productId);
                }
            } else {
                finish();
            }
        }
    }

    private void loadProductFromApi(int productId) {
        productCall = RetrofitClient.getApiService(this).getProductById(productId);
        productCall.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (!isFinishing()) {
                    if (response.isSuccessful() && response.body() != null) {
                        product = response.body();
                        basePrice = product.getPrice();
                        displayPrice = basePrice;
                        displayProductDetails();
                        Log.d(TAG, "Loaded product: " + product.getName());
                    } else {
                        Log.e(TAG, "Failed to load product, code: " + response.code());
                        try {
                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Không có thông tin lỗi";
                            Log.e(TAG, "Error body: " + errorBody);
                            if (response.code() == 401) {
                                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                                prefs.edit().remove("token").apply();
                                Toast.makeText(ProductDetailActivity.this, "Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
                                redirectToSignin();
                            } else {
                                Toast.makeText(ProductDetailActivity.this, "Lỗi tải sản phẩm: " + errorBody, Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        } catch (IOException e) {
                            Log.e(TAG, "Error parsing error body: " + e.getMessage());
                            Toast.makeText(ProductDetailActivity.this, "Lỗi không xác định khi tải sản phẩm", Toast.LENGTH_SHORT).show();
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

    private void updatePriceAndDisplay() {
        if (product == null) return;

        displayPrice = basePrice;
        if (selectedSize.equals("M")) {
            displayPrice = basePrice.add(new BigDecimal("5000"));
        } else if (selectedSize.equals("L")) {
            displayPrice = basePrice.add(new BigDecimal("10000"));
        }
        displayPrice = displayPrice.multiply(new BigDecimal(quantity));

        NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        numberFormat.setMinimumFractionDigits(0);
        numberFormat.setMaximumFractionDigits(0);
        productPrice.setText(numberFormat.format(displayPrice) + " VNĐ");
    }

    private void displayProductDetails() {
        if (product != null) {
            // Log để kiểm tra product.getName()
            Log.d(TAG, "product.getName(): " + product.getName() + ", length: " + (product.getName() != null ? product.getName().length() : "null"));

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
            updatePriceAndDisplay();
            quantityText.setText(String.valueOf(quantity));
        }
    }

    private void updateSizeButtonStyles() {
        sizeSButton.setBackgroundResource(selectedSize.equals("S") ? R.drawable.selected_size_button : R.drawable.normal_size_button);
        sizeMButton.setBackgroundResource(selectedSize.equals("M") ? R.drawable.selected_size_button : R.drawable.normal_size_button);
        sizeLButton.setBackgroundResource(selectedSize.equals("L") ? R.drawable.selected_size_button : R.drawable.normal_size_button);
    }

    private void addToCart() {
        if (product != null) {
            // Log để kiểm tra các giá trị trước khi gửi request
            Log.d(TAG, "product.getName(): " + product.getName() + ", length: " + (product.getName() != null ? product.getName().length() : "null"));
            Log.d(TAG, "selectedSize: " + selectedSize + ", length: " + (selectedSize != null ? selectedSize.length() : "null"));

            CartAddRequest request = new CartAddRequest();
            request.setProductId(product.getId());
            request.setQuantity(quantity);
            request.setSize(selectedSize);

            addToCartCall = RetrofitClient.getApiService(this).addToCart(request);
            addToCartCall.enqueue(new Callback<CartDTO>() {
                @Override
                public void onResponse(Call<CartDTO> call, Response<CartDTO> response) {
                    if (!isFinishing()) {
                        if (response.isSuccessful() && response.body() != null) {
                            // Xử lý chuỗi an toàn trong Toast
                            String safeProductName = product.getName() != null && product.getName().length() >= 23 ? product.getName().substring(0, 23) : product.getName();
                            String safeSelectedSize = selectedSize != null && selectedSize.length() >= 23 ? selectedSize.substring(0, 23) : selectedSize;
                            String toastMessage = "Đã thêm " + quantity + " " + safeProductName + " (Kích thước: " + safeSelectedSize + ", Giá: " + NumberFormat.getNumberInstance(new Locale("vi", "VN")).format(displayPrice) + " VNĐ) vào giỏ hàng";
                            Toast.makeText(ProductDetailActivity.this, toastMessage, Toast.LENGTH_LONG).show();
                            startActivity(new Intent(ProductDetailActivity.this, CartActivity.class));
                            finish();
                        } else {
                            Log.e(TAG, "Failed to add to cart, code: " + response.code());
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "Không có thông tin lỗi";
                                Log.e(TAG, "Error body: " + errorBody);
                                if (response.code() == 401) {
                                    SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                                    prefs.edit().remove("token").apply();
                                    Toast.makeText(ProductDetailActivity.this, "Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
                                    redirectToSignin();
                                } else {
                                    Toast.makeText(ProductDetailActivity.this, "Không thể thêm vào giỏ hàng: " + errorBody, Toast.LENGTH_SHORT).show();
                                }
                            } catch (IOException e) {
                                Log.e(TAG, "Error parsing error body: " + e.getMessage());
                                Toast.makeText(ProductDetailActivity.this, "Lỗi không xác định khi thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<CartDTO> call, Throwable t) {
                    if (!call.isCanceled() && !isFinishing()) {
                        Log.e(TAG, "Error adding to cart: " + t.getMessage());
                        Toast.makeText(ProductDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (productCall != null && !productCall.isCanceled()) {
            productCall.cancel();
        }
        if (addToCartCall != null && !addToCartCall.isCanceled()) {
            addToCartCall.cancel();
        }
    }
}