package com.padelclubapp.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.padelclubapp.adapter.FieldsAdapter;
import com.padelclubapp.databinding.ActivityFieldsSetupBinding;
import com.padelclubapp.dataclass.Campi;
import com.padelclubapp.dataclass.Users;

import java.util.ArrayList;

public class FieldsSetupActivity extends AppCompatActivity {
    private ActivityFieldsSetupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFieldsSetupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setUpToolbar();
        setAdapter();

    }

    private void setAdapter() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://padelclubapp-default-rtdb.firebaseio.com").getReference("campi");
        binding.recycle.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<Campi> campi = new ArrayList<>();
        FieldsAdapter adapter = new FieldsAdapter(campi, this);
        binding.recycle.setAdapter(adapter);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    campi.add(userSnapshot.getValue(Campi.class));
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    private void setUpToolbar() {
        binding.toolbar.backIcon.setVisibility(View.GONE);
        binding.toolbar.toolbarTitle.setText("Personalizza la tua app");
    }

}