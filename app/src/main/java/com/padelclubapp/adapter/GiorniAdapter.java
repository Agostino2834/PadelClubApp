package com.padelclubapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.padelclubapp.R;
import com.padelclubapp.item.GiornoItem;

import java.util.List;

public class GiorniAdapter extends RecyclerView.Adapter<GiorniAdapter.GiornoViewHolder> {

    private List<GiornoItem> lista;
    private OnGiornoClickListener listener;

    public interface OnGiornoClickListener {
        void onGiornoClick(GiornoItem giorno);
    }

    public GiorniAdapter(List<GiornoItem> lista, OnGiornoClickListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GiornoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_giorno, parent, false);
        return new GiornoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GiornoViewHolder holder, int position) {
        GiornoItem giorno = lista.get(position);
        holder.txtGiorno.setText(giorno.getGiornoSettimana());
        holder.txtGiornoNumero.setText(giorno.getGiornoSettimanaNumero());
        holder.txtMese.setText(giorno.getMese());

        holder.itemView.setOnClickListener(view -> listener.onGiornoClick(giorno));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class GiornoViewHolder extends RecyclerView.ViewHolder {
        TextView txtGiornoNumero, txtGiorno, txtMese;

        GiornoViewHolder(View itemView) {
            super(itemView);
            txtMese = itemView.findViewById(R.id.txtMese);
            txtGiorno = itemView.findViewById(R.id.txtGiorno);
            txtGiornoNumero = itemView.findViewById(R.id.txtGiornoNumero);
        }
    }
}

