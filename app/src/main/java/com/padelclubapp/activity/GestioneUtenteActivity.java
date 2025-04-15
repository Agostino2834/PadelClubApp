package com.padelclubapp.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.padelclubapp.R;
import com.padelclubapp.adapter.SegnalazioniUtenteAdapter;
import com.padelclubapp.databinding.ActivityGestioneUtenteBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestioneUtenteActivity extends AppCompatActivity {
    private ActivityGestioneUtenteBinding binding;
    private SegnalazioniUtenteAdapter adapter;

    private DatabaseReference database;
    private List<String> segnalazioni;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGestioneUtenteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        userId = intent.getStringExtra("id");
        database = FirebaseDatabase.getInstance().getReference();
        segnalazioni = new ArrayList<>();
        adapter = new SegnalazioniUtenteAdapter(segnalazioni, new SegnalazioniUtenteAdapter.OnAzioniListener() {
            @Override
            public void onModificaClick(String segnalazione) {
                showModificaSegnalazioneDialog(segnalazione);
            }

            @Override
            public void onEliminaClick(String segnalazione) {
                showDialogConfermaEliminazione(segnalazione);
            }
        });
        binding.recycle.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        binding.recycle.setAdapter(adapter);
        getData();
        setToolBar();
        binding.cardBandisciUtente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bandisciUtente();
            }
        });
        binding.cardSospendiUtente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sospendiUtente();
            }
        });
        binding.cardTogliBanUtente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togliBan();
            }
        });
        binding.cardTogliSospensioneUtente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togliSospensione();
            }
        });
    }


    private void getData() {
        database.child("segnalazioni").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot segnalazioneSnapshot : snapshot.getChildren()) {
                    String segnalazione = segnalazioneSnapshot.getValue(String.class);
                    if (segnalazione != null) {
                        segnalazioni.add(segnalazione);
                    }
                }
                adapter.notifyDataSetChanged();
                if (segnalazioni.isEmpty()) {
                    binding.recycle.setVisibility(View.GONE);
                    binding.txtNoSegnalazioni.setVisibility(View.VISIBLE);
                } else {
                    binding.recycle.setVisibility(View.VISIBLE);
                    binding.txtNoSegnalazioni.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Errore nel recupero dei dati", error.toException());
            }
        });
    }

    private void setToolBar() {
        binding.toolbar.backIcon.setVisibility(View.VISIBLE);
        binding.toolbar.toolbarTitle.setText("");
        binding.toolbar.rightIcon.setVisibility(View.INVISIBLE);
        binding.toolbar.backIcon.setOnClickListener(v -> finish());
    }

    private void showModificaSegnalazioneDialog(String segnalazione) {

        Dialog dialog = new Dialog(GestioneUtenteActivity.this);
        dialog.setContentView(R.layout.dialog_modifica_segnalazione);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        EditText editTextSegnalazione = dialog.findViewById(R.id.editTextSegnalazione);
        Button buttonSalva = dialog.findViewById(R.id.buttonSalva);

        editTextSegnalazione.setText(segnalazione);

        buttonSalva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nuovaSegnalazione = editTextSegnalazione.getText().toString().trim();
                if (!nuovaSegnalazione.isEmpty()) {
                    segnalazioni.clear();
                    aggiornaSegnalazione(nuovaSegnalazione, segnalazione);
                    dialog.dismiss();
                } else {
                    Toast.makeText(GestioneUtenteActivity.this, "Il campo non può essere vuoto", Toast.LENGTH_SHORT).show();
                }
            }
        });


        dialog.show();
    }

    private void aggiornaSegnalazione(String nuovaSegnalazione, String vecchiaSegnalazione) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("segnalazioni").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> listaSegnalazioni = new ArrayList<>();

                for (DataSnapshot segnalazioneSnap : snapshot.getChildren()) {
                    String segnalazione = segnalazioneSnap.getValue(String.class);
                    listaSegnalazioni.add(segnalazione);
                }

                int index = listaSegnalazioni.indexOf(vecchiaSegnalazione);
                if (index != -1) {
                    listaSegnalazioni.set(index, nuovaSegnalazione);
                    mDatabase.child("segnalazioni").child(userId).setValue(listaSegnalazioni)
                            .addOnSuccessListener(aVoid -> {
                                getData();
                                Toast.makeText(GestioneUtenteActivity.this, "Segnalazione aggiornata", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(GestioneUtenteActivity.this, "Errore nell'aggiornamento", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Toast.makeText(GestioneUtenteActivity.this, "Segnalazione non trovata", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Errore nel recupero delle segnalazioni", error.toException());
            }
        });
    }

    private void showDialogConfermaEliminazione(String testoSegnalazione) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_elimina_segnalazione);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(true);

        TextView message = dialog.findViewById(R.id.dialogMessage);
        Button btnAnnulla = dialog.findViewById(R.id.btnAnnulla);
        Button btnElimina = dialog.findViewById(R.id.btnElimina);

        message.setText("Sei sicuro di voler eliminare questa segnalazione?");

        btnAnnulla.setOnClickListener(v -> dialog.dismiss());

        btnElimina.setOnClickListener(v -> {
            DatabaseReference segnalazioniRef = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("segnalazioni")
                    .child(userId);
            segnalazioniRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot segnalazioneSnap : snapshot.getChildren()) {
                        String segnalazione = segnalazioneSnap.getValue(String.class);
                        if (segnalazione != null && segnalazione.equals(testoSegnalazione)) {
                            segnalazioneSnap.getRef().removeValue()
                                    .addOnSuccessListener(unused -> {
                                        segnalazioni.clear();
                                        getData();
                                        Toast.makeText(GestioneUtenteActivity.this, "Segnalazione eliminata", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(GestioneUtenteActivity.this, "Errore durante l'eliminazione", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    });
                            return;
                        }
                    }
                    Toast.makeText(GestioneUtenteActivity.this, "Segnalazione non trovata", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(GestioneUtenteActivity.this, "Errore nel recupero dei dati", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
        });

        dialog.show();
    }


    private void sospendiUtente() {


        long setteGiorniInMillis = 7L * 24 * 60 * 60 * 1000;
        long tempoSospensione = System.currentTimeMillis() + setteGiorniInMillis;


        Map<String, Object> update = new HashMap<>();
        update.put("sospeso", true);
        update.put("tempoSospensione", tempoSospensione);

        database.child("users").child(userId).updateChildren(update).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                binding.cardBandisciUtente.setClickable(false);
                binding.cardSospendiUtente.setVisibility(View.GONE);
                binding.cardTogliSospensioneUtente.setVisibility(View.VISIBLE);
                Toast.makeText(GestioneUtenteActivity.this, "Utente sospeso", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(GestioneUtenteActivity.this, "Errore durante la sospensione", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void bandisciUtente() {
        database.child("users").child(userId).child("bandito").setValue(true).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                binding.cardSospendiUtente.setClickable(false);
                binding.cardBandisciUtente.setVisibility(View.GONE);
                binding.cardTogliBanUtente.setVisibility(View.VISIBLE);
                Toast.makeText(GestioneUtenteActivity.this, "Utente bandito", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(GestioneUtenteActivity.this, "Errore durante la sospensione permanete", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void togliSospensione() {
        Map<String, Object> update = new HashMap<>();
        update.put("sospeso", false);
        update.put("tempoSospensione", 0L);

        database.child("users").child(userId).updateChildren(update).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                binding.cardBandisciUtente.setClickable(true);
                binding.cardSospendiUtente.setVisibility(View.VISIBLE);
                binding.cardTogliSospensioneUtente.setVisibility(View.GONE);
                Toast.makeText(GestioneUtenteActivity.this, "La sospesensione è stata annulata", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(GestioneUtenteActivity.this, "Errore durante l'annullamento della sospensione", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void togliBan() {
        database.child("users").child(userId).child("bandito").setValue(false).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                binding.cardSospendiUtente.setClickable(true);
                binding.cardBandisciUtente.setVisibility(View.VISIBLE);
                binding.cardTogliBanUtente.setVisibility(View.GONE);
                Toast.makeText(GestioneUtenteActivity.this, "la sospensione permamente è stata annulata", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(GestioneUtenteActivity.this, "Errore durante l'annullamento della sospensione permanete", Toast.LENGTH_LONG).show();
            }
        });
    }
}