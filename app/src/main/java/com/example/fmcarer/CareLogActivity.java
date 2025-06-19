package com.example.fmcarer;

import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.fmcarer.Model.Child;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CareLogActivity extends AppCompatActivity {
    private LinearLayout logContainer;
    private String currentUserId;
    private List<Child> childList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_care_log);

        logContainer = findViewById(R.id.logContainer);
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        loadChildrenAndReminders();
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

                    // Tìm tên trẻ
                    String childName = "Không rõ";
                    for (Child c : childList) {
                        if (c.getChildId().equals(reminder.childId)) {
                            childName = c.getName();
                            break;
                        }
                    }

                    TextView tv = new TextView(CareLogActivity.this);
                    tv.setText("👶 " + childName + "\n📌 " + reminder.title + "\n🕒 " + reminder.time);
                    tv.setPadding(20, 20, 20, 20);
                    tv.setBackground(ContextCompat.getDrawable(CareLogActivity.this, R.drawable.reminder_card));
                    tv.setTextColor(Color.BLACK);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 0, 0, 30);
                    tv.setLayoutParams(params);

                    logContainer.addView(tv);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
