package com.padelclubapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.padelclubapp.databinding.ActivityLoginBinding;
import com.padelclubapp.dataclass.Users;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.toolbar.toolbarTitle.setText("LOG-IN");
        binding.toolbar.backIcon.setVisibility(View.GONE);
        mAuth = FirebaseAuth.getInstance();


        mDatabase = FirebaseDatabase.getInstance("https://padelclubapp-default-rtdb.firebaseio.com").getReference("users");
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logIn(binding.email.getText().toString(), binding.password.getText().toString());
            }
        });

        binding.registrati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void logIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            isFirstAccessAdmin(email, new FirstAccessCallback() {
                                @Override
                                public void onCallback(boolean isFirstAccessAdmin) {
                                    Intent intent;
                                    if (isFirstAccessAdmin) {
                                        intent = new Intent(LoginActivity.this, LogoSetUpActivity.class);
                                    } else {
                                        intent = new Intent(LoginActivity.this, MainActivity.class);
                                    }
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        } else {
                            binding.errorMessage.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private void isFirstAccessAdmin(String email, FirstAccessCallback callback) {

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isFirstAccess = false;
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    Users user = userSnapshot.getValue(Users.class);
                    if (user.getEmail().equalsIgnoreCase(email) && user.getAdmin() && user.isFirstLogin()) {
                        isFirstAccess = true;
                        break;
                    }
                }
                callback.onCallback(isFirstAccess);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public interface FirstAccessCallback {
        void onCallback(boolean isFirstAccessAdmin);
    }
}