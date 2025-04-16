package com.example.doan.models;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.doan.R;
import com.example.doan.admin.Admin_SystemConfig_Edit_Activity;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SystemConfigAdapter extends RecyclerView.Adapter<SystemConfigAdapter.SystemConfigViewHolder> {

    private Context context;
    private List<SystemConfig> systemConfigList;
    private OnSystemConfigClickListener listener;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public interface OnSystemConfigClickListener {
        void onEditClick(SystemConfig config);
        void onDeleteClick(SystemConfig config);
    }

    public SystemConfigAdapter(Context context, List<SystemConfig> systemConfigList, OnSystemConfigClickListener listener) {
        this.context = context;
        this.systemConfigList = systemConfigList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SystemConfigViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_systemconfig_item, parent, false);
        return new SystemConfigViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SystemConfigViewHolder holder, int position) {
        SystemConfig config = systemConfigList.get(position);
        holder.tvFromValuePrice.setText(config.getFromValuePrice() != null ? config.getFromValuePrice().toString() : "N/A");
        holder.tvCurrency.setText(config.getCurrency() != null && config.getCurrency().getCurrencyCode() != null ? config.getCurrency().getCurrencyCode() : "N/A");
        holder.tvToValuePoint.setText(config.getToValuePoint() != null ? config.getToValuePoint().toString() : "N/A");
        holder.tvFromDate.setText(config.getFromDate() != null ? config.getFromDate().format(dateFormatter) : "N/A");
        holder.tvEndDate.setText(config.getThruDate() != null ? config.getThruDate().format(dateFormatter) : "N/A");

        // Nút chỉnh sửa
        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(config));

        // Nút xóa
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(config));
    }

    @Override
    public int getItemCount() {
        return systemConfigList != null ? systemConfigList.size() : 0;
    }

    public void updateSystemConfigs(List<SystemConfig> newConfigs) {
        this.systemConfigList = newConfigs;
        notifyDataSetChanged();
    }

    static class SystemConfigViewHolder extends RecyclerView.ViewHolder {
        TextView tvFromValuePrice, tvCurrency, tvToValuePoint, tvFromDate, tvEndDate;
        ImageButton btnEdit, btnDelete;

        public SystemConfigViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFromValuePrice = itemView.findViewById(R.id.tv_FromValuePrice);
            tvCurrency = itemView.findViewById(R.id.tv_Currency);
            tvToValuePoint = itemView.findViewById(R.id.tv_ToValuePoint);
            tvFromDate = itemView.findViewById(R.id.tv_FromDate);
            tvEndDate = itemView.findViewById(R.id.tv_EndDate);
            btnEdit = itemView.findViewById(R.id.btn_edit_system);
            btnDelete = itemView.findViewById(R.id.btn_delete_system);
        }
    }
}