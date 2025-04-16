package com.example.doan.admin;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.doan.R;
import com.example.doan.api.ApiService;
import com.example.doan.api.RetrofitClient;
import com.example.doan.models.Currencies;
import com.example.doan.models.SystemConfig;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Admin_SystemConfig_Edit_Activity extends AppCompatActivity {

    private EditText editFromDate, editEndDate, editFromValuePrice, editToValuePoint;
    private Spinner spinnerCurrencies;
    private Button btnSaveSystem;
    private List<Currencies> currenciesList;
    private Currencies selectedCurrency;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private int configId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_systemconfig_edit);

        // Ánh xạ view
        editFromDate = findViewById(R.id.editFromDate);
        editEndDate = findViewById(R.id.editEndDate);
        editFromValuePrice = findViewById(R.id.edit_FromValuePrice);
        editToValuePoint = findViewById(R.id.edit_ToValuePoint);
        spinnerCurrencies = findViewById(R.id.spinnerCurrencies);
        btnSaveSystem = findViewById(R.id.btnSaveSystem);

        // Thiết lập DatePicker và TimePicker
        setupDateTimePicker(editFromDate);
        setupDateTimePicker(editEndDate);

        // Lấy dữ liệu từ Intent
        configId = getIntent().getIntExtra("CONFIG_ID", -1);
        editFromValuePrice.setText(getIntent().getStringExtra("FROM_VALUE_PRICE"));
        editToValuePoint.setText(getIntent().getStringExtra("TO_VALUE_POINT"));
        editFromDate.setText(getIntent().getStringExtra("FROM_DATE"));
        editEndDate.setText(getIntent().getStringExtra("THRU_DATE"));
        String currencyCode = getIntent().getStringExtra("CURRENCY_CODE");

        // Tải danh sách Currencies
        loadCurrencies(currencyCode);

        // Xử lý nút Lưu
        btnSaveSystem.setOnClickListener(v -> saveSystemConfig());

        // Xử lý chọn Currency từ Spinner
        spinnerCurrencies.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCurrency = currenciesList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCurrency = null;
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

    private void loadCurrencies(String selectedCurrencyCode) {
        ApiService apiService = RetrofitClient.getApiService(this);
        Call<List<Currencies>> call = apiService.getAllCurrencies("MyAppPrefs");
        call.enqueue(new Callback<List<Currencies>>() {
            @Override
            public void onResponse(Call<List<Currencies>> call, Response<List<Currencies>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currenciesList = response.body();
                    List<String> currencyCodes = new ArrayList<>();
                    int selectedPosition = 0;
                    for (int i = 0; i < currenciesList.size(); i++) {
                        currencyCodes.add(currenciesList.get(i).getCurrencyCode());
                        if (currenciesList.get(i).getCurrencyCode().equals(selectedCurrencyCode)) {
                            selectedPosition = i;
                        }
                    }
                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                            Admin_SystemConfig_Edit_Activity.this,
                            android.R.layout.simple_spinner_item,
                            currencyCodes);
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCurrencies.setAdapter(spinnerAdapter);
                    spinnerCurrencies.setSelection(selectedPosition);
                } else {
                    handleApiError(response);
                }
            }

            @Override
            public void onFailure(Call<List<Currencies>> call, Throwable t) {
                Toast.makeText(Admin_SystemConfig_Edit_Activity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveSystemConfig() {
        String fromValuePriceStr = editFromValuePrice.getText().toString().trim();
        String toValuePointStr = editToValuePoint.getText().toString().trim();
        String fromDateStr = editFromDate.getText().toString().trim();
        String thruDateStr = editEndDate.getText().toString().trim();

        if (fromValuePriceStr.isEmpty() || toValuePointStr.isEmpty() || fromDateStr.isEmpty() || selectedCurrency == null) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            SystemConfig config = new SystemConfig();
            config.setId(configId);
            config.setFromValuePrice(new BigDecimal(fromValuePriceStr));
            config.setToValuePoint(new BigDecimal(toValuePointStr));
            config.setFromDate(LocalDateTime.parse(fromDateStr, dateFormatter));
            config.setThruDate(thruDateStr.isEmpty() ? null : LocalDateTime.parse(thruDateStr, dateFormatter));
            config.setCurrency(selectedCurrency);

            ApiService apiService = RetrofitClient.getApiService(this);
            Call<SystemConfig> call = apiService.updateSystemConfig("", configId, config);
            call.enqueue(new Callback<SystemConfig>() {
                @Override
                public void onResponse(Call<SystemConfig> call, Response<SystemConfig> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(Admin_SystemConfig_Edit_Activity.this, "Cập nhật cấu hình thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        handleApiError(response);
                    }
                }

                @Override
                public void onFailure(Call<SystemConfig> call, Throwable t) {
                    Toast.makeText(Admin_SystemConfig_Edit_Activity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi định dạng dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void handleApiError(Response<?> response) {
        String errorMessage = "Lỗi không xác định";
        if (response.code() == 401) {
            errorMessage = "Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.";
            // TODO: Chuyển hướng đến màn hình đăng nhập
        } else if (response.code() == 403) {
            errorMessage = "Bạn không có quyền truy cập chức năng này.";
        } else if (response.errorBody() != null) {
            try {
                errorMessage = response.errorBody().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }
}