package com.padelclubapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.padelclubapp.R;

import java.util.List;

public class OrariAdapter extends RecyclerView.Adapter<OrariAdapter.OrarioViewHolder> {

    public interface OnClick {
        void onClick(String orario);
    }

    private List<String> orari;
    private OnClick listener;

    public OrariAdapter(List<String> orari, OnClick listener) {
        this.orari = orari;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orari_recycle, parent, false);
        return new OrarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrarioViewHolder holder, int position) {
        String orario = orari.get(position);
        holder.orarioText.setText(orario);
        holder.itemView.setOnClickListener(v -> listener.onClick(orario));
    }

    @Override
    public int getItemCount() {
        return orari.size();
    }

    public void updateOrari(List<String> nuoviOrari) {
        orari.clear();
        orari.addAll(nuoviOrari);
        notifyDataSetChanged();
    }

    static class OrarioViewHolder extends RecyclerView.ViewHolder {
        TextView orarioText;

        public OrarioViewHolder(@NonNull View itemView) {
            super(itemView);
            orarioText = itemView.findViewById(R.id.txt_orario);
        }
    }
}

