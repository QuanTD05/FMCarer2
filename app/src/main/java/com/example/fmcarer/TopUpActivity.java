package com.example.fmcarer;

import android.net.Uri;
import android.os.Bundle;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.HashMap;
import java.util.Map;

public class TopUpActivity extends AppCompatActivity {

    private EditText edtAmount, edtCardNumber, edtBankName;
    private Button btnTopUp, btnShowQR;
    private ImageView imgQRCode;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up);

        edtAmount = findViewById(R.id.edtAmount);
        edtCardNumber = findViewById(R.id.edtCardNumber);
        edtBankName = findViewById(R.id.edtBankName);
        btnTopUp = findViewById(R.id.btnTopUp);
        btnShowQR = findViewById(R.id.btnShowQR);
        imgQRCode = findViewById(R.id.imgQRCode);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            Toast.makeText(this, "Bạn chưa đăng nhập!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Nạp tiền vào số dư (yêu cầu nhập thẻ ngân hàng)
        btnTopUp.setOnClickListener(v -> {
            String amountStr = edtAmount.getText().toString().trim();
            String cardNumber = edtCardNumber.getText().toString().trim();
            String bankName = edtBankName.getText().toString().trim();

            if (amountStr.isEmpty() || cardNumber.isEmpty() || bankName.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            long amount = Long.parseLong(amountStr);
            if (amount <= 0) {
                Toast.makeText(this, "Số tiền không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            saveBankCard(cardNumber, bankName);
            topUpAmount(amount);
        });

        // Tạo QR chuyển khoản VietQR
        btnShowQR.setOnClickListener(v -> {
            String amountStr = edtAmount.getText().toString().trim();
            if (amountStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập số tiền để tạo QR", Toast.LENGTH_SHORT).show();
                return;
            }

            String qrUrl = "https://img.vietqr.io/image/vcb-1035734330-compact.png"
                    + "?amount=" + amountStr
                    + "&addInfo=" + Uri.encode("Nap tien vao tai khoan");

            Glide.with(this).load(qrUrl).into(imgQRCode);
            imgQRCode.setVisibility(ImageView.VISIBLE);
        });
    }

    // Lưu thẻ ngân hàng
    private void saveBankCard(String card, String bank) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("BankCards").child(firebaseUser.getUid());
        Map<String, Object> data = new HashMap<>();
        data.put("cardNumber", card);
        data.put("bankName", bank);
        ref.setValue(data);
    }

    // Nạp tiền vào Firebase
    private void topUpAmount(long amount) {
        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("Users").child(firebaseUser.getUid());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                long balance = 0;
                if (snapshot.child("balance").exists()) {
                    balance = Long.parseLong(snapshot.child("balance").getValue(String.class));
                }

                long newBalance = balance + amount;

                Map<String, Object> updates = new HashMap<>();
                updates.put("balance", String.valueOf(newBalance));

                userRef.updateChildren(updates).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        saveTopUpTransaction(amount);
                        Toast.makeText(TopUpActivity.this, "Nạp tiền thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(TopUpActivity.this, "Lỗi nạp tiền!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TopUpActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Lưu lịch sử nạp
    private void saveTopUpTransaction(long amount) {
        DatabaseReference tRef = FirebaseDatabase.getInstance()
                .getReference("TopUpHistories")
                .child(firebaseUser.getUid())
                .push();

        Map<String, Object> trans = new HashMap<>();
        trans.put("amount", amount);
        trans.put("timestamp", System.currentTimeMillis());

        tRef.setValue(trans);
    }
}
