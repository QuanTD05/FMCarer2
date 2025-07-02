package com.example.fmcarer.Adapter;

import android.content.Context;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fmcarer.Model.Transaction;
import com.example.fmcarer.R;

import java.text.SimpleDateFormat;
import java.util.*;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private Context context;
    private List<Transaction> list;

    public TransactionAdapter(Context context, List<Transaction> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int i) {
        Transaction t = list.get(i);
        h.txtAmount.setText("Số tiền: " + t.amount + " VNĐ");
        h.txtDays.setText("Gói: " + t.durationDays + " ngày");
        h.txtTime.setText("Thời gian: " + formatTime(t.timestamp));
    }

    private String formatTime(long timeMillis) {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(new Date(timeMillis));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtAmount, txtDays, txtTime;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtAmount = itemView.findViewById(R.id.txtAmount);
            txtDays = itemView.findViewById(R.id.txtDays);
            txtTime = itemView.findViewById(R.id.txtTime);
        }
    }
}
