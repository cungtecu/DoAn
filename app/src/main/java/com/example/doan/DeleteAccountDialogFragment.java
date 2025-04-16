package com.example.doan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DeleteAccountDialogFragment extends DialogFragment {

    private OnDeleteAccountListener listener;

    // Interface để callback về activity khi người dùng nhấn "Xóa Tài Khoản"
    public interface OnDeleteAccountListener {
        void onDeleteConfirmed();
    }

    // Phương thức để set listener từ activity
    public void setOnDeleteAccountListener(OnDeleteAccountListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout notification_3.xml
        View view = inflater.inflate(R.layout.notification_3, container, false);

        // Ánh xạ các view trong dialog
        Button btnDelete = view.findViewById(R.id.btn_delete);
        ImageView btnClose = view.findViewById(R.id.btn_close);

        // Xử lý sự kiện nhấn nút "Xóa Tài Khoản"
        btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteConfirmed();
            }
            dismiss();
        });

        // Xử lý sự kiện nhấn nút "Đóng"
        btnClose.setOnClickListener(v -> dismiss());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Đảm bảo dialog hiển thị full width và căn giữa
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
}