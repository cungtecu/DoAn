package com.example.doan;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

public class PaymentWebviewActivity extends AppCompatActivity {
    private static final String TAG = "PaymentWebviewActivity";
    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_webview);

        webView = findViewById(R.id.webview_payment);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(TAG, "Redirect URL: " + url);
                // Kiểm tra URL callback từ VNPay
                if (url.contains("/api/orders/callback")) {
                    // Kiểm tra trạng thái thanh toán
                    if (url.contains("vnp_ResponseCode=00") || url.contains("vnp_TransactionStatus=00")) {
                        Log.d(TAG, "Payment successful, redirecting to OrderSuccessActivity");
                        Intent intent = new Intent(PaymentWebviewActivity.this, OrderSuccessActivity.class);
                        intent.putExtra("total_price", getIntent().getDoubleExtra("total_price", 0));
                        intent.putExtra("used_drips", getIntent().getIntExtra("used_drips", 0));
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();
                        return true; // Ngăn WebView load URL
                    } else if (url.contains("vnp_ResponseCode") || url.contains("vnp_TransactionStatus")) {
                        Log.d(TAG, "Payment failed, redirecting to PaymentActivity");
                        Intent intent = new Intent(PaymentWebviewActivity.this, PaymentActivity.class);
                        intent.putExtra("payment_status", "failed");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();
                        return true; // Ngăn WebView load URL
                    } else {
                        Log.d(TAG, "Unknown callback URL format: " + url);
                    }
                }
                // Cho phép WebView load các URL khác (như trang thanh toán VNPay)
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, "Page finished loading: " + url);
                // Kiểm tra lại URL sau khi trang tải xong
                if (url.contains("/api/orders/callback")) {
                    if (url.contains("vnp_ResponseCode=00") || url.contains("vnp_TransactionStatus=00")) {
                        Log.d(TAG, "Payment successful (onPageFinished), redirecting to OrderSuccessActivity");
                        Intent intent = new Intent(PaymentWebviewActivity.this, OrderSuccessActivity.class);
                        intent.putExtra("total_price", getIntent().getDoubleExtra("total_price", 0));
                        intent.putExtra("used_drips", getIntent().getIntExtra("used_drips", 0));
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();
                    } else if (url.contains("vnp_ResponseCode") || url.contains("vnp_TransactionStatus")) {
                        Log.d(TAG, "Payment failed (onPageFinished), redirecting to PaymentActivity");
                        Intent intent = new Intent(PaymentWebviewActivity.this, PaymentActivity.class);
                        intent.putExtra("payment_status", "failed");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.d(TAG, "Unknown callback URL format (onPageFinished): " + url);
                    }
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Log.e(TAG, "WebView error: code=" + errorCode + ", description=" + description + ", url=" + failingUrl);
                Toast.makeText(PaymentWebviewActivity.this, "Lỗi tải trang thanh toán: " + description, Toast.LENGTH_LONG).show();
            }
        });

        String paymentUrl = getIntent().getStringExtra("payment_url");
        if (paymentUrl != null) {
            Log.d(TAG, "Loading payment URL: " + paymentUrl);
            webView.loadUrl(paymentUrl);
        } else {
            Log.e(TAG, "Payment URL is null");
            Toast.makeText(this, "Không tìm thấy URL thanh toán", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}