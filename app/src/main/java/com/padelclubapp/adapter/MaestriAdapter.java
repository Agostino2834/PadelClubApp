package com.padelclubapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.padelclubapp.R;
import com.padelclubapp.dataclass.Maestro;
import com.padelclubapp.dataclass.Utente;

import java.util.ArrayList;
import java.util.List;

public class MaestriAdapter extends RecyclerView.Adapter<MaestriAdapter.MaestroViewHolder> {

    private List<Maestro> listaMaestri;
    private Context context;
    private OnItemClickListener listener;
    private List<Maestro> filterList;

    public interface OnItemClickListener {
        void onClick(Maestro maestro);
    }

    public MaestriAdapter(List<Maestro> listaMaestri, Context context, OnItemClickListener listener) {
        this.listaMaestri = listaMaestri;
        this.context = context;
        this.filterList = new ArrayList<>(listaMaestri);
        this.listener = listener;
    }
    public void filter(String query) {
        filterList.clear();
        if (query.isEmpty()) {
            filterList.addAll(listaMaestri);
        } else {
            String lowerQuery = query.toLowerCase();
            for (Maestro maestro : listaMaestri) {
                if (maestro.getNome().toLowerCase().contains(lowerQuery)) {
                    filterList.add(maestro);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MaestroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.maestri_recycle, parent, false);
        return new MaestroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MaestroViewHolder holder, int position) {
        Maestro maestro = filterList.get(position);

        holder.nomeTextView.setText(maestro.getNome() + " " + maestro.getCognome());
        holder.emailTextView.setText(maestro.getEmail());

        holder.itemView.setOnClickListener(v -> listener.onClick(maestro));
    }

    @Override
    public int getItemCount() {
        return filterList.size();
    }

    public static class MaestroViewHolder extends RecyclerView.ViewHolder {
        TextView nomeTextView, emailTextView;

        public MaestroViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeTextView = itemView.findViewById(R.id.nome_maestro);
            emailTextView = itemView.findViewById(R.id.email);
        }
    }
}
