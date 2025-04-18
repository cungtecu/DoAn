package com.example.doan;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.doan.models.Category;
import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categoryList;
    private OnCategoryClickListener listener;
    private Context context;

    public interface OnCategoryClickListener {
        void onCategoryClick(int categoryId);
    }

    public CategoryAdapter(Context context, List<Category> categoryList, OnCategoryClickListener listener) {
        this.context = context;
        this.categoryList = categoryList != null ? categoryList : new ArrayList<>();
        this.listener = listener;
        Log.d("CategoryAdapter", "Initialized with " + this.categoryList.size() + " categories");
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.caterogy_items, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        if (categoryList == null || position >= categoryList.size()) {
            Log.e("CategoryAdapter", "Invalid position: " + position + ", list size: " + (categoryList != null ? categoryList.size() : 0));
            return;
        }

        Category category = categoryList.get(position);
        holder.categoryName.setText(category.getName() != null ? category.getName() : "N/A");
        Log.d("CategoryAdapter", "Binding category: " + category.getName() + ", position: " + position);

        if (category.getImage() != null && !category.getImage().isEmpty()) {
            Glide.with(context)
                    .load(category.getImage())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_gallery)
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

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                Log.d("CategoryAdapter", "Category clicked: " + category.getId());
                listener.onCategoryClick(category.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        int count = categoryList != null ? categoryList.size() : 0;
        Log.d("CategoryAdapter", "Item count: " + count);
        return count;
    }

    public void updateCategories(List<Category> newCategories) {
        this.categoryList = newCategories != null ? newCategories : new ArrayList<>();
        Log.d("CategoryAdapter", "Updated categories: " + this.categoryList.size());
        notifyDataSetChanged();
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
            if (categoryImage == null || categoryName == null) {
                Log.e("CategoryAdapter", "ViewHolder initialization failed: missing views");
            }
        }
    }
}