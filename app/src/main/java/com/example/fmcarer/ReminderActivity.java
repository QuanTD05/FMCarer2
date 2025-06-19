package com.example.fmcarer;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fmcarer.Model.Child;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReminderActivity extends AppCompatActivity {
    private EditText edtTitle, edtDesc, edtTime, edtRepeatType;
    private CheckBox chkRepeat;
    private Spinner childSpinner;
    private List<Child> childList = new ArrayList<>();
    private String selectedChildId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        edtTitle = findViewById(R.id.edtTitle);
        edtDesc = findViewById(R.id.edtDescription);
        edtTime = findViewById(R.id.edtTime);
        edtRepeatType = findViewById(R.id.edtRepeatType);
        chkRepeat = findViewById(R.id.chkRepeat);
        childSpinner = findViewById(R.id.spinnerChildren);

        loadChildren();

        findViewById(R.id.btnSaveReminder).setOnClickListener(v -> saveReminder());
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

                ArrayAdapter<String> adapter = new ArrayAdapter<>(ReminderActivity.this,
                        android.R.layout.simple_spinner_item,
                        childList.stream().map(Child::getName).collect(Collectors.toList()));
                childSpinner.setAdapter(adapter);

                childSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedChildId = childList.get(position).getChildId();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }


    private void saveReminder() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("reminders");
        String id = ref.push().getKey();

        Reminder reminder = new Reminder();
        reminder.reminderId = id;
        reminder.childId = selectedChildId;
        reminder.title = edtTitle.getText().toString();
        reminder.description = edtDesc.getText().toString();
        reminder.time = edtTime.getText().toString();
        reminder.isRepeat = chkRepeat.isChecked();
        reminder.repeatType = edtRepeatType.getText().toString();

        ref.child(id).setValue(reminder);
        finish();
    }
}
