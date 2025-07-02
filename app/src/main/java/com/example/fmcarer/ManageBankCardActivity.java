package com.example.fmcarer;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fmcarer.Model.BankCard;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class ManageBankCardActivity extends AppCompatActivity {

    private EditText edtCardNumber, edtBankName;
    private Button btnSave, btnDelete;
    private FirebaseUser firebaseUser;
    private DatabaseReference cardRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_bank_card);

        edtCardNumber = findViewById(R.id.edtCardNumber);
        edtBankName = findViewById(R.id.edtBankName);
        btnSave = findViewById(R.id.btnSaveCard);
        btnDelete = findViewById(R.id.btnDeleteCard);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        cardRef = FirebaseDatabase.getInstance().getReference("BankCards").child(firebaseUser.getUid());

        loadBankCard();

        btnSave.setOnClickListener(v -> saveCard());
        btnDelete.setOnClickListener(v -> deleteCard());
    }

    private void loadBankCard() {
        cardRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    BankCard card = snapshot.getValue(BankCard.class);
                    if (card != null) {
                        edtCardNumber.setText(card.cardNumber);
                        edtBankName.setText(card.bankName);
                        btnDelete.setVisibility(View.VISIBLE);
                    }
                } else {
                    btnDelete.setVisibility(View.GONE);
                }
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ManageBankCardActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveCard() {
        String cardNumber = edtCardNumber.getText().toString().trim();
        String bankName = edtBankName.getText().toString().trim();

        if (cardNumber.isEmpty() || bankName.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        BankCard card = new BankCard(cardNumber, bankName);
        cardRef.setValue(card).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Đã lưu thẻ", Toast.LENGTH_SHORT).show();
            btnDelete.setVisibility(View.VISIBLE);
        });
    }

    private void deleteCard() {
        cardRef.removeValue().addOnSuccessListener(aVoid ->
                Toast.makeText(this, "Đã xóa thẻ", Toast.LENGTH_SHORT).show());
        edtCardNumber.setText("");
        edtBankName.setText("");
        btnDelete.setVisibility(View.GONE);
    }
}
