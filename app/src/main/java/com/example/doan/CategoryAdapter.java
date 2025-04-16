package com.example.doan;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.doan.models.Category;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categoryList;
    private Context context;
    private OnCategoryClickListener clickListener; // Dùng cho OrderActivity
    private OnCategoryActionListener actionListener; // Dùng cho Admin_Cat_Activity

    // Interface cho OrderActivity (functional interface)
    @FunctionalInterface
    public interface OnCategoryClickListener {
        void onCategoryClick(int categoryId);
    }

    // Interface cho Admin_Cat_Activity
    public interface OnCategoryActionListener {
        void onEditClick(int categoryId);
        void onDeleteClick(int categoryId);
    }

    // Constructor cho OrderActivity
    public CategoryAdapter(Context context, List<Category> categoryList, OnCategoryClickListener clickListener) {
        this.context = context;
        this.categoryList = categoryList;
        this.clickListener = clickListener;
        this.actionListener = null;
    }

    // Constructor cho Admin_Cat_Activity
    public CategoryAdapter(Context context, List<Category> categoryList, OnCategoryActionListener actionListener) {
        this.context = context;
        this.categoryList = categoryList;
        this.clickListener = null;
        this.actionListener = actionListener;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (actionListener != null) {
            // Dùng layout admin_item_category cho Admin_Cat_Activity
            view = LayoutInflater.from(context).inflate(R.layout.admin_category_item, parent, false);
        } else {
            // Dùng layout cat_item_order cho OrderActivity (nếu có)
            view = LayoutInflater.from(context).inflate(R.layout.cat_item_order, parent, false);
        }
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.categoryName.setText(category.getName());
        Log.d("CategoryAdapter", "Binding category: " + category.getName());

        if (category.getImage() != null && !category.getImage().isEmpty()) {
            Glide.with(context)
                    .load(category.getImage())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(holder.categoryImage);
        } else {
            holder.categoryImage.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        if (category.getDescription() != null && !category.getDescription().isEmpty()) {
            holder.categoryDescription.setText(category.getDescription());
            holder.categoryDescription.setVisibility(View.VISIBLE);
        } else {
            holder.categoryDescription.setVisibility(View.GONE);
        }

        // Xử lý cho OrderActivity
        if (clickListener != null) {
            holder.itemView.setOnClickListener(v -> clickListener.onCategoryClick(category.getId()));
            // Ẩn nút edit/delete nếu có trong layout
            if (holder.btnEdit != null) holder.btnEdit.setVisibility(View.GONE);
            if (holder.btnDelete != null) holder.btnDelete.setVisibility(View.GONE);
        }

        // Xử lý cho Admin_Cat_Activity
        if (actionListener != null && holder.btnEdit != null && holder.btnDelete != null) {
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnEdit.setOnClickListener(v -> actionListener.onEditClick(category.getId()));
            holder.btnDelete.setOnClickListener(v -> actionListener.onDeleteClick(category.getId()));
        }
    }

    @Override
    public int getItemCount() {
        return categoryList != null ? categoryList.size() : 0;
    }

    public void updateList(List<Category> newList) {
        this.categoryList = newList;
        notifyDataSetChanged();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView categoryImage;
        TextView categoryName;
        TextView categoryDescription;
        ImageButton btnEdit;
        ImageButton btnDelete;

        CategoryViewHolder(View itemView) {
            super(itemView);
            categoryImage = itemView.findViewById(R.id.product_image);
            categoryName = itemView.findViewById(R.id.category_name);
            categoryDescription = itemView.findViewById(R.id.category_des);
            btnEdit = itemView.findViewById(R.id.btn_edit_product);
            btnDelete = itemView.findViewById(R.id.btn_delete_product);
        }
    }
}