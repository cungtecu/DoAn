package com.example.doan.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doan.R;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

public class PriceHistoryAdapter extends RecyclerView.Adapter<PriceHistoryAdapter.PriceHistoryViewHolder> {

    private Context context;
    private List<PriceHistory> priceHistoryList;
    private Map<Integer, String> productNameMap;
    private Map<Integer, String> productImageMap; // Thêm ánh xạ ảnh
    private String singleProductName;
    private String singleProductImage; // Để tương thích với Admin_HistoryPrice_Activity

    // Constructor cho Admin_AllPriceHistory_Activity
    public PriceHistoryAdapter(Context context, List<PriceHistory> priceHistoryList,
                               Map<Integer, String> productNameMap, Map<Integer, String> productImageMap) {
        this.context = context;
        this.priceHistoryList = priceHistoryList;
        this.productNameMap = productNameMap;
        this.productImageMap = productImageMap;
        this.singleProductName = null;
        this.singleProductImage = null;
    }

    // Constructor cho Admin_HistoryPrice_Activity
    public PriceHistoryAdapter(Context context, List<PriceHistory> priceHistoryList,
                               String productName, String productImage) {
        this.context = context;
        this.priceHistoryList = priceHistoryList;
        this.singleProductName = productName;
        this.singleProductImage = productImage;
        this.productNameMap = null;
        this.productImageMap = null;
    }

    @NonNull
    @Override
    public PriceHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_pricehistory_item, parent, false);
        return new PriceHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PriceHistoryViewHolder holder, int position) {
        PriceHistory priceHistory = priceHistoryList.get(position);

        DecimalFormat df = new DecimalFormat("#,##0 VNĐ");
        // Hiển thị tên sản phẩm
        if (singleProductName != null) {
            holder.tvNameProduct.setText(singleProductName);
        } else {
            String productName = productNameMap != null && priceHistory.getProductId() != null
                    ? productNameMap.getOrDefault(priceHistory.getProductId(), "N/A")
                    : "N/A";
            holder.tvNameProduct.setText(productName);
        }

        // Hiển thị ảnh sản phẩm
        String imageUrl;
        if (singleProductImage != null) {
            imageUrl = singleProductImage;
        } else {
            imageUrl = productImageMap != null && priceHistory.getProductId() != null
                    ? productImageMap.getOrDefault(priceHistory.getProductId(), null)
                    : null;
        }
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery) // Ảnh mặc định khi đang tải
                    .error(android.R.drawable.ic_menu_gallery) // Ảnh khi lỗi
                    .into(holder.productImage);
        } else {
            holder.productImage.setImageResource(android.R.drawable.ic_menu_gallery); // Ảnh mặc định
        }

        // Hiển thị giá và ngày
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

    private String formatDate(String isoDate) {
        try {
            return isoDate.replace("T", " ").substring(0, 19);
        } catch (Exception e) {
            return isoDate;
        }
    }

    static class PriceHistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvNameProduct, tvOldPrice, tvNewPrice, tvChangedAt;
        ImageView productImage;

        public PriceHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNameProduct = itemView.findViewById(R.id.tv_nameProduct);
            tvOldPrice = itemView.findViewById(R.id.tv_oldprice);
            tvNewPrice = itemView.findViewById(R.id.tv_newprice);
            tvChangedAt = itemView.findViewById(R.id.tv_datechange);
            productImage = itemView.findViewById(R.id.product_image);
        }
    }
}