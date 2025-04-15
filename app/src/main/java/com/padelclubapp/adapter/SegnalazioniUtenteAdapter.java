package com.padelclubapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.padelclubapp.R;

import java.util.List;

public class SegnalazioniUtenteAdapter extends RecyclerView.Adapter<SegnalazioniUtenteAdapter.MyViewHolder> {

    private List<String> dataList;
    private OnAzioniListener listener;

    public interface OnAzioniListener {
        void onModificaClick(String segnalazione);

        void onEliminaClick(String segnalazione);
    }

    public SegnalazioniUtenteAdapter(List<String> dataList, OnAzioniListener listener) {
        this.dataList = dataList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.segnalazioni_utente_recycle, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String segnalazione = dataList.get(position);
        holder.segnalazione.setText(segnalazione);


        holder.modifica.setOnClickListener(v -> listener.onModificaClick(segnalazione));
        holder.elimina.setOnClickListener(v -> listener.onEliminaClick(segnalazione));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView segnalazione;
        Button modifica, elimina;

        public MyViewHolder(View itemView) {
            super(itemView);
            segnalazione = itemView.findViewById(R.id.segnalazione);
            modifica = itemView.findViewById(R.id.modifica);
            elimina = itemView.findViewById(R.id.elimina);
        }
    }
}
