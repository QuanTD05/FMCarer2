package com.example.fmcarer;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.example.fmcarer.Model.Child;
import com.example.fmcarer.Model.Notification;
import com.example.fmcarer.Model.Reminder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.*;

public class ReminderActivity extends AppCompatActivity {
    private EditText edtDescription, edtTime;
    private CheckBox chkRepeat;
    private Spinner childSpinner, spinnerTitle, spinnerRepeatType;
    private List<Child> childList = new ArrayList<>();
    private String selectedChildId;
    private String selectedChildName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        edtDescription = findViewById(R.id.edtDescription);
        edtTime = findViewById(R.id.edtTime);
        chkRepeat = findViewById(R.id.chkRepeat);
        childSpinner = findViewById(R.id.spinnerChildren);
        spinnerTitle = findViewById(R.id.spinnerTitle);
        spinnerRepeatType = findViewById(R.id.spinnerRepeatType);

        setupTitleSpinner();
        setupRepeatSpinner();
        loadChildren();

        edtTime.setOnClickListener(v -> showTimePicker());
        findViewById(R.id.btnSaveReminder).setOnClickListener(v -> saveReminder());
    }

    private void setupTitleSpinner() {
        String[] titles = {"Ăn", "Ngủ", "Tắm", "Uống sữa", "Chơi", "Khác"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, titles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTitle.setAdapter(adapter);
    }

    private void setupRepeatSpinner() {
        String[] options = {"Không lặp", "Hằng ngày", "Hằng tuần", "Hằng tháng"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRepeatType.setAdapter(adapter);
    }

    private void showTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute1) -> {
                    String time = String.format("%02d:%02d", hourOfDay, minute1);
                    edtTime.setText(time);
                }, hour, minute, true);
        timePickerDialog.show();
    }

    private void loadChildren() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("children");
        ref.orderByChild("userId").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                childList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Child child = snap.getValue(Child.class);
                    if (child != null) childList.add(child);
                }

                if (!childList.isEmpty()) {
                    List<String> names = new ArrayList<>();
                    for (Child c : childList) {
                        names.add(c.getName());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(ReminderActivity.this,
                            android.R.layout.simple_spinner_item, names);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    childSpinner.setAdapter(adapter);

                    childSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selectedChildId = childList.get(position).getChildId();
                            selectedChildName = childList.get(position).getName();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {}
                    });
                    childSpinner.setSelection(0);
                    selectedChildId = childList.get(0).getChildId();
                    selectedChildName = childList.get(0).getName();
                } else {
                    Toast.makeText(ReminderActivity.this, "Không tìm thấy trẻ nào", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void saveReminder() {
        if (selectedChildId == null || edtTime.getText().toString().isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("reminders");
        String id = ref.push().getKey();

        Reminder reminder = new Reminder();
        reminder.reminderId = id;
        reminder.childId = selectedChildId;
        reminder.title = spinnerTitle.getSelectedItem().toString();
        reminder.description = edtDescription.getText().toString();
        reminder.time = edtTime.getText().toString();
        reminder.isRepeat = chkRepeat.isChecked();
        reminder.repeatType = spinnerRepeatType.getSelectedItem().toString();

        ref.child(id).setValue(reminder).addOnSuccessListener(unused -> {
            saveReminderNotification(reminder);
            saveActivityNotification(reminder);
            showSystemNotification(reminder);
            Toast.makeText(this, "Đã lưu nhắc nhở", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void saveReminderNotification(Reminder reminder) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference notiRef = FirebaseDatabase.getInstance()
                .getReference("ReminderNotifications")
                .child(uid);
        String notiId = notiRef.push().getKey();

        Notification notification = new Notification();
        notification.setUserid(uid);
        notification.setText("Bạn đã đặt lịch cho bé " + selectedChildName + ": " +
                reminder.title + " lúc " + reminder.time);
        notification.setPostid(reminder.reminderId);
        notification.setIspost(false);

        notiRef.child(notiId).setValue(notification);
    }

    private void saveActivityNotification(Reminder reminder) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference activityRef = FirebaseDatabase.getInstance()
                .getReference("Notifications")
                .child(uid);
        String id = activityRef.push().getKey();

        Notification activityNoti = new Notification();
        activityNoti.setUserid(uid);
        activityNoti.setText("Đã thêm nhắc nhở cho bé " + selectedChildName + ": " +
                reminder.title + " vào lúc " + reminder.time);
        activityNoti.setPostid(reminder.reminderId);
        activityNoti.setIspost(false);

        activityRef.child(id).setValue(activityNoti);
    }

    private void showSystemNotification(Reminder reminder) {
        String channelId = "reminder_channel";
        String channelName = "Nhắc nhở";
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Tạo kênh nếu Android >= O
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Thông báo nhắc nhở");
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            channel.enableVibration(true);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(android.R.drawable.ic_popup_reminder)
                .setContentTitle("Lịch nhắc nhở mới cho bé " + selectedChildName)
                .setContentText(reminder.title + " lúc " + reminder.time)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
