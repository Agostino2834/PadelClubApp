package com.padelclubapp.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.padelclubapp.R;
import com.padelclubapp.databinding.ActivityDettaglioUtenteBinding;
import com.padelclubapp.dataclass.Utente;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class DettaglioUtenteActivity extends AppCompatActivity {

    private ActivityDettaglioUtenteBinding binding;
    private FirebaseAuth mAuth;
    private Dialog dialog;
    private DatabaseReference mDatabase;
    private String userId;
    private Double votaRanking;
    private Integer countRanking = 0;
    private Double rankingAttuale;
    private Dialog rankingDialog;
    private Double reputazioneAttuale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        userId = intent.getStringExtra("id");
        binding = ActivityDettaglioUtenteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dialog = new Dialog(this);
        rankingDialog = new Dialog(this);
        setToolBar();
        setDialog();
        dialog.show();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        getData();
        isAdmin();

        binding.cardRanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isVota();
            }
        });


        binding.cardReputazione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSegnala();
            }
        });
        binding.gestisciUtenti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), GestioneUtenteActivity.class);
                intent.putExtra("id", userId);
                startActivity(intent);
            }
        });
    }

    private void isAdmin() {
        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Utente utente = snapshot.getValue(Utente.class);
                if (utente != null)
                    if (utente.getAdmin())
                        binding.gestisciUtenti.setVisibility(View.VISIBLE);
                    else
                        binding.gestisciUtenti.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getData() {
        mDatabase.child("users").child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Utente user = snapshot.getValue(Utente.class);
                        if (user != null) {
                            binding.textViewNomeCompleto.setText(user.getNome() + " " + user.getCognome());
                            binding.textViewEmail.setText(user.getEmail());
                            binding.textViewBio.setText(user.getBio());
                            Glide.with(getBaseContext()).load(user.getFotoUtente()).into(binding.fotoProfilo);
                            getStars(user.getReputazione(), user.getRanking());
                            countRanking = user.getCountRanking();
                            rankingAttuale = user.getRanking();
                            reputazioneAttuale = user.getReputazione();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase", "Errore nel recupero dei dati", error.toException());
                    }
                });
        dialog.dismiss();
    }

    private void setToolBar() {
        binding.toolbar.backIcon.setVisibility(View.VISIBLE);
        binding.toolbar.toolbarTitle.setText("");
        binding.toolbar.rightIcon.setVisibility(View.INVISIBLE);
        binding.toolbar.backIcon.setOnClickListener(v -> finish());
    }

    private void setDialog() {
        dialog.setContentView(R.layout.dialog_progress_bar);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false);
    }

    public void getStars(Double reputazione, Double ranking) {
        binding.textViewRanking.setText(String.valueOf(ranking));
        binding.textViewReputazione.setText(String.valueOf(reputazione));
        int stellePieneRanking = (int) Math.floor(ranking);
        int stellePieneReputazione = (int) Math.floor(reputazione);

        ImageView[] stelleRanking = new ImageView[5];
        stelleRanking[0] = binding.rankingStella1;
        stelleRanking[1] = binding.rankingStella2;
        stelleRanking[2] = binding.rankingStella3;
        stelleRanking[3] = binding.rankingStella4;
        stelleRanking[4] = binding.rankingStella5;

        ImageView[] stelleReputazione = new ImageView[5];
        stelleReputazione[0] = binding.reputazioneStella1;
        stelleReputazione[1] = binding.reputazioneStella2;
        stelleReputazione[2] = binding.reputazioneStella3;
        stelleReputazione[3] = binding.reputazioneStella4;
        stelleReputazione[4] = binding.reputazioneStella5;

        for (int i = 0; i < 5; i++) {
            if (i < stellePieneRanking) {
                stelleRanking[i].setImageResource(R.drawable.icon_stella_piena);
            }
            if (i < stellePieneReputazione)
                stelleReputazione[i].setImageResource(R.drawable.icon_stella_piena);
        }
    }

    private void showRankingDialog() {
        rankingDialog.setContentView(R.layout.dialog_ranking);
        Objects.requireNonNull(rankingDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        ImageView stella1 = rankingDialog.findViewById(R.id.dialogStella1);
        ImageView stella2 = rankingDialog.findViewById(R.id.dialogStella2);
        ImageView stella3 = rankingDialog.findViewById(R.id.dialogStella3);
        ImageView stella4 = rankingDialog.findViewById(R.id.dialogStella4);
        ImageView stella5 = rankingDialog.findViewById(R.id.dialogStella5);

        stella1.setOnClickListener(v -> {
            stella1.setImageResource(R.drawable.icon_stella_piena);
            stella2.setImageResource(R.drawable.icon_stella_vuota);
            stella3.setImageResource(R.drawable.icon_stella_vuota);
            stella4.setImageResource(R.drawable.icon_stella_vuota);
            stella5.setImageResource(R.drawable.icon_stella_vuota);
            votaRanking = 1.0;
            setRanking();
        });

        stella2.setOnClickListener(v -> {
            stella1.setImageResource(R.drawable.icon_stella_piena);
            stella2.setImageResource(R.drawable.icon_stella_piena);
            stella3.setImageResource(R.drawable.icon_stella_vuota);
            stella4.setImageResource(R.drawable.icon_stella_vuota);
            stella5.setImageResource(R.drawable.icon_stella_vuota);
            votaRanking = 2.0;
            setRanking();
        });

        stella3.setOnClickListener(v -> {
            stella1.setImageResource(R.drawable.icon_stella_piena);
            stella2.setImageResource(R.drawable.icon_stella_piena);
            stella3.setImageResource(R.drawable.icon_stella_piena);
            stella4.setImageResource(R.drawable.icon_stella_vuota);
            stella5.setImageResource(R.drawable.icon_stella_vuota);
            votaRanking = 3.0;
            setRanking();
        });

        stella4.setOnClickListener(v -> {
            stella1.setImageResource(R.drawable.icon_stella_piena);
            stella2.setImageResource(R.drawable.icon_stella_piena);
            stella3.setImageResource(R.drawable.icon_stella_piena);
            stella4.setImageResource(R.drawable.icon_stella_piena);
            stella5.setImageResource(R.drawable.icon_stella_vuota);
            votaRanking = 4.0;
            setRanking();
        });

        stella5.setOnClickListener(v -> {
            stella1.setImageResource(R.drawable.icon_stella_piena);
            stella2.setImageResource(R.drawable.icon_stella_piena);
            stella3.setImageResource(R.drawable.icon_stella_piena);
            stella4.setImageResource(R.drawable.icon_stella_piena);
            stella5.setImageResource(R.drawable.icon_stella_piena);
            votaRanking = 5.0;
            setRanking();
        });

        rankingDialog.show();
    }

    public void setRanking() {
        Map<String, Object> updates = new HashMap<>();
        Double nuovoVotoRanking = (rankingAttuale * countRanking + votaRanking) / (countRanking + 1);
        countRanking++;
        updates.put("ranking", nuovoVotoRanking);
        updates.put("countRanking", countRanking);

        mDatabase.child("users").child(userId).updateChildren(updates)
                .addOnSuccessListener(unused -> {
                    rankingDialog.dismiss();
                })
                .addOnFailureListener(e -> {
                    rankingDialog.dismiss();
                });

        mDatabase.child("vota").child(mAuth.getCurrentUser().getUid()).child(userId).setValue(true);
        getStars(reputazioneAttuale, nuovoVotoRanking);
    }

    public void isVota() {
        mDatabase.child("vota")
                .child(mAuth.getCurrentUser().getUid())
                .child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Boolean haGiaVotato = snapshot.getValue(Boolean.class);
                        if (haGiaVotato != null && haGiaVotato) {
                            Toast.makeText(getBaseContext(), "Hai già votato", Toast.LENGTH_SHORT).show();
                        } else {
                            showRankingDialog();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    public void isSegnala() {
        mDatabase.child("segnalazioni")
                .child(userId)
                .child(mAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Toast.makeText(getBaseContext(), "Hai già segnalato questo utente", Toast.LENGTH_SHORT).show();
                        } else {
                            showReportDialog();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void showReportDialog() {
        final String[] motiviSegnalazione = {
                "Falsa dichiarazione di vittoria",
                "Comportamenti antisportivi",
                "Linguaggio violento",
                "Altri motivi"
        };

        new AlertDialog.Builder(this)
                .setTitle("Segnala utente")
                .setSingleChoiceItems(motiviSegnalazione, -1, null)
                .setPositiveButton("Invia", (dialog, which) -> {
                    AlertDialog alertDialog = (AlertDialog) dialog;
                    int selectedPosition = alertDialog.getListView().getCheckedItemPosition();
                    if (selectedPosition != -1) {
                        String motivo = motiviSegnalazione[selectedPosition];
                        inviaSegnalazione(motivo);
                    } else {
                        Toast.makeText(this, "Seleziona un motivo", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Annulla", null)
                .show();
    }


    private void inviaSegnalazione(String motivo) {
        mDatabase.child("segnalazioni")
                .child(userId)
                .child(mAuth.getCurrentUser().getUid())
                .setValue(motivo)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Segnalazione inviata", Toast.LENGTH_SHORT).show();
                    if (reputazioneAttuale > 1.0) {
                        Random random = new Random();
                        double penalizzazioneReputazione = random.nextDouble() * 0.6;
                        getStars(reputazioneAttuale - penalizzazioneReputazione, rankingAttuale);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Errore nell'invio", Toast.LENGTH_SHORT).show();
                });


    }
}
