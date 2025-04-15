package com.padelclubapp.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.padelclubapp.databinding.ActivitySplashBinding;
import com.padelclubapp.dataclass.Utente;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private final Handler handler = new Handler();
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        setLogo();

    }

    private void isFirstAccessAdmin(FirstAccessCallback callback) {
        mDatabase.child("users").child(mAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Utente user = snapshot.getValue(Utente.class);
                        callback.onCallback(user.getAdmin() && user.isFirstLogin(), user.isSospeso(), user.isBandito());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }

                });
    }
    private void setLogo(){
        mDatabase.child("infoApp").child("logo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String logo=snapshot.getValue(String.class);
                    Glide.with(SplashActivity.this).load(logo).into(binding.logo);
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        if (currentUser != null) {
                            isFirstAccessAdmin(new FirstAccessCallback() {
                                @Override
                                public void onCallback(boolean isFirstAccessAdmin, boolean isSospeso, boolean isBandito) {
                                    Intent intent;
                                    if (isFirstAccessAdmin)
                                        intent = new Intent(SplashActivity.this, LogoSetUpActivity.class);
                                    else if (isSospeso || isBandito)
                                        intent = new Intent(SplashActivity.this, LoginActivity.class);
                                    else
                                        intent = new Intent(SplashActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        } else {
                            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }, 3000);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                handler.postDelayed(() -> {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }, 3000);
            }
        });
    }
    public interface FirstAccessCallback {
        void onCallback(boolean isFirstAccessAdmin, boolean isSospeso, boolean isBandito);
    }
}