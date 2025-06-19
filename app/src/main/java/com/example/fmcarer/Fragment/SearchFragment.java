package com.example.fmcarer.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fmcarer.Adapter.ChildAdapter;
import com.example.fmcarer.DepositActivity;
import com.example.fmcarer.Model.Child;
import com.example.fmcarer.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SearchFragment extends Fragment {

    private DatabaseReference dbRef;
    private List<Child> childList;
    private ChildAdapter adapter;
    private String currentUserId;
    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private Uri selectedImageUri = null;
    private ImageView imgPreview;

    private TextView txtBalance;
    private int balance = 0;

    private static final int REQUEST_NAP_TIEN = 1001;
    private static final int CHILD_COST = 10000; // Ví dụ: 10,000 VNĐ để thêm trẻ thứ 2+

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    if (imgPreview != null && selectedImageUri != null) {
                        imgPreview.setImageURI(selectedImageUri);
                    }
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dbRef = FirebaseDatabase.getInstance().getReference("children");

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        childList = new ArrayList<>();
        adapter = new ChildAdapter(getContext(), childList);
        recyclerView.setAdapter(adapter);
        loadChildren();

        fabAdd = view.findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v -> showAddDialog());

        updateBalanceUI();

        return view;
    }

    private void updateBalanceUI() {
        if (txtBalance != null) {
            txtBalance.setText("Số dư: " + balance + " VNĐ");
        }
    }

    private void loadChildren() {
        dbRef.orderByChild("userId").equalTo(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        childList.clear();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            Child child = snap.getValue(Child.class);
                            if (child != null) {
                                childList.add(child);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void showAddDialog() {
        if (childList.size() >= 1 && balance < CHILD_COST) {
            new AlertDialog.Builder(getContext())
                    .setTitle("Cần nạp tiền")
                    .setMessage("Bạn đã thêm 1 trẻ miễn phí. Để thêm trẻ tiếp theo, bạn cần nạp ít nhất " + CHILD_COST + " VNĐ. Bạn có muốn nạp tiền không?")
                    .setPositiveButton("Nạp tiền", (dialog, which) -> {
                        Intent intent = new Intent(getActivity(), DepositActivity.class);
                        startActivityForResult(intent, REQUEST_NAP_TIEN);
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
            return;
        }

        selectedImageUri = null;
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_child, null);
        EditText edtName = view.findViewById(R.id.edtName);
        EditText edtBirthday = view.findViewById(R.id.edtBirthday);
        EditText edtGender = view.findViewById(R.id.edtGender);
        EditText edtAddress = view.findViewById(R.id.edtAddress);
        imgPreview = view.findViewById(R.id.imgPick);

        imgPreview.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        new AlertDialog.Builder(getContext())
                .setTitle("Thêm trẻ")
                .setView(view)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    String id = dbRef.push().getKey();
                    String name = edtName.getText().toString().trim();
                    String birthdate = edtBirthday.getText().toString().trim();
                    String gender = edtGender.getText().toString().trim();
                    String address = edtAddress.getText().toString().trim();
                    int age = calculateAgeFromBirthdate(birthdate);

                    if (id == null || name.isEmpty() || birthdate.isEmpty()) {
                        Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Child child = new Child(id, name, age, gender, birthdate, address, "");
                    child.setUserId(currentUserId);

                    if (selectedImageUri != null) {
                        FirebaseStorage.getInstance().getReference("child_images")
                                .child(id + ".jpg")
                                .putFile(selectedImageUri)
                                .addOnSuccessListener(taskSnapshot ->
                                        taskSnapshot.getStorage().getDownloadUrl()
                                                .addOnSuccessListener(uri -> {
                                                    child.setImageUrl(uri.toString());
                                                    dbRef.child(id).setValue(child);
                                                }));
                    } else {
                        dbRef.child(id).setValue(child);
                    }

                    if (childList.size() >= 1) {
                        balance -= CHILD_COST;
                        updateBalanceUI();
                        Toast.makeText(getContext(), "Đã trừ " + CHILD_COST + " VNĐ để thêm trẻ.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private int calculateAgeFromBirthdate(String birthdate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date birthDate = sdf.parse(birthdate);
            Calendar birthCal = Calendar.getInstance();
            birthCal.setTime(birthDate);

            Calendar today = Calendar.getInstance();
            int age = today.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR);
            if (today.get(Calendar.DAY_OF_YEAR) < birthCal.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }
            return age;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_NAP_TIEN && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getBooleanExtra("nap_thanh_cong", false)) {
                int soTienNap = data.getIntExtra("so_tien", 0);
                balance += soTienNap;
                updateBalanceUI();
                Toast.makeText(getContext(), "Đã nạp " + soTienNap + " VNĐ thành công!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
