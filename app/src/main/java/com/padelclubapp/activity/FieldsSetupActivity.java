package com.padelclubapp.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.padelclubapp.adapter.FieldsAdapter;
import com.padelclubapp.databinding.ActivityFieldsSetupBinding;
import com.padelclubapp.dataclass.Campi;

import java.util.ArrayList;

public class FieldsSetupActivity extends AppCompatActivity {
    private ActivityFieldsSetupBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityFieldsSetupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setUpToolbar();
        binding.recycle.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<Campi> padelCourtList = new ArrayList<>();
        // Aggiungi alcuni dati alla lista
        padelCourtList.add(new Campi("Campo 1", "Esteriore", true,2));
        padelCourtList.add(new Campi("Campo 2", "Interiore", true, 2));
        // Aggiungi altri campi se necessario

        FieldsAdapter adapter = new FieldsAdapter(padelCourtList, this);
        binding.recycle.setAdapter(adapter);
    }
    private void setUpToolbar() {
        binding.toolbar.backIcon.setVisibility(View.GONE);
        binding.toolbar.toolbarTitle.setText("Personalizza la tua app");
    }

}