//package com.example.doan;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.ImageView;
//import android.widget.TableLayout;
//import android.widget.TableRow;
//import android.widget.TextView;
//import android.widget.Toast;
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//import com.example.doan.api.RetrofitClient;
//import com.example.doan.models.LoyaltyPointDTO;
//import com.example.doan.models.OrderConfirmRequest;
//import com.example.doan.models.OrderDetailResponse;
//import com.example.doan.models.OrderResponse;
//import org.threeten.bp.LocalDateTime;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import java.io.IOException;
//import java.text.NumberFormat;
//import java.util.List;
//import java.util.Locale;
//
//public class PaymentActivity extends AppCompatActivity {
//
//    private static final String TAG = "PaymentActivity";
//    private static final String PREFS_NAME = "MyAppPrefs";
//    private static final int SIGNIN_REQUEST_CODE = 1002;
//    private TextView tvAddedAt, tvTotalPriceTemp, tvSalePrice, tvTotalPrice, tvPoints, dripsPoints;
//    private TableLayout productListTable;
//    private Button btnOrderCart;
//    private ImageView btnBack;
//    private TextView btnAddProduct;
//    private CheckBox cbUsePoints;
//    private Call<OrderResponse> orderCall;
//    private Call<List<LoyaltyPointDTO>> loyaltyPointCall;
//    private String orderId;
//    private String selectedPaymentMethod = "CASH";
//    private double totalPrice = 0;
//    private int availableDrips = 0;
//    private int usedDrips = 0;
//    private boolean isDripsUsed = false;
//    private int userId;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_payment);
//
//
//        orderId = getIntent().getStringExtra("orderId");
//        if (orderId == null) {
//            Toast.makeText(this, "Không tìm thấy đơn hàng!", Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
//
//        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
//        String token = prefs.getString("token", null);
//        userId = prefs.getInt("userId", -1);
//        if (token == null || userId == -1) {
//            redirectToSignin();
//            return;
//        }
//
//        // Khởi tạo các view
//        tvAddedAt = findViewById(R.id.tv_added_at);
//        tvTotalPriceTemp = findViewById(R.id.tv_total_price_temp);
//        tvSalePrice = findViewById(R.id.tv_sale_price);
//        tvTotalPrice = findViewById(R.id.tv_total_price);
//        tvPoints = findViewById(R.id.tv_points);
//        dripsPoints = findViewById(R.id.drips_points);
//        productListTable = findViewById(R.id.product_list);
//        btnOrderCart = findViewById(R.id.btn_oder_cart);
//        btnBack = findViewById(R.id.btn_back);
//        btnAddProduct = findViewById(R.id.btn_add_product);
//        cbUsePoints = findViewById(R.id.is_use_points);
//
//        // Lấy orderId từ Intent
//        orderId = getIntent().getStringExtra("orderId");
//        if (orderId == null) {
//            Toast.makeText(this, "Không tìm thấy đơn hàng!", Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
//
//        // Tải chi tiết đơn hàng và điểm Drips
//        loadOrderDetails();
//        loadLoyaltyPoints();
//
//        // Xử lý sự kiện nút quay lại
//        btnBack.setOnClickListener(v -> {
//            Intent intent = new Intent(PaymentActivity.this, CartActivity.class);
//            startActivity(intent);
//            finish();
//        });
//
//        // Xử lý nút thêm sản phẩm
//        btnAddProduct.setOnClickListener(v -> {
//            Intent intent = new Intent(PaymentActivity.this, OrderActivity.class);
//            startActivity(intent);
//        });
//
//        // Xử lý chọn phương thức thanh toán
//        btnOrderCart.setOnClickListener(v -> showPaymentMethodDialog());
//
//        // Xử lý checkbox sử dụng Drips
//        cbUsePoints.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            isDripsUsed = isChecked;
//            if (isChecked) {
//                tvPoints.setTextColor(Color.parseColor("#006241"));
//                usedDrips = Math.min(availableDrips, (int) (totalPrice / 1000)); // 1 Drip = 1000 VNĐ
//            } else {
//                tvPoints.setTextColor(getResources().getColor(R.color.gray2));
//                usedDrips = 0;
//            }
//            updatePriceDisplay();
//        });
//    }
//
//    private boolean isLoggedIn() {
//        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
//        String token = prefs.getString("token", null);
//        return token != null;
//    }
//
//    private void redirectToSignin() {
//        Intent intent = new Intent(this, SigninActivity.class);
//        startActivityForResult(intent, SIGNIN_REQUEST_CODE);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == SIGNIN_REQUEST_CODE) {
//            if (resultCode == RESULT_OK) {
//                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
//                userId = prefs.getInt("userId", -1);
//                loadOrderDetails();
//                loadLoyaltyPoints();
//            } else {
//                finish();
//            }
//        }
//    }
//
//    private void loadOrderDetails() {
//        orderCall = RetrofitClient.getApiService(this).getOrderDetails(orderId);
//        orderCall.enqueue(new Callback<OrderResponse>() {
//            @Override
//            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
//                if (!isFinishing()) {
//                    if (response.isSuccessful() && response.body() != null) {
//                        OrderResponse order = response.body();
//                        displayOrderDetails(order);
//                    } else {
//                        Log.e(TAG, "Failed to load order, code: " + response.code());
//                        try {
//                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Không có thông tin lỗi";
//                            Log.e(TAG, "Error body: " + errorBody);
//                            if (response.code() == 401) {
//                                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
//                                prefs.edit().remove("token").remove("userId").apply();
//                                Toast.makeText(PaymentActivity.this, "Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
//                                redirectToSignin();
//                            } else {
//                                Toast.makeText(PaymentActivity.this, "Không thể tải chi tiết đơn hàng: " + errorBody, Toast.LENGTH_SHORT).show();
//                            }
//                        } catch (IOException e) {
//                            Log.e(TAG, "Error parsing error body: " + e.getMessage());
//                            Toast.makeText(PaymentActivity.this, "Lỗi không xác định khi tải đơn hàng", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<OrderResponse> call, Throwable t) {
//                if (!call.isCanceled() && !isFinishing()) {
//                    Log.e(TAG, "Error loading order: " + t.getMessage());
//                    Toast.makeText(PaymentActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }
//
//    private void loadLoyaltyPoints() {
//        loyaltyPointCall = RetrofitClient.getApiService(this).getLoyaltyPoints(userId);
//        loyaltyPointCall.enqueue(new Callback<List<LoyaltyPointDTO>>() {
//            @Override
//            public void onResponse(Call<List<LoyaltyPointDTO>> call, Response<List<LoyaltyPointDTO>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    List<LoyaltyPointDTO> pointsList = response.body();
//                    availableDrips = calculateAvailablePoints(pointsList);
//                    tvPoints.setText(String.valueOf(availableDrips));
//                } else {
//                    Log.e(TAG, "Failed to load loyalty points, code: " + response.code());
//                    Toast.makeText(PaymentActivity.this, "Không thể tải điểm Drips!", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<LoyaltyPointDTO>> call, Throwable t) {
//                Log.e(TAG, "Error loading loyalty points: " + t.getMessage());
//                Toast.makeText(PaymentActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private int calculateAvailablePoints(List<LoyaltyPointDTO> pointsList) {
//        int totalPoints = 0;
//        LocalDateTime now = LocalDateTime.now();
//        for (LoyaltyPointDTO point : pointsList) {
//            if (point.getExpiresAt() == null || point.getExpiresAt().isAfter(now)) {
//                totalPoints += point.getPoints();
//            }
//        }
//        return totalPoints;
//    }
//
//    private void displayOrderDetails(OrderResponse order) {
//        // Hiển thị ngày tạo đơn
//        tvAddedAt.setText("Ngày tạo đơn: " + order.getOrderDate().toString());
//
//        // Hiển thị danh sách sản phẩm
//        productListTable.removeAllViews();
//        totalPrice = 0;
//        NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
//        numberFormat.setMinimumFractionDigits(0);
//        numberFormat.setMaximumFractionDigits(0);
//
//        for (OrderDetailResponse item : order.getOrderDetails()) {
//            TableRow row = new TableRow(this);
//            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
//
//            TextView name = new TextView(this);
//            name.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
//            name.setGravity(android.view.Gravity.CENTER);
//            name.setText(item.getProductName());
//            name.setTextColor(getResources().getColor(android.R.color.black));
//            name.setTextSize(16);
//            name.setPadding(8, 8, 8, 8);
//
//            TextView quantity = new TextView(this);
//            quantity.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
//            quantity.setGravity(android.view.Gravity.CENTER);
//            quantity.setText(String.valueOf(item.getQuantity()));
//            quantity.setTextColor(getResources().getColor(android.R.color.black));
//            quantity.setTextSize(16);
//            quantity.setPadding(8, 8, 8, 8);
//
//            TextView price = new TextView(this);
//            price.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
//            price.setGravity(android.view.Gravity.CENTER);
//            double itemTotalPrice = item.getItemTotalPrice() * item.getQuantity();
//            price.setText(numberFormat.format(itemTotalPrice) + " VNĐ");
//            price.setTextColor(getResources().getColor(android.R.color.black));
//            price.setTextSize(16);
//            price.setPadding(8, 8, 8, 8);
//
//            row.addView(name);
//            row.addView(quantity);
//            row.addView(price);
//
//            productListTable.addView(row);
//            totalPrice += itemTotalPrice;
//        }
//
//        // Cập nhật hiển thị giá
//        updatePriceDisplay();
//    }
//
//    private void updatePriceDisplay() {
//        NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
//        numberFormat.setMinimumFractionDigits(0);
//        numberFormat.setMaximumFractionDigits(0);
//
//        // Hiển thị tổng giá trị đơn hàng
//        tvTotalPriceTemp.setText(numberFormat.format(totalPrice) + " VNĐ");
//
//        // Hiển thị số tiền giảm giá
//        double discount = usedDrips * 1000; // 1 Drip = 1000 VNĐ
//        tvSalePrice.setText(numberFormat.format(discount) + " VNĐ");
//
//        // Hiển thị tổng thanh toán
//        double finalPrice = totalPrice - discount;
//        tvTotalPrice.setText(numberFormat.format(finalPrice) + " VNĐ");
//
//        // Hiển thị điểm Drips dự kiến (10,000 VNĐ = 1 điểm, làm tròn xuống)
//        int expectedDrips = (int) (finalPrice / 10000);
//        dripsPoints.setText(String.valueOf(expectedDrips));
//    }
//
//    private void showPaymentMethodDialog() {
//        String[] paymentMethods = {"Tiền mặt (COD)", "Thẻ tín dụng", "Ví điện tử"};
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Chọn phương thức thanh toán");
//        builder.setItems(paymentMethods, (dialog, which) -> {
//            switch (which) {
//                case 0:
//                    selectedPaymentMethod = "CASH";
//                    Toast.makeText(PaymentActivity.this, "Tiền mặt (COD)", Toast.LENGTH_SHORT).show();
//                    break;
//                case 1:
//                    selectedPaymentMethod = "CARD";
//                    Toast.makeText(PaymentActivity.this, "Thẻ tín dụng", Toast.LENGTH_SHORT).show();
//                    break;
//                case 2:
//                    selectedPaymentMethod = "WALLET";
//                    Toast.makeText(PaymentActivity.this, "Ví điện tử", Toast.LENGTH_SHORT).show();
//                    break;
//            }
////            confirmOrder(); // Gọi xác nhận đơn hàng sau khi chọn phương thức
//        });
//        builder.show();
//    }
//
////    private void confirmOrder() {
////        // Tạo yêu cầu xác nhận đơn hàng
////        OrderConfirmRequest request = new OrderConfirmRequest();
////        request.setOrderId(orderId);
////        request.setPaymentMethod(selectedPaymentMethod);
////        request.setUsedDrips(usedDrips);
////
////        RetrofitClient.getApiService(this).confirmOrder(request).enqueue(new Callback<OrderResponse>() {
////            @Override
////            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
////                if (!isFinishing()) {
////                    if (response.isSuccessful() && response.body() != null) {
////                        Toast.makeText(PaymentActivity.this, "Xác nhận đơn hàng thành công!", Toast.LENGTH_LONG).show();
////                        Intent intent = new Intent(PaymentActivity.this, OrderSuccessActivity.class);
////                        intent.putExtra("orderId", orderId);
////                        intent.putExtra("usedDrips", usedDrips);
////                        intent.putExtra("expectedDrips", (int) ((totalPrice - usedDrips * 1000) / 10000));
////                        startActivity(intent);
////                        finish();
////                    } else {
////                        Log.e(TAG, "Failed to confirm order, code: " + response.code());
////                        try {
////                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Không có thông tin lỗi";
////                            Log.e(TAG, "Error body: " + errorBody);
////                            if (response.code() == 401) {
////                                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
////                                prefs.edit().remove("token").remove("userId").apply();
////                                Toast.makeText(PaymentActivity.this, "Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
////                                redirectToSignin();
////                            } else {
////                                Toast.makeText(PaymentActivity.this, "Không thể xác nhận đơn hàng: " + errorBody, Toast.LENGTH_SHORT).show();
////                            }
////                        } catch (IOException e) {
////                            Log.e(TAG, "Error parsing error body: " + e.getMessage());
////                            Toast.makeText(PaymentActivity.this, "Lỗi không xác định khi xác nhận đơn hàng", Toast.LENGTH_SHORT).show();
////                        }
////                    }
////                }
////            }
////
////            @Override
////            public void onFailure(Call<OrderResponse> call, Throwable t) {
////                if (!call.isCanceled() && !isFinishing()) {
////                    Log.e(TAG, "Error confirming order: " + t.getMessage());
////                    Toast.makeText(PaymentActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
////                }
////            }
////        });
////    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (orderCall != null && !orderCall.isCanceled()) {
//            orderCall.cancel();
//        }
//        if (loyaltyPointCall != null && !loyaltyPointCall.isCanceled()) {
//            loyaltyPointCall.cancel();
//        }
//    }
//}