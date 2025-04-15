package com.padelclubapp.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.padelclubapp.R;
import com.padelclubapp.activity.LoginActivity;
import com.padelclubapp.activity.ModificaProfiloActivity;
import com.padelclubapp.databinding.FragmentInfoBinding;
import com.padelclubapp.dataclass.Utente;

import java.util.Objects;


public class InfoFragment extends Fragment {
    private FragmentInfoBinding binding;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private Dialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentInfoBinding.inflate(inflater, container, false);
        dialog = new Dialog(getContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setToolBar();
        setDialog();
        dialog.show();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        setData();
        binding.toolbar.rightIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutDialog();
            }
        });
        binding.modifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ModificaProfiloActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setData();
    }

    private void setData() {

        mDatabase.child("users").child(mAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Utente user = snapshot.getValue(Utente.class);
                        if (user != null) {
                            binding.textViewNomeCompleto.setText(user.getNome() + " " + user.getCognome());
                            binding.textViewEmail.setText(user.getEmail());
                            binding.textViewBio.setText(user.getBio());
                            binding.textViewRanking.setText(String.valueOf(user.getRanking()));
                            binding.textViewReputazione.setText(String.valueOf(user.getReputazione()));
                            Glide.with(getContext()).load(user.getFotoUtente()).into(binding.fotoProfilo);
                            setStars(user.getReputazione(), user.getRanking());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase", "Errore nel recupero dei dati", error.toException());
                    }
                });
        dialog.dismiss();
    }

    private void setToolBar() {
        binding.toolbar.backIcon.setVisibility(View.INVISIBLE);
        binding.toolbar.toolbarTitle.setText("");
        binding.toolbar.rightIcon.setVisibility(View.VISIBLE);

    }

    private void setDialog() {
        dialog.setContentView(R.layout.dialog_progress_bar);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false);
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Sei sicuro?")
                .setMessage("Vuoi davvero uscire dal tuo account?")
                .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mAuth.signOut();
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        requireActivity().finish();
                    }
                })
                .setNegativeButton("Annulla", null)
                .setIcon(android.R.drawable.ic_dialog_alert);

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                ContextCompat.getColor(requireContext(), android.R.color.background_dark));
        dialog.getWindow().setBackgroundDrawable(
                ContextCompat.getDrawable(requireContext(), R.drawable.dialog_background));
    }

    public void setStars(Double reputazione, Double ranking) {
        int stellePieneRanking = (int) Math.floor(ranking);
        int stellePieneReputazione = (int) Math.floor(reputazione);

        ImageView[] stelleRanking = new ImageView[5];
        stelleRanking[0] = binding.rankingStella1;
        stelleRanking[1] = binding.rankingStella2;
        stelleRanking[2] = binding.rankingStella3;
        stelleRanking[3] = binding.rankingStella4;
        stelleRanking[4] = binding.rankingStella5;

        ImageView[] stelleReputazione = new ImageView[5];
        stelleReputazione[0] = binding.reputazioneStella1;
        stelleReputazione[1] = binding.reputazioneStella2;
        stelleReputazione[2] = binding.reputazioneStella3;
        stelleReputazione[3] = binding.reputazioneStella4;
        stelleReputazione[4] = binding.reputazioneStella5;

        for (int i = 0; i < 5; i++) {
            if (i < stellePieneRanking) {
                stelleRanking[i].setImageResource(R.drawable.icon_stella_piena);
            }
            if (i < stellePieneReputazione)
                stelleReputazione[i].setImageResource(R.drawable.icon_stella_piena);
        }


    }
}