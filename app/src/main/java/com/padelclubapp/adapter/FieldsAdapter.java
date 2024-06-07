package com.padelclubapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.padelclubapp.R;
import com.padelclubapp.dataclass.Campi;

import java.util.List;

public class FieldsAdapter extends RecyclerView.Adapter<FieldsAdapter.FieldsViewHolder> {
    private List<Campi> padelCourtList;
    private Context context;

    public FieldsAdapter(List<Campi> padelCourtList, Context context) {
        this.padelCourtList = padelCourtList;
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
        Campi campo = padelCourtList.get(position);
        holder.nomeCampo.setText(campo.getNome());
        holder.tipoCampo.setText(campo.getTipoCampo());
    }

    @Override
    public int getItemCount() {
        return padelCourtList.size();
    }

    public static class FieldsViewHolder extends RecyclerView.ViewHolder {
        TextView nomeCampo;
        TextView tipoCampo;
        TextView postiDisponibili;

        public FieldsViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeCampo = itemView.findViewById(R.id.title);
            tipoCampo = itemView.findViewById(R.id.tipo);
        }
    }
}
