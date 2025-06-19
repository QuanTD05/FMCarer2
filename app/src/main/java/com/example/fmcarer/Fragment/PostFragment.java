package com.example.fmcarer.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

import com.example.fmcarer.Adapter.PostAdapted;
import com.example.fmcarer.Adapter.StroryAdapter;
import com.example.fmcarer.Model.Post;
import com.example.fmcarer.Model.Story;
import com.example.fmcarer.Model.User;
import com.example.fmcarer.PostActivity;
import com.example.fmcarer.R;

public class PostFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapted postAdapted;
    private List<Post> postLists;

    TextView posttui;
    ImageView userIcon;

    private FirebaseUser firebaseUser;
    private String profileId;

    private RecyclerView recyclerView_story;
    private StroryAdapter storyAdapter;
    private List<Story> storyList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        // Initialize Firebase user and SharedPreferences for profile ID
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileId = prefs.getString("profileid", "none");

        posttui = view.findViewById(R.id.inputText);
        posttui.setOnClickListener(v -> startActivity(new Intent(getActivity(), PostActivity.class)));

        userIcon = view.findViewById(R.id.userIcon);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        postLists = new ArrayList<>();
        postAdapted = new PostAdapted(getContext(), postLists);
        recyclerView.setAdapter(postAdapted);

        recyclerView_story = view.findViewById(R.id.recycler_view_story);
        recyclerView_story.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView_story.setLayoutManager(linearLayoutManager1);
        storyList = new ArrayList<>();
        storyAdapter = new StroryAdapter(getContext(), storyList);
        recyclerView_story.setAdapter(storyAdapter);

        // Tải tất cả bài đăng mà KHÔNG lọc following
        readPosts();

        // Tải stories
        readStory();

        // Tải ảnh tài khoản
        loadUserInfo();

        return view;
    }

    private void readPosts(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postLists.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Post post = snapshot1.getValue(Post.class);
                    if (post == null) continue;

                    // Thêm tất cả bài đăng mà KHÔNG lọc following
                    postLists.add(post);
                }
                postAdapted.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadUserInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getContext() == null) return;
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    Glide.with(getContext()).load(user.getImageurl()).into(userIcon);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ProfileFragment", "Failed to load user info: " + error.getMessage());
            }
        });
    }

    private void readStory(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long timecurrent = System.currentTimeMillis();
                storyList.clear();

                // Thêm dòng story của chính bạn nếu có
                storyList.add(new Story("", 0, 0,"",
                        FirebaseAuth.getInstance().getCurrentUser().getUid()));

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    int countStory = 0;
                    Story story = null;
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        story = ds.getValue(Story.class);
                        if (timecurrent > story.getTimestart() && timecurrent < story.getTimeend()) {
                            countStory++;
                        }
                    }
                    if (countStory > 0) {
                        storyList.add(story);
                    }
                }
                storyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
