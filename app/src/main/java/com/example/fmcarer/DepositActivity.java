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

public class DepositActivity extends AppCompatActivity {

    private TextView txtPlanName, txtPlanDays, txtPlanPrice;
    private Button btnConfirmPayment, btnShowQR, btnConfirmManual;
    private ImageView imgQRCode;
    private FirebaseUser firebaseUser;

    private int days = 0;
    private int price = 0;
    private String planName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit);

        // Ánh xạ view
        txtPlanName = findViewById(R.id.txtPlanName);
        txtPlanDays = findViewById(R.id.txtPlanDays);
        txtPlanPrice = findViewById(R.id.txtPlanPrice);
        btnConfirmPayment = findViewById(R.id.btnConfirmPayment);
        btnShowQR = findViewById(R.id.btnShowQR);
        btnConfirmManual = findViewById(R.id.btnConfirmManual);  // 👉 QUAN TRỌNG: Bổ sung dòng này
        imgQRCode = findViewById(R.id.imgQRCode);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            Toast.makeText(this, "Chưa đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Nhận dữ liệu từ Intent
        planName = getIntent().getStringExtra("plan");
        if (planName == null) {
            Toast.makeText(this, "Không có dữ liệu gói", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Thiết lập số ngày & giá
        switch (planName) {
            case "1 ngày":
                days = 1;
                price = 10000;
                break;
            case "1 tuần":
                days = 7;
                price = 60000;
                break;
            case "1 tháng":
                days = 30;
                price = 200000;
                break;
            default:
                days = 1;
                price = 10000;
                break;
        }

        // Hiển thị thông tin gói
        txtPlanName.setText("Gói: " + planName);
        txtPlanDays.setText("Sử dụng trong: " + days + " ngày");
        txtPlanPrice.setText("Tổng tiền: " + price + " VNĐ");

        // Nút xác nhận thanh toán bằng số dư
        btnConfirmPayment.setOnClickListener(v -> confirmPayment());

        // Nút tạo QR VietQR
        btnShowQR.setOnClickListener(v -> {
            String bankCode = "vcb";  // Mã ngân hàng
            String accountNumber = "1035734330";  // Số tài khoản nhận
            String description = "Nap tien goi " + planName;

            String qrUrl = "https://img.vietqr.io/image/" + bankCode + "-" + accountNumber + "-compact.png"
                    + "?amount=" + price + "&addInfo=" + Uri.encode(description);

            Glide.with(this).load(qrUrl).into(imgQRCode);
            imgQRCode.setVisibility(ImageView.VISIBLE);
            btnConfirmManual.setVisibility(Button.VISIBLE);  // 👉 Hiện nút xác nhận
        });

        // Nút "Tôi đã chuyển khoản xong"
        btnConfirmManual.setOnClickListener(v -> {
            Toast.makeText(this, "Cảm ơn! Chúng tôi sẽ kiểm tra giao dịch của bạn.", Toast.LENGTH_LONG).show();
            btnConfirmManual.setVisibility(Button.GONE);
        });
    }

    private void confirmPayment() {
        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("Users").child(firebaseUser.getUid());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                long balance = 0, paidUntil = System.currentTimeMillis();

                if (snapshot.child("balance").exists()) {
                    balance = Long.parseLong(snapshot.child("balance").getValue(String.class));
                }
                if (snapshot.child("paidUntil").exists()) {
                    long old = Long.parseLong(snapshot.child("paidUntil").getValue(String.class));
                    paidUntil = Math.max(old, System.currentTimeMillis());
                }

                if (balance < price) {
                    Toast.makeText(DepositActivity.this, "Không đủ số dư!", Toast.LENGTH_SHORT).show();
                    return;
                }

                long newBalance = balance - price;
                long newPaidUntil = paidUntil + days * 86400000L;

                Map<String, Object> updates = new HashMap<>();
                updates.put("balance", String.valueOf(newBalance));
                updates.put("paidUntil", String.valueOf(newPaidUntil));

                userRef.updateChildren(updates).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        saveTransaction(price, days);
                        Toast.makeText(DepositActivity.this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(DepositActivity.this, "Lỗi thanh toán!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DepositActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveTransaction(int amount, int days) {
        DatabaseReference tRef = FirebaseDatabase.getInstance()
                .getReference("TransactionHistories")
                .child(firebaseUser.getUid())
                .push();

        Map<String, Object> trans = new HashMap<>();
        trans.put("amount", amount);
        trans.put("days", days);
        trans.put("timestamp", System.currentTimeMillis());

        tRef.setValue(trans);
    }
}
