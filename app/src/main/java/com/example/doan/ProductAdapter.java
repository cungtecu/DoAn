package com.example.doan;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.doan.models.Product;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;
    private List<Product> productListFull;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList != null ? new ArrayList<>(productList) : new ArrayList<>();
        this.productListFull = productList != null ? new ArrayList<>(productList) : new ArrayList<>();
        Log.d("ProductAdapter", "Initialized with " + this.productList.size() + " products");
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.menu_item_order, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        if (productList == null || position >= productList.size()) {
            Log.e("ProductAdapter", "Invalid position: " + position + ", list size: " + (productList != null ? productList.size() : 0));
            return;
        }

        Product product = productList.get(position);
        holder.productNameTextView.setText(product.getName() != null ? product.getName() : "N/A");
        holder.productPriceTextView.setText(product.getPrice() != 0 ? String.format("%,.0f VNĐ", product.getPrice()) : "N/A");
        Log.d("ProductAdapter", "Binding product: " + product.getName() + ", position: " + position);

        if (product.getImage() != null && !product.getImage().isEmpty()) {
            Glide.with(context)
                    .load(product.getImage())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_gallery)
                    .into(holder.productImageView);
        } else {
            holder.productImageView.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        if (product.getDescription() != null && !product.getDescription().isEmpty()) {
            holder.productDescriptionTextView.setText(product.getDescription());
            holder.productDescriptionTextView.setVisibility(View.VISIBLE);
        } else {
            holder.productDescriptionTextView.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            Log.d("ProductAdapter", "Product clicked: " + product.getId());
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("productId", product.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        int count = productList != null ? productList.size() : 0;
        Log.d("ProductAdapter", "Item count: " + count);
        return count;
    }

    public void updateProducts(List<Product> newProductList) {
        this.productList = newProductList != null ? new ArrayList<>(newProductList) : new ArrayList<>();
        this.productListFull = newProductList != null ? new ArrayList<>(newProductList) : new ArrayList<>();
        Log.d("ProductAdapter", "Updated products: " + this.productList.size());
        notifyDataSetChanged();
    }

    public void filter(String query) {
        productList.clear();
        if (query == null || query.trim().isEmpty()) {
            productList.addAll(productListFull);
        } else {
            String normalizedQuery = normalizeString(query.toLowerCase());
            for (Product product : productListFull) {
                String normalizedProductName = normalizeString(product.getName() != null ? product.getName().toLowerCase() : "");
                if (normalizedProductName.contains(normalizedQuery)) {
                    productList.add(product);
                }
            }
        }
        notifyDataSetChanged();
    }

    private String normalizeString(String input) {
        if (input == null) return "";
        // Bỏ dấu tiếng Việt
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        // Giữ khoảng trắng, chỉ chuẩn hóa hoa/thường
        return normalized;
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImageView;
        TextView productNameTextView;
        TextView productPriceTextView;
        TextView productDescriptionTextView;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.product_image);
            productNameTextView = itemView.findViewById(R.id.product_name);
            productPriceTextView = itemView.findViewById(R.id.product_price);
            productDescriptionTextView = itemView.findViewById(R.id.product_description);
            if (productImageView == null || productNameTextView == null || productPriceTextView == null) {
                Log.e("ProductAdapter", "ViewHolder initialization failed: missing views");
            }
        }
    }
}