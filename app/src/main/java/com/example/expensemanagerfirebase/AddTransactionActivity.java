package com.example.expensemanagerfirebase;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.expensemanagerfirebase.databinding.ActivityAddTransactionBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class AddTransactionActivity extends AppCompatActivity {

    ActivityAddTransactionBinding binding;
    FirebaseFirestore fStore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String type="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding=ActivityAddTransactionBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        fStore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();

        binding.expenseCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type="Expense";
                binding.expenseCheckBox.setChecked(true);
                binding.incomeCheckBox.setChecked(false);
            }
        });

        binding.incomeCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type="Income";
                binding.expenseCheckBox.setChecked(false);
                binding.incomeCheckBox.setChecked(true);
            }
        });

        binding.btnAddTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount=binding.userAmountAdd.getText().toString().trim();
                String note=binding.userNoteAdd.getText().toString().trim();
                if(amount.length()<=0){
                    return;
                }
                if(type.length()<=0){
                    Toast.makeText(AddTransactionActivity.this, "select transaction type", Toast.LENGTH_SHORT).show();
                }
                SimpleDateFormat sdf=new SimpleDateFormat("dd MM yyyy_HH:mm", Locale.getDefault());
                String currentDateandTime=sdf.format(new Date());

                String id= UUID.randomUUID().toString();
                Map<String, Object> transaction=new HashMap<>();
                transaction.put("id",id);
                transaction.put("amount",amount);
                transaction.put("note", note);
                transaction.put("type", type);
                transaction.put("date",currentDateandTime);

                fStore.collection("Expenses").document(firebaseAuth.getUid()).collection("Notes").document(id)
                        .set(transaction)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(AddTransactionActivity.this, "Added", Toast.LENGTH_SHORT).show();
                                binding.userAmountAdd.setText("");
                                binding.userNoteAdd.setText("");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddTransactionActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}