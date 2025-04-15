package com.padelclubapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.padelclubapp.R;
import com.padelclubapp.dataclass.Utente;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UtentiAdapter extends RecyclerView.Adapter<UtentiAdapter.UserViewHolder> {


    public interface OnUtenteClickListener {
        void onUtenteClick(String id);
    }

    private Context context;
    private List<Utente> utentiList;
    private List<Utente> filterList;
    private OnUtenteClickListener listener;


    public UtentiAdapter(Context context, List<Utente> utentiList, OnUtenteClickListener listener) {
        this.context = context;
        this.utentiList = utentiList;
        this.filterList = new ArrayList<>(utentiList);
        this.listener = listener;
    }


    public static class UserViewHolder extends RecyclerView.ViewHolder {
        CircleImageView image;
        TextView nomeUtente, email;
        ImageView stella1, stella2, stella3, stella4, stella5;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            nomeUtente = itemView.findViewById(R.id.nome_utente);
            email = itemView.findViewById(R.id.email);
            stella1 = itemView.findViewById(R.id.rankingStella1);
            stella2 = itemView.findViewById(R.id.rankingStella2);
            stella3 = itemView.findViewById(R.id.rankingStella3);
            stella4 = itemView.findViewById(R.id.rankingStella4);
            stella5 = itemView.findViewById(R.id.rankingStella5);
        }
    }

    public void filter(String query) {
        filterList.clear();
        if (query.isEmpty()) {
            filterList.addAll(utentiList);
        } else {
            String lowerQuery = query.toLowerCase();
            for (Utente utente : utentiList) {
                if (utente.getNome().toLowerCase().contains(lowerQuery)) {
                    filterList.add(utente);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void filterRanking(boolean isFilter) {
        filterList.clear();
        filterList.addAll(utentiList);
        if (isFilter) {
            filterList.sort((u1, u2) -> Double.compare(u2.getRanking(), u1.getRanking()));
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.utenti_recycle, parent, false);  // Layout della riga
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        Utente utente = filterList.get(position);
        holder.nomeUtente.setText(utente.getNome() + " " + utente.getCognome());
        holder.email.setText(utente.getEmail());
        Glide.with(context).load(utente.getFotoUtente()).into(holder.image);
        int stellePieneRanking = (int) Math.floor(utente.getRanking());

        ImageView[] stelleRanking = new ImageView[5];
        stelleRanking[0] = holder.stella1;
        stelleRanking[1] = holder.stella2;
        stelleRanking[2] = holder.stella3;
        stelleRanking[3] = holder.stella4;
        stelleRanking[4] = holder.stella5;

        for (int i = 0; i < 5; i++) {
            if (i < stellePieneRanking) {
                stelleRanking[i].setImageResource(R.drawable.icon_stella_piena);
            } else {
                stelleRanking[i].setImageResource(R.drawable.icon_stella_vuota);
            }
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onUtenteClick(utente.getUserId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return filterList.size();
    }
}
