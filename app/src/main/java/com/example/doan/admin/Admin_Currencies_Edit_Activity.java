package com.example.doan.admin;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.doan.R;
import com.example.doan.api.ApiService;
import com.example.doan.api.RetrofitClient;
import com.example.doan.models.Currencies;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Admin_Currencies_Edit_Activity extends AppCompatActivity {

    private EditText editCurrencyName, editCurrencyCode;
    private Button btnSaveCurrencies;
    private String authToken;
    private int currencyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_currencies_edit);

        // Lấy token từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        authToken = "Bearer " + prefs.getString("token", null);
        if (authToken == null) {
            Toast.makeText(this, "Không tìm thấy token, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Khởi tạo views
        editCurrencyName = findViewById(R.id.editcurrencyName);
        editCurrencyCode = findViewById(R.id.editcurrencyCode);
        btnSaveCurrencies = findViewById(R.id.btnSaveCurrencies);

        // Lấy dữ liệu từ Intent
        currencyId = getIntent().getIntExtra("CURRENCY_ID", -1);
        if (currencyId != -1) {
            String currencyName = getIntent().getStringExtra("CURRENCY_NAME");
            String currencyCode = getIntent().getStringExtra("CURRENCY_CODE");
            editCurrencyName.setText(currencyName);
            editCurrencyCode.setText(currencyCode);
        }

        // Xử lý nút Lưu
        btnSaveCurrencies.setOnClickListener(v -> saveCurrency());
    }

    private void saveCurrency() {
        String currencyName = editCurrencyName.getText().toString().trim();
        String currencyCode = editCurrencyCode.getText().toString().trim();

        if (currencyName.isEmpty() || currencyCode.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currencyCode.length() > 3) {
            Toast.makeText(this, "Mã tiền tệ phải có tối đa 3 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }

        Currencies currency = new Currencies(currencyId, currencyCode, currencyName);
        ApiService apiService = RetrofitClient.getApiService(this);
        Call<Currencies> call = apiService.updateCurrency(authToken, currencyId, currency);

        call.enqueue(new Callback<Currencies>() {
            @Override
            public void onResponse(Call<Currencies> call, Response<Currencies> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Admin_Currencies_Edit_Activity.this, "Cập nhật tiền tệ thành công", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Toast.makeText(Admin_Currencies_Edit_Activity.this, "Lỗi: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(Admin_Currencies_Edit_Activity.this, "Lỗi: " + response.code(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Currencies> call, Throwable t) {
                Toast.makeText(Admin_Currencies_Edit_Activity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}