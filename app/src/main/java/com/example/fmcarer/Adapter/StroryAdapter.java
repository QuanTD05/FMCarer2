package com.example.fmcarer.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import com.example.fmcarer.AddStoryActivity;
import com.example.fmcarer.Model.Story;
import com.example.fmcarer.Model.User;
import com.example.fmcarer.R;
import com.example.fmcarer.StoryActivity;

public class StroryAdapter extends RecyclerView.Adapter<StroryAdapter.ViewHolder> {
    private Context mContext;
    private List<Story> mStory;

    public StroryAdapter(Context mContext, List<Story> mStory) {
        this.mContext = mContext;
        this.mStory = mStory;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        if( i == 0){
            View view = LayoutInflater.from(mContext).inflate(R.layout.add_story_item, parent,false);
            return new ViewHolder(view);
        }else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.story_item, parent,false);
            return new ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int i) {


         Story story = mStory.get(i);
            userInfo(holder, story.getUserid(),i);
        if (holder.getAdapterPosition() !=0){
            seenStory(holder, story.getUserid());
        }

        if (holder.getAdapterPosition() ==0){
            myStory(holder.addstory_text, holder.story_plus, false);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.getAdapterPosition() == 0) {
                    myStory(holder.addstory_text, holder.story_plus, true);
                } else {
                   Intent intent = new Intent(mContext, StoryActivity.class);
                   intent.putExtra("userid", story.getUserid());
                   mContext.startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mStory.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView story_photo, story_plus, story_photo_seen;
        public TextView story_username, addstory_text;




        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            story_photo = itemView.findViewById(R.id.story_photo);
            story_plus = itemView.findViewById(R.id.story_plus);
            story_photo_seen = itemView.findViewById(R.id.story_photo_seen);
            story_username = itemView.findViewById(R.id.story_username);
            addstory_text= itemView.findViewById(R.id.addstory_text);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return 0;
        }
        return 1;
    }
    private void userInfo(final ViewHolder viewHolder, final String userId, final int pos) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(mContext)
                        .load(user.getImageurl())
                        .into(viewHolder.story_photo);
                if (pos != 0) {

                    Glide.with(mContext)
                            .load(user.getImageurl())
                            .into(viewHolder.story_photo_seen);
                    viewHolder.story_username.setText(user.getUsername());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseError", "Error fetching user data: " + databaseError.getMessage());
            }
        });
    }

    private void myStory(TextView textView, ImageView imageView, boolean click) {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("Story")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count = 0 ;
                long timecurrent = System.currentTimeMillis();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Story story = snapshot.getValue(Story.class);
                    if (timecurrent > story.getTimestart() &&  timecurrent < story.getTimeend()){
                        count++;
                    }
                }
                if (click){
                   if (count >0){
                       AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                       alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Xem tin của mình", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               Intent intent = new Intent(mContext, StoryActivity.class);
                               intent.putExtra("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                               mContext.startActivity(intent);
                               dialog.dismiss();
                           }
                       });
                       alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Thêm tin", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               Intent intent = new Intent(mContext, AddStoryActivity.class);
                               mContext.startActivity(intent);
                               dialog.dismiss();
                           }
                       });
                       alertDialog.show();
                   }else {
                       Intent intent = new Intent(mContext, AddStoryActivity.class);
                       mContext.startActivity(intent);
                   }
                }else {
                    if (count >0){
                        textView.setText("Xem tin của mình");
                        imageView.setVisibility(View.GONE);
                    } else {
                        textView.setText("Thêm tin");
                        imageView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseError", "Error fetching story data: " + databaseError.getMessage());
            }
        });
    }

    private void seenStory(ViewHolder viewHolder, String userid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if(!dataSnapshot.child("views").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .exists() && System.currentTimeMillis() < dataSnapshot.getValue(Story.class).getTimeend()){
                        i++;
                    }
                }

                if (i > 0){
                    viewHolder.story_photo.setVisibility(View.VISIBLE);
                    viewHolder.story_photo_seen.setVisibility(View.GONE);
                } else {
                    viewHolder.story_photo.setVisibility(View.GONE);
                    viewHolder.story_photo_seen.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
