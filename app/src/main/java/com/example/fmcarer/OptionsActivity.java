package com.example.fmcarer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class OptionsActivity extends AppCompatActivity {

    TextView logout, settings, sub,Bank;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        logout = findViewById(R.id.logout);
        settings = findViewById(R.id.settings);
        sub = findViewById(R.id.subbu);
        Bank = findViewById(R.id.Bank);
        auth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Cài đặt");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        // 🔒 Giới hạn quyền tài khoản phụ
        String uid = auth.getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                String role = snapshot.child("role").getValue(String.class);
                if ("sub".equalsIgnoreCase(role)) {
                    sub.setVisibility(View.GONE); // Ẩn nếu là tài khoản phụ
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) { }
        });

        logout.setOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(OptionsActivity.this, StartActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        });

        sub.setOnClickListener(v -> {
            Intent intent = new Intent(OptionsActivity.this, SubListActivity.class);
            startActivity(intent);
        });
        settings.setOnClickListener(v -> {
            Intent intent = new Intent(OptionsActivity.this, TransactionHistoryActivity.class);
            startActivity(intent);
        });
        Bank.setOnClickListener(v -> {
            Intent intent = new Intent(OptionsActivity.this, ManageBankCardActivity.class);
            startActivity(intent);
        });

        TextView changePass = findViewById(R.id.changePasswordBtn);
        changePass.setOnClickListener(v -> {
            Intent intent = new Intent(OptionsActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });
    }
}
