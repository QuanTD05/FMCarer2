package com.example.fmcarer;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fmcarer.Adapter.TransactionAdapter;
import com.example.fmcarer.Model.Transaction;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.*;

public class TransactionHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private List<Transaction> transactionList;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        recyclerView = findViewById(R.id.recycler_transaction);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        transactionList = new ArrayList<>();
        adapter = new TransactionAdapter(this, transactionList);
        recyclerView.setAdapter(adapter);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        loadTransactions();
    }

    private void loadTransactions() {
        if (firebaseUser == null) return;

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("TransactionHistories")
                .child(firebaseUser.getUid());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                transactionList.clear();
                for (DataSnapshot s : snapshot.getChildren()) {
                    Transaction t = s.getValue(Transaction.class);
                    if (t != null) transactionList.add(t);
                }
                Collections.reverse(transactionList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TransactionHistoryActivity.this, "Lỗi tải lịch sử", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
