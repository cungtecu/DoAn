//package com.example.doan;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageButton;
//import android.widget.TextView;
//import android.widget.Toast;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import com.daimajia.androidanimations.library.Techniques;
//import com.daimajia.androidanimations.library.YoYo;
//import com.example.doan.api.RetrofitClient;
//import com.example.doan.models.CartCheckoutRequest;
//import com.example.doan.models.CartDTO;
//import com.example.doan.models.CartItem;
//import com.example.doan.models.CartItemDTO;
//import com.example.doan.models.CartUpdateRequest;
//import com.example.doan.models.OrderResponse;
//import com.example.doan.models.UserProfileDTO;
//import java.io.IOException;
//import java.text.NumberFormat;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Locale;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class CartActivity extends AppCompatActivity {
//
//    private static final String TAG = "CartActivity";
//    private static final String PREFS_NAME = "MyAppPrefs";
//    private static final int SIGNIN_REQUEST_CODE = 1002;
//    private RecyclerView cartRecyclerView;
//    private CartAdapter cartAdapter;
//    private List<CartItem> cartItems;
//    private TextView totalPriceTextView;
//    private Button checkoutButton;
//    private ImageButton btnCancel;
//    private Call<CartDTO> cartCall;
//    private Call<OrderResponse> checkoutCall;
//    private boolean isLoading = false;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_cart);
//
//        // Khởi tạo các view
//        cartRecyclerView = findViewById(R.id.cart_recycler_view);
//        totalPriceTextView = findViewById(R.id.total_price);
//        checkoutButton = findViewById(R.id.btn_checkout);
//        btnCancel = findViewById(R.id.btn_cancel);
//
//        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        cartItems = new ArrayList<>();
//        cartAdapter = new CartAdapter(cartItems, new CartAdapter.OnCartActionListener() {
//            @Override
//            public void onUpdateQuantity(CartItem item, int newQuantity) {
//                updateCartItem(item, newQuantity);
//            }
//
//            @Override
//            public void onRemove(CartItem item) {
//                removeCartItem(item);
//            }
//        });
//        cartRecyclerView.setAdapter(cartAdapter);
//
//        // Kiểm tra token và tải giỏ hàng
//        checkTokenAndLoadCart();
//
//        // Xử lý sự kiện nút checkout
//        checkoutButton.setOnClickListener(v -> {
//            if (cartItems.isEmpty()) {
//                Toast.makeText(this, "Giỏ hàng trống!", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            checkout();
//        });
//
//        // Xử lý sự kiện nút cancel
//        btnCancel.setOnClickListener(v -> {
//            Intent intent = new Intent(CartActivity.this, OrderActivity.class);
//            startActivity(intent);
//            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
//            finish();
//        });
//
//        // Hiệu ứng animation cho RecyclerView
//        YoYo.with(Techniques.FadeIn)
//                .duration(700)
//                .playOn(cartRecyclerView);
//    }
//
//    private void checkTokenAndLoadCart() {
//        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
//        String token = prefs.getString("token", null);
//        if (token == null) {
//            Toast.makeText(this, "Không tìm thấy token, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
//            redirectToSignin();
//            return;
//        }
//
//        // Kiểm tra token hợp lệ
//        RetrofitClient.isTokenValid(this, new Callback<UserProfileDTO>() {
//            @Override
//            public void onResponse(Call<UserProfileDTO> call, Response<UserProfileDTO> response) {
//                if (!isFinishing()) {
//                    if (response.isSuccessful()) {
//                        // Token hợp lệ, tải giỏ hàng
//                        loadCartItems();
//                    } else {
//                        // Token không hợp lệ, xóa token và chuyển về đăng nhập
//                        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
//                        prefs.edit().remove("token").apply();
//                        Toast.makeText(CartActivity.this, "Phiên đăng nhập hết hạn, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
//                        redirectToSignin();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<UserProfileDTO> call, Throwable t) {
//                if (!isFinishing()) {
//                    Toast.makeText(CartActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//                    redirectToSignin();
//                }
//            }
//        });
//    }
//
//    private void redirectToSignin() {
//        Intent intent = new Intent(this, SigninActivity.class);
//        startActivityForResult(intent, SIGNIN_REQUEST_CODE);
//        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == SIGNIN_REQUEST_CODE) {
//            if (resultCode == RESULT_OK) {
//                // Đăng nhập thành công, kiểm tra lại token và tải giỏ hàng
//                checkTokenAndLoadCart();
//            } else {
//                finish();
//            }
//        }
//    }
//
//    private void loadCartItems() {
//        if (isLoading) return;
//        isLoading = true;
//        cartRecyclerView.setVisibility(View.GONE); // Ẩn RecyclerView trong khi tải
//
//        cartCall = RetrofitClient.getApiService(this).getCartItems();
//        cartCall.enqueue(new Callback<CartDTO>() {
//            @Override
//            public void onResponse(Call<CartDTO> call, Response<CartDTO> response) {
//                isLoading = false;
//                if (!isFinishing()) {
//                    cartRecyclerView.setVisibility(View.VISIBLE);
//                    if (response.isSuccessful() && response.body() != null) {
//                        cartItems.clear();
//                        for (CartItemDTO itemDTO : response.body().getCartItems()) {
//                            CartItem cartItem = new CartItem(
//                                    itemDTO.getId(),
//                                    itemDTO.getProduct().getId(),
//                                    itemDTO.getProduct().getName(),
//                                    itemDTO.getSize(),
//                                    itemDTO.getQuantity(),
//                                    itemDTO.getProduct().getPrice(),
//                                    itemDTO.getProduct().getImage()
//                            );
//                            cartItems.add(cartItem);
//                        }
//                        cartAdapter.notifyDataSetChanged();
//                        updateTotalPriceAndQuantity();
//                        Log.d(TAG, "Tải giỏ hàng thành công, số lượng: " + cartItems.size());
//                    } else {
//                        handleApiError(response, "Không thể tải giỏ hàng");
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<CartDTO> call, Throwable t) {
//                isLoading = false;
//                if (!call.isCanceled() && !isFinishing()) {
//                    cartRecyclerView.setVisibility(View.VISIBLE);
//                    Log.e(TAG, "Lỗi kết nối tải giỏ hàng: " + t.getMessage());
//                    Toast.makeText(CartActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }
//
//    private void updateCartItem(CartItem item, int newQuantity) {
//        if (isLoading) return;
//        isLoading = true;
//
//        CartUpdateRequest request = new CartUpdateRequest();
//        request.setCartItemId(item.getCartItemId());
//        request.setProductId(item.getProductId());
//        request.setQuantity(newQuantity);
//        request.setSize(item.getSize());
//
//        RetrofitClient.getApiService(this).updateCartItem(request).enqueue(new Callback<CartDTO>() {
//            @Override
//            public void onResponse(Call<CartDTO> call, Response<CartDTO> response) {
//                isLoading = false;
//                if (!isFinishing()) {
//                    if (response.isSuccessful() && response.body() != null) {
//                        cartItems.clear();
//                        for (CartItemDTO itemDTO : response.body().getCartItems()) {
//                            CartItem cartItem = new CartItem(
//                                    itemDTO.getId(),
//                                    itemDTO.getProduct().getId(),
//                                    itemDTO.getProduct().getName(),
//                                    itemDTO.getSize(),
//                                    itemDTO.getQuantity(),
//                                    itemDTO.getProduct().getPrice(),
//                                    itemDTO.getProduct().getImage()
//                            );
//                            cartItems.add(cartItem);
//                        }
//                        cartAdapter.notifyDataSetChanged();
//                        updateTotalPriceAndQuantity();
//                        Toast.makeText(CartActivity.this, "Cập nhật số lượng thành công", Toast.LENGTH_SHORT).show();
//                    } else {
//                        handleApiError(response, "Không thể cập nhật số lượng");
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<CartDTO> call, Throwable t) {
//                isLoading = false;
//                if (!call.isCanceled() && !isFinishing()) {
//                    Log.e(TAG, "Lỗi cập nhật giỏ hàng: " + t.getMessage());
//                    Toast.makeText(CartActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }
//
//    private void removeCartItem(CartItem item) {
//        if (isLoading) return;
//        isLoading = true;
//
//        RetrofitClient.getApiService(this).removeFromCart(item.getCartItemId()).enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(Call<Void> call, Response<Void> response) {
//                isLoading = false;
//                if (!isFinishing()) {
//                    if (response.isSuccessful()) {
//                        cartItems.remove(item);
//                        cartAdapter.notifyDataSetChanged();
//                        updateTotalPriceAndQuantity();
//                        Toast.makeText(CartActivity.this, "Đã xóa sản phẩm khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
//                    } else {
//                        handleApiError(response, "Không thể xóa sản phẩm");
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Void> call, Throwable t) {
//                isLoading = false;
//                if (!call.isCanceled() && !isFinishing()) {
//                    Log.e(TAG, "Lỗi xóa sản phẩm: " + t.getMessage());
//                    Toast.makeText(CartActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }
//
//    private void checkout() {
//        if (isLoading) return;
//        isLoading = true;
//        checkoutButton.setEnabled(false); // Vô hiệu hóa nút trong khi xử lý
//
//        CartCheckoutRequest request = new CartCheckoutRequest();
//        request.setPaymentMethod("CASH");
//
//        checkoutCall = RetrofitClient.getApiService(this).checkout(request);
//        checkoutCall.enqueue(new Callback<OrderResponse>() {
//            @Override
//            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
//                isLoading = false;
//                checkoutButton.setEnabled(true);
//                if (!isFinishing()) {
//                    if (response.isSuccessful() && response.body() != null) {
//                        OrderResponse order = response.body();
//                        Toast.makeText(CartActivity.this, "Đặt hàng thành công! Mã đơn hàng: " + order.getId(), Toast.LENGTH_LONG).show();
//                        cartItems.clear();
//                        cartAdapter.notifyDataSetChanged();
//                        updateTotalPriceAndQuantity();
//                        Intent intent = new Intent(CartActivity.this, PaymentActivity.class);
//                        intent.putExtra("orderId", String.valueOf(order.getId()));
//                        startActivity(intent);
//                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//                        finish();
//                    } else {
//                        handleApiError(response, "Không thể đặt hàng");
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<OrderResponse> call, Throwable t) {
//                isLoading = false;
//                checkoutButton.setEnabled(true);
//                if (!call.isCanceled() && !isFinishing()) {
//                    Log.e(TAG, "Lỗi đặt hàng: " + t.getMessage());
//                    Toast.makeText(CartActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }
//
//    private void handleApiError(Response<?> response, String defaultMessage) {
//        try {
//            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Không có thông tin lỗi";
//            Log.e(TAG, "Error code: " + response.code() + ", body: " + errorBody);
//            if (response.code() == 401) {
//                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
//                prefs.edit().remove("token").apply();
//                Toast.makeText(CartActivity.this, "Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
//                redirectToSignin();
//            } else {
//                Toast.makeText(CartActivity.this, defaultMessage + ": " + errorBody, Toast.LENGTH_SHORT).show();
//            }
//        } catch (IOException e) {
//            Log.e(TAG, "Lỗi parse error body: " + e.getMessage());
//            Toast.makeText(CartActivity.this, defaultMessage + ": Lỗi không xác định", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void updateTotalPriceAndQuantity() {
//        double totalPrice = 0;
//        int totalQuantity = 0;
//        for (CartItem item : cartItems) {
//            totalPrice += item.getPrice() * item.getQuantity();
//            totalQuantity += item.getQuantity();
//        }
//        NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
//        numberFormat.setMinimumFractionDigits(0);
//        numberFormat.setMaximumFractionDigits(0);
//        totalPriceTextView.setText(numberFormat.format(totalPrice) + " VNĐ");
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        // Hủy tất cả các yêu cầu mạng
//        if (cartCall != null && !cartCall.isCanceled()) {
//            cartCall.cancel();
//        }
//        if (checkoutCall != null && !checkoutCall.isCanceled()) {
//            checkoutCall.cancel();
//        }
//    }
//}
package com.example.doan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.example.doan.models.OrderResponse;
import com.example.doan.models.UserProfileDTO;
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
    private Call<CartDTO> cartCall;
    private Call<OrderResponse> checkoutCall;
    private boolean isLoading = false;
    private boolean isTokenValid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Khởi tạo các view
        cartRecyclerView = findViewById(R.id.cart_recycler_view);
        totalPriceTextView = findViewById(R.id.total_price);
        checkoutButton = findViewById(R.id.btn_checkout);
        btnCancel = findViewById(R.id.btn_cancel);

        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartItems = new ArrayList<>();
        cartAdapter = new CartAdapter(cartItems, new CartAdapter.OnCartActionListener() {
            @Override
            public void onUpdateQuantity(CartItem item, int newQuantity) {
                if (isTokenValid) {
                    updateCartItem(item, newQuantity);
                } else {
                    showToast("Phiên đăng nhập hết hạn, vui lòng đăng nhập lại");
                    redirectToSignin();
                }
            }

            @Override
            public void onRemove(CartItem item) {
                if (isTokenValid) {
                    removeCartItem(item);
                } else {
                    showToast("Phiên đăng nhập hết hạn, vui lòng đăng nhập lại");
                    redirectToSignin();
                }
            }
        });
        cartRecyclerView.setAdapter(cartAdapter);

        // Kiểm tra token và tải giỏ hàng
        checkTokenAndLoadCart();

        // Xử lý sự kiện nút checkout
        checkoutButton.setOnClickListener(v -> {
            if (!isTokenValid) {
                showToast("Phiên đăng nhập hết hạn, vui lòng đăng nhập lại");
                redirectToSignin();
                return;
            }
            if (cartItems.isEmpty()) {
                showToast("Giỏ hàng trống!");
                return;
            }
            checkout();
        });

        // Xử lý sự kiện nút cancel
        btnCancel.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, OrderActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
        });

        // Hiệu ứng animation cho RecyclerView
        YoYo.with(Techniques.FadeIn)
                .duration(700)
                .playOn(cartRecyclerView);
    }

    private void checkTokenAndLoadCart() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String token = prefs.getString("token", null);
        if (token == null) {
            showToast("Không tìm thấy token, vui lòng đăng nhập lại");
            redirectToSignin();
            return;
        }

        // Kiểm tra token hợp lệ
        RetrofitClient.isTokenValid(this, new Callback<UserProfileDTO>() {
            @Override
            public void onResponse(Call<UserProfileDTO> call, Response<UserProfileDTO> response) {
                if (!isFinishing()) {
                    if (response.isSuccessful()) {
                        isTokenValid = true;
                        loadCartItems();
                    } else {
                        isTokenValid = false;
                        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                        prefs.edit().remove("token").apply();
                        showToast("Phiên đăng nhập hết hạn, vui lòng đăng nhập lại");
                        redirectToSignin();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserProfileDTO> call, Throwable t) {
                if (!isFinishing()) {
                    isTokenValid = false;
                    showToast("Lỗi kết nối: " + t.getMessage());
                    redirectToSignin();
                }
            }
        });
    }

    private void redirectToSignin() {
        Intent intent = new Intent(this, SigninActivity.class);
        startActivityForResult(intent, SIGNIN_REQUEST_CODE);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGNIN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                checkTokenAndLoadCart();
            } else {
                finish();
            }
        }
    }

    private void loadCartItems() {
        if (isLoading || !isTokenValid) return;
        isLoading = true;
        cartRecyclerView.setVisibility(View.GONE);

        cartCall = RetrofitClient.getApiService(this).getCartItems();
        cartCall.enqueue(new Callback<CartDTO>() {
            @Override
            public void onResponse(Call<CartDTO> call, Response<CartDTO> response) {
                isLoading = false;
                if (!isFinishing()) {
                    cartRecyclerView.setVisibility(View.VISIBLE);
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
                        Log.d(TAG, "Tải giỏ hàng thành công, số lượng: " + cartItems.size());
                    } else {
                        handleApiError(response, "Không thể tải giỏ hàng");
                    }
                }
            }

            @Override
            public void onFailure(Call<CartDTO> call, Throwable t) {
                isLoading = false;
                if (!call.isCanceled() && !isFinishing()) {
                    cartRecyclerView.setVisibility(View.VISIBLE);
                    Log.e(TAG, "Lỗi kết nối tải giỏ hàng: " + t.getMessage());
                    showToast("Lỗi kết nối: " + t.getMessage());
                }
            }
        });
    }

    private void updateCartItem(CartItem item, int newQuantity) {
        if (isLoading || !isTokenValid) return;
        isLoading = true;

        CartUpdateRequest request = new CartUpdateRequest();
        request.setCartItemId(item.getCartItemId());
        request.setProductId(item.getProductId());
        request.setQuantity(newQuantity);
        request.setSize(item.getSize());

        RetrofitClient.getApiService(this).updateCartItem(request).enqueue(new Callback<CartDTO>() {
            @Override
            public void onResponse(Call<CartDTO> call, Response<CartDTO> response) {
                isLoading = false;
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
                        showToast("Cập nhật số lượng thành công");
                    } else {
                        handleApiError(response, "Không thể cập nhật số lượng");
                    }
                }
            }

            @Override
            public void onFailure(Call<CartDTO> call, Throwable t) {
                isLoading = false;
                if (!call.isCanceled() && !isFinishing()) {
                    Log.e(TAG, "Lỗi cập nhật giỏ hàng: " + t.getMessage());
                    showToast("Lỗi kết nối: " + t.getMessage());
                }
            }
        });
    }

    private void removeCartItem(CartItem item) {
        if (isLoading || !isTokenValid) return;
        isLoading = true;

        RetrofitClient.getApiService(this).removeFromCart(item.getCartItemId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                isLoading = false;
                if (!isFinishing()) {
                    if (response.isSuccessful()) {
                        cartItems.remove(item);
                        cartAdapter.notifyDataSetChanged();
                        updateTotalPriceAndQuantity();
                        showToast("Đã xóa sản phẩm khỏi giỏ hàng");
                    } else {
                        handleApiError(response, "Không thể xóa sản phẩm");
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                isLoading = false;
                if (!call.isCanceled() && !isFinishing()) {
                    Log.e(TAG, "Lỗi xóa sản phẩm: " + t.getMessage());
                    showToast("Lỗi kết nối: " + t.getMessage());
                }
            }
        });
    }

    private void checkout() {
        if (isLoading || !isTokenValid) return;
        isLoading = true;
        checkoutButton.setEnabled(false);

        CartCheckoutRequest request = new CartCheckoutRequest();
        request.setPaymentMethod("CASH");

        checkoutCall = RetrofitClient.getApiService(this).checkout(request);
        checkoutCall.enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                isLoading = false;
                checkoutButton.setEnabled(true);
                if (!isFinishing()) {
                    if (response.isSuccessful() && response.body() != null) {
                        OrderResponse order = response.body();
                        showToast("Đặt hàng thành công! Mã đơn hàng: " + order.getId());
                        cartItems.clear();
                        cartAdapter.notifyDataSetChanged();
                        updateTotalPriceAndQuantity();
                        Intent intent = new Intent(CartActivity.this, PaymentActivity.class);
                        intent.putExtra("orderId", String.valueOf(order.getId()));
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    } else {
                        handleApiError(response, "Không thể đặt hàng");
                    }
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                isLoading = false;
                checkoutButton.setEnabled(true);
                if (!call.isCanceled() && !isFinishing()) {
                    Log.e(TAG, "Lỗi đặt hàng: " + t.getMessage());
                    showToast("Lỗi kết nối: " + t.getMessage());
                }
            }
        });
    }

    private void handleApiError(Response<?> response, String defaultMessage) {
        try {
            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Không có thông tin lỗi";
            Log.e(TAG, "Error code: " + response.code() + ", body: " + errorBody);
            if (response.code() == 401) {
                isTokenValid = false;
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                prefs.edit().remove("token").apply();
                showToast("Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại!");
                redirectToSignin();
            } else {
                showToast(defaultMessage + ": " + errorBody);
            }
        } catch (IOException e) {
            Log.e(TAG, "Lỗi parse error body: " + e.getMessage());
            showToast(defaultMessage + ": Lỗi không xác định");
        }
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
        totalPriceTextView.setText(numberFormat.format(totalPrice) + " VNĐ");
    }

    private void showToast(String message) {
        if (!isFinishing()) {
            new Handler(Looper.getMainLooper()).post(() -> {
                if (!isFinishing()) {
                    Toast.makeText(CartActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cartCall != null && !cartCall.isCanceled()) {
            cartCall.cancel();
            Log.d(TAG, "Hủy yêu cầu tải giỏ hàng trong onDestroy");
        }
        if (checkoutCall != null && !checkoutCall.isCanceled()) {
            checkoutCall.cancel();
            Log.d(TAG, "Hủy yêu cầu đặt hàng trong onDestroy");
        }
    }
}