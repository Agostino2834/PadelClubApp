package com.padelclubapp.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.padelclubapp.adapter.HomeFieldsAdapter;
import com.padelclubapp.databinding.FragmentHomeBinding;
import com.padelclubapp.dataclass.Campo;
import com.padelclubapp.dataclass.Prenotazione;
import com.padelclubapp.dataclass.PrenotazioneMaestro;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private ArrayList<Campo> campi;
    private HomeFieldsAdapter adapter;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String ora;
    private String data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        campi = new ArrayList<>();
        adapter = new HomeFieldsAdapter(campi, getContext(), new HomeFieldsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String id) {
                GiorniBottomSheet bottomSheet = new GiorniBottomSheet();
                Bundle bundle = new Bundle();
                bundle.putString("id_campo", id);
                bottomSheet.setArguments(bundle);
                bottomSheet.show(getParentFragmentManager(), bottomSheet.getTag());
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        setAdapter();
        gestisciPrenotazioniCampo();
        gestisciPrenotazioniMaestro();
    }

    private void gestisciPrenotazioniMaestro() {
        DatabaseReference dbUtente = FirebaseDatabase.getInstance().getReference("users")
                .child(mAuth.getCurrentUser().getUid())
                .child("prenotazioneCampoMaestro").child("dettagli");

        dbUtente.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    PrenotazioneMaestro prenotazioneMaestro = dataSnapshot.getValue(PrenotazioneMaestro.class);

                    String dataOrario = prenotazioneMaestro.getData() + " " + prenotazioneMaestro.getOra();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());

                    try {
                        Date dataOrarioDate = sdf.parse(dataOrario);

                        if (dataOrarioDate != null) {
                            Date currentDate = new Date();
                            if (dataOrarioDate.before(currentDate)) {
                                dbUtente.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                    }
                                });
                            } else {
                                Log.d("Confronto", "La prenotazione è futura.");
                            }
                        }
                    } catch (Exception e) {
                        Log.e("Errore Data", "Errore nel parsing della data: " + e.getMessage());
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseError", "Errore durante il recupero dei dati", databaseError.toException());
            }
        });

    }

    private void gestisciPrenotazioniCampo() {
        DatabaseReference dbUtente = FirebaseDatabase.getInstance().getReference("users")
                .child(mAuth.getCurrentUser().getUid())
                .child("prenotazioneCampo").child("dettagli");

        dbUtente.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Prenotazione prenotazione = dataSnapshot.getValue(Prenotazione.class);
                    String dataOrario = prenotazione.getData() + " " + prenotazione.getOra();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());

                    try {
                        Date dataOrarioDate = sdf.parse(dataOrario);

                        if (dataOrarioDate != null) {
                            Date currentDate = new Date();
                            if (dataOrarioDate.before(currentDate)) {
                                dbUtente.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                    }
                                });
                            } else {
                                Log.d("Confronto", "La prenotazione è futura.");
                            }
                        }
                    } catch (Exception e) {
                        Log.e("Errore Data", "Errore nel parsing della data: " + e.getMessage());
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseError", "Errore durante il recupero dei dati", databaseError.toException());
            }
        });

    }

    private void setAdapter() {
        binding.recycle.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recycle.setAdapter(adapter);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://padelclubapp-default-rtdb.firebaseio.com").getReference("campi");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    campi.add(userSnapshot.getValue(Campo.class));
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}