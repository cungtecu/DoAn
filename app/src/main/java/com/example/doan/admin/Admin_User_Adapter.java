package com.example.doan.admin;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan.models.User;

import java.util.List;

public class Admin_User_Adapter extends RecyclerView.Adapter<Admin_User_Adapter.UserViewHolder> {
    private List<User> userList;
    private OnUserActionListener listener;

    public interface OnUserActionListener {
        void onEditUser(User user);
        void onDeleteUser(int userId);
    }

    public Admin_User_Adapter(List<User> userList, OnUserActionListener listener) {
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.tvUsername.setText(user.getName());
        holder.tvUserEmail.setText(user.getEmail());
        holder.tvUserDrips.setText("Điểm: " + user.getPoints());

        holder.btnEditUser.setOnClickListener(v -> listener.onEditUser(user));
        holder.btnDeleteUser.setOnClickListener(v -> listener.onDeleteUser(user.getId()));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void updateList(List<User> newList) {
        this.userList = newList;
        notifyDataSetChanged();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvUserEmail, tvUserDrips;
        ImageButton btnEditUser, btnDeleteUser;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tv_username);
            tvUserEmail = itemView.findViewById(R.id.tv_useremail);
            tvUserDrips = itemView.findViewById(R.id.tv_userdrips);
            btnEditUser = itemView.findViewById(R.id.btn_edit_user);
            btnDeleteUser = itemView.findViewById(R.id.btn_delete_user);
        }
    }
}
