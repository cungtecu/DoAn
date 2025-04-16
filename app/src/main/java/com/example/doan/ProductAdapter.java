package com.example.doan;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.doan.models.Product;

import de.hdodenhof.circleimageview.CircleImageView;
import java.text.DecimalFormat;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;
    private OnProductClickListener listener;
    private String authToken;

    public ProductAdapter(Context context, List<Product> productList, OnProductClickListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
        // Lấy token từ SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        authToken = "Bearer " + prefs.getString("token", null);
    }

    public ProductAdapter(OrderActivity context, List<Product> productList) {
    }

    public interface OnProductClickListener {
        void onEditClick(Product product);
        void onDeleteClick(Product product);
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_product_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.productName.setText(product.getName());
        DecimalFormat df = new DecimalFormat("#,##0");
        holder.productPrice.setText(df.format(product.getPrice()) + " VNĐ");

        if (product.getImage() != null && !product.getImage().isEmpty()) {
            Glide.with(context)
                    .load(product.getImage())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(holder.productImage);
        } else {
            holder.productImage.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(product));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(product));
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        CircleImageView productImage;
        TextView productName;
        TextView productPrice;
        ImageButton btnEdit;
        ImageButton btnDelete;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            btnEdit = itemView.findViewById(R.id.btn_edit_product);
            btnDelete = itemView.findViewById(R.id.btn_delete_product);
        }
    }

    public void updateProducts(List<Product> newProductList) {
        this.productList = newProductList;
        notifyDataSetChanged();
    }

    public String getAuthToken() {
        return authToken;
    }
}