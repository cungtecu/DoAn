package com.example.doan.admin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.doan.R;
import com.example.doan.api.ApiService;
import com.example.doan.api.RetrofitClient;
import com.example.doan.models.Currencies;
import com.example.doan.models.SystemConfig;
import com.example.doan.models.SystemConfigAdapter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import java.util.Calendar;

public class Admin_SystemConfig_Activity extends AppCompatActivity implements SystemConfigAdapter.OnSystemConfigClickListener {

    private EditText editFromDate, editEndDate, editFromValuePrice, editToValuePoint;
    private Spinner spinnerCurrencies;
    private Button btnAddSystem;
    private RecyclerView rcvSystemConfig;
    private SystemConfigAdapter systemConfigAdapter;
    private List<SystemConfig> systemConfigList;
    private List<Currencies> currenciesList;
    private String authToken;
    private ImageView btnCurrencies;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private boolean isProcessing = false;
    private static final int EDIT_SYSTEM_CONFIG_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_systemconfig);

        // Lấy token từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        authToken = "Bearer " + prefs.getString("token", null);
        if (authToken == null) {
            Toast.makeText(this, "Không tìm thấy token, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Ánh xạ view
        editFromDate = findViewById(R.id.editFromDate);
        editEndDate = findViewById(R.id.editEndDate);
        editFromValuePrice = findViewById(R.id.edit_FromValuePrice);
        editToValuePoint = findViewById(R.id.edit_ToValuePoint);
        spinnerCurrencies = findViewById(R.id.spinnerCurrencies);
        btnAddSystem = findViewById(R.id.btnAddSystem);
        rcvSystemConfig = findViewById(R.id.rcv_systemconfig);
        btnCurrencies = findViewById(R.id.btnCurrencies);

        // Thiết lập RecyclerView
        systemConfigList = new ArrayList<>();
        systemConfigAdapter = new SystemConfigAdapter(this, systemConfigList, this);
        rcvSystemConfig.setLayoutManager(new LinearLayoutManager(this));
        rcvSystemConfig.setAdapter(systemConfigAdapter);

        // Thiết lập DatePicker và TimePicker cho fromDate và endDate
        setupDateTimePicker(editFromDate);
        setupDateTimePicker(editEndDate);

        // Tải danh sách Currencies và SystemConfig
        currenciesList = new ArrayList<>();
        loadCurrencies();
        loadSystemConfigs();

        // Xử lý sự kiện nút Thêm
        btnAddSystem.setOnClickListener(v -> {
            if (!isProcessing) {
                addSystemConfig();
            }
        });
        btnCurrencies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Admin_SystemConfig_Activity.this, Admin_Currencies_Activity.class);
                startActivity(intent);
            }
        });
    }

    private void setupDateTimePicker(EditText editText) {
        editText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        calendar.set(selectedYear, selectedMonth, selectedDay);
                        TimePickerDialog timePickerDialog = new TimePickerDialog(
                                this,
                                (timeView, selectedHour, selectedMinute) -> {
                                    calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                                    calendar.set(Calendar.MINUTE, selectedMinute);
                                    LocalDateTime dateTime = LocalDateTime.ofInstant(
                                            calendar.toInstant(), calendar.getTimeZone().toZoneId());
                                    editText.setText(dateTime.format(dateFormatter));
                                },
                                hour, minute, true);
                        timePickerDialog.show();
                    },
                    year, month, day);
            datePickerDialog.show();
        });
    }

    private class CurrenciesAdapter extends ArrayAdapter<Currencies> {
        public CurrenciesAdapter(Context context, List<Currencies> currencies) {
            super(context, android.R.layout.simple_spinner_item, currencies);
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getView(position, convertView, parent);
            Currencies currency = getItem(position);
            if (currency != null) {
                view.setText(currency.getCurrencyCode());
            }
            return view;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getDropDownView(position, convertView, parent);
            Currencies currency = getItem(position);
            if (currency != null) {
                view.setText(currency.getCurrencyCode());
            }
            return view;
        }
    }

    private void loadCurrencies() {
        ApiService apiService = RetrofitClient.getApiService(this);
        Call<List<Currencies>> call = apiService.getAllCurrencies(authToken);
        call.enqueue(new Callback<List<Currencies>>() {
            @Override
            public void onResponse(Call<List<Currencies>> call, Response<List<Currencies>> response) {
                if (response.isSuccessful()) {
                    currenciesList.clear();
                    currenciesList.addAll(response.body());
                    CurrenciesAdapter adapter = new CurrenciesAdapter(Admin_SystemConfig_Activity.this, currenciesList);
                    spinnerCurrencies.setAdapter(adapter);
                } else {
                        Toast.makeText(Admin_SystemConfig_Activity.this, "Lỗi tải danh mục tiền tệ: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

            @Override
            public void onFailure(Call<List<Currencies>> call, Throwable t) {
                Toast.makeText(Admin_SystemConfig_Activity.this, "Lỗi kết nối1: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadSystemConfigs() {
        ApiService apiService = RetrofitClient.getApiService(this);
        Call<List<SystemConfig>> call = apiService.getAllSystemConfigs(authToken);
        call.enqueue(new Callback<List<SystemConfig>>() {
            @Override
            public void onResponse(Call<List<SystemConfig>> call, Response<List<SystemConfig>> response) {
                if (response.isSuccessful()) {
                    systemConfigList.clear();
                    systemConfigList.addAll(response.body());
                    systemConfigAdapter.updateSystemConfigs(systemConfigList);
                } else {
                        Toast.makeText(Admin_SystemConfig_Activity.this, "Lỗi tải danh sách cấu hình: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
            }
            @Override
            public void onFailure(Call<List<SystemConfig>> call, Throwable t) {
                Toast.makeText(Admin_SystemConfig_Activity.this, "Lỗi kết nối2: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addSystemConfig() {
        if (isProcessing) return;
        isProcessing = true;
        btnAddSystem.setEnabled(false);

        String fromValuePriceStr = editFromValuePrice.getText().toString().trim();
        String toValuePointStr = editToValuePoint.getText().toString().trim();
        String fromDateStr = editFromDate.getText().toString().trim();
        String thruDateStr = editEndDate.getText().toString().trim();
        Currencies selectedCurrency = (Currencies) spinnerCurrencies.getSelectedItem();

        if (TextUtils.isEmpty(fromValuePriceStr) || TextUtils.isEmpty(toValuePointStr) || TextUtils.isEmpty(fromDateStr) || selectedCurrency == null) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            isProcessing = false;
            btnAddSystem.setEnabled(true);
            return;
        }

        if (selectedCurrency.getId() <= 0) {
            Toast.makeText(this, "Tiền tệ không hợp lệ", Toast.LENGTH_SHORT).show();
            isProcessing = false;
            btnAddSystem.setEnabled(true);
            return;
        }

        BigDecimal fromValuePrice, toValuePoint;
        try {
            fromValuePrice = new BigDecimal(fromValuePriceStr);
            toValuePoint = new BigDecimal(toValuePointStr);
            if (fromValuePrice.compareTo(BigDecimal.ZERO) <= 0 || toValuePoint.compareTo(BigDecimal.ZERO) <= 0) {
                Toast.makeText(this, "Giá trị và điểm phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                isProcessing = false;
                btnAddSystem.setEnabled(true);
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Giá trị hoặc điểm không hợp lệ", Toast.LENGTH_SHORT).show();
            isProcessing = false;
            btnAddSystem.setEnabled(true);
            return;
        }

        try {
            SystemConfig newConfig = new SystemConfig();
            newConfig.setFromValuePrice(fromValuePrice);
            newConfig.setToValuePoint(toValuePoint);
            newConfig.setFromDate(LocalDateTime.parse(fromDateStr, dateFormatter));
            newConfig.setThruDate(TextUtils.isEmpty(thruDateStr) ? null : LocalDateTime.parse(thruDateStr, dateFormatter));
            newConfig.setCurrency(selectedCurrency);

            ApiService apiService = RetrofitClient.getApiService(this);
            Call<SystemConfig> call = apiService.addSystemConfig(authToken, newConfig);
            call.enqueue(new Callback<SystemConfig>() {
                @Override
                public void onResponse(Call<SystemConfig> call, Response<SystemConfig> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        SystemConfig addedConfig = response.body();
                        if (addedConfig.getId() <= 0) {
                            Toast.makeText(Admin_SystemConfig_Activity.this, "Lỗi: ID cấu hình không hợp lệ từ server", Toast.LENGTH_LONG).show();
                        } else {
                            clearInputs();
                            loadSystemConfigs(); // Tự động tải lại danh sách
                            Toast.makeText(Admin_SystemConfig_Activity.this, "Thêm cấu hình thành công!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        try {
                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                            Toast.makeText(Admin_SystemConfig_Activity.this, "Lỗi khi thêm: " + errorBody + " (Mã: " + response.code() + ")", Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            Toast.makeText(Admin_SystemConfig_Activity.this, "Lỗi khi thêm: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    isProcessing = false;
                    btnAddSystem.setEnabled(true);
                }

                @Override
                public void onFailure(Call<SystemConfig> call, Throwable t) {
                    Toast.makeText(Admin_SystemConfig_Activity.this, "Lỗi kết nối3: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    isProcessing = false;
                    btnAddSystem.setEnabled(true);
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi định dạng dữ liệu: " + e.getMessage(), Toast.LENGTH_LONG).show();
            isProcessing = false;
            btnAddSystem.setEnabled(true);
        }
    }

    private void clearInputs() {
        editFromValuePrice.setText("");
        editToValuePoint.setText("");
        editFromDate.setText("");
        editEndDate.setText("");
        if (spinnerCurrencies.getAdapter() != null && spinnerCurrencies.getAdapter().getCount() > 0) {
            spinnerCurrencies.setSelection(0);
        }
    }

    @Override
    public void onEditClick(SystemConfig config) {
        if (config.getId() <= 0) {
            Toast.makeText(this, "ID cấu hình không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, Admin_SystemConfig_Edit_Activity.class);
        intent.putExtra("CONFIG_ID", config.getId());
        intent.putExtra("FROM_VALUE_PRICE", config.getFromValuePrice() != null ? config.getFromValuePrice().toString() : "");
        intent.putExtra("TO_VALUE_POINT", config.getToValuePoint() != null ? config.getToValuePoint().toString() : "");
        intent.putExtra("FROM_DATE", config.getFromDate() != null ? config.getFromDate().format(dateFormatter) : "");
        intent.putExtra("THRU_DATE", config.getThruDate() != null ? config.getThruDate().format(dateFormatter) : "");
        intent.putExtra("CURRENCY_CODE", config.getCurrency() != null ? config.getCurrency().getCurrencyCode() : "");
        startActivityForResult(intent, EDIT_SYSTEM_CONFIG_REQUEST);
    }

    @Override
    public void onDeleteClick(SystemConfig config) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa cấu hình này?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteSystemConfig(config.getId()))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteSystemConfig(int configId) {
        if (isProcessing || configId <= 0) {
            Toast.makeText(this, "ID cấu hình không hợp lệ hoặc đang xử lý", Toast.LENGTH_SHORT).show();
            return;
        }
        isProcessing = true;

        ApiService apiService = RetrofitClient.getApiService(this);
        Call<Void> call = apiService.deleteSystemConfig(authToken, configId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    systemConfigList.removeIf(config -> config.getId() == configId);
                    systemConfigAdapter.notifyDataSetChanged();
                    Toast.makeText(Admin_SystemConfig_Activity.this, "Xóa cấu hình thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Toast.makeText(Admin_SystemConfig_Activity.this, "Lỗi khi xóa: " + errorBody + " (Mã: " + response.code() + ")", Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        Toast.makeText(Admin_SystemConfig_Activity.this, "Lỗi khi xóa: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
                isProcessing = false;
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(Admin_SystemConfig_Activity.this, "Lỗi kết nối4: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                isProcessing = false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_SYSTEM_CONFIG_REQUEST && resultCode == RESULT_OK) {
            loadSystemConfigs();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSystemConfigs();
    }
}