package com.padelclubapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.padelclubapp.R;
import com.padelclubapp.dataclass.Campo;

import java.util.List;

public class FieldsAdapter extends RecyclerView.Adapter<FieldsAdapter.FieldsViewHolder> {
    private List<Campo> lista;
    private Context context;

    public FieldsAdapter(List<Campo> lista, Context context) {
        this.lista = lista;
        this.context = context;
    }

    @NonNull
    @Override
    public FieldsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.field_recycle, parent, false);
        return new FieldsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FieldsViewHolder holder, int position) {
        Campo campo = lista.get(position);

        // Set nome nel campo EditText
        holder.nomeCampo.setText(campo.getNome());

        // Spinner setup
        String[] tipoOpzioni = {"Indoor", "Outdoor"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, tipoOpzioni);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinnerTipo.setAdapter(spinnerAdapter);

        String tipoCampo = campo.getTipoCampo();
        int spinnerPosition = getSpinnerPosition(tipoCampo, tipoOpzioni);
        holder.spinnerTipo.setSelection(spinnerPosition);

        // Rimozione elemento
        holder.cancelLogo.setOnClickListener(v -> {
            lista.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, lista.size());
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    private int getSpinnerPosition(String tipoCampo, String[] tipoOpzioni) {
        for (int i = 0; i < tipoOpzioni.length; i++) {
            if (tipoOpzioni[i].equalsIgnoreCase(tipoCampo)) {
                return i;
            }
        }
        return 0; // default
    }

    public void aggiornaCampi(RecyclerView recyclerView) {
        for (int i = 0; i < lista.size(); i++) {
            FieldsViewHolder holder = (FieldsViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
            if (holder != null) {
                String nome = holder.nomeCampo.getText().toString().trim();
                String tipo = holder.spinnerTipo.getSelectedItem().toString();
                Campo campo = lista.get(i);
                campo.setNome(nome);
                campo.setTipoCampo(tipo);
            }
        }
    }

    // ViewHolder
    public static class FieldsViewHolder extends RecyclerView.ViewHolder {
        EditText nomeCampo;
        Spinner spinnerTipo;
        ImageView cancelLogo;

        public FieldsViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeCampo = itemView.findViewById(R.id.title);
            spinnerTipo = itemView.findViewById(R.id.spinnerTipo);
            cancelLogo = itemView.findViewById(R.id.cancel_logo);
        }
    }
}
