package com.example.fmcarer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class PaymentRequiredActivity extends AppCompatActivity {

    private TextView statusText;
    private Button payNowButton, logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_required);

        statusText = findViewById(R.id.status_text);
        payNowButton = findViewById(R.id.pay_now_button);
        logoutButton = findViewById(R.id.logout_button);

        // Nút "Nạp tiền ngay"
        payNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO: Chuyển sang activity xử lý thanh toán
                // (Bạn có thể dùng Momo, ZaloPay, hoặc redirect đến trang web)
                Intent intent = new Intent(PaymentRequiredActivity.this, PaymentGatewayActivity.class);
                startActivity(intent);
            }
        });

        // Nút "Đăng xuất"
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Đăng xuất và quay lại màn hình đăng nhập
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(PaymentRequiredActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}
