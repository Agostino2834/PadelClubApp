package com.padelclubapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.padelclubapp.adapter.FieldsAdapter;
import com.padelclubapp.databinding.ActivityGestioneCampiBinding;
import com.padelclubapp.dataclass.Campo;

import java.util.ArrayList;

public class GestioneCampiActivity extends AppCompatActivity {
    private ActivityGestioneCampiBinding binding;
    private ArrayList<Campo> campi;
    private FieldsAdapter adapter;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGestioneCampiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        campi = new ArrayList<>();
        adapter = new FieldsAdapter(campi, this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        setUpToolbar();
        setAdapter();

        binding.aggiungi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                campi.add(new Campo());
                adapter.notifyDataSetChanged();
            }
        });
        salvaDati();
    }


    private void salvaDati() {
        binding.salva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.aggiornaCampi(binding.recycle);
                if (!campi.isEmpty()) {
                    mDatabase.child("campi").removeValue().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (Campo campo : campi) {
                                String key = mDatabase.child("campi").push().getKey();
                                campo.setId(key);
                                mDatabase.child("campi").child(key).setValue(campo);
                            }
                            mDatabase.child("infoApp").child("nCampi").setValue(campi.size());
                            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("firstLogin").setValue(false);
                            Toast.makeText(GestioneCampiActivity.this, "Campi aggiornati con successo", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(GestioneCampiActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(GestioneCampiActivity.this, "Errore durante l'aggiornamento", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    private void setAdapter() {
        binding.recycle.setLayoutManager(new LinearLayoutManager(this));
        binding.recycle.setAdapter(adapter);
        /*DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://padelclubapp-default-rtdb.firebaseio.com").getReference("campi");
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
        });*/

    }

    private void setUpToolbar() {
        binding.toolbar.backIcon.setVisibility(View.GONE);
        binding.toolbar.toolbarTitle.setText("Personalizza la tua app");
    }

}