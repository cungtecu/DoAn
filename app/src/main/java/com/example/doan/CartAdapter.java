package com.example.doan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.doan.models.CartItem;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItems;
    private OnCartActionListener actionListener;

    public interface OnCartActionListener {
        void onUpdateQuantity(CartItem item, int newQuantity);
        void onRemove(CartItem item);
    }

    public CartAdapter(List<CartItem> cartItems, OnCartActionListener listener) {
        this.cartItems = cartItems;
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.productNameTextView.setText(item.getProductName());
        holder.sizeTextView.setText("Kích thước: " + item.getSize());
        holder.quantityTextView.setText(String.valueOf(item.getQuantity()));

        NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        numberFormat.setMinimumFractionDigits(0);
        numberFormat.setMaximumFractionDigits(0);
        holder.priceTextView.setText(numberFormat.format(item.getPrice() * item.getQuantity()) + " VNĐ");

        // Load hình ảnh
        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(item.getImageUrl())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_close_clear_cancel)
                    .into(holder.productImage);
        }

        // Tăng số lượng
        holder.incrementButton.setOnClickListener(v -> {
            int newQuantity = item.getQuantity() + 1;
            actionListener.onUpdateQuantity(item, newQuantity);
        });

        // Giảm số lượng
        holder.decrementButton.setOnClickListener(v -> {
            int newQuantity = item.getQuantity() - 1;
            actionListener.onUpdateQuantity(item, newQuantity);
        });

        // Xóa sản phẩm
        holder.removeButton.setOnClickListener(v -> actionListener.onRemove(item));
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productNameTextView, sizeTextView, quantityTextView, priceTextView;
        ImageView decrementButton, incrementButton, removeButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.cart_product_image);
            productNameTextView = itemView.findViewById(R.id.cart_product_name);
            sizeTextView = itemView.findViewById(R.id.cart_size);
            quantityTextView = itemView.findViewById(R.id.cart_quantity);
            priceTextView = itemView.findViewById(R.id.cart_price);
            decrementButton = itemView.findViewById(R.id.cart_decrement);
            incrementButton = itemView.findViewById(R.id.cart_increment);
            removeButton = itemView.findViewById(R.id.cart_remove);
        }
    }
}