package com.example.doan;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class PaymentActivity extends AppCompatActivity {

    private TextView orderIdTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        orderIdTextView = findViewById(R.id.order_id_text);

        // Lấy orderId từ Intent
        int orderId = getIntent().getIntExtra("orderId", -1);
        if (orderId != -1) {
            orderIdTextView.setText("Mã đơn hàng: " + orderId);
        } else {
            orderIdTextView.setText("Không tìm thấy mã đơn hàng");
        }
    }
}