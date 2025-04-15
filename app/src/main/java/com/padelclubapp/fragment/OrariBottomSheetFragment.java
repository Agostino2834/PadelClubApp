package com.padelclubapp.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.padelclubapp.R;
import com.padelclubapp.activity.LoginActivity;
import com.padelclubapp.activity.MainActivity;
import com.padelclubapp.activity.RegistrationActivity;
import com.padelclubapp.adapter.OrariAdapter;
import com.padelclubapp.databinding.FragmentOrariBottomSheetBinding;
import com.padelclubapp.dataclass.Prenotazione;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class OrariBottomSheetFragment extends BottomSheetDialogFragment {
    private FragmentOrariBottomSheetBinding binding;
    private OrariAdapter adapter;
    private String idCampo;
    private String data;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();

        if (arguments != null) {
            data = arguments.getString("data");
            idCampo = arguments.getString("id_campo");
        }
        mAuth = FirebaseAuth.getInstance();
        binding = FragmentOrariBottomSheetBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getOrariDisponibili();
        adapter = new OrariAdapter(new ArrayList<>(), new OrariAdapter.OnClick() {
            @Override
            public void onClick(String orario) {

                prenotato(orario);
            }
        });
        binding.recyclerOrari.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerOrari.setAdapter(adapter);

    }

    public void prenotato(String orario) {
        DatabaseReference dbUtente = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getCurrentUser().getUid()).child("prenotazioneCampo");
        dbUtente.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    showDialog("Prenotazione", "spiacente hai già una prenotazione attiva");
                else
                    prenota(orario);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.e("FirebaseError", "Errore durante il recupero dei dati", databaseError.toException());
            }
        });
    }

    private void prenota(String orario) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference dbUtente = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getCurrentUser().getUid());
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("prenotazioni")
                .child(idCampo)
                .child(data)
                .child(orario);
        String key = dbRef.child(orario).push().getKey();
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Prenotazione prenotazione = dataSnapshot.getValue(Prenotazione.class);

                if (prenotazione != null) {
                    if (prenotazione.getnPosti() < 4) {
                        prenotazione.setnPosti();
                        if (prenotazione.getnPosti() == 4) {
                            prenotazione.setDisponibile(false);
                        }
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("nPosti", prenotazione.getnPosti());
                        updates.put("disponibile", prenotazione.getDisponibile());

                        dbRef.updateChildren(updates)
                                .addOnSuccessListener(unused -> {
                                    dbUtente.child("prenotazioneCampo").child("dettagli").setValue(prenotazione);
                                    dbRef.child("utenti").child(mAuth.getCurrentUser().getUid()).setValue(mAuth.getCurrentUser().getUid());
                                    dialogPrenotazione(true);
                                    dismiss();
                                })
                                .addOnFailureListener(e -> {
                                   dialogPrenotazione(false);
                                });

                    }
                } else {
                    Prenotazione prenotazioneCampo = new Prenotazione(key, idCampo, data, orario, true);
                    dbRef.setValue(prenotazioneCampo)
                            .addOnSuccessListener(unused -> {
                                dbUtente.child("prenotazioneCampo").child("dettagli").setValue(prenotazioneCampo);
                                dbRef.child("utenti").child(mAuth.getCurrentUser().getUid()).setValue(mAuth.getCurrentUser().getUid());
                                dialogPrenotazione(true);
                                dismiss();
                            })
                            .addOnFailureListener(e -> {
                                dialogPrenotazione(false);
                            });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void getOrariDisponibili() {
        List<String> orari = new ArrayList<>(Arrays.asList(
                "09:00", "10:30", "12:00",
                "14:00", "15:30", "17:00",
                "18:30", "20:00"
        ));
        DatabaseReference dbUtente = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getCurrentUser().getUid()).child("prenotazioneCampoMaestro").child(idCampo);
        dbUtente.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot maestroSnapshot : dataSnapshot.getChildren()) {
                    String dataOrario = maestroSnapshot.getValue(String.class);
                    String orario = dataOrario.substring(dataOrario.indexOf(" "), dataOrario.length()).trim();
                    orari.remove(orario);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.e("FirebaseError", "Errore durante il recupero dei dati", databaseError.toException());
            }
        });
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("prenotazioni")
                .child(idCampo)
                .child(data);

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot orarioSnapshot : dataSnapshot.getChildren()) {
                    Prenotazione prenotazione = orarioSnapshot.getValue(Prenotazione.class);
                    if (prenotazione != null) {
                        if (prenotazione.getDisponibile()) {
                            DataSnapshot utentiSnapshot = orarioSnapshot.child("utenti");
                            for (DataSnapshot utenteSnapshot : utentiSnapshot.getChildren()) {
                                String utenteId = utenteSnapshot.getValue(String.class);
                                if (mAuth.getCurrentUser().getUid().equals(utenteId))
                                    orari.remove(prenotazione.getOra());
                            }
                        } else
                            orari.remove(prenotazione.getOra());
                    }

                }
                adapter.updateOrari(orari);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Errore nel recupero dei dati: ", databaseError.toException());
            }
        });
    }

    private void dialogPrenotazione(boolean isSuccesso) {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_result);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        ImageView image = dialog.findViewById(R.id.image_result);
        TextView text = dialog.findViewById(R.id.text_result);

        if (isSuccesso) {
            image.setImageResource(R.drawable.success_icon);
            text.setText("Prenotazione avvenuta con successo!");
        } else {
            image.setImageResource(R.drawable.error_icon);
            text.setText("Si è verificato un errore durante la prenotazione.");
        }
        dialog.show();
        new Handler().postDelayed(() -> {
            dialog.dismiss();
            dismiss();
        }, 2000);
    }
    private void showDialog(String titolo, String messaggio) {
        new AlertDialog.Builder(getContext())
                .setTitle(titolo)
                .setMessage(messaggio)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                    dismiss();
                })
                .setCancelable(false)
                .show();
    }

}