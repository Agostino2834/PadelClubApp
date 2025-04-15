package com.padelclubapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.padelclubapp.adapter.GiorniAdapter;
import com.padelclubapp.databinding.FragmentPrenotaCampoOraBottomSheetBinding;
import com.padelclubapp.dataclass.Campo;
import com.padelclubapp.item.GiornoItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class PrenotaCampoOraBottomSheetFragment extends BottomSheetDialogFragment {

    private FragmentPrenotaCampoOraBottomSheetBinding binding;
    private ArrayList<GiornoItem> giorniList;
    private GiorniAdapter adapter;
    private String idMaestro;
    private DatabaseReference database;
    private List<Campo> campi;
    private String idCampo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();

        if (arguments != null) {
            idMaestro = arguments.getString("id_maestro");
        }
        binding = FragmentPrenotaCampoOraBottomSheetBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        database = FirebaseDatabase.getInstance().getReference();
        campi = new ArrayList<>();
        giorniList = new ArrayList<>();
        giorniList.addAll(getSettimanaProssima());
        adapter = new GiorniAdapter(giorniList, new GiorniAdapter.OnGiornoClickListener() {
            @Override
            public void onGiornoClick(GiornoItem giorno) {
                PrenotaMaestriOraBottomSheetFragment bottomSheet = new PrenotaMaestriOraBottomSheetFragment();
                Bundle bundle = new Bundle();
                bundle.putString("data", giorno.getData());
                bundle.putString("id_maestro", idMaestro);
                bundle.putString("id_campo", idCampo);
                bottomSheet.setArguments(bundle);
                bottomSheet.show(getParentFragmentManager(), bottomSheet.getTag());
            }
        });
        binding.recyclerGiorni.setLayoutManager(new GridLayoutManager(getContext(), 3));
        binding.recyclerGiorni.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        getCampi();
    }

    public void getCampi() {
        database.child("campi").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot campiSnapshot : snapshot.getChildren()) {
                    Campo campo = campiSnapshot.getValue(Campo.class);
                    if (campo != null)
                        campi.add(campo);
                }
                getSpinner(campi);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getSpinner(List<Campo> campi) {
        List<String> nomeCampi = new ArrayList<>();
        for (int i = 0; i < campi.size(); i++) {
            nomeCampi.add(campi.get(i).getNome() + " (" + campi.get(i).getTipoCampo() + ")");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                nomeCampi
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCampo.setAdapter(adapter);

        binding.spinnerCampo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                idCampo = campi.get(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    public static List<GiornoItem> getSettimanaProssima() {
        List<GiornoItem> giorni = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdfData = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        SimpleDateFormat sdfGiornoNumero = new SimpleDateFormat("dd", Locale.getDefault());
        SimpleDateFormat sdfGiorno = new SimpleDateFormat("EEEE", new Locale("it", "IT"));
        SimpleDateFormat sdfMese = new SimpleDateFormat("MMMM", new Locale("it", "IT"));

        for (int i = 1; i <= 9; i++) {
            cal.add(Calendar.DAY_OF_YEAR, 1);
            String data = sdfData.format(cal.getTime());
            String giornoNumero = sdfGiornoNumero.format(cal.getTime());
            String giorno = sdfGiorno.format(cal.getTime());
            String mese = sdfMese.format(cal.getTime());
            giorni.add(new GiornoItem(data, giornoNumero, giorno, mese));
        }

        return giorni;
    }
}