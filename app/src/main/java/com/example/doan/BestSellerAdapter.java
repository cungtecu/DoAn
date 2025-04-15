//package com.example.doan;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//import com.example.doan.models.Product;
//import java.util.List;
//
//public class BestSellerAdapter extends RecyclerView.Adapter<BestSellerAdapter.ProductViewHolder> {
//    private List<Product> productList;
//
//    public BestSellerAdapter(List<Product> productList) {
//        this.productList = productList;
//    }
//
//    @Override
//    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_best_seller, parent, false);
//        return new ProductViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(ProductViewHolder holder, int position) {
//        Product product = productList.get(position);
//        holder.imgProduct.setImageResource(product.getImage());
//        holder.txtProductName.setText(product.getName());
//        holder.txtProductPrice.setText(product.getPrice() + "đ");
//    }
//
//    @Override
//    public int getItemCount() {
//        return productList.size();
//    }
//
//    public static class ProductViewHolder extends RecyclerView.ViewHolder {
//        ImageView imgProduct;
//        TextView txtProductName, txtProductPrice;
//
//        public ProductViewHolder(@NonNull View itemView) {
//            super(itemView);
//            imgProduct = itemView.findViewById(R.id.imgProduct);
//            txtProductName = itemView.findViewById(R.id.txtProductName);
//            txtProductPrice = itemView.findViewById(R.id.txtProductPrice);
//        }
//    }
//}
package com.example.doan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.doan.models.Product;
import java.util.List;

public class BestSellerAdapter extends RecyclerView.Adapter<BestSellerAdapter.ProductViewHolder> {
    private List<Product> productList;

    public BestSellerAdapter(List<Product> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_best_seller, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        // Load ảnh từ URL bằng Glide
        if (product.getImage() != null && !product.getImage().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(product.getImage())
                    .placeholder(android.R.drawable.ic_menu_gallery) // Ảnh mặc định khi đang tải
                    .error(android.R.drawable.ic_menu_close_clear_cancel) // Ảnh khi lỗi
                    .into(holder.imgProduct);
        } else {
            holder.imgProduct.setImageResource(android.R.drawable.ic_menu_gallery); // Ảnh mặc định nếu không có URL
        }

        // Gán tên sản phẩm
        holder.txtProductName.setText(product.getName());

        // Gán giá sản phẩm
        holder.txtProductPrice.setText(String.format("%.0fđ", product.getPrice())); // Định dạng giá
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0; // Kiểm tra null để tránh crash
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView txtProductName, txtProductPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtProductPrice = itemView.findViewById(R.id.txtProductPrice);
        }
    }

    // Phương thức để cập nhật danh sách sản phẩm (nếu cần)
    public void updateProducts(List<Product> newProductList) {
        this.productList = newProductList;
        notifyDataSetChanged();
    }
}