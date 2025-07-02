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

        return view;
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
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    private void showAddDialog() {
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

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Thêm trẻ")
                .setView(view)
                .setPositiveButton("Lưu", null)
                .setNegativeButton("Hủy", null)
                .create();

        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String birthdate = edtBirthday.getText().toString().trim();
            String gender = edtGender.getText().toString().trim();
            String address = edtAddress.getText().toString().trim();

            if (name.isEmpty() || birthdate.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            String childId = dbRef.push().getKey();
            int age = calculateAgeFromBirthdate(birthdate);

            Child child = new Child(childId, name, age, gender, birthdate, address, "");
            child.setUserId(currentUserId);

            if (selectedImageUri != null) {
                FirebaseStorage.getInstance().getReference("child_images")
                        .child(childId + ".jpg")
                        .putFile(selectedImageUri)
                        .addOnSuccessListener(taskSnapshot ->
                                taskSnapshot.getStorage().getDownloadUrl()
                                        .addOnSuccessListener(uri -> {
                                            child.setImageUrl(uri.toString());
                                            dbRef.child(childId).setValue(child);
                                        }));
            } else {
                dbRef.child(childId).setValue(child);
            }

            Toast.makeText(getContext(), "Thêm trẻ thành công!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
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
}
