package com.example.fmcarer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    EditText email, password;
    Button login;
    TextView txt_signup, txtForgot;
    RadioGroup roleGroup;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        txt_signup = findViewById(R.id.txt_signup);
        txtForgot = findViewById(R.id.txtForgot);
        roleGroup = findViewById(R.id.roleGroup);
        auth = FirebaseAuth.getInstance();

        txt_signup.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
        txtForgot.setOnClickListener(v -> showForgotPasswordDialog());

        login.setOnClickListener(v -> {
            String str_email = email.getText().toString().trim();
            String str_password = password.getText().toString().trim();
            int selectedId = roleGroup.getCheckedRadioButtonId();

            if (TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ email và mật khẩu!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedId == -1) {
                Toast.makeText(this, "Vui lòng chọn loại tài khoản!", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedRoleButton = findViewById(selectedId);
            String userRole = selectedRoleButton.getTag().toString(); // "main" hoặc "sub"

            // Xoá SharedPreferences cũ
            SharedPreferences preferences = getSharedPreferences("PREFS", MODE_PRIVATE);
            preferences.edit().clear().apply();

            ProgressDialog pd = new ProgressDialog(LoginActivity.this);
            pd.setMessage("Đang kiểm tra thông tin...");
            pd.setCancelable(false);
            pd.show();

            login.setEnabled(false);

            auth.signInWithEmailAndPassword(str_email, str_password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String uid = auth.getCurrentUser().getUid();
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(uid);

                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    pd.dismiss();
                                    login.setEnabled(true);

                                    if (snapshot.exists()) {
                                        String roleInDb = snapshot.child("role").getValue(String.class);
                                        if (roleInDb != null && roleInDb.equals(userRole)) {
                                            Intent intent;
                                            if (userRole.equals("main")) {
                                                intent = new Intent(LoginActivity.this, MainActivity3.class);
                                            } else {
                                                intent = new Intent(LoginActivity.this, SubMainActivity.class);
                                            }
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            auth.signOut();
                                            Toast.makeText(LoginActivity.this, "Sai loại tài khoản!", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        auth.signOut();
                                        Toast.makeText(LoginActivity.this, "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    pd.dismiss();
                                    login.setEnabled(true);
                                    Toast.makeText(LoginActivity.this, "Lỗi truy cập dữ liệu!", Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else {
                            pd.dismiss();
                            login.setEnabled(true);
                            Toast.makeText(LoginActivity.this, "Sai email hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void showForgotPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quên mật khẩu");
        builder.setMessage("Nhập email để đặt lại mật khẩu");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Gửi", (dialog, which) -> {
            String email = input.getText().toString().trim();
            if (!TextUtils.isEmpty(email)) {
                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Email đặt lại mật khẩu đã được gửi.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "Không thể gửi email đặt lại mật khẩu.", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(LoginActivity.this, "Vui lòng nhập email!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}
