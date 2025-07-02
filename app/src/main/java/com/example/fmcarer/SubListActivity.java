package com.example.fmcarer;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fmcarer.Adapter.SubAdapter;
import com.example.fmcarer.Model.Sub;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.*;
import com.google.firebase.database.*;

import java.util.ArrayList;

public class SubListActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private RecyclerView staffRecyclerView;
    private EditText searchEmail;
    private FloatingActionButton btnAddStaff;
    private ArrayList<Sub> userList, filteredList;
    private SubAdapter adapter;
    private DatabaseReference userRef;
    private FirebaseAuth auth;
    private Uri selectedImageUri;
    private ImageView imagePreviewGlobal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_list);

        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            finish();
            return;
        }

        // 🔒 Chặn truy cập nếu là tài khoản phụ
        FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String role = snapshot.child("role").getValue(String.class);
                        if ("sub".equalsIgnoreCase(role)) {
                            Toast.makeText(SubListActivity.this, "Bạn không có quyền truy cập!", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }

                    @Override public void onCancelled(@NonNull DatabaseError error) { }
                });

        initViews();
        loadData();
        setupListeners();
    }

    private void initViews() {
        staffRecyclerView = findViewById(R.id.staffRecyclerView);
        searchEmail = findViewById(R.id.searchEmail);
        btnAddStaff = findViewById(R.id.btnAddSub);

        userList = new ArrayList<>();
        filteredList = new ArrayList<>();
        adapter = new SubAdapter(this, filteredList);

        staffRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        staffRecyclerView.setAdapter(adapter);

        userRef = FirebaseDatabase.getInstance().getReference("Users");
    }

    private void setupListeners() {
        btnAddStaff.setOnClickListener(v -> showAddDialog());

        searchEmail.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterEmail(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void loadData() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) return;

        String currentUid = currentUser.getUid();

        userRef.addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Sub user = data.getValue(Sub.class);
                    if (user != null && "sub".equalsIgnoreCase(user.getRole())
                            && currentUid.equals(user.getCreatorId())) {
                        user.setUser_id(data.getKey());
                        userList.add(user);
                    }
                }
                filterEmail(searchEmail.getText().toString());
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void filterEmail(String email) {
        filteredList.clear();
        for (Sub user : userList) {
            if (user.getEmail().toLowerCase().contains(email.toLowerCase())) {
                filteredList.add(user);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void showAddDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_sub_form, null);

        EditText inputName = view.findViewById(R.id.inputName);
        EditText inputUsername = view.findViewById(R.id.inputUsername);
        EditText inputEmail = view.findViewById(R.id.inputEmail);
        EditText inputPassword = view.findViewById(R.id.inputPassword);
        EditText inputPhone = view.findViewById(R.id.inputPhone);
        EditText inputRole = view.findViewById(R.id.inputRole);

        imagePreviewGlobal = view.findViewById(R.id.imagePreview);
        Button btnSelectImage = view.findViewById(R.id.btnSelectImage);

        inputRole.setText("sub");
        inputRole.setVisibility(View.GONE);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Bạn chưa đăng nhập!", Toast.LENGTH_SHORT).show();
            return;
        }

        String creatorId = currentUser.getUid();

        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        new AlertDialog.Builder(this)
                .setTitle("Thêm tài khoản phụ")
                .setView(view)
                .setPositiveButton("Thêm", (dialog, which) -> {
                    String name = inputName.getText().toString().trim();
                    String username = inputUsername.getText().toString().trim();
                    String email = inputEmail.getText().toString().trim();
                    String password = inputPassword.getText().toString().trim();
                    String phone = inputPhone.getText().toString().trim();
                    String imageUrl = selectedImageUri != null ? selectedImageUri.toString() : "";

                    if (email.isEmpty() || password.isEmpty()) {
                        Toast.makeText(this, "Email và mật khẩu không được để trống", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    FirebaseAuth tempAuth = FirebaseAuth.getInstance();

                    tempAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    String subId = task.getResult().getUser().getUid();

                                    Sub subUser = new Sub(subId, name, username, email, password, phone, imageUrl, "sub", creatorId);

                                    userRef.child(subId).setValue(subUser)
                                            .addOnCompleteListener(saveTask -> {
                                                if (saveTask.isSuccessful()) {
                                                    Toast.makeText(this, "Đã tạo tài khoản phụ", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(this, "Lỗi lưu dữ liệu: " + saveTask.getException(), Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                    tempAuth.signOut();
                                } else {
                                    Toast.makeText(this, "Lỗi tạo tài khoản: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                })
                .setNegativeButton("Huỷ", null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            if (imagePreviewGlobal != null) {
                imagePreviewGlobal.setImageURI(selectedImageUri);
            }
        }
    }
}
