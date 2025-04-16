package com.example.doan.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan.R;
import com.example.doan.models.PriceHistory;
import java.text.DecimalFormat;
import java.util.List;

public class PriceHistoryAdapter extends RecyclerView.Adapter<PriceHistoryAdapter.PriceHistoryViewHolder> {

    private Context context;
    private List<PriceHistory> priceHistoryList;
    private String productName; // Tên sản phẩm để hiển thị

    public PriceHistoryAdapter(Context context, List<PriceHistory> priceHistoryList, String productName) {
        this.context = context;
        this.priceHistoryList = priceHistoryList;
        this.productName = productName;
    }

    @NonNull
    @Override
    public PriceHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_item_historyprice, parent, false);
        return new PriceHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PriceHistoryViewHolder holder, int position) {
        PriceHistory priceHistory = priceHistoryList.get(position);

        DecimalFormat df = new DecimalFormat("#,##0 VNĐ");
        holder.tvNameProduct.setText(productName != null ? productName : "N/A");
        holder.tvOldPrice.setText(priceHistory.getOldPrice() != null ? df.format(priceHistory.getOldPrice()) : "N/A");
        holder.tvNewPrice.setText(df.format(priceHistory.getNewPrice()));
        holder.tvChangedAt.setText(priceHistory.getChangedAt() != null ? formatDate(priceHistory.getChangedAt()) : "N/A");
    }

    @Override
    public int getItemCount() {
        return priceHistoryList != null ? priceHistoryList.size() : 0;
    }

    public void updatePriceHistory(List<PriceHistory> newPriceHistoryList) {
        this.priceHistoryList = newPriceHistoryList;
        notifyDataSetChanged();
    }

    // Định dạng ngày từ ISO sang dạng dễ đọc
    private String formatDate(String isoDate) {
        try {
            // Giả sử changedAt là chuỗi ISO như "2025-04-15T10:00:00"
            return isoDate.replace("T", " ").substring(0, 19); // Cắt bỏ mili giây nếu có
        } catch (Exception e) {
            return isoDate;
        }
    }

    static class PriceHistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvNameProduct, tvOldPrice, tvNewPrice, tvChangedAt;

        public PriceHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNameProduct = itemView.findViewById(R.id.tv_nameProduct);
            tvOldPrice = itemView.findViewById(R.id.tv_oldprice);
            tvNewPrice = itemView.findViewById(R.id.tv_newprice);
            tvChangedAt = itemView.findViewById(R.id.tv_datechange);
        }
    }
}