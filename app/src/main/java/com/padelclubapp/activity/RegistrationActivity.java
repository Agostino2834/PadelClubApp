package com.padelclubapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.padelclubapp.databinding.ActivityRegistrationBinding;
import com.padelclubapp.dataclass.Users;

import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {
    private ActivityRegistrationBinding binding;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private static final boolean isAdmin = false;
    private static final boolean isFirstAccess = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setToolBar();
        setFirebase();
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isValidateData(
                        binding.nome.getText().toString(),
                        binding.cognome.getText().toString(),
                        binding.email.getText().toString(),
                        binding.password.getText().toString(),
                        binding.confirmPassword.getText().toString())) {

                    createUser(binding.email.getText().toString(), binding.password.getText().toString());


                } else {
                    Toast.makeText(RegistrationActivity.this, "non valido", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void createUser(String email, String password) {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            setUser();
                            deleteFields();
                            Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                        }
                    }
                });
    }

    private void setToolBar() {
        binding.toolbar.toolbarTitle.setText("REGISTRAZIONE");
        binding.toolbar.backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://padelclubapp-default-rtdb.firebaseio.com").getReference();
    }

    private void setUser() {
        Users user = new Users(
                mDatabase.child("users").push().getKey(),
                binding.nome.getText().toString(),
                binding.cognome.getText().toString(),
                binding.email.getText().toString(),
                binding.password.getText().toString(), isAdmin, isFirstAccess);

        mDatabase.child("users").push().setValue(user, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {

                } else {
                }
            }
        });
    }

    private void deleteFields() {
        binding.nome.setText("");
        binding.cognome.setText("");
        binding.email.setText("");
        binding.password.setText("");
        binding.confirmPassword.setText("");
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