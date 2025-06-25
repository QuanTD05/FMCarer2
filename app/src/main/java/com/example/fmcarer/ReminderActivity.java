package com.example.fmcarer;

import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

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
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public class ReminderActivity extends AppCompatActivity {
    private EditText edtDescription, edtTime;
    private CheckBox chkRepeat;
    private Spinner childSpinner, spinnerTitle, spinnerRepeatType;
    private List<Child> childList = new ArrayList<>();
    private String selectedChildId;

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
                (TimePicker view, int hourOfDay, int minute1) -> {
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
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(ReminderActivity.this,
                            android.R.layout.simple_spinner_item,
                            childList.stream().map(Child::getName).collect(Collectors.toList()));
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    childSpinner.setAdapter(adapter);
                    childSpinner.setEnabled(false);
                    selectedChildId = childList.get(0).getChildId();
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

        ref.child(id).setValue(reminder);
        Toast.makeText(this, "Đã lưu nhắc nhở", Toast.LENGTH_SHORT).show();
        finish();
    }
}
