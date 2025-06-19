package com.example.fmcarer.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fmcarer.Model.Child;
import com.example.fmcarer.R;
import com.example.fmcarer.activity_child_detail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ChildViewHolder> {
    private Context context;
    private List<Child> childList;

    public ChildAdapter(Context context, List<Child> childList) {
        this.context = context;
        this.childList = childList;
    }

    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_child, parent, false);
        return new ChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder holder, int position) {
        Child child = childList.get(position);

        holder.nameTextView.setText(child.getName());
        holder.genderTextView.setText("Giới tính: " + child.getGender());
        holder.birthdateTextView.setText("Ngày sinh: " + child.getBirthdate());
        holder.addressTextView.setText("Địa chỉ: " + child.getAddress());

        // ✅ Tính tuổi từ ngày sinh
        String age = calculateAge(child.getBirthdate());
        holder.ageTextView.setText("Tuổi: " + age);

        // ✅ Load ảnh
        Glide.with(context)
                .load(child.getImageUrl())
                .placeholder(R.drawable.ic_cam)
                .into(holder.imageView);

        // ✅ Mở chi tiết
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, activity_child_detail.class);
            intent.putExtra("CHILD_OBJECT", child);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return childList.size();
    }

    // ✅ Tính tuổi từ birthdate
    private String calculateAge(String birthdateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Calendar birthDate = Calendar.getInstance();
            birthDate.setTime(sdf.parse(birthdateStr));

            Calendar today = Calendar.getInstance();

            int age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);
            if (today.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }

            return String.valueOf(age);
        } catch (ParseException e) {
            return "Không rõ";
        }
    }

    public class ChildViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, ageTextView, genderTextView, birthdateTextView, addressTextView;
        ImageView imageView;

        public ChildViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textViewName);
            ageTextView = itemView.findViewById(R.id.textViewAge);
            genderTextView = itemView.findViewById(R.id.textViewGender);
            birthdateTextView = itemView.findViewById(R.id.textViewBirthdate);
            addressTextView = itemView.findViewById(R.id.textViewAddress);
            imageView = itemView.findViewById(R.id.imageViewChild);
        }
    }
}
