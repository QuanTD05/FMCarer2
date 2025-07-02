package com.example.fmcarer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class PaymentGatewayActivity extends AppCompatActivity {

    private Button btnConfirmPayment;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_gateway);

        btnConfirmPayment = findViewById(R.id.btn_confirm_payment);
        progressBar = findViewById(R.id.progress_bar);

        btnConfirmPayment.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            btnConfirmPayment.setEnabled(false);

            // Giả lập thời gian xử lý thanh toán (ví dụ: 2 giây)
            new Handler().postDelayed(() -> {
                updateUserStatusAsPaid();
            }, 2000);
        });
    }

    private void updateUserStatusAsPaid() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        HashMap<String, Object> update = new HashMap<>();
        update.put("account_status", "active");
        update.put("expiry_date", System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000)); // +30 ngày

        FirebaseDatabase.getInstance().getReference("Users")
                .child(userId)
                .updateChildren(update)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Thanh toán thành công! Tài khoản đã được kích hoạt.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PaymentGatewayActivity.this, MainActivity3.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    btnConfirmPayment.setEnabled(true);
                    Toast.makeText(this, "Thanh toán thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
