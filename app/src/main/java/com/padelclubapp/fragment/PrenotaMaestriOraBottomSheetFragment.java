package com.padelclubapp.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
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
import com.padelclubapp.adapter.OrariAdapter;
import com.padelclubapp.databinding.FragmentPrenotaMaestriOraBottomSheetBinding;
import com.padelclubapp.dataclass.Prenotazione;
import com.padelclubapp.dataclass.PrenotazioneMaestro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PrenotaMaestriOraBottomSheetFragment extends BottomSheetDialogFragment {
    private FragmentPrenotaMaestriOraBottomSheetBinding binding;
    private OrariAdapter adapter;
    private String idCampo;
    private String data;
    private String idMaestro;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();

        if (arguments != null) {
            data = arguments.getString("data");
            idCampo = arguments.getString("id_campo");
            idMaestro = arguments.getString("id_maestro");
        }
        mAuth = FirebaseAuth.getInstance();
        binding = FragmentPrenotaMaestriOraBottomSheetBinding.inflate(inflater, container, false);
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
        DatabaseReference dbUtente = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getCurrentUser().getUid()).child("prenotazioneCampoMaestro");
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
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("prenotazioniMaestri")
                .child(idMaestro)
                .child(idCampo)
                .child(data)
                .child(orario);
        String key = dbRef.push().getKey();
        PrenotazioneMaestro prenotazioneMaestro = new PrenotazioneMaestro(key, idMaestro, idCampo, mAuth.getCurrentUser().getUid(), data, orario);
        dbRef.setValue(prenotazioneMaestro)
                .addOnSuccessListener(unused -> {
                    dbUtente.child("prenotazioneCampoMaestro").child("dettagli").setValue(prenotazioneMaestro);
                    dialogPrenotazione(true);
                    dismiss();
                })
                .addOnFailureListener(e -> {
                    dialogPrenotazione(false);
                });

    }

    public void getOrariDisponibili() {
        List<String> orari = new ArrayList<>(Arrays.asList(
                "09:00", "10:30", "12:00",
                "14:00", "15:30", "17:00",
                "18:30", "20:00"
        ));
        DatabaseReference dbPrenotazioni = FirebaseDatabase.getInstance().getReference("prenotazioni")
                .child(idCampo)
                .child(data);

        dbPrenotazioni.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot orarioSnapshot : dataSnapshot.getChildren()) {
                    Prenotazione prenotazione = orarioSnapshot.getValue(Prenotazione.class);
                    if (prenotazione != null) {
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
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("prenotazioniMaestri")
                .child(idMaestro)
                .child(idCampo)
                .child(data);

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot orarioSnapshot : dataSnapshot.getChildren()) {
                    PrenotazioneMaestro prenotazione = orarioSnapshot.getValue(PrenotazioneMaestro.class);
                    if (prenotazione != null) {
                        if (prenotazione.getIdUtente().equals(mAuth.getCurrentUser().getUid())) {
                            orari.remove(prenotazione.getOra());
                        }
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