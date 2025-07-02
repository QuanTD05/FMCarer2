// file: ProfileFragment.java
package com.example.fmcarer.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.fmcarer.Adapter.MyFotoAdapter;
import com.example.fmcarer.DepositActivity;
import com.example.fmcarer.DialogTopUpFragment;
import com.example.fmcarer.EditProfileActivity;
import com.example.fmcarer.Model.*;
import com.example.fmcarer.OptionsActivity;
import com.example.fmcarer.R;
import com.example.fmcarer.TopUpActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class ProfileFragment extends Fragment {

    private ImageView imageProfile, options;
    private TextView posts, followers, following, fullname, bio, username;
    private Button editProfile, btnTopUp, btnAddBalance;
    private ImageButton myFotos, savedFotos;
    private TextView txtBalance, txtPaidUntil;
    private RecyclerView recyclerView, recyclerViewSave;
    private MyFotoAdapter myFotoAdapter, myFotoAdapterSave;
    private List<Post> postList, postListSave;
    private List<String> mySaves;
    private FirebaseUser firebaseUser;
    private String profileId;

    private String selectedPlan = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences prefs = requireContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileId = prefs.getString("profileid", "none");

        imageProfile = view.findViewById(R.id.image_profile);
        options = view.findViewById(R.id.options);
        posts = view.findViewById(R.id.posts);
        followers = view.findViewById(R.id.followers);
        following = view.findViewById(R.id.following);
        fullname = view.findViewById(R.id.fullname);
        bio = view.findViewById(R.id.bio);
        username = view.findViewById(R.id.username);
        editProfile = view.findViewById(R.id.edit_profile);
        myFotos = view.findViewById(R.id.my_fotos);
        savedFotos = view.findViewById(R.id.saved_fotos);
        txtBalance = view.findViewById(R.id.txtBalance);
        txtPaidUntil = view.findViewById(R.id.txtPaidUntil);
        btnTopUp = view.findViewById(R.id.btnTopUp);
        btnAddBalance = view.findViewById(R.id.btnAddBalance);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        postList = new ArrayList<>();
        myFotoAdapter = new MyFotoAdapter(getContext(), postList);
        recyclerView.setAdapter(myFotoAdapter);

        recyclerViewSave = view.findViewById(R.id.recycler_view_save);
        recyclerViewSave.setLayoutManager(new GridLayoutManager(getContext(), 3));
        postListSave = new ArrayList<>();
        myFotoAdapterSave = new MyFotoAdapter(getContext(), postListSave);
        recyclerViewSave.setAdapter(myFotoAdapterSave);
        options.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), OptionsActivity.class);
            startActivity(intent);
        });
        btnAddBalance.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), TopUpActivity.class));
        });

        handleVisibility();
        loadAllData();
        setupListeners();

        return view;
    }

    private void handleVisibility() {
        if (!profileId.equals(firebaseUser.getUid())) {
            savedFotos.setVisibility(View.GONE);
            txtBalance.setVisibility(View.GONE);
            txtPaidUntil.setVisibility(View.GONE);
            btnTopUp.setVisibility(View.GONE);
        }
    }

    private void loadAllData() {
        loadUserInfo();
        loadPostCount();
        loadUserPosts();
        loadSavedPosts();
        loadAccountInfo();
    }

    private void setupListeners() {
        if (profileId.equals(firebaseUser.getUid())) {
            editProfile.setText("Chỉnh sửa hồ sơ");
        }

        editProfile.setOnClickListener(v -> {
            if ("Chỉnh sửa hồ sơ".equals(editProfile.getText().toString())) {
                startActivity(new Intent(getContext(), EditProfileActivity.class));
            }
        });

        myFotos.setOnClickListener(v -> toggleRecyclerViews(true));
        savedFotos.setOnClickListener(v -> toggleRecyclerViews(false));

        btnTopUp.setOnClickListener(v -> showPlanSelectionDialog());
    }

    private void toggleRecyclerViews(boolean showMy) {
        recyclerView.setVisibility(showMy ? View.VISIBLE : View.GONE);
        recyclerViewSave.setVisibility(showMy ? View.GONE : View.VISIBLE);
    }

    private void showPlanSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Chọn gói thanh toán")
                .setItems(new String[]{"1 ngày", "1 tuần", "1 tháng"}, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            selectedPlan = "1 ngày";
                            break;
                        case 1:
                            selectedPlan = "1 tuần";
                            break;
                        case 2:
                            selectedPlan = "1 tháng";
                            break;
                    }
                    checkBankCardAndProceed();
                });
        builder.show();
    }

    private void checkBankCardAndProceed() {
        DatabaseReference cardRef = FirebaseDatabase.getInstance()
                .getReference("BankCards").child(firebaseUser.getUid());

        cardRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    goToDepositActivity();
                } else {
                    DialogTopUpFragment dialog = new DialogTopUpFragment(() -> {
                        // Sau khi lưu thẻ xong, kiểm tra lại
                        checkBankCardAndProceed();
                    });
                    dialog.show(getParentFragmentManager(), "AddCardDialog");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("BankCheck", "Firebase Error: " + error.getMessage());
                Toast.makeText(getContext(), "Lỗi kiểm tra thẻ: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void goToDepositActivity() {
        Intent intent = new Intent(getActivity(), DepositActivity.class);
        intent.putExtra("plan", selectedPlan);
        startActivity(intent);
    }

    private void loadUserInfo() {
        FirebaseDatabase.getInstance()
                .getReference("Users").child(profileId)
                .addValueEventListener(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot snap) {
                        User u = snap.getValue(User.class);
                        if (u != null && getContext() != null) {
                            Glide.with(requireContext()).load(u.getImageurl()).into(imageProfile);
                            username.setText(u.getUsername());
                            fullname.setText(u.getFullname());
                            bio.setText(u.getBio());
                        }
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void loadPostCount() {
        FirebaseDatabase.getInstance().getReference("Posts")
                .addValueEventListener(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot snap) {
                        int count = 0;
                        for (DataSnapshot s : snap.getChildren()) {
                            Post p = s.getValue(Post.class);
                            if (p != null && profileId.equals(p.getPublisher())) count++;
                        }
                        posts.setText(String.valueOf(count));
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void loadUserPosts() {
        FirebaseDatabase.getInstance().getReference("Posts")
                .addValueEventListener(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot snap) {
                        postList.clear();
                        for (DataSnapshot s : snap.getChildren()) {
                            Post p = s.getValue(Post.class);
                            if (p != null && profileId.equals(p.getPublisher())) postList.add(p);
                        }
                        Collections.reverse(postList);
                        myFotoAdapter.notifyDataSetChanged();
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void loadSavedPosts() {
        mySaves = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("Saves")
                .child(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot snap) {
                        mySaves.clear();
                        for (DataSnapshot s : snap.getChildren()) {
                            mySaves.add(s.getKey());
                        }
                        fetchSavedPosts();
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void fetchSavedPosts() {
        FirebaseDatabase.getInstance().getReference("Posts")
                .addValueEventListener(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot snap) {
                        postListSave.clear();
                        for (DataSnapshot s : snap.getChildren()) {
                            Post p = s.getValue(Post.class);
                            if (p != null && mySaves.contains(p.getPostid())) postListSave.add(p);
                        }
                        myFotoAdapterSave.notifyDataSetChanged();
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void loadAccountInfo() {
        FirebaseDatabase.getInstance().getReference("Users").child(profileId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String bal = snapshot.child("balance").getValue(String.class);
                        String until = snapshot.child("paidUntil").getValue(String.class);
                        txtBalance.setText("Số dư: " + (bal != null ? bal : "--") + " VNĐ");
                        if (until != null) {
                            long ts = Long.parseLong(until);
                            txtPaidUntil.setText("Hạn sử dụng: " +
                                    new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date(ts)));
                        } else {
                            txtPaidUntil.setText("Hạn sử dụng: --");
                        }
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}
