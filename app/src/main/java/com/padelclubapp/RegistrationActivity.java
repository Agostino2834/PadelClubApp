package com.padelclubapp;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.padelclubapp.databinding.ActivityRegistrationBinding;
import com.padelclubapp.dataclass.Users;

public class RegistrationActivity extends AppCompatActivity {
    private ActivityRegistrationBinding binding;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.toolbar.toolbarTitle.setText("REGISTRAZIONE");
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase = FirebaseDatabase.getInstance("https://padelclubapp-default-rtdb.firebaseio.com").getReference();
                Users user = new Users(
                        binding.nome.getText().toString(),
                        binding.cognome.getText().toString(),
                        binding.email.getText().toString(),
                        binding.password.getText().toString(),
                        binding.passwordConfirm.getText().toString(), true);

                mDatabase.child("users").child(binding.nome.getText().toString()).setValue(user);

            }
        });

    }
}