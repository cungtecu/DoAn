package com.example.doan.admin;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
//import com.example.doan.databinding.ActivityAdminPointBinding;
import com.example.doan.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Admin_Point_Activity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_systemconfig); // Liên kết với layout XML

//    private ActivityAdminPointBinding binding;
//    private SystemConfigAdapter configAdapter;
//    private List<SystemConfig> configList;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = ActivityAdminPointBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        // Khởi tạo danh sách và adapter cho RecyclerView
//        configList = new ArrayList<>();
//        configAdapter = new SystemConfigAdapter(configList);
//        binding.rcvSystemconfig.setLayoutManager(new LinearLayoutManager(this));
//        binding.rcvSystemconfig.setAdapter(configAdapter);
//
//        // Tải dữ liệu mẫu
//        loadConfigs();
//
//        // Thiết lập Spinner
//        setupSpinner();
//
//        // Thiết lập DatePicker cho các EditText ngày
//        setupDatePickers();
//
//        // Xử lý sự kiện nút Lưu
//        binding.btnSaveSystem.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                saveSystemConfig();
//            }
//        });
//    }
//
//    // Thiết lập Spinner với các tùy chọn (giả định là loại tiền tệ)
//    private void setupSpinner() {
//        String[] currencyOptions = {"VND", "USD", "EUR"};
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencyOptions);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        binding.spinnerFromValuePrice.setAdapter(adapter);
//    }
//
//    // Thiết lập DatePicker cho EditText ngày bắt đầu và ngày kết thúc
//    private void setupDatePickers() {
//        final Calendar calendar = Calendar.getInstance();
//
//        // DatePicker cho Ngày bắt đầu
//        binding.editFromDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int year = calendar.get(Calendar.YEAR);
//                int month = calendar.get(Calendar.MONTH);
//                int day = calendar.get(Calendar.DAY_OF_MONTH);
//
//                DatePickerDialog datePickerDialog = new DatePickerDialog(
//                        Admin_Point_Activity.this,
//                        (view, selectedYear, selectedMonth, selectedDay) -> {
//                            String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
//                            binding.editFromDate.setText(date);
//                        },
//                        year, month, day);
//                datePickerDialog.show();
//            }
//        });
//
//        // DatePicker cho Ngày kết thúc
//        binding.editEndDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int year = calendar.get(Calendar.YEAR);
//                int month = calendar.get(Calendar.MONTH);
//                int day = calendar.get(Calendar.DAY_OF_MONTH);
//
//                DatePickerDialog datePickerDialog = new DatePickerDialog(
//                        Admin_Point_Activity.this,
//                        (view, selectedYear, selectedMonth, selectedDay) -> {
//                            String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
//                            binding.editEndDate.setText(date);
//                        },
//                        year, month, day);
//                datePickerDialog.show();
//            }
//        });
//    }
//
//    // Tải dữ liệu mẫu (thay bằng nguồn dữ liệu thực tế)
//    private void loadConfigs() {
//        configList.clear();
//        configList.add(new SystemConfig("01/01/2025", "31/12/2025", 1000000, "VND", 100));
//        configList.add(new SystemConfig("01/02/2025", "28/02/2025", 50000, "USD", 50));
//        configList.add(new SystemConfig("01/03/2025", "31/03/2025", 2000000, "VND", 200));
//        configAdapter.notifyDataSetChanged();
//    }
//
//    // Lưu cấu hình hệ thống
//    private void saveSystemConfig() {
//        String fromDate = binding.editFromDate.getText().toString().trim();
//        String endDate = binding.editEndDate.getText().toString().trim();
//        String fromValuePriceStr = binding.editFromValuePrice.getText().toString().trim();
//        String currency = binding.spinnerFromValuePrice.getSelectedItem().toString();
//        String toValuePointStr = binding.editToValuePoint.getText().toString().trim();
//
//        // Kiểm tra dữ liệu đầu vào
//        if (fromDate.isEmpty() || endDate.isEmpty() || fromValuePriceStr.isEmpty() || toValuePointStr.isEmpty()) {
//            // Hiển thị thông báo lỗi nếu cần (ví dụ: Toast)
//            return;
//        }
//
//        int fromValuePrice = Integer.parseInt(fromValuePriceStr);
//        int toValuePoint = Integer.parseInt(toValuePointStr);
//
//        // Tạo đối tượng SystemConfig mới
//        SystemConfig newConfig = new SystemConfig(fromDate, endDate, fromValuePrice, currency, toValuePoint);
//
//        // Thêm vào danh sách và cập nhật RecyclerView
//        configList.add(newConfig);
//        configAdapter.notifyDataSetChanged();
//
//        // Xóa các trường nhập sau khi lưu
//        binding.editFromDate.setText("");
//        binding.editEndDate.setText("");
//        binding.editFromValuePrice.setText("");
//        binding.editToValuePoint.setText("");
//    }
    }
}