package com.example.fmcarer;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.HashMap;
import java.util.Map;

public class DialogTopUpFragment extends DialogFragment {

    private EditText edtCardNumber, edtBankName;
    private Button btnSave;
    private Runnable onCardAdded;

    public DialogTopUpFragment() {}

    public DialogTopUpFragment(Runnable callback) {
        this.onCardAdded = callback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_top_up, container, false);

        edtCardNumber = view.findViewById(R.id.edtCardNumber);
        edtBankName = view.findViewById(R.id.edtBankName);
        btnSave = view.findViewById(R.id.btnSave);

        btnSave.setOnClickListener(v -> saveBankCard());

        return view;
    }

    private void saveBankCard() {
        String card = edtCardNumber.getText().toString().trim();
        String bank = edtBankName.getText().toString().trim();

        if (card.isEmpty() || bank.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập đầy đủ", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("BankCards").child(user.getUid());
        Map<String, Object> data = new HashMap<>();
        data.put("cardNumber", card);
        data.put("bankName", bank);

        ref.setValue(data).addOnSuccessListener(unused -> {
            Toast.makeText(getContext(), "Đã lưu thẻ", Toast.LENGTH_SHORT).show();
            dismiss();
            if (onCardAdded != null) onCardAdded.run();
        });
    }
}
