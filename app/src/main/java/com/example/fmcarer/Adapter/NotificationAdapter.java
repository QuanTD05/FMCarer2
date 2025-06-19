//package fpoly.md19304.instagram.Adapter;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.FragmentActivity;
//import androidx.recyclerview.widget.RecyclerView;
//
//
//import com.bumptech.glide.Glide;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.io.FileInputStream;
//import java.util.List;
//
//import fpoly.md19304.instagram.Fragment.NotificationFragment;
//import fpoly.md19304.instagram.Fragment.ProfileFragment;
//import fpoly.md19304.instagram.Model.Notification;
//import fpoly.md19304.instagram.Model.Post;
//import fpoly.md19304.instagram.Model.User;
//import fpoly.md19304.instagram.R;
//
//public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
//
//    private Context mContext;
//    private List<Notification> mNotification;
//
//    public NotificationAdapter(Context mContext, List<Notification> mNotification) {
//        this.mContext = mContext;
//        this.mNotification = mNotification;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(mContext).inflate(R.layout.notification_item, parent, false);
//        return new NotificationAdapter.ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        final  Notification notification = mNotification.get(position);
//
//        holder.text.setText(notification.getText());
//
//        getUserInfo(holder.image_profile, holder.username, notification.getUserid());
//
//        if (notification.isIspost()){
//            holder.post_image.setVisibility(View.GONE);
//            getPostImage(holder.post_image, notification.getPostid());
//
//        }else {
//            holder.post_image.setVisibility(View.GONE);
//        }
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (notification.isIspost()){
//                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
//                    editor.putString("postid", notification.getPostid());
//                    editor.apply();
//
//                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NotificationFragment()).commit();
//                }else {
//                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
//                    editor.putString("profileid", notification.getUserid());
//                    editor.apply();
//
//                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
//                }
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return mNotification.size();
//    }
//
//    public  class ViewHolder extends RecyclerView.ViewHolder {
//
//        public ImageView image_profile, post_image;
//
//        public TextView username, text;
//        public ViewHolder(@NonNull View itemView) {
//
//            super(itemView);
//
//            image_profile = itemView.findViewById(R.id.image_profile);
//            post_image = itemView.findViewById(R.id.post_image);
//            username = itemView.findViewById(R.id.username);
//            text = itemView.findViewById(R.id.comment);
//        }
//    }
//
//
//    private  void getUserInfo(final ImageView imageView, final TextView username, String publisherid){
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(publisherid);
//
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                User user = snapshot.getValue(User.class);
//                Glide.with(mContext).load(user.getImageurl()).into(imageView);
//                username.setText(user.getUsername());
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//
//    private void getPostImage(ImageView imageView, String postid){
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(postid);
//         reference.addValueEventListener(new ValueEventListener() {
//             @Override
//             public void onDataChange(@NonNull DataSnapshot snapshot) {
//                 Post post = snapshot.getValue(Post.class);
//                 Glide.with(mContext).load(post.getPostimage()).into(imageView);
//             }
//
//             @Override
//             public void onCancelled(@NonNull DatabaseError error) {
//
//             }
//         });
//    }
//}





// sửa lại code trên


package com.example.fmcarer.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import com.example.fmcarer.Fragment.ProfileFragment;
import com.example.fmcarer.Model.Notification;
import com.example.fmcarer.Model.Post;
import com.example.fmcarer.Model.User;
import com.example.fmcarer.PostDetailFragment;
import com.example.fmcarer.R;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private final Context mContext;
    private final List<Notification> mNotification;

    public NotificationAdapter(Context mContext, List<Notification> mNotification) {
        this.mContext = mContext;
        this.mNotification = mNotification;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.notification_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Notification notification = mNotification.get(position);

        holder.text.setText(notification.getText());
        getUserInfo(holder.image_profile, holder.username, notification.getUserid());

        if (notification.isIspost()) {
            holder.post_image.setVisibility(View.VISIBLE);
            getPostImage(holder.post_image, notification.getPostid());
        } else {
            holder.post_image.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
            if (notification.isIspost()) {
                editor.putString("postid", notification.getPostid());
                editor.apply();
                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new PostDetailFragment())
                        .commit();
            } else {
                editor.putString("profileid", notification.getUserid());
                editor.apply();
                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ProfileFragment())
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNotification.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView image_profile;
        public final ImageView post_image;
        public final TextView username;
        public final TextView text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image_profile = itemView.findViewById(R.id.image_profile);
            post_image = itemView.findViewById(R.id.post_image);
            username = itemView.findViewById(R.id.username);
            text = itemView.findViewById(R.id.comment);
        }
    }

    private void getUserInfo(final ImageView imageView, final TextView username, String publisherid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(publisherid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    Glide.with(mContext).load(user.getImageurl()).into(imageView);
                    username.setText(user.getUsername());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
            }
        });
    }

    private void getPostImage(ImageView imageView, String postid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post = snapshot.getValue(Post.class);
                if (post != null) {
                    Glide.with(mContext).load(post.getPostimage()).into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
            }
        });
    }
}

