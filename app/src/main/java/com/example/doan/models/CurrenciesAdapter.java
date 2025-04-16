package com.example.doan.models;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.example.doan.R;
import com.example.doan.admin.Admin_Currencies_Edit_Activity;
import com.example.doan.api.ApiService;
import com.example.doan.api.RetrofitClient;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CurrenciesAdapter extends RecyclerView.Adapter<CurrenciesAdapter.CurrenciesViewHolder> {

    private Context context;
    private List<Currencies> currenciesList;
    private String authToken;

    public CurrenciesAdapter(Context context, List<Currencies> currenciesList, String authToken) {
        this.context = context;
        this.currenciesList = currenciesList;
        this.authToken = authToken;
    }

    @NonNull
    @Override
    public CurrenciesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_currencies_item, parent, false);
        return new CurrenciesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CurrenciesViewHolder holder, int position) {
        Currencies currency = currenciesList.get(position);
        holder.tvCurrencyName.setText(currency.getCurrencyName());
        holder.tvCurrencyCode.setText(currency.getCurrencyCode());

        // Xử lý nút chỉnh sửa
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, Admin_Currencies_Edit_Activity.class);
            intent.putExtra("CURRENCY_ID", currency.getId());
            intent.putExtra("CURRENCY_NAME", currency.getCurrencyName());
            intent.putExtra("CURRENCY_CODE", currency.getCurrencyCode());
            context.startActivity(intent);
        });

        // Xử lý nút xóa
        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc muốn xóa tiền tệ " + currency.getCurrencyName() + "?")
                    .setPositiveButton("Xóa", (dialog, which) -> deleteCurrency(currency.getId(), position))
                    .setNegativeButton("Hủy", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return currenciesList != null ? currenciesList.size() : 0;
    }

    private void deleteCurrency(Integer id, int position) {
        ApiService apiService = RetrofitClient.getApiService(context);
        Call<Void> call = apiService.deleteCurrency(authToken, id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    currenciesList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, currenciesList.size());
                } else {
                    // Handle error
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Handle failure
            }
        });
    }

    public void updateCurrencies(List<Currencies> newCurrencies) {
        this.currenciesList = newCurrencies;
        notifyDataSetChanged();
    }

    static class CurrenciesViewHolder extends RecyclerView.ViewHolder {
        TextView tvCurrencyName, tvCurrencyCode;
        ImageButton btnEdit, btnDelete;

        public CurrenciesViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCurrencyName = itemView.findViewById(R.id.tv_currencyName);
            tvCurrencyCode = itemView.findViewById(R.id.tv_currencyCode);
            btnEdit = itemView.findViewById(R.id.btn_edit_currencies);
            btnDelete = itemView.findViewById(R.id.btn_delete_currencies);
        }
    }
}