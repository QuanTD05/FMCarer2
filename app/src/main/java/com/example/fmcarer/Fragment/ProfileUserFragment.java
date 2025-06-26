package com.example.fmcarer.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fmcarer.EditProfileActivity;
import com.example.fmcarer.OptionsActivity;
import com.example.fmcarer.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;
public class ProfileUserFragment extends Fragment {
    private ImageView  options;
    private FirebaseUser firebaseUser;
    private CircleImageView imageProfile, imageChild;
    private TextView username, fullname, bio;
    private TextView txtChildName, txtChildBirthdate, txtChildGender;
    private LinearLayout childInfoLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile_user, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        username = view.findViewById(R.id.username);
        fullname = view.findViewById(R.id.fullname);
        bio = view.findViewById(R.id.bio);
        imageProfile = view.findViewById(R.id.image_profile);
        options = view.findViewById(R.id.options);
        imageChild = view.findViewById(R.id.image_child);
        txtChildName = view.findViewById(R.id.child_name);
        txtChildBirthdate = view.findViewById(R.id.child_birthdate);
        txtChildGender = view.findViewById(R.id.child_gender);
        childInfoLayout = view.findViewById(R.id.child_info_layout);


        loadChildInfo();

        return view;

    }

    private void loadUserInfo() {
        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("Users").child(firebaseUser.getUid());

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) return;

                String name = snapshot.child("fullname").getValue(String.class);
                String bioText = snapshot.child("bio").getValue(String.class);
                String imageUrl = snapshot.child("imageUrl").getValue(String.class);
                String uname = snapshot.child("username").getValue(String.class);

                username.setText(uname);
                fullname.setText(name);
                bio.setText(bioText);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadChildInfo() {
        DatabaseReference childRef = FirebaseDatabase.getInstance().getReference("children");

        childRef.orderByChild("userId").equalTo(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            childInfoLayout.setVisibility(View.GONE);
                            return;
                        }

                        for (DataSnapshot childSnap : snapshot.getChildren()) {
                            String name = childSnap.child("name").getValue(String.class);
                            String birthdate = childSnap.child("birthdate").getValue(String.class);
                            String gender = childSnap.child("gender").getValue(String.class);
                            String imageUrl = childSnap.child("imageUrl").getValue(String.class);

                            txtChildName.setText(name);
                            txtChildBirthdate.setText("Ngày sinh: " + birthdate);
                            txtChildGender.setText("Giới tính: " + gender);

                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                Glide.with(getContext()).load(imageUrl).into(imageChild);
                            } else {
                                imageChild.setImageResource(R.drawable.ic_cam);
                            }

                            childInfoLayout.setVisibility(View.VISIBLE);
                            break; // chỉ load 1
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Lỗi tải con: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), OptionsActivity.class);
                startActivity(intent);
            }
        });
    }
}

