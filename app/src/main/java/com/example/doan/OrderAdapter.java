package com.example.doan;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.doan.models.OrderDetailResponse;
import com.example.doan.models.OrderResponse;
import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<OrderResponse> orderList;
    private List<OrderResponse> orderListFull; // Danh sách đầy đủ để tìm kiếm
    private Context context;

    public OrderAdapter(Context context, List<OrderResponse> orderList) {
        this.context = context;
        this.orderList = orderList;
        this.orderListFull = new ArrayList<>(orderList); // Sao chép danh sách để tìm kiếm
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.his_order_item_activity, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderResponse order = orderList.get(position);
        holder.tvOrderId.setText(String.valueOf(order.getId()));
        holder.tvOrderDate.setText(order.getOrderDate());
        holder.tvTotalPrice.setText(String.format("%,d VNĐ", (long) order.getTotalPrice()));

        // Hiển thị danh sách sản phẩm
        List<OrderDetailResponse> details = order.getOrderDetails();
        if (details != null && !details.isEmpty()) {
            StringBuilder productList = new StringBuilder();
            for (OrderDetailResponse detail : details) {
                productList.append(detail.getProductName())
                        .append(" x")
                        .append(detail.getQuantity())
                        .append(", ");
            }
            // Xóa dấu ", " cuối cùng
            if (productList.length() > 2) {
                productList.setLength(productList.length() - 2);
            }
            holder.tvProductList.setText(productList.toString());
        } else {
            holder.tvProductList.setText("Không có sản phẩm");
        }

        // Hiển thị trạng thái
        holder.tvOrderStatus.setText(order.getStatus());

        // Xử lý nhấn "Xem chi tiết"
        holder.tvViewDetails.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderDetailActivity.class);
            intent.putExtra("orderId", order.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }

    public void updateOrders(List<OrderResponse> newOrders) {
        this.orderList = newOrders;
        this.orderListFull = new ArrayList<>(newOrders);
        notifyDataSetChanged();
    }

    public void filter(String query) {
        orderList.clear();
        if (query.isEmpty()) {
            orderList.addAll(orderListFull);
        } else {
            for (OrderResponse order : orderListFull) {
                if (String.valueOf(order.getId()).contains(query) ||
                        order.getOrderDate().toLowerCase().contains(query.toLowerCase()) ||
                        order.getStatus().toLowerCase().contains(query.toLowerCase())) {
                    orderList.add(order);
                } else {
                    // Kiểm tra trong danh sách sản phẩm
                    for (OrderDetailResponse detail : order.getOrderDetails()) {
                        if (detail.getProductName().toLowerCase().contains(query.toLowerCase())) {
                            orderList.add(order);
                            break;
                        }
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderDate, tvProductList, tvTotalPrice, tvOrderStatus, tvViewDetails;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date);
            tvProductList = itemView.findViewById(R.id.tv_product_list);
            tvTotalPrice = itemView.findViewById(R.id.tv_total_price);
            tvOrderStatus = itemView.findViewById(R.id.tv_order_status);
            tvViewDetails = itemView.findViewById(R.id.tv_view_details);
        }
    }
}