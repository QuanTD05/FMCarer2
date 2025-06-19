package com.example.fmcarer;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DepositActivity extends AppCompatActivity {

    private EditText edtSoTien;
    private Button btnNap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit);

        edtSoTien = findViewById(R.id.edtSoTien);
        btnNap = findViewById(R.id.btnNap);

        btnNap.setOnClickListener(v -> {
            String soTien = edtSoTien.getText().toString().trim();
            if (soTien.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập số tiền.", Toast.LENGTH_SHORT).show();
                return;
            }

            int amount;
            try {
                amount = Integer.parseInt(soTien);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Số tiền không hợp lệ!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (amount <= 0) {
                Toast.makeText(this, "Số tiền phải lớn hơn 0.", Toast.LENGTH_SHORT).show();
                return;
            }

            saveToLocalWallet(amount);      // Lưu vào SharedPreferences
            saveToAdminRevenue(amount);     // Lưu vào Firebase

            Toast.makeText(this, "Nạp " + amount + " VNĐ thành công!", Toast.LENGTH_SHORT).show();

            Intent resultIntent = new Intent();
            resultIntent.putExtra("nap_thanh_cong", true);
            resultIntent.putExtra("so_tien", amount);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        });
    }

    private void saveToLocalWallet(int amount) {
        SharedPreferences prefs = getSharedPreferences("wallet", MODE_PRIVATE);
        int currentBalance = prefs.getInt("balance", 0);
        prefs.edit().putInt("balance", currentBalance + amount).apply();
    }

    private void saveToAdminRevenue(int amount) {
        DatabaseReference paymentsRef = FirebaseDatabase.getInstance().getReference("payments");
        String paymentId = paymentsRef.push().getKey();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        Map<String, Object> paymentData = new HashMap<>();
        paymentData.put("userId", userId);
        paymentData.put("amount", amount);
        paymentData.put("timestamp", timestamp);

        if (paymentId != null) {
            paymentsRef.child(paymentId).setValue(paymentData);
        }
    }
}
