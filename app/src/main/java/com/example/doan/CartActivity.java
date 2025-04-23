package com.example.doan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.doan.api.RetrofitClient;
import com.example.doan.models.CartCheckoutRequest;
import com.example.doan.models.CartDTO;
import com.example.doan.models.CartItem;
import com.example.doan.models.CartItemDTO;
import com.example.doan.models.CartUpdateRequest;
import com.example.doan.models.CheckoutResponse;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity {

    private static final String TAG = "CartActivity";
    private static final String PREFS_NAME = "MyAppPrefs";
    private static final int SIGNIN_REQUEST_CODE = 1002;
    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;
    private TextView totalPriceTextView;
    private Button checkoutButton;
    private ImageButton btnCancel;
    private ProgressBar progressBar;

    private String authToken;
    private Call<CartDTO> cartCall;
    private Call<CheckoutResponse> checkoutCall;
    private Call<Void> removeCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        authToken = "Bearer " + prefs.getString("token", null);
        if (authToken == null) {
            Toast.makeText(this, "Không tìm thấy token, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        cartRecyclerView = findViewById(R.id.cart_recycler_view);
        totalPriceTextView = findViewById(R.id.total_price);
        checkoutButton = findViewById(R.id.btn_checkout);
        btnCancel = findViewById(R.id.btn_cancel);
        progressBar = findViewById(R.id.progress_bar);

        if (cartRecyclerView == null) {
            Log.e(TAG, "RecyclerView not found in layout");
            Toast.makeText(this, "Lỗi giao diện: Không tìm thấy RecyclerView", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartItems = new ArrayList<>();
        cartAdapter = new CartAdapter(cartItems, new CartAdapter.OnCartActionListener() {
            @Override
            public void onUpdateQuantity(CartItem item, int newQuantity) {
                if (isLoggedIn()) {
                    updateCartItem(item, newQuantity);
                } else {
                    redirectToSignin();
                }
            }

            @Override
            public void onRemove(CartItem item) {
                if (isLoggedIn()) {
                    removeCartItem(item);
                } else {
                    redirectToSignin();
                }
            }
        });
        cartRecyclerView.setAdapter(cartAdapter);

        if (isLoggedIn()) {
            loadCartItems();
        } else {
            redirectToSignin();
        }

        // Kiểm tra trạng thái thanh toán
        String paymentStatus = getIntent().getStringExtra("payment_status");
        if ("success".equals(paymentStatus)) {
            Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_LONG).show();
            loadCartItems(); // Làm mới giỏ hàng
        } else if ("failed".equals(paymentStatus)) {
            Toast.makeText(this, "Thanh toán thất bại!", Toast.LENGTH_LONG).show();
        }

        checkoutButton.setOnClickListener(v -> {
            Log.d(TAG, "Nút Thanh toán được nhấn");
            if (cartItems.isEmpty()) {
                Toast.makeText(this, "Giỏ hàng trống!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (isLoggedIn()) {
                Intent intent = new Intent(CartActivity.this, PaymentActivity.class);
                startActivity(intent);
            } else {
                redirectToSignin();
            }
        });

        btnCancel.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, OrderActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
        });

        // Tạm thời tắt animation để kiểm tra ANR
        // YoYo.with(Techniques.FadeIn)
        //     .duration(700)
        //     .playOn(cartRecyclerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Làm mới giỏ hàng khi quay lại từ OrderSuccessActivity
        if (isLoggedIn()) {
            loadCartItems();
        }
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
                SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                authToken = "Bearer " + prefs.getString("token", null);
                loadCartItems();
            } else {
                finish();
            }
        }
    }

    private void loadCartItems() {
        cartCall = RetrofitClient.getApiService(this).getCartItems();
        cartCall.enqueue(new Callback<CartDTO>() {
            @Override
            public void onResponse(Call<CartDTO> call, Response<CartDTO> response) {
                if (!isFinishing()) {
                    if (response.isSuccessful() && response.body() != null) {
                        cartItems.clear();
                        for (CartItemDTO itemDTO : response.body().getCartItems()) {
                            CartItem cartItem = new CartItem(
                                    itemDTO.getId(),
                                    itemDTO.getProduct().getId(),
                                    itemDTO.getProduct().getName(),
                                    itemDTO.getSize(),
                                    itemDTO.getQuantity(),
                                    itemDTO.getProduct().getPrice(),
                                    itemDTO.getProduct().getImage()
                            );
                            cartItems.add(cartItem);
                        }
                        cartAdapter.notifyDataSetChanged();
                        updateTotalPriceAndQuantity();
                    } else {
                        Log.e(TAG, "Failed to load cart, code: " + response.code());
                        try {
                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Không có thông tin lỗi";
                            Log.e(TAG, "Error body: " + errorBody);
                            if (response.code() == 401) {
                                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                                prefs.edit().remove("token").apply();
                                Toast.makeText(CartActivity.this, "Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
                                redirectToSignin();
                            } else {
                                Toast.makeText(CartActivity.this, "Không thể tải giỏ hàng: " + errorBody, Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            Log.e(TAG, "Error parsing error body: " + e.getMessage());
                            Toast.makeText(CartActivity.this, "Lỗi không xác định khi tải giỏ hàng", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<CartDTO> call, Throwable t) {
                if (!call.isCanceled() && !isFinishing()) {
                    Log.e(TAG, "Error loading cart: " + t.getMessage());
                    Toast.makeText(CartActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateCartItem(CartItem item, int newQuantity) {
        if (newQuantity < 1) {
            removeCartItem(item);
            return;
        }

        CartUpdateRequest request = new CartUpdateRequest();
        request.setCartItemId(item.getCartItemId());
        request.setProductId(item.getProductId());
        request.setQuantity(newQuantity);
        request.setSize(item.getSize());

        RetrofitClient.getApiService(this).updateCartItem(request).enqueue(new Callback<CartDTO>() {
            @Override
            public void onResponse(Call<CartDTO> call, Response<CartDTO> response) {
                if (!isFinishing()) {
                    if (response.isSuccessful() && response.body() != null) {
                        cartItems.clear();
                        for (CartItemDTO itemDTO : response.body().getCartItems()) {
                            CartItem cartItem = new CartItem(
                                    itemDTO.getId(),
                                    itemDTO.getProduct().getId(),
                                    itemDTO.getProduct().getName(),
                                    itemDTO.getSize(),
                                    itemDTO.getQuantity(),
                                    itemDTO.getProduct().getPrice(),
                                    itemDTO.getProduct().getImage()
                            );
                            cartItems.add(cartItem);
                        }
                        cartAdapter.notifyDataSetChanged();
                        updateTotalPriceAndQuantity();
                    } else {
                        Log.e(TAG, "Failed to update cart item, code: " + response.code());
                        try {
                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Không có thông tin lỗi";
                            Log.e(TAG, "Error body: " + errorBody);
                            if (response.code() == 401) {
                                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                                prefs.edit().remove("token").apply();
                                Toast.makeText(CartActivity.this, "Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
                                redirectToSignin();
                            } else {
                                Toast.makeText(CartActivity.this, "Không thể cập nhật số lượng: " + errorBody, Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            Log.e(TAG, "Error parsing error body: " + e.getMessage());
                            Toast.makeText(CartActivity.this, "Lỗi không xác định khi cập nhật giỏ hàng", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<CartDTO> call, Throwable t) {
                if (!call.isCanceled() && !isFinishing()) {
                    Log.e(TAG, "Error updating cart item: " + t.getMessage());
                    Toast.makeText(CartActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void removeCartItem(CartItem item) {
        if (item == null || item.getProductId() == 0) {
            Log.e(TAG, "Invalid cart item or productId: " + (item != null ? item.getProductId() : "null"));
            Toast.makeText(this, "Không thể xóa sản phẩm: Dữ liệu không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        int position = cartItems.indexOf(item);
        if (position == -1) {
            Log.e(TAG, "ItemAGC not found in cartItems list: " + item.getProductId());
            Toast.makeText(this, "Không thể xóa sản phẩm: Không tìm thấy trong giỏ hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        removeCall = RetrofitClient.getApiService(this).removeFromCart(authToken, item.getProductId());
        removeCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!isFinishing()) {
                    if (response.isSuccessful()) {
                        cartItems.remove(position);
                        cartAdapter.notifyItemRemoved(position);
                        cartAdapter.notifyItemRangeChanged(position, cartItems.size());
                        updateTotalPriceAndQuantity();
                    } else {
                        Log.e(TAG, "Failed to remove cart item, code: " + response.code());
                        try {
                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Không có thông tin lỗi";
                            Log.e(TAG, "Error body: " + errorBody);
                            if (response.code() == 401) {
                                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                                prefs.edit().remove("token").apply();
                                Toast.makeText(CartActivity.this, "Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
                                redirectToSignin();
                            } else {
                                Toast.makeText(CartActivity.this, "Không thể xóa sản phẩm: " + errorBody, Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            Log.e(TAG, "Error parsing error body: " + e.getMessage());
                            Toast.makeText(CartActivity.this, "Lỗi không xác định khi xóa sản phẩm", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (!call.isCanceled() && !isFinishing()) {
                    Log.e(TAG, "Error removing cart item: " + t.getMessage());
                    Toast.makeText(CartActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateTotalPriceAndQuantity() {
        double totalPrice = 0;
        int totalQuantity = 0;
        for (CartItem item : cartItems) {
            totalPrice += item.getPrice() * item.getQuantity();
            totalQuantity += item.getQuantity();
        }
        NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        numberFormat.setMinimumFractionDigits(0);
        numberFormat.setMaximumFractionDigits(0);
        totalPriceTextView.setText("Tổng Tạm Tính: " + numberFormat.format(totalPrice) + " VNĐ");
    }

    private void checkout() {
        Log.d(TAG, "Bắt đầu quá trình checkout");
        CartCheckoutRequest request = new CartCheckoutRequest();
        request.setPaymentMethod("VNPAY");

        // Hiển thị ProgressBar
        progressBar.setVisibility(View.VISIBLE);
        checkoutButton.setEnabled(false); // Vô hiệu hóa nút để tránh nhấn liên tục

        checkoutCall = RetrofitClient.getApiService(this).checkout(request);
        checkoutCall.enqueue(new Callback<CheckoutResponse>() {
            @Override
            public void onResponse(Call<CheckoutResponse> call, Response<CheckoutResponse> response) {
                // Ẩn ProgressBar
                progressBar.setVisibility(View.GONE);
                checkoutButton.setEnabled(true);

                if (!isFinishing()) {
                    Log.d(TAG, "Nhận phản hồi từ API checkout, code: " + response.code());
                    if (response.isSuccessful() && response.body() != null) {
                        CheckoutResponse checkoutResponse = response.body();
                        String paymentUrl = checkoutResponse.getPaymentUrl();
                        Log.d(TAG, "Payment URL: " + paymentUrl);
                        if (paymentUrl != null) {
                            Toast.makeText(CartActivity.this, "Đang chuyển hướng đến trang thanh toán...", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CartActivity.this, PaymentActivity.class);
                            intent.putExtra("payment_url", paymentUrl);
                            startActivity(intent);
                        } else {
                            Log.e(TAG, "Payment URL là null");
                            Toast.makeText(CartActivity.this, "Không nhận được URL thanh toán!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "Failed to checkout, code: " + response.code());
                        try {
                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Không có thông tin lỗi";
                            Log.e(TAG, "Error body: " + errorBody);
                            if (response.code() == 401) {
                                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                                prefs.edit().remove("token").apply();
                                Toast.makeText(CartActivity.this, "Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
                                redirectToSignin();
                            } else {
                                Toast.makeText(CartActivity.this, "Không thể thanh toán: " + errorBody, Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            Log.e(TAG, "Error parsing error body: " + e.getMessage());
                            Toast.makeText(CartActivity.this, "Lỗi không xác định khi thanh toán", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<CheckoutResponse> call, Throwable t) {
                if (!call.isCanceled() && !isFinishing()) {
                    // Ẩn ProgressBar
                    progressBar.setVisibility(View.GONE);
                    checkoutButton.setEnabled(true);

                    Log.e(TAG, "Error during checkout: " + t.getMessage());
                    Toast.makeText(CartActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cartCall != null && !cartCall.isCanceled()) {
            cartCall.cancel();
        }
        if (checkoutCall != null && !checkoutCall.isCanceled()) {
            checkoutCall.cancel();
        }
        if (removeCall != null && !removeCall.isCanceled()) {
            removeCall.cancel();
        }
    }
}