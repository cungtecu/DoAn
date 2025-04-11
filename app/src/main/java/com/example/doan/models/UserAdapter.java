package com.example.doan.models;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan.R;
import com.example.doan.models.UserSummaryDTO;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<UserSummaryDTO> userList;
    private OnUserDeleteListener deleteListener;
    private OnUserClickListener clickListener;

    public interface OnUserDeleteListener {
        void onDelete(UserSummaryDTO user);
    }
    public interface OnUserClickListener {
        void onClick(UserSummaryDTO user);
    }
    public UserAdapter(List<UserSummaryDTO> userList, OnUserDeleteListener deleteListener, OnUserClickListener clickListener) {
        this.userList = userList;
        this.deleteListener = deleteListener;
        this.clickListener = clickListener;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        UserSummaryDTO user = userList.get(position);
        holder.tvUsername.setText(user.getName());
        holder.tvUserpoints.setText(String.valueOf(user.getPoints()));

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDelete(user);
            }
        });
        holder.lnUser.setOnClickListener(v -> clickListener.onClick(user));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void updateList(List<UserSummaryDTO> newList) {
        userList = newList;
        notifyDataSetChanged();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvUserpoints;
        ImageButton btnDelete;
        LinearLayout lnUser;

        public UserViewHolder(View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tv_username);
            tvUserpoints = itemView.findViewById(R.id.tv_userpoints);
            btnDelete = itemView.findViewById(R.id.btn_delete_user);
            lnUser = itemView.findViewById(R.id.ln_user);
        }
    }
}