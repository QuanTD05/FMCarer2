//package fpoly.md19304.instagram;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//import android.widget.Toolbar;
//
//import androidx.activity.EdgeToEdge;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
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
//import java.util.HashMap;
//import java.util.Objects;
//
//import fpoly.md19304.instagram.Model.User;
//
//public class CommentsActivity extends AppCompatActivity {
//    EditText addcomment;
//    ImageView image_profile;
//    TextView post;
//    String postid;
//    String publisherid;
//    FirebaseUser firebaseUser;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_comments);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setActionBar(toolbar);
//        getSupportActionBar().setTitle("Comments");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//        addcomment = findViewById(R.id.add_comment);
//        image_profile = findViewById(R.id.image_profile);
//        post = findViewById(R.id.post);
//
//        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//
//
//        Intent intent = getIntent();
//        postid = intent.getStringExtra("postid");
//        publisherid = intent.getStringExtra("publisherid");
//
//
//        post.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (addcomment.getText().toString().equals("")){
//                    Toast.makeText(CommentsActivity.this, "You can't send empty comment", Toast.LENGTH_SHORT).show();
//                } else {
//                    addComment();
//                }
//            }
//        });
//        getImage();
//    }
//    private void addComment(){
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postid);
//
//        HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put("comment", addcomment.getText().toString());
//        hashMap.put("publisherid", firebaseUser.getUid());
//        reference.push().setValue(hashMap);
//        addcomment.setText("");
//    }
//    private void getImage(){
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                User user = snapshot.getValue(User.class);
//                Glide.with(getApplicationContext()).load(user.getImageurl()).into(image_profile);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//}
//package fpoly.md19304.instagram;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
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
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//import fpoly.md19304.instagram.Adapter.CommentAdapter;
//import fpoly.md19304.instagram.Model.Comment;
//import fpoly.md19304.instagram.Model.User;
//
//public class CommentsActivity extends AppCompatActivity {
//
//    private RecyclerView recyclerView;
//    private CommentAdapter commentAdapter;
//    private List<Comment> commentList;
//
//    EditText addcomment;
//    ImageView image_profile;
//    TextView post;
//    String postid;
//    String publisherid;
//    FirebaseUser firebaseUser;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_comments);
//
//        // Setting window insets for edge-to-edge layout
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        // Set up the toolbar
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);  // Correct way to set toolbar in AppCompatActivity
//
//        // Check for null safety
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setTitle("Comments");
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        }
//
//        // Navigate back when toolbar back button is pressed
//        toolbar.setNavigationOnClickListener(v -> finish());
//
//
//        recyclerView = findViewById(R.id.recycler_view);
//        recyclerView.setHasFixedSize(true);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(linearLayoutManager);
//        commentList = new ArrayList<>();
//        commentAdapter = new CommentAdapter(this, commentList);
//        recyclerView.setAdapter(commentAdapter);
//
//        // Initializing views
//        addcomment = findViewById(R.id.add_comment);
//        image_profile = findViewById(R.id.image_profile);
//        post = findViewById(R.id.post);
//
//        // Get the current Firebase user
//        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//
//        // Get data from Intent
//        Intent intent = getIntent();
//        postid = intent.getStringExtra("postid");
//        publisherid = intent.getStringExtra("publisherid");
//
//        // Post comment on click
//        post.setOnClickListener(v -> {
//            if (addcomment.getText().toString().isEmpty()) {
//                Toast.makeText(CommentsActivity.this, "You can't send an empty comment", Toast.LENGTH_SHORT).show();
//            } else {
//                addComment();
//            }
//        });
//
//        // Load profile image
//        getImage();
//        readComments();
//    }
//
//    private void addComment() {
//        // Ensure the current user is not null
//        if (firebaseUser != null) {
//            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postid);
//
//            // Creating a comment entry
//            HashMap<String, Object> hashMap = new HashMap<>();
//            hashMap.put("comment", addcomment.getText().toString());
//            hashMap.put("publisherid", firebaseUser.getUid());
//
//            // Add the comment to Firebase
//            reference.push().setValue(hashMap);
//            addNotifications();
//            addcomment.setText("");  // Clear the comment box after posting
//        } else {
//            Toast.makeText(CommentsActivity.this, "You need to be logged in to comment", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void addNotifications(){
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(publisherid);
//
//        HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put("userid", firebaseUser.getUid());
//        hashMap.put("text", "commented: " + addcomment.getText().toString());
//        hashMap.put("postid", postid);
//        hashMap.put("ispost", true);
//
//        reference.push().setValue(hashMap);
//
//    }
//
//    private void getImage() {
//        if (firebaseUser != null) {
//            // Retrieve current user's profile image
//            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
//            reference.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    // Safely retrieve User object from snapshot
//                    User user = snapshot.getValue(User.class);
//                    if (user != null) {
//                        // Load the user's image into the profile ImageView using Glide
//                        Glide.with(getApplicationContext()).load(user.getImageurl()).into(image_profile);
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//                    // Handle error when fetching data
//                    Toast.makeText(CommentsActivity.this, "Failed to load image: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//    }
//
//
//    private  void readComments(){
//
//
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postid);
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                commentList.clear();
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
//                    Comment comment = dataSnapshot.getValue(Comment.class);
//                    commentList.add(comment);
//                }
//
//                commentAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(CommentsActivity.this, "Failed to load comments: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//}



