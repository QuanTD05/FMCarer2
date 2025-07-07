package com.example.fmcarer.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fmcarer.Model.ReminderNotification;
import com.example.fmcarer.R;

import java.util.List;

public class ReminderNotificationAdapter extends RecyclerView.Adapter<ReminderNotificationAdapter.ViewHolder> {
    private Context mContext;
    private List<ReminderNotification> mList;

    public ReminderNotificationAdapter(Context context, List<ReminderNotification> list) {
        this.mContext = context;
        this.mList = list;
    }

    @NonNull
    @Override
    public ReminderNotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_reminder_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderNotificationAdapter.ViewHolder holder, int position) {
        ReminderNotification notification = mList.get(position);
        holder.title.setText("👶 " + notification.getTitle());
        holder.content.setText("📌 " + notification.getContent());
        holder.time.setText("🕒 " + notification.getTime());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title, content, time;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textTitle);
            content = itemView.findViewById(R.id.textContent);
            time = itemView.findViewById(R.id.textTime);
        }
    }
}
