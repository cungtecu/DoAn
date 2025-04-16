package com.example.doan.admin;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.doan.R;
import com.example.doan.api.ApiService;
import com.example.doan.api.RetrofitClient;
import com.example.doan.models.Currencies;
import com.example.doan.models.CurrenciesAdapter;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Admin_Currencies_Activity extends AppCompatActivity {

    private RecyclerView rcvCurrencies;
    private CurrenciesAdapter currenciesAdapter;
    private List<Currencies> currenciesList;
    private Button btnAddCurrencies;
    private EditText editCurrencyName, editCurrencyCode;
    private String authToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_currencies);

        // Lấy token từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        authToken = "Bearer " + prefs.getString("token", null);
        if (authToken == null) {
            Toast.makeText(this, "Không tìm thấy token, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Khởi tạo views
        rcvCurrencies = findViewById(R.id.rcv_currencies);
        btnAddCurrencies = findViewById(R.id.btnAddCurrencies);
        editCurrencyName = findViewById(R.id.editcurrencyName);
        editCurrencyCode = findViewById(R.id.editcurrencyCode);

        // Thiết lập RecyclerView
        currenciesList = new ArrayList<>();
        currenciesAdapter = new CurrenciesAdapter(this, currenciesList, authToken);
        rcvCurrencies.setLayoutManager(new LinearLayoutManager(this));
        rcvCurrencies.setAdapter(currenciesAdapter);

        // Sự kiện nút Thêm
        btnAddCurrencies.setOnClickListener(v -> addCurrency());

        // Tải dữ liệu
        loadCurrencies();
    }

    private void addCurrency() {
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

        Currencies currency = new Currencies(null, currencyCode, currencyName);
        ApiService apiService = RetrofitClient.getApiService(this);
        Call<Currencies> call = apiService.addCurrency(authToken, currency);

        call.enqueue(new Callback<Currencies>() {
            @Override
            public void onResponse(Call<Currencies> call, Response<Currencies> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Admin_Currencies_Activity.this, "Thêm tiền tệ thành công", Toast.LENGTH_SHORT).show();
                    editCurrencyName.setText("");
                    editCurrencyCode.setText("");
                    loadCurrencies(); // Tải lại danh sách
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Toast.makeText(Admin_Currencies_Activity.this, "Lỗi: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(Admin_Currencies_Activity.this, "Lỗi: " + response.code(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Currencies> call, Throwable t) {
                Toast.makeText(Admin_Currencies_Activity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadCurrencies() {
        ApiService apiService = RetrofitClient.getApiService(this);
        Call<List<Currencies>> call = apiService.getAllCurrencies(authToken);
        call.enqueue(new Callback<List<Currencies>>() {
            @Override
            public void onResponse(Call<List<Currencies>> call, Response<List<Currencies>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currenciesList.clear();
                    currenciesList.addAll(response.body());
                    currenciesAdapter.updateCurrencies(currenciesList);
                    if (currenciesList.isEmpty()) {
                        Toast.makeText(Admin_Currencies_Activity.this, "Không có tiền tệ nào", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Admin_Currencies_Activity.this, "Lỗi tải danh sách tiền tệ: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Currencies>> call, Throwable t) {
                Toast.makeText(Admin_Currencies_Activity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCurrencies(); // Tải lại danh sách khi quay lại từ Edit Activity
    }
}