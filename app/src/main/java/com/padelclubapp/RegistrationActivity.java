package com.padelclubapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.padelclubapp.databinding.ActivityRegistrationBinding;
import com.padelclubapp.dataclass.Users;

import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {
    private ActivityRegistrationBinding binding;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.toolbar.toolbarTitle.setText("REGISTRAZIONE");
        binding.toolbar.backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase = FirebaseDatabase.getInstance("https://padelclubapp-default-rtdb.firebaseio.com").getReference();
                if (isValidateData(
                        binding.nome.getText().toString(),
                        binding.cognome.getText().toString(),
                        binding.email.getText().toString(),
                        binding.password.getText().toString(),
                        binding.confirmPassword.getText().toString())) {
                    Users user = new Users(
                            mDatabase.child("users").push().getKey(),
                            binding.nome.getText().toString(),
                            binding.cognome.getText().toString(),
                            binding.email.getText().toString(),
                            binding.password.getText().toString(), true);

                    mDatabase.child("users").push().setValue(user, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                            } else {
                            }
                        }
                    });

                    binding.nome.setText("");
                    binding.cognome.setText("");
                    binding.email.setText("");
                    binding.password.setText("");
                    binding.confirmPassword.setText("");
                } else {
                    Toast.makeText(RegistrationActivity.this, "non valido", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean checkNameSurname(String name) {
        String namePattern = "^[A-Za-zÀ-ÖØ-öø-ÿ' -]+$";
        Pattern pattern = Pattern.compile(namePattern);
        return !name.isEmpty() && pattern.matcher(name).matches();
    }

    private boolean checkEmail(String email) {
        return !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean checkPassword(String password) {
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#%^&*()\\-_=+])[A-Za-z\\d!@#%^&*()\\-_=+]{8,}$";
        Pattern pattern = Pattern.compile(passwordRegex);
        return !password.isEmpty() && pattern.matcher(password).matches();
    }

    private boolean isEqualPassword(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }

    private boolean isValidateData(String name, String surname, String email, String password, String confirmPassword) {
        return checkNameSurname(name) && checkNameSurname(surname) && checkEmail(email) && checkPassword(password) && checkPassword(confirmPassword) && isEqualPassword(password, confirmPassword);
    }
}