//package fpoly.md19304.instagram.Fragment;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.bumptech.glide.Glide;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import fpoly.md19304.instagram.Adapter.UserAdapter;
//import fpoly.md19304.instagram.Model.Post;
//import fpoly.md19304.instagram.Model.User;
//import fpoly.md19304.instagram.R;
//
//
//public class ProfileFragment extends Fragment {
//        ImageView image_profile, options;
//
//        TextView posts, followers, following, fullname, bio, username;
//
//        Button edit_profile;
//        FirebaseUser firebaseUser;
//        String profileid;
//        ImageButton my_fotos, saved_fotos;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
//
//
//        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
//        profileid = prefs.getString("profileid", "none");
//
//        image_profile = view.findViewById(R.id.image_profile);
//        options = view.findViewById(R.id.options);
//        posts = view.findViewById(R.id.posts);
//        followers = view.findViewById(R.id.followers);
//        following = view.findViewById(R.id.following);
//        fullname = view.findViewById(R.id.fullname);
//        bio = view.findViewById(R.id.bio);
//        username = view.findViewById(R.id.username);
//        edit_profile = view.findViewById(R.id.edit_profile);
//        my_fotos = view.findViewById(R.id.my_fotos);
//        saved_fotos = view.findViewById(R.id.saved_fotos);
//
//        userInfo();
//        getFollowers();
//        getNrPosts();
//
//        if (profileid.equals(firebaseUser.getUid())){
//            edit_profile.setText("Edit Profile");
//        }else {
//            checkFollow();
//            saved_fotos.setVisibility(View.GONE);
//        }
//
//        edit_profile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String btn = edit_profile.getText().toString();
//                if (btn.equals("Edit Profile")){
//
//                } else if(btn.equals("follow")){
//                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
//                            .child("following").child(profileid).setValue(true);
//                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
//                            .child("followers").child(firebaseUser.getUid()).setValue(true);
//                } else if (btn.equals("following")){
//
//
//                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
//                            .child("following").child(profileid).removeValue();
//                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
//                            .child("followers").child(firebaseUser.getUid()).removeValue();
//                }
//            }
//        });
//
//        return view;
//    }
//
//
//
//    private  void userInfo(){
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (getContext() == null){
//                    return;
//                }
//
//                User user = snapshot.getValue(User.class);
//
//                Glide.with(getContext()).load(user.getImageurl()).into(image_profile);
//                username.setText(user.getUsername());
//                fullname.setText(user.getFullname());
//                bio.setText(user.getBio());
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//
//
//
//    private void checkFollow(){
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
//                .child("Follow").child(firebaseUser.getUid()).child("following");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.child(profileid).exists()){
//                    edit_profile.setText("following");
//
//                }else {
//                    edit_profile.setText("follow");
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//
//
//    private void getFollowers(){
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
//                .child("Follow").child(profileid).child("followers");
//
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                followers.setText(""+snapshot.getChildrenCount());
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//
//
//    private void getNrPosts(){
//        DatabaseReference reference =FirebaseDatabase.getInstance().getReference("Posts");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                int i = 0;
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
//                    Post post = dataSnapshot.getValue(Post.class);
//                    if (post.getPublisher().equals(profileid)){
//                        i++;
//                    }
//                }
//
//                posts.setText(""+ i);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//}



//
//package fpoly.md19304.instagram.Fragment;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.TextView;
//import com.bumptech.glide.Glide;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import fpoly.md19304.instagram.Model.Post;
//import fpoly.md19304.instagram.Model.User;
//import fpoly.md19304.instagram.R;
//
//public class ProfileFragment extends Fragment {
//    private ImageView image_profile, options;
//    private TextView posts, followers, following, fullname, bio, username;
//    private Button edit_profile;
//    private FirebaseUser firebaseUser;
//    private String profileid;
//    private ImageButton my_fotos, saved_fotos;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_profile, container, false);
//
//        // Initialize Firebase and SharedPreferences
//        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
//        profileid = prefs.getString("profileid", "none");
//
//        // Bind views
//        image_profile = view.findViewById(R.id.image_profile);
//        options = view.findViewById(R.id.options);
//        posts = view.findViewById(R.id.posts);
//        followers = view.findViewById(R.id.followers);
//        following = view.findViewById(R.id.following);
//        fullname = view.findViewById(R.id.fullname);
//        bio = view.findViewById(R.id.bio);
//        username = view.findViewById(R.id.username);
//        edit_profile = view.findViewById(R.id.edit_profile);
//        my_fotos = view.findViewById(R.id.my_fotos);
//        saved_fotos = view.findViewById(R.id.saved_fotos);
//
//        // Load user information and other data
//        userInfo();
//        getFollowers();
//        getNrPosts();
//
//        // Set up the profile edit button
//        if (profileid.equals(firebaseUser.getUid())) {
//            edit_profile.setText("Edit Profile");
//        } else {
//            checkFollow();
//            saved_fotos.setVisibility(View.GONE);
//        }
//
//        edit_profile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String btn = edit_profile.getText().toString();
//                if (btn.equals("Edit Profile")) {
//                    // Implement edit profile functionality here
//                } else if (btn.equals("follow")) {
//                    // Handle follow action
//                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
//                            .child("following").child(profileid).setValue(true);
//                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
//                            .child("followers").child(firebaseUser.getUid()).setValue(true);
//                } else if (btn.equals("following")) {
//                    // Handle unfollow action
//                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
//                            .child("following").child(profileid).removeValue();
//                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
//                            .child("followers").child(firebaseUser.getUid()).removeValue();
//                }
//            }
//        });
//
//        return view;
//    }
//
//    private void userInfo() {
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (getContext() == null) {
//                    return;
//                }
//
//                User user = snapshot.getValue(User.class);
//                if (user != null) {
//                    Glide.with(getContext()).load(user.getImageurl()).into(image_profile);
//                    username.setText(user.getUsername());
//                    fullname.setText(user.getFullname());
//                    bio.setText(user.getBio());
//                } else {
//                    Log.e("ProfileFragment", "User data is null");
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e("ProfileFragment", "Failed to read user info: " + error.getMessage());
//            }
//        });
//    }
//
//    private void checkFollow() {
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
//                .child("Follow").child(firebaseUser.getUid()).child("following");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.child(profileid).exists()) {
//                    edit_profile.setText("following");
//                } else {
//                    edit_profile.setText("follow");
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e("ProfileFragment", "Failed to check follow status: " + error.getMessage());
//            }
//        });
//    }
//
//    private void getFollowers() {
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
//                .child("Follow").child(profileid).child("followers");
//
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                followers.setText(String.valueOf(snapshot.getChildrenCount()));
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e("ProfileFragment", "Failed to get followers count: " + error.getMessage());
//            }
//        });
//    }
//
//    private void getNrPosts() {
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                int postCount = 0;
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    Post post = dataSnapshot.getValue(Post.class);
//                    if (post != null && post.getPublisher().equals(profileid)) {
//                        postCount++;
//                    }
//                }
//                posts.setText(String.valueOf(postCount));
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e("ProfileFragment", "Failed to get number of posts: " + error.getMessage());
//            }
//        });
//    }
//}




//
//package fpoly.md19304.instagram.Fragment;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.TextView;
//import com.bumptech.glide.Glide;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//
//import fpoly.md19304.instagram.Adapter.MyFotoAdapter;
//import fpoly.md19304.instagram.EditProfileActivity;
//import fpoly.md19304.instagram.Model.Post;
//import fpoly.md19304.instagram.Model.User;
//import fpoly.md19304.instagram.R;
//
//public class ProfileFragment extends Fragment {
//    private ImageView image_profile, options;
//    private TextView posts, followers, following, fullname, bio, username;
//    private Button edit_profile;
//    private FirebaseUser firebaseUser;
//    private String profileid;
//    private ImageButton my_fotos, saved_fotos;
//    private List<String> mySaves;
//    RecyclerView recyclerView_save;
//    MyFotoAdapter  myFotoAdapter_save;
//    List<Post> postList_save;
//
//    RecyclerView recyclerView;
//    MyFotoAdapter  myFotoAdapter;
//    List<Post> postList;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_profile, container, false);
//
//        // Initialize Firebase and SharedPreferences
//        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
//        profileid = prefs.getString("profileid", "none");
//
//        // Bind views
//        image_profile = view.findViewById(R.id.image_profile);
//        options = view.findViewById(R.id.options);
//        posts = view.findViewById(R.id.posts);
//        followers = view.findViewById(R.id.followers);
//        following = view.findViewById(R.id.following);
//        fullname = view.findViewById(R.id.fullname);
//        bio = view.findViewById(R.id.bio);
//        username = view.findViewById(R.id.username);
//        edit_profile = view.findViewById(R.id.edit_profile);
//        my_fotos = view.findViewById(R.id.my_fotos);
//        saved_fotos = view.findViewById(R.id.saved_fotos);
//
//        recyclerView = view.findViewById(R.id.recycler_view);
//        recyclerView.setHasFixedSize(true);
//        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 3);
//        recyclerView.setLayoutManager(linearLayoutManager);
//        postList = new ArrayList<>();
//        myFotoAdapter = new MyFotoAdapter(getContext(), postList);
//        recyclerView.setAdapter(myFotoAdapter);
//
//        recyclerView_save = view.findViewById(R.id.recycler_view_save);
//        recyclerView_save.setHasFixedSize(true);
//        LinearLayoutManager linearLayoutManager_save = new GridLayoutManager(getContext(), 3);
//        recyclerView_save.setLayoutManager(linearLayoutManager_save);
//        postList_save = new ArrayList<>();
//        myFotoAdapter_save = new MyFotoAdapter(getContext(), postList_save);
//        recyclerView_save.setAdapter(myFotoAdapter_save);
//
//        recyclerView.setVisibility(View.VISIBLE);
//        recyclerView_save.setVisibility(View.GONE);
//
//        // Load user information and other data
//        userInfo();
//        getFollowers();
//        getFollowing();  // Load the following count
//        getNrPosts();
//        myFotos();
//        mysaves();
//
//        // Set up the profile edit button
//        if (profileid.equals(firebaseUser.getUid())) {
//            edit_profile.setText("Edit Profile");
//        } else {
//            checkFollow();
//            saved_fotos.setVisibility(View.GONE);
//        }
//
//        edit_profile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String btn = edit_profile.getText().toString();
//                if (btn.equals("Edit Profile")) {
//                    startActivity(new Intent(getContext(), EditProfileActivity.class));
//                } else if (btn.equals("follow")) {
//                    followUser();
//                    addNotifications();
//                } else if (btn.equals("following")) {
//                    unfollowUser();
//                }
//            }
//        });
//
//        my_fotos.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                recyclerView.setVisibility(View.VISIBLE);
//                recyclerView_save.setVisibility(View.GONE);
//            }
//        });
//
//        saved_fotos.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                recyclerView.setVisibility(View.GONE);
//                recyclerView_save.setVisibility(View.VISIBLE);
//            }
//        });
//
//        return view;
//    }
//
//    // Method to follow a user
//    private void followUser() {
//        FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
//                .child("following").child(profileid).setValue(true)
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
//                                .child("followers").child(firebaseUser.getUid()).setValue(true);
//                    }
//                }).addOnFailureListener(e -> Log.e("ProfileFragment", "Failed to follow: " + e.getMessage()));
//    }
//
//    // Method to unfollow a user
//    private void unfollowUser() {
//        FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
//                .child("following").child(profileid).removeValue()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
//                                .child("followers").child(firebaseUser.getUid()).removeValue();
//                    }
//                }).addOnFailureListener(e -> Log.e("ProfileFragment", "Failed to unfollow: " + e.getMessage()));
//    }
//
//    private void addNotifications(){
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(profileid);
//
//        HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put("userid", firebaseUser.getUid());
//        hashMap.put("text", "started following you");
//        hashMap.put("postid", "");
//        hashMap.put("ispost", false);
//
//        reference.push().setValue(hashMap);
//
//    }
//
//
//
//    // Load user information (profile image, name, bio, etc.)
//    private void userInfo() {
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (getContext() == null) {
//                    return;
//                }
//
//                User user = snapshot.getValue(User.class);
//                if (user != null) {
//                    Glide.with(getContext()).load(user.getImageurl()).into(image_profile);
//                    username.setText(user.getUsername());
//                    fullname.setText(user.getFullname());
//                    bio.setText(user.getBio());
//                } else {
//                    Log.e("ProfileFragment", "User data is null");
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e("ProfileFragment", "Failed to read user info: " + error.getMessage());
//            }
//        });
//    }
//
//    // Check if the current user is following the profile user
//    private void checkFollow() {
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
//                .child("Follow").child(firebaseUser.getUid()).child("following");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.child(profileid).exists()) {
//                    edit_profile.setText("following");
//                } else {
//                    edit_profile.setText("follow");
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e("ProfileFragment", "Failed to check follow status: " + error.getMessage());
//            }
//        });
//    }
//
//    // Get followers count
//    private void getFollowers() {
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
//                .child("Follow").child(profileid).child("followers");
//
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                followers.setText(String.valueOf(snapshot.getChildrenCount()));
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e("ProfileFragment", "Failed to get followers count: " + error.getMessage());
//            }
//        });
//    }
//
//    // Get following count
//    private void getFollowing() {
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
//                .child("Follow").child(profileid).child("following");
//
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                following.setText(String.valueOf(snapshot.getChildrenCount()));
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e("ProfileFragment", "Failed to get following count: " + error.getMessage());
//            }
//        });
//    }
//
//    // Get the number of posts made by the user
//    private void getNrPosts() {
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                int postCount = 0;
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    Post post = dataSnapshot.getValue(Post.class);
//                    if (post != null && post.getPublisher().equals(profileid)) {
//                        postCount++;
//                    }
//                }
//                posts.setText(String.valueOf(postCount));
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e("ProfileFragment", "Failed to get number of posts: " + error.getMessage());
//            }
//        });
//    }
//
//
//    private void myFotos(){
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                postList.clear();
//                for (DataSnapshot dataSnapshot :snapshot.getChildren()){
//                    Post post = dataSnapshot.getValue(Post.class);
//                    if (post.getPublisher().equals(profileid)){
//                        postList.add(post);
//                    }
//                }
//
//
//                Collections.reverse(postList);
//                myFotoAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//
//
//    private  void mysaves(){
//        mySaves = new ArrayList<>();
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Saves").child(firebaseUser.getUid());
//        reference
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        for (DataSnapshot snapshot1 : snapshot.getChildren()){
//                            mySaves.add(snapshot1.getKey());
//                        }
//                        readSaves();
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//    }
//
//    private void readSaves(){
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                postList_save.clear();
//                for (DataSnapshot snapshot1 : snapshot.getChildren()){
//                    Post post = snapshot1.getValue(Post.class);
//
//                    for (String id : mySaves){
//                        if (post.getPostid().equals(id)){
//                            postList_save.add(post);
//                        }
//                    }
//                }
//                myFotoAdapter_save.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//}





// sửa lại theo phần notification

package com.example.fmcarer.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.example.fmcarer.Adapter.MyFotoAdapter;
import com.example.fmcarer.EditProfileActivity;
import com.example.fmcarer.Model.Post;
import com.example.fmcarer.Model.User;
import com.example.fmcarer.OptionsActivity;
import com.example.fmcarer.R;

public class ProfileFragment extends Fragment {

    // UI Elements
    private ImageView imageProfile, options;
    private TextView posts, followers, following, fullname, bio, username;
    private Button editProfile;
    private ImageButton myFotos, savedFotos;

    // RecyclerView for user posts and saved posts
    private RecyclerView recyclerView, recyclerViewSave;
    private MyFotoAdapter myFotoAdapter, myFotoAdapterSave;
    private List<Post> postList, postListSave;
    private List<String> mySaves;

    // Firebase
    private FirebaseUser firebaseUser;
    private String profileId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Firebase user and SharedPreferences for profile ID
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileId = prefs.getString("profileid", "none");

        // Initialize UI components
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

        // Setup RecyclerView for posts and saved posts
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        postList = new ArrayList<>();
        myFotoAdapter = new MyFotoAdapter(getContext(), postList);
        recyclerView.setAdapter(myFotoAdapter);

        recyclerViewSave = view.findViewById(R.id.recycler_view_save);
        recyclerViewSave.setHasFixedSize(true);
        recyclerViewSave.setLayoutManager(new GridLayoutManager(getContext(), 3));
        postListSave = new ArrayList<>();
        myFotoAdapterSave = new MyFotoAdapter(getContext(), postListSave);
        recyclerViewSave.setAdapter(myFotoAdapterSave);

        // Default view is the user's own posts
        recyclerView.setVisibility(View.VISIBLE);
        recyclerViewSave.setVisibility(View.GONE);

        // Load user data
        loadUserInfo();
        loadFollowers();
        loadFollowing();
        loadPostCount();
        loadUserPosts();
        loadSavedPosts();

        // Check if viewing own profile to display "Edit Profile" button
        if (profileId.equals(firebaseUser.getUid())) {
            editProfile.setText("Chỉnh sửa hồ sơ");
        } else {
            checkIfFollowing();
            savedFotos.setVisibility(View.GONE); // Hide saved posts option if viewing another user’s profile
        }

        // Edit Profile or Follow/Unfollow button functionality
        editProfile.setOnClickListener(v -> {
            String btnText = editProfile.getText().toString();
            if (btnText.equals("Chỉnh sửa hồ sơ")) {
                startActivity(new Intent(getContext(), EditProfileActivity.class));
            } else if (btnText.equals("follow")) {
                followUser();
               addNotifications();
            } else if (btnText.equals("following")) {
                unfollowUser();
            }
        });
        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), OptionsActivity.class);
                startActivity(intent);
            }
        });

        // Toggle between user posts and saved posts
        myFotos.setOnClickListener(v -> {
            recyclerView.setVisibility(View.VISIBLE);
            recyclerViewSave.setVisibility(View.GONE);
        });

        savedFotos.setOnClickListener(v -> {
            recyclerView.setVisibility(View.GONE);
            recyclerViewSave.setVisibility(View.VISIBLE);
        });




        return view;
    }
    private void addNotifications(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(profileId);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "bắt đầu theo dõi bạn");
        hashMap.put("postid", "");
        hashMap.put("ispost", false);

        reference.push().setValue(hashMap);

    }

    // Follow a user and update Firebase database
    private void followUser() {
        FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                .child("following").child(profileId).setValue(true).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId)
                                .child("followers").child(firebaseUser.getUid()).setValue(true);
                    }
                }).addOnFailureListener(e -> Log.e("ProfileFragment", "Failed to follow: " + e.getMessage()));
    }

    // Unfollow a user and update Firebase database
    private void unfollowUser() {
        FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                .child("following").child(profileId).removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId)
                                .child("followers").child(firebaseUser.getUid()).removeValue();
                    }
                }).addOnFailureListener(e -> Log.e("ProfileFragment", "Failed to unfollow: " + e.getMessage()));
    }



    // Load user profile information
    private void loadUserInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getContext() == null) return;
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    Glide.with(getContext()).load(user.getImageurl()).into(imageProfile);
                    username.setText(user.getUsername());
                    fullname.setText(user.getFullname());
                    bio.setText(user.getBio());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ProfileFragment", "Failed to load user info: " + error.getMessage());
            }
        });
    }

    // Check if the current user follows the profile user
    private void checkIfFollowing() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(profileId).exists()) {
                    editProfile.setText("following");
                } else {
                    editProfile.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ProfileFragment", "Failed to check follow status: " + error.getMessage());
            }
        });
    }

    // Load number of followers
    private void loadFollowers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profileId).child("followers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followers.setText(String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ProfileFragment", "Failed to load followers: " + error.getMessage());
            }
        });
    }

    // Load number of users the profile user is following
    private void loadFollowing() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profileId).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                following.setText(String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ProfileFragment", "Failed to load following: " + error.getMessage());
            }
        });
    }

    // Load the number of posts
    private void loadPostCount() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileId)) {
                        count++;
                    }
                }
                posts.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ProfileFragment", "Failed to load post count: " + error.getMessage());
            }
        });
    }

    // Load user's own posts
    private void loadUserPosts() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileId)) {
                        postList.add(post);
                    }
                }
                Collections.reverse(postList);
                myFotoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ProfileFragment", "Failed to load user posts: " + error.getMessage());
            }
        });
    }

    // Load saved posts
    private void loadSavedPosts() {
        mySaves = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Saves")
                .child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    mySaves.add(dataSnapshot.getKey());
                }
                fetchSavedPosts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ProfileFragment", "Failed to load saved posts: " + error.getMessage());
            }
        });
    }

    // Fetch saved posts from the database
    private void fetchSavedPosts() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postListSave.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    for (String id : mySaves) {
                        if (post.getPostid().equals(id)) {
                            postListSave.add(post);
                        }
                    }
                }
                myFotoAdapterSave.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ProfileFragment", "Failed to fetch saved posts: " + error.getMessage());
            }
        });
    }
}

