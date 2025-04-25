package com.example.doan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.doan.models.Category;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final Context context;
    private final List<Category> categoryList;
    private final OnCategoryClickListener listener;
    private int selectedPosition = 0; // Vị trí category được chọn

    public interface OnCategoryClickListener {
        void onCategoryClick(int categoryId);
    }

    public CategoryAdapter(Context context, List<Category> categoryList, OnCategoryClickListener listener) {
        this.context = context;
        this.categoryList = categoryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.caterogy_items, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.categoryName.setText(category.getName());

        // Load hình ảnh danh mục
        if (category.getImage() != null && !category.getImage().isEmpty()) {
            Glide.with(context)
                    .load(category.getImage())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_close_clear_cancel)
                    .into(holder.categoryImage);
        }

        // Tạo AnimationSet để kết hợp Scale (nếu có) và Translate
        AnimationSet animationSet = new AnimationSet(true);

        // Hiệu ứng phóng to/thu nhỏ (chỉ áp dụng cho danh mục được chọn)
        float scale = position == selectedPosition ? 1.4f : 1.0f;
        if (position == selectedPosition) {
            ScaleAnimation scaleAnimation = new ScaleAnimation(
                    scale, scale, // fromX, toX (giữ scale để tránh lặp lại animation)
                    scale, scale, // fromY, toY
                    Animation.RELATIVE_TO_SELF, 0.5f, // pivotXType, pivotXValue
                    Animation.RELATIVE_TO_SELF, 0.5f  // pivotYType, pivotYValue
            );
            scaleAnimation.setDuration(200);
            scaleAnimation.setFillAfter(true);
            animationSet.addAnimation(scaleAnimation);
        }

        // Hiệu ứng dịch chuyển
        float translationY;
        if (position == selectedPosition) {
            translationY = 0f; // Danh mục được chọn giữ nguyên vị trí
        } else if (position < selectedPosition) {
            translationY = -10f; // Danh mục trên đẩy lên 10px
        } else {
            translationY = 10f; // Danh mục dưới đẩy xuống 10px
        }
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.ABSOLUTE, 0f, Animation.ABSOLUTE, 0f, // fromX, toX
                Animation.ABSOLUTE, translationY, Animation.ABSOLUTE, translationY // fromY, toY (giữ translationY)
        );
        translateAnimation.setDuration(200);
        translateAnimation.setFillAfter(true);
        animationSet.addAnimation(translateAnimation);

        // Áp dụng animation
        holder.itemView.startAnimation(animationSet);

        // Đặt scale và translationY trực tiếp để đảm bảo trạng thái được duy trì
        holder.itemView.setScaleX(scale);
        holder.itemView.setScaleY(scale);
        holder.itemView.setTranslationY(translationY);

        holder.itemView.setOnClickListener(v -> {
            // Cập nhật vị trí được chọn
            int previousPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();

            // Cập nhật giao diện cho tất cả các item
            notifyDataSetChanged();

            // Gọi listener để tải sản phẩm
            listener.onCategoryClick(category.getId());
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView categoryImage;
        TextView categoryName;
        TextView categoryDescription;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryImage = itemView.findViewById(R.id.category_image);
            categoryName = itemView.findViewById(R.id.category_name);
            categoryDescription = itemView.findViewById(R.id.category_description);
        }
    }
}