package com.padelclubapp.fragment;

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
import com.padelclubapp.adapter.MaestriAdapter;
import com.padelclubapp.databinding.FragmentPrenotaLezioneBinding;
import com.padelclubapp.dataclass.Maestro;

import java.util.ArrayList;
import java.util.List;


public class PrenotaLezioneFragment extends Fragment {

    private FragmentPrenotaLezioneBinding binding;
    private DatabaseReference database;
    private FirebaseAuth auth;
    private MaestriAdapter adapter;
    private List<Maestro> maestri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPrenotaLezioneBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        database = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        maestri = new ArrayList<>();
        adapter = new MaestriAdapter(maestri, getContext(), new MaestriAdapter.OnItemClickListener() {
            @Override
            public void onClick(Maestro maestro) {
                PrenotaCampoOraBottomSheetFragment bottomSheet = new PrenotaCampoOraBottomSheetFragment();
                Bundle bundle = new Bundle();
                bundle.putString("id_maestro", maestro.getId());
                bottomSheet.setArguments(bundle);
                bottomSheet.show(getParentFragmentManager(), bottomSheet.getTag());
            }
        });
        binding.recycle.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recycle.setAdapter(adapter);
        getData();
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

    }

    public void getData() {
        database.child("maestri").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot maestriSnapshot : snapshot.getChildren()) {
                    Maestro maestro = maestriSnapshot.getValue(Maestro.class);
                    if (maestro != null)
                        maestri.add(maestro);
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