// sửa theo notification



package com.example.fmcarer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import java.util.HashMap;
import java.util.List;

import com.example.fmcarer.Adapter.CommentAdapter;
import com.example.fmcarer.Model.Comment;
import com.example.fmcarer.Model.User;

public class CommentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;

    EditText addcomment;
    ImageView image_profile;
    TextView post;
    String postid;
    String publisherid;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        // Setting window insets for edge-to-edge layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);  // Correct way to set toolbar in AppCompatActivity

        // Check for null safety
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Comments");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Navigate back when toolbar back button is pressed
        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentList);
        recyclerView.setAdapter(commentAdapter);

        // Initializing views
        addcomment = findViewById(R.id.add_comment);
        image_profile = findViewById(R.id.image_profile);
        post = findViewById(R.id.post);

        // Get the current Firebase user
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // Get data from Intent
        Intent intent = getIntent();
        postid = intent.getStringExtra("postid");
        publisherid = intent.getStringExtra("publisherid");

        // Post comment on click
        post.setOnClickListener(v -> {
            if (addcomment.getText().toString().isEmpty()) {
                Toast.makeText(CommentsActivity.this, "Bạn không thể gửi bình luận trống", Toast.LENGTH_SHORT).show();
            } else {
                addComment();
            }
        });

        // Load profile image
        getImage();
        readComments();
    }

    private void addComment() {
        // Ensure the current user is not null
        if (firebaseUser != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postid);

            // Creating a comment entry
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("comment", addcomment.getText().toString());
            hashMap.put("publisherid", firebaseUser.getUid());

            // Add the comment to Firebase
            reference.push().setValue(hashMap).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    addNotifications();
                    addcomment.setText("");  // Clear the comment box after posting
                } else {
                    Toast.makeText(CommentsActivity.this, "Không đăng được bình luận", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(CommentsActivity.this, "Bạn cần phải đăng nhập để bình luận", Toast.LENGTH_SHORT).show();
        }
    }

    private void addNotifications(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(publisherid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "Bình luận: " + addcomment.getText().toString());
        hashMap.put("postid", postid);
        hashMap.put("ispost", true);

        reference.push().setValue(hashMap);
    }

    private void getImage() {
        if (firebaseUser != null) {
            // Retrieve current user's profile image
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // Safely retrieve User object from snapshot
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        // Load the user's image into the profile ImageView using Glide
                        Glide.with(getApplicationContext()).load(user.getImageurl()).into(image_profile);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error when fetching data
                    Toast.makeText(CommentsActivity.this, "Failed to load image: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void readComments(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Comment comment = dataSnapshot.getValue(Comment.class);
                    commentList.add(comment);
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CommentsActivity.this, "Failed to load comments: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
