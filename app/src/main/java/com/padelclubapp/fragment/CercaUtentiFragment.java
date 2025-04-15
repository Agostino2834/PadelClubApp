package com.padelclubapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.padelclubapp.R;
import com.padelclubapp.activity.DettaglioUtenteActivity;
import com.padelclubapp.adapter.UtentiAdapter;
import com.padelclubapp.databinding.FragmentCercaUtentiBinding;
import com.padelclubapp.dataclass.Utente;

import java.util.ArrayList;
import java.util.List;


public class CercaUtentiFragment extends Fragment {

    private FragmentCercaUtentiBinding binding;
    private List<Utente> utenti;
    private UtentiAdapter adapter;
    private boolean isFilterSelected = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCercaUtentiBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        utenti = new ArrayList<>();
        adapter = new UtentiAdapter(getContext(), utenti, new UtentiAdapter.OnUtenteClickListener() {
            @Override
            public void onUtenteClick(String id) {
                Intent intent = new Intent(getContext(), DettaglioUtenteActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

        binding.recycleUtenti.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recycleUtenti.setAdapter(adapter);
        getUtenti();
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return true;
            }
        });
        adapter.notifyDataSetChanged();
        binding.filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFilterSelected) {
                    binding.filter.setImageResource(R.drawable.filter_ranking_selected);
                } else {
                    binding.filter.setImageResource(R.drawable.filter_ranking);
                }
                adapter.filterRanking(isFilterSelected);
                isFilterSelected = !isFilterSelected;
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void getUtenti() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("users");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    Utente utente = userSnapshot.getValue(Utente.class);
                    if (utente != null && !utente.getAdmin() && !utente.getUserId().equals(auth.getCurrentUser().getUid())) {
                        utenti.add(utente);
                    }
                }
                adapter.notifyDataSetChanged();
                adapter.filter("");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}