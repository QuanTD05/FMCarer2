// file: TopUpActivity.java
package com.example.fmcarer;

import android.os.Bundle;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class TopUpActivity extends AppCompatActivity {

    private EditText edtAmount;
    private Button btnTopUp;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up);

        edtAmount = findViewById(R.id.edtAmount);
        btnTopUp = findViewById(R.id.btnTopUp);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        btnTopUp.setOnClickListener(v -> performTopUp());
    }

    private void performTopUp() {
        String amountStr = edtAmount.getText().toString().trim();
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Nhập số tiền cần nạp", Toast.LENGTH_SHORT).show();
            return;
        }

        int topUpAmount = Integer.parseInt(amountStr);

        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("Users").child(firebaseUser.getUid());

        userRef.child("balance").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                int currentBalance = 0;
                if (snapshot.exists()) {
                    try {
                        currentBalance = Integer.parseInt(snapshot.getValue(String.class));
                    } catch (Exception e) {
                        currentBalance = 0;
                    }
                }

                int newBalance = currentBalance + topUpAmount;
                userRef.child("balance").setValue(String.valueOf(newBalance));
                Toast.makeText(TopUpActivity.this, "Nạp thành công! Số dư mới: " + newBalance + " VNĐ", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TopUpActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
