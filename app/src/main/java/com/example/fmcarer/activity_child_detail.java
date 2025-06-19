package com.example.fmcarer;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.fmcarer.Model.Child;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class activity_child_detail extends AppCompatActivity {

    private ImageView detailImageView;
    private TextView detailNameTextView, detailAgeTextView, detailGenderTextView, detailBirthdateTextView, detailAddressTextView;
    private LinearLayout btnReminder, btnDiary, btnEdit;

    private Child child;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_detail); // sử dụng đúng layout mới

        // Ánh xạ view
        detailImageView = findViewById(R.id.detailImageView);
        detailNameTextView = findViewById(R.id.detailNameTextView);
        detailAgeTextView = findViewById(R.id.detailAgeTextView);
        detailGenderTextView = findViewById(R.id.detailGenderTextView);
        detailBirthdateTextView = findViewById(R.id.detailBirthdateTextView);
        detailAddressTextView = findViewById(R.id.detailAddressTextView);

        btnReminder = findViewById(R.id.btnReminder);
        btnDiary = findViewById(R.id.btnDiary);
        btnEdit = findViewById(R.id.btnEdit);

        dbRef = FirebaseDatabase.getInstance().getReference("children");

        // Nhận dữ liệu từ Intent
        child = (Child) getIntent().getSerializableExtra("CHILD_OBJECT");

        if (child != null) {
            detailNameTextView.setText(child.getName());
            detailBirthdateTextView.setText("Ngày sinh: " + child.getBirthdate());
            detailGenderTextView.setText("Giới tính: " + child.getGender());
            detailAddressTextView.setText("Địa chỉ: " + child.getAddress());
            detailAgeTextView.setText("Tuổi: " + calculateAge(child.getBirthdate()));
            // Nếu có URL ảnh, bạn có thể dùng Glide/Picasso để load ảnh từ Firebase Storage
             Glide.with(this).load(child.getImageUrl()).into(detailImageView);
        }

        // Xử lý click
        btnReminder.setOnClickListener(v -> {
            Intent intent = new Intent(this, ReminderActivity.class);
            intent.putExtra("CHILD_ID", child.getChildId());
            startActivity(intent);
        });

        btnDiary.setOnClickListener(v -> {
            Intent intent = new Intent(this, CareLogActivity.class);
            intent.putExtra("CHILD_ID", child.getChildId());
            startActivity(intent);
        });

        btnEdit.setOnClickListener(v -> showEditDialog());
    }

    private void showEditDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_child, null);
        EditText edtName = view.findViewById(R.id.edtName);
        EditText edtBirthday = view.findViewById(R.id.edtBirthday);
        EditText edtGender = view.findViewById(R.id.edtGender);
        EditText edtAddress = view.findViewById(R.id.edtAddress);

        edtName.setText(child.getName());
        edtBirthday.setText(child.getBirthdate());
        edtGender.setText(child.getGender());
        edtAddress.setText(child.getAddress());

        new AlertDialog.Builder(this)
                .setTitle("Chỉnh sửa thông tin")
                .setView(view)
                .setPositiveButton("Cập nhật", (dialog, which) -> {
                    child.setName(edtName.getText().toString());
                    child.setBirthdate(edtBirthday.getText().toString());
                    child.setGender(edtGender.getText().toString());
                    child.setAddress(edtAddress.getText().toString());

                    dbRef.child(child.getChildId()).setValue(child);

                    detailNameTextView.setText(child.getName());
                    detailBirthdateTextView.setText("Ngày sinh: " + child.getBirthdate());
                    detailGenderTextView.setText("Giới tính: " + child.getGender());
                    detailAddressTextView.setText("Địa chỉ: " + child.getAddress());
                    detailAgeTextView.setText("Tuổi: " + calculateAge(child.getBirthdate()));
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private String calculateAge(String birthdateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Calendar birthDate = Calendar.getInstance();
            birthDate.setTime(sdf.parse(birthdateStr));

            Calendar today = Calendar.getInstance();

            int age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);
            if (today.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }

            return String.valueOf(age);
        } catch (ParseException e) {
            return "Không xác định";
        }
    }
}
