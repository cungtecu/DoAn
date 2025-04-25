package com.example.doan;

import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.doan.api.RetrofitClient;
import com.example.doan.models.OrderResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryOrderActivity extends AppCompatActivity {

    private static final String TAG = "HistoryOrderActivity";
    private RecyclerView rvOrderList;
    private OrderAdapter orderAdapter;
    private List<OrderResponse> orderList;
    private EditText searchEditText;
    private ImageButton btnBack;
    private Call<List<OrderResponse>> orderCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_order);

        rvOrderList = findViewById(R.id.history_order_recycler_view);
        searchEditText = findViewById(R.id.search_edit_text);
        btnBack = findViewById(R.id.btn_back);

        // Khởi tạo RecyclerView
        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(this, orderList);
        rvOrderList.setLayoutManager(new LinearLayoutManager(this));
        rvOrderList.setAdapter(orderAdapter);

        // Lấy userId từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", -1); // -1 là giá trị mặc định nếu không tìm thấy
        String token = sharedPreferences.getString("token", null);

        if (userId == -1 || token == null) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng. Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, SigninActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        // Lấy danh sách đơn hàng
        fetchOrders(userId, token);

        // Xử lý tìm kiếm
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                orderAdapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Xử lý nút Back
        btnBack.setOnClickListener(v -> {
            finish();
        });
    }

    private void fetchOrders(int userId, String token) {
        orderCall = RetrofitClient.getApiService(this).getUserOrders("Bearer " + token, userId);
        orderCall.enqueue(new Callback<List<OrderResponse>>() {
            @Override
            public void onResponse(Call<List<OrderResponse>> call, Response<List<OrderResponse>> response) {
                if (!isFinishing()) {
                    if (response.isSuccessful() && response.body() != null) {
                        orderList = response.body();

                        // Sắp xếp danh sách đơn hàng theo ngày giảm dần (mới nhất trước)
                        Collections.sort(orderList, new Comparator<OrderResponse>() {
                            @Override
                            public int compare(OrderResponse o1, OrderResponse o2) {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                try {
                                    Date date1 = dateFormat.parse(o1.getOrderDate());
                                    Date date2 = dateFormat.parse(o2.getOrderDate());
                                    return date2.compareTo(date1); // Sắp xếp giảm dần (mới nhất trước)
                                } catch (ParseException e) {
                                    Log.e(TAG, "Lỗi parse ngày: " + e.getMessage());
                                    return 0;
                                }
                            }
                        });

                        orderAdapter.updateOrders(orderList);
                        if (orderList.isEmpty()) {
                            Toast.makeText(HistoryOrderActivity.this, "Không có đơn hàng nào!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "Failed to fetch orders, code: " + response.code());
                        Toast.makeText(HistoryOrderActivity.this, "Không thể tải lịch sử đơn hàng!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<OrderResponse>> call, Throwable t) {
                if (!call.isCanceled() && !isFinishing()) {
                    Log.e(TAG, "Error fetching orders: " + t.getMessage());
                    Toast.makeText(HistoryOrderActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (orderCall != null && !orderCall.isCanceled()) {
            orderCall.cancel();
        }
    }
}