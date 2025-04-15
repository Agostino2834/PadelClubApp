package com.padelclubapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.padelclubapp.R;
import com.padelclubapp.dataclass.Campo;

import java.util.List;

public class HomeFieldsAdapter extends RecyclerView.Adapter<HomeFieldsAdapter.CampoViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(String id);
    }

    private List<Campo> listaCampi;
    private Context context;
    private OnItemClickListener listener;

    public HomeFieldsAdapter(List<Campo> listaCampi, Context context, OnItemClickListener listener) {
        this.listaCampi = listaCampi;
        this.context = context;
        this.listener = listener;
    }

    public static class CampoViewHolder extends RecyclerView.ViewHolder {
        TextView nomeCampo, tipo;

        public CampoViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeCampo = itemView.findViewById(R.id.nome_campo);
            tipo = itemView.findViewById(R.id.tipo);
        }
    }

    @NonNull
    @Override
    public CampoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_field_recycle, parent, false); // <-- cambia con il tuo nome layout
        return new CampoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CampoViewHolder holder, int position) {
        Campo campo = listaCampi.get(position);
        holder.nomeCampo.setText(campo.getNome());
        holder.tipo.setText(campo.getTipoCampo());
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(campo.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaCampi.size();
    }
}
