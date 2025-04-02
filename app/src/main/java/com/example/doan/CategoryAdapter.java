//package com.example.doan;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//import androidx.recyclerview.widget.RecyclerView;
//import com.example.doan.models.Category;
//import java.util.List;
//
//public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
//
//    private List<Category> categoryList;
//    private OnCategoryClickListener listener;
//
//    // Interface để xử lý sự kiện click
//    public interface OnCategoryClickListener {
//        void onCategoryClick(int categoryId);
//    }
//
//    public CategoryAdapter(List<Category> categoryList, OnCategoryClickListener listener) {
//        this.categoryList = categoryList;
//        this.listener = listener;
//    }
//
//    @Override
//    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cat_item_order, parent, false);
//        return new CategoryViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(CategoryViewHolder holder, int position) {
//        Category category = categoryList.get(position);
//        holder.categoryImage.setImageResource(category.getImageResId());
//        holder.categoryName.setText(category.getName());
//
//        // Hiển thị description nếu có
//        if (category.getDescription() != null && !category.getDescription().isEmpty()) {
//            holder.categoryDescription.setText(category.getDescription());
//            holder.categoryDescription.setVisibility(View.VISIBLE);
//        } else {
//            holder.categoryDescription.setVisibility(View.GONE);
//        }
//
//        // Xử lý sự kiện click
//        holder.itemView.setOnClickListener(v -> {
//            if (listener != null) {
//                listener.onCategoryClick(category.getId());
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return categoryList.size();
//    }
//
//    static class CategoryViewHolder extends RecyclerView.ViewHolder {
//        ImageView categoryImage;
//        TextView categoryName;
//        TextView categoryDescription;
//
//        CategoryViewHolder(View itemView) {
//            super(itemView);
//            categoryImage = itemView.findViewById(R.id.category_image);
//            categoryName = itemView.findViewById(R.id.category_name);
//            categoryDescription = itemView.findViewById(R.id.category_description);
//        }
//    }
//}
package com.example.doan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.doan.models.Category;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categoryList;
    private OnCategoryClickListener listener;

    // Interface để xử lý sự kiện click
    public interface OnCategoryClickListener {
        void onCategoryClick(int categoryId);
    }

    public CategoryAdapter(List<Category> categoryList, OnCategoryClickListener listener) {
        this.categoryList = categoryList;
        this.listener = listener;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cat_item_order, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.categoryImage.setImageResource(category.getImageResId());
        holder.categoryName.setText(category.getName());

        // Hiển thị description nếu có
        if (category.getDescription() != null && !category.getDescription().isEmpty()) {
            holder.categoryDescription.setText(category.getDescription());
            holder.categoryDescription.setVisibility(View.VISIBLE);
        } else {
            holder.categoryDescription.setVisibility(View.GONE);
        }

        // Xử lý sự kiện click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCategoryClick(category.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList != null ? categoryList.size() : 0;
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView categoryImage;
        TextView categoryName;
        TextView categoryDescription;

        CategoryViewHolder(View itemView) {
            super(itemView);
            categoryImage = itemView.findViewById(R.id.category_image);
            categoryName = itemView.findViewById(R.id.category_name);
            categoryDescription = itemView.findViewById(R.id.category_description);
        }
    }
}