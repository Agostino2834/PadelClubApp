package com.padelclubapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.padelclubapp.adapter.GiorniAdapter;
import com.padelclubapp.databinding.FragmentGiorniBottomSheetBinding;
import com.padelclubapp.item.GiornoItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class GiorniBottomSheet extends BottomSheetDialogFragment {

    private FragmentGiorniBottomSheetBinding binding;
    private ArrayList<GiornoItem> giorniList;
    private GiorniAdapter adapter;
    private String idCampo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();

        if (arguments != null) {
            idCampo = arguments.getString("id_campo");
        }
        binding = FragmentGiorniBottomSheetBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        giorniList = new ArrayList<>();
        giorniList.addAll(getSettimanaProssima());
        adapter = new GiorniAdapter(giorniList, new GiorniAdapter.OnGiornoClickListener() {
            @Override
            public void onGiornoClick(GiornoItem giorno) {
                OrariBottomSheetFragment bottomSheet = new OrariBottomSheetFragment();
                Bundle bundle = new Bundle();
                bundle.putString("data", giorno.getData());
                bundle.putString("id_campo", idCampo);
                bottomSheet.setArguments(bundle);
                bottomSheet.show(getParentFragmentManager(), bottomSheet.getTag());
            }
        });
        binding.recyclerGiorni.setLayoutManager(new GridLayoutManager(getContext(), 3));
        binding.recyclerGiorni.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        super.onViewCreated(view, savedInstanceState);
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