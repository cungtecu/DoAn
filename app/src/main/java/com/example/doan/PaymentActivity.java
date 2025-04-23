package com.example.doan;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.doan.api.RetrofitClient;
import com.example.doan.models.CartCheckoutRequest;
import com.example.doan.models.CartDTO;
import com.example.doan.models.CartItemDTO;
import com.example.doan.models.CheckoutResponse;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity {
    private static final String TAG = "PaymentActivity";
    private TextView tvAddedAt, tvTotalPriceTemp, tvSalePrice, tvTotalPrice, tvDripsPoints, tvPoints, btnAddProduct;
    private TableLayout productList;
    private Button btnOrderCart;
    private ImageView btnBack;
    private CheckBox isUsePoints;
    private Call<CartDTO> cartCall;
    private Call<CheckoutResponse> checkoutCall;
    private double totalPrice = 0;
    private double discount = 0; // Số tiền giảm giá từ Drips
    private int usedDrips = 0; // Số điểm Drips đã sử dụng

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Khởi tạo các view
        tvAddedAt = findViewById(R.id.tv_added_at);
        tvTotalPriceTemp = findViewById(R.id.tv_total_price_temp);
        tvSalePrice = findViewById(R.id.tv_sale_price);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        tvDripsPoints = findViewById(R.id.drips_points);
        tvPoints = findViewById(R.id.tv_points);
        productList = findViewById(R.id.product_list);
        btnOrderCart = findViewById(R.id.btn_oder_cart);
        btnBack = findViewById(R.id.btn_back);
        isUsePoints = findViewById(R.id.is_use_points);
        btnAddProduct = findViewById(R.id.btn_add_product);

        // Thiết lập ngày tạo đơn
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        tvAddedAt.setText("Ngày tạo đơn: " + dateFormat.format(new Date()));

        // Kiểm tra trạng thái thanh toán (nếu quay lại từ PaymentWebviewActivity)
        String paymentStatus = getIntent().getStringExtra("payment_status");
        if ("failed".equals(paymentStatus)) {
            Toast.makeText(this, "Thanh toán thất bại! Vui lòng thử lại.", Toast.LENGTH_LONG).show();
        }

        // Gọi API để lấy dữ liệu giỏ hàng
        loadCartItems();

        // Sự kiện nhấn nút Back
        btnBack.setOnClickListener(v -> {
            finish();
        });

        // Sự kiện nhấn nút Thanh toán (btn_oder_cart)
        btnOrderCart.setOnClickListener(v -> {
            if (totalPrice <= 0) {
                Toast.makeText(this, "Không có sản phẩm để thanh toán!", Toast.LENGTH_SHORT).show();
                return;
            }
            checkout();
        });

        // Sự kiện nhấn nút Thêm sản phẩm (btn_add_product)
        btnAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(PaymentActivity.this, OrderActivity.class);
            // Truyền dữ liệu điểm Drips
            String usedDripsStr = tvPoints.getText().toString().replace("Drips đã sử dụng: ", "");
            String expectedDrips = tvDripsPoints.getText().toString().replace("Drips dự kiến: ", "");
            intent.putExtra("used_drips", usedDripsStr);
            intent.putExtra("expected_drips", expectedDrips);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // Sự kiện tick CheckBox "Sử dụng điểm Drips"
        isUsePoints.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Lấy số điểm Drips từ tvPoints
                String pointsStr = tvPoints.getText().toString().replace("Drips đã sử dụng: ", "");
                try {
                    usedDrips = Integer.parseInt(pointsStr);
                } catch (NumberFormatException e) {
                    usedDrips = 0;
                    Log.e(TAG, "Error parsing Drips points: " + e.getMessage());
                }

                // Quy đổi điểm Drips thành tiền: 100 điểm = 1000 VNĐ
                double discountFromDrips = (usedDrips / 100.0) * 1000;

                // Kiểm tra điều kiện: Số tiền giảm không vượt quá 50% tổng giá trị đơn hàng
                double maxDiscount = totalPrice * 0.5;
                if (discountFromDrips > maxDiscount) {
                    discountFromDrips = maxDiscount;
                    usedDrips = (int) (discountFromDrips / 1000 * 100); // Cập nhật lại số điểm đã sử dụng
                }

                // Cập nhật số tiền giảm giá
                discount = discountFromDrips;
            } else {
                // Nếu bỏ tick, không sử dụng điểm Drips
                discount = 0;
                usedDrips = 0;
            }

            // Cập nhật lại giao diện
            updatePaymentDetails();
        });
    }

    private void loadCartItems() {
        cartCall = RetrofitClient.getApiService(this).getCartItems();
        cartCall.enqueue(new Callback<CartDTO>() {
            @Override
            public void onResponse(Call<CartDTO> call, Response<CartDTO> response) {
                if (!isFinishing()) {
                    if (response.isSuccessful() && response.body() != null) {
                        productList.removeAllViews(); // Xóa danh sách sản phẩm cũ
                        totalPrice = 0;

                        for (CartItemDTO itemDTO : response.body().getCartItems()) {
                            // Tạo một hàng mới cho mỗi sản phẩm
                            TableRow row = new TableRow(PaymentActivity.this);
                            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                            // Tên sản phẩm
                            TextView tvProductName = new TextView(PaymentActivity.this);
                            tvProductName.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
                            tvProductName.setGravity(android.view.Gravity.CENTER);
                            tvProductName.setText(itemDTO.getProduct().getName());
                            tvProductName.setTextColor(getResources().getColor(R.color.brown));
                            tvProductName.setTextSize(16);
                            tvProductName.setPadding(8, 8, 8, 8);

                            // Số lượng
                            TextView tvQuantity = new TextView(PaymentActivity.this);
                            tvQuantity.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
                            tvQuantity.setGravity(android.view.Gravity.CENTER);
                            tvQuantity.setText(String.valueOf(itemDTO.getQuantity()));
                            tvQuantity.setTextColor(getResources().getColor(R.color.brown));
                            tvQuantity.setTextSize(16);
                            tvQuantity.setPadding(8, 8, 8, 8);

                            // Giá
                            TextView tvPrice = new TextView(PaymentActivity.this);
                            tvPrice.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
                            tvPrice.setGravity(android.view.Gravity.CENTER);
                            double price = itemDTO.getProduct().getPrice() * itemDTO.getQuantity();
                            totalPrice += price;
                            NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
                            numberFormat.setMinimumFractionDigits(0);
                            tvPrice.setText(numberFormat.format(price) + " VNĐ");
                            tvPrice.setTextColor(getResources().getColor(R.color.brown));
                            tvPrice.setTextSize(16);
                            tvPrice.setPadding(8, 8, 8, 8);

                            // Thêm các TextView vào TableRow
                            row.addView(tvProductName);
                            row.addView(tvQuantity);
                            row.addView(tvPrice);

                            // Thêm TableRow vào TableLayout
                            productList.addView(row);
                        }

                        // Cập nhật các trường tổng giá trị, giảm giá, v.v.
                        updatePaymentDetails();
                    } else {
                        Log.e(TAG, "Failed to load cart, code: " + response.code());
                        Toast.makeText(PaymentActivity.this, "Không thể tải dữ liệu giỏ hàng!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<CartDTO> call, Throwable t) {
                if (!call.isCanceled() && !isFinishing()) {
                    Log.e(TAG, "Error loading cart: " + t.getMessage());
                    Toast.makeText(PaymentActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updatePaymentDetails() {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        numberFormat.setMinimumFractionDigits(0);

        // Tổng giá trị đơn hàng
        tvTotalPriceTemp.setText(numberFormat.format(totalPrice) + " VNĐ");

        // Số tiền giảm giá từ Drips
        tvSalePrice.setText(numberFormat.format(discount) + " VNĐ");

        // Tổng thanh toán (tổng giá - giảm giá)
        double finalPrice = totalPrice - discount;
        tvTotalPrice.setText(numberFormat.format(finalPrice) + " VNĐ");

        // Drips dự kiến (giả sử 1 VNĐ = 1 Drip, sau khi giảm giá)
        int expectedDrips = (int) finalPrice;
        tvDripsPoints.setText("Drips dự kiến: " + expectedDrips);

        // Drips đã sử dụng
        tvPoints.setText("Drips đã sử dụng: " + usedDrips);
    }

    private void checkout() {
        CartCheckoutRequest request = new CartCheckoutRequest();
        request.setPaymentMethod("VNPAY");

        checkoutCall = RetrofitClient.getApiService(this).checkout(request);
        checkoutCall.enqueue(new Callback<CheckoutResponse>() {
            @Override
            public void onResponse(Call<CheckoutResponse> call, Response<CheckoutResponse> response) {
                if (!isFinishing()) {
                    if (response.isSuccessful() && response.body() != null) {
                        String paymentUrl = response.body().getPaymentUrl();
                        if (paymentUrl != null) {
                            // Chuyển sang giao diện thanh toán VNPay
                            Intent intent = new Intent(PaymentActivity.this, PaymentWebviewActivity.class);
                            intent.putExtra("payment_url", paymentUrl);
                            intent.putExtra("total_price", totalPrice - discount); // Truyền tổng giá sau khi giảm
                            intent.putExtra("used_drips", usedDrips); // Truyền số điểm đã sử dụng
                            startActivity(intent);
                        } else {
                            Toast.makeText(PaymentActivity.this, "Không nhận được URL thanh toán!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "Failed to checkout, code: " + response.code());
                        Toast.makeText(PaymentActivity.this, "Không thể thanh toán!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<CheckoutResponse> call, Throwable t) {
                if (!call.isCanceled() && !isFinishing()) {
                    Log.e(TAG, "Error during checkout: " + t.getMessage());
                    Toast.makeText(PaymentActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
    }
}