package com.example.fmcarer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.fmcarer.Model.Child;
import com.example.fmcarer.Reminder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CareLogActivity extends AppCompatActivity {
    private LinearLayout logContainer;
    private String currentUserId;
    private String filterChildId = null;
    private List<Child> childList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_care_log);

        logContainer = findViewById(R.id.logContainer);
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Nhận childId từ Intent nếu có
        filterChildId = getIntent().getStringExtra("childId");

        loadChildrenAndReminders();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Nhật ký chăm sóc");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void loadChildrenAndReminders() {
        DatabaseReference childRef = FirebaseDatabase.getInstance().getReference("children");
        childRef.orderByChild("userId").equalTo(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                childList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Child child = snap.getValue(Child.class);
                    if (child != null) childList.add(child);
                }
                loadReminders();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadReminders() {
        DatabaseReference reminderRef = FirebaseDatabase.getInstance().getReference("reminders");
        reminderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                logContainer.removeAllViews();

                for (DataSnapshot snap : snapshot.getChildren()) {
                    Reminder reminder = snap.getValue(Reminder.class);
                    if (reminder == null) continue;

                    // Lọc theo childId nếu cần
                    if (filterChildId != null && !filterChildId.equals(reminder.childId)) continue;

                    String childName = "Không rõ";
                    for (Child c : childList) {
                        if (c.getChildId().equals(reminder.childId)) {
                            childName = c.getName();
                            break;
                        }
                    }

                    // Hiển thị UI
                    LinearLayout card = new LinearLayout(CareLogActivity.this);
                    card.setOrientation(LinearLayout.VERTICAL);
                    card.setPadding(32, 32, 32, 32);
                    card.setBackground(ContextCompat.getDrawable(CareLogActivity.this, R.drawable.reminder_card));

                    LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    cardParams.setMargins(0, 0, 0, 30);
                    card.setLayoutParams(cardParams);

                    TextView content = new TextView(CareLogActivity.this);
                    content.setText("👶 " + childName +
                            "\n📌 " + reminder.title +
                            "\n🕒 " + reminder.time +
                            (reminder.isRepeat ? "\n🔁 Lặp: " + reminder.repeatType : ""));
                    content.setTextSize(16);
                    content.setTextColor(Color.BLACK);
                    card.addView(content);

                    TextView editBtn = new TextView(CareLogActivity.this);
                    editBtn.setText("✏️ Sửa");
                    editBtn.setTextColor(Color.BLUE);
                    editBtn.setPadding(0, 16, 0, 0);
                    editBtn.setOnClickListener(v -> {
                        Intent i = new Intent(CareLogActivity.this, ReminderActivity.class);
                        i.putExtra("reminderId", reminder.reminderId);
                        startActivity(i);
                    });
                    card.addView(editBtn);

                    TextView deleteBtn = new TextView(CareLogActivity.this);
                    deleteBtn.setText("🗑️ Xoá");
                    deleteBtn.setTextColor(Color.RED);
                    deleteBtn.setPadding(0, 8, 0, 16);
                    deleteBtn.setOnClickListener(v -> {
                        FirebaseDatabase.getInstance().getReference("reminders")
                                .child(reminder.reminderId)
                                .removeValue();
                        logContainer.removeView(card);
                    });
                    card.addView(deleteBtn);

                    logContainer.addView(card);

                    // 👇 TỰ ĐỘNG ĐẶT BÁO THỨC
                    try {
                        String[] parts = reminder.time.split(":");
                        int hour = Integer.parseInt(parts[0].trim());
                        int minute = Integer.parseInt(parts[1].trim());

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, hour);
                        calendar.set(Calendar.MINUTE, minute);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);

                        // Nếu thời gian nhắc đã qua, thì bỏ qua
                        if (calendar.getTimeInMillis() >= System.currentTimeMillis()) {
                            setReminder(calendar, childName, reminder.title);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void setReminder(Calendar time, String childName, String message) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("child_name", childName);
        intent.putExtra("message", message);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                (int) System.currentTimeMillis(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), pendingIntent);
    }
}
