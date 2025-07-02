package com.example.fmcarer.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fmcarer.Model.Sub;
import com.example.fmcarer.R;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SubAdapter extends RecyclerView.Adapter<SubAdapter.SubViewHolder> {

    private Context context;
    private ArrayList<Sub> subList;

    public SubAdapter(Context context, ArrayList<Sub> subList) {
        this.context = context;
        this.subList = subList;
    }

    @NonNull
    @Override
    public SubViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sub_item, parent, false);
        return new SubViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubViewHolder holder, int position) {
        Sub sub = subList.get(position);
        holder.staffName.setText(sub.getFullname());
        holder.staffEmail.setText(sub.getEmail());

        holder.menuButton.setOnClickListener(v -> {
            String[] options = {"Sửa", "Xoá"};
            new AlertDialog.Builder(context)
                    .setTitle("Tuỳ chọn")
                    .setItems(options, (dialog, which) -> {
                        if (which == 0) editSub(sub);
                        else deleteSub(sub);
                    }).show();
        });
    }

    private void editSub(Sub sub) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_sub_form, null);
        EditText inputName = view.findViewById(R.id.inputName);
        EditText inputEmail = view.findViewById(R.id.inputEmail);
        EditText inputPassword = view.findViewById(R.id.inputPassword);
        EditText inputPhone = view.findViewById(R.id.inputPhone);
        EditText inputRole = view.findViewById(R.id.inputRole);

        inputName.setText(sub.getFullname());
        inputEmail.setText(sub.getEmail());
        inputPassword.setText(sub.getPassword());
        inputPhone.setText(sub.getPhone());
        inputRole.setText(sub.getRole());

        new AlertDialog.Builder(context)
                .setTitle("Sửa người dùng")
                .setView(view)
                .setPositiveButton("Cập nhật", (dialog, which) -> {
                    sub.setFullname(inputName.getText().toString());
                    sub.setEmail(inputEmail.getText().toString());
                    sub.setPassword(inputPassword.getText().toString());
                    sub.setPhone(inputPhone.getText().toString());
                    sub.setRole(inputRole.getText().toString());

                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(sub.getUser_id()).setValue(sub);
                    Toast.makeText(context, "Đã cập nhật", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Huỷ", null)
                .show();
    }

    private void deleteSub(Sub sub) {
        FirebaseDatabase.getInstance().getReference("Users")
                .child(sub.getUser_id()).removeValue();
        Toast.makeText(context, "Đã xoá", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return subList.size();
    }

    public static class SubViewHolder extends RecyclerView.ViewHolder {
        TextView staffName, staffEmail;
        ImageButton menuButton;

        public SubViewHolder(@NonNull View itemView) {
            super(itemView);
            staffName = itemView.findViewById(R.id.staffName);
            staffEmail = itemView.findViewById(R.id.staffEmail);
            menuButton = itemView.findViewById(R.id.menuButton);
        }
    }
}
