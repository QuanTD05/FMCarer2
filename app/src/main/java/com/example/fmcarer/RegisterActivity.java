package com.example.fmcarer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText username, fullname, email, password;
    Button register;
    TextView txt_login;
    FirebaseAuth auth;
    DatabaseReference reference;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.username);
        fullname = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        register = findViewById(R.id.register);
        txt_login = findViewById(R.id.txt_login);

        auth = FirebaseAuth.getInstance();

        txt_login.setOnClickListener(v -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));

        register.setOnClickListener(v -> {
            pd = new ProgressDialog(RegisterActivity.this);
            pd.setMessage("Đang đăng ký...");
            pd.setCancelable(false);
            pd.show();

            String str_username = username.getText().toString().trim();
            String str_fullname = fullname.getText().toString().trim();
            String str_email = email.getText().toString().trim();
            String str_password = password.getText().toString().trim();

            if (TextUtils.isEmpty(str_username) || TextUtils.isEmpty(str_fullname) ||
                    TextUtils.isEmpty(str_password) || TextUtils.isEmpty(str_email)) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            } else if (str_password.length() < 6) {
                Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự!", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            } else {
                registerMainAccount(str_username, str_fullname, str_email, str_password);
            }
        });
    }

    private void registerMainAccount(String username, String fullname, String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        String userid = firebaseUser.getUid();

                        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("id", userid);
                        hashMap.put("username", username.toLowerCase());
                        hashMap.put("fullname", fullname);
                        hashMap.put("bio", "");
                        hashMap.put("role", "main");  // ✅ Chỉ đăng ký tài khoản chính
                        hashMap.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/sof102.appspot.com/o/logoHa.png?alt=media&token=97ad2f36-a8f7-405d-8862-2ed7b8f17d96");

                        reference.setValue(hashMap).addOnCompleteListener(task1 -> {
                            pd.dismiss();
                            startActivity(new Intent(RegisterActivity.this, MainActivity3.class));
                            finish();
                        });
                    } else {
                        pd.dismiss();
                        Toast.makeText(RegisterActivity.this, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
