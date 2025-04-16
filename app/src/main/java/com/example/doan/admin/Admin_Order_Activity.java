package com.example.doan.admin;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.doan.R;
import com.example.doan.api.ApiService;
import com.example.doan.api.RetrofitClient;
import com.example.doan.models.Order;
import com.example.doan.models.OrderAdapter;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Admin_Order_Activity extends AppCompatActivity {

    private OrderAdapter orderAdapter;
    private List<Order> orderList;
    private EditText edt_search;
    private List<Order> filteredList;
    private View btnFilter;
    private ApiService apiService;
    private RecyclerView rcv_order;
    private Spinner spinnerFilter;
    private String authToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_order);

        // Lấy token từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        authToken = "Bearer " + prefs.getString("token", null);
        if (authToken == null) {
            Toast.makeText(this, "Không tìm thấy token, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        edt_search = findViewById(R.id.edt_search);
        rcv_order = findViewById(R.id.rcv_order);
        btnFilter = findViewById(R.id.btnFilter);
        spinnerFilter = findViewById(R.id.spinnerFilter);

        // Initialize API service
        apiService = RetrofitClient.getApiService(this); // Thêm this

        // Initialize lists and adapter
        orderList = new ArrayList<>();
        filteredList = new ArrayList<>();
        orderAdapter = new OrderAdapter(filteredList);
        rcv_order.setLayoutManager(new LinearLayoutManager(this));
        rcv_order.setAdapter(orderAdapter);

        // Load data from API
//        loadOrdersFromApi();

        // Setup Spinner
        setupSpinner();

        // Handle filter button click
//        btnFilter.setOnClickListener(v -> applyFilter());
    }

    // Handle search icon click
    public void onSearchClicked(View view) {
        String query = edt_search.getText().toString().trim();
        searchOrders(query);
    }

//    // Load data from API
//    private void loadOrdersFromApi() {
//        Call<List<Order>> call = apiService.getListOrder(authToken);
//        call.enqueue(new Callback<List<Order>>() {
//            @Override
//            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    orderList.clear();
//                    orderList.addAll(response.body());
//                    filteredList.clear();
//                    filteredList.addAll(orderList);
//                    orderAdapter.notifyDataSetChanged();
//                } else {
//                    Toast.makeText(Admin_Order_Activity.this, "Không thể tải danh sách đơn hàng: " + response.code(), Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<Order>> call, Throwable t) {
//                Toast.makeText(Admin_Order_Activity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    // Setup Spinner with filter options
    private void setupSpinner() {
        String[] filterOptions = {"Tất cả", "Mới nhất", "Cũ nhất", "Đã huỷ", "Chưa thanh toán", "Giá trị tăng dần", "Giá trị giảm dần"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, filterOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(adapter);

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Optional: Apply filter immediately when selection changes
                // applyFilter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    // Apply filter based on Spinner selection
//    private void applyFilter() {
//        String selectedFilter = spinnerFilter.getSelectedItem().toString();
//        filteredList.clear();
//
//        switch (selectedFilter) {
//            case "Tất cả":
//                filteredList.addAll(orderList);
//                break;
//            case "Mới nhất":
//                filteredList.addAll(orderList); // Giả sử orderList đã sắp xếp mới nhất
//                break;
//            case "Cũ nhất":
//                filteredList.addAll(orderList); // Đảo ngược nếu cần
//                break;
//            case "Đã huỷ":
//                for (Order order : orderList) {
//                    if ("Đã huỷ".equals(order.getStatus())) {
//                        filteredList.add(order);
//                    }
//                }
//                break;
//            case "Chưa thanh toán":
//                for (Order order : orderList) {
//                    if ("Chưa thanh toán".equals(order.getStatus())) {
//                        filteredList.add(order);
//                    }
//                }
//                break;
//            case "Giá trị tăng dần":
//                filteredList.addAll(orderList);
//                filteredList.sort((o1, o2) -> o1.getTotalPrice().compareTo(o2.getTotalPrice()));
//                break;
//            case "Giá trị giảm dần":
//                filteredList.addAll(orderList);
//                filteredList.sort((o1, o2) -> o2.getTotalPrice().compareTo(o1.getTotalPrice()));
//                break;
//        }
//        orderAdapter.notifyDataSetChanged();
//    }

    // Search orders based on query
    private void searchOrders(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(orderList);
        } else {
            for (Order order : orderList) {
                if (String.valueOf(order.getId()).contains(query) ||
                        String.valueOf(order.getTotalPrice()).contains(query) ||
                        order.getStatus().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(order);
                }
            }
        }
        orderAdapter.notifyDataSetChanged();
    }
}