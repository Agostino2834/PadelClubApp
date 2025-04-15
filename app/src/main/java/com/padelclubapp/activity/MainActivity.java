package com.padelclubapp.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DatabaseReference;
import com.padelclubapp.R;
import com.padelclubapp.databinding.ActivityMainBinding;
import com.padelclubapp.fragment.CercaUtentiFragment;
import com.padelclubapp.fragment.HomeFragment;
import com.padelclubapp.fragment.InfoFragment;
import com.padelclubapp.fragment.PrenotaLezioneFragment;

public class MainActivity extends AppCompatActivity {
    private FirebaseAnalytics mFirebaseAnalytics;
    private ActivityMainBinding binding;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new HomeFragment());
        binding.bottomBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.home) {
                    replaceFragment(new HomeFragment());
                    return true;
                } else if (menuItem.getItemId() == R.id.search) {
                    replaceFragment(new CercaUtentiFragment());
                    return true;
                } else if (menuItem.getItemId() == R.id.booking) {
                    replaceFragment(new PrenotaLezioneFragment());
                    return true;
                } else if (menuItem.getItemId() == R.id.account) {
                    replaceFragment(new InfoFragment());
                    return true;
                }
                return true;
            }
        });

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

}