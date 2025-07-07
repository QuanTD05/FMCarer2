package com.example.fmcarer.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fmcarer.Adapter.NotificationAdapter;
import com.example.fmcarer.Model.Notification;
import com.example.fmcarer.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {

    private RecyclerView activityRecycler, reminderRecycler;
    private NotificationAdapter activityAdapter, reminderAdapter;
    private List<Notification> activityList, reminderList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        initializeViews(view);
        loadActivityNotifications();
        loadReminderNotifications();

        return view;
    }

    private void initializeViews(View view) {
        activityRecycler = view.findViewById(R.id.activityNotificationRecycler);
        activityRecycler.setHasFixedSize(true);
        activityRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        activityList = new ArrayList<>();
        activityAdapter = new NotificationAdapter(getContext(), activityList);
        activityRecycler.setAdapter(activityAdapter);

        reminderRecycler = view.findViewById(R.id.reminderNotificationRecycler);
        reminderRecycler.setHasFixedSize(true);
        reminderRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        reminderList = new ArrayList<>();
        reminderAdapter = new NotificationAdapter(getContext(), reminderList);
        reminderRecycler.setAdapter(reminderAdapter);
    }

    private void loadActivityNotifications() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) return;

        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("Notifications")
                .child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                activityList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Notification notification = data.getValue(Notification.class);
                    if (notification != null) {
                        activityList.add(notification);
                    }
                }
                activityAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void loadReminderNotifications() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) return;

        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("ReminderNotifications")
                .child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reminderList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Notification notification = data.getValue(Notification.class);
                    if (notification != null) {
                        reminderList.add(notification);
                    }
                }
                reminderAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
}
