package com.example.doan;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan.R;  // Replace with your package name
import com.example.doan.models.Product;  // Replace with your package name

import de.hdodenhof.circleimageview.CircleImageView;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;
    private boolean isGrid;

    public ProductAdapter(Context context, List<Product> productList, boolean isGrid) {
        this.context = context;
        this.productList = productList;
        this.isGrid = isGrid;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;
        if (isGrid) {
            view = inflater.inflate(R.layout.item_best_seller, parent, false);
        } else {
            view = inflater.inflate(R.layout.product_list_item, parent, false);
        }
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.productNameTextView.setText(product.getName());

        // Load the image - Handle both resource IDs and file paths


        if (!isGrid) {
            holder.productDescriptionTextView.setText(product.getDescription());
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        CircleImageView productImageView;
        TextView productNameTextView;
        TextView productDescriptionTextView;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.product_image);
            productNameTextView = itemView.findViewById(R.id.product_name);
            productDescriptionTextView = itemView.findViewById(R.id.product_description); // Only in list item
        }
    }
}