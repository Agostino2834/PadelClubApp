package com.padelclubapp.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.padelclubapp.R;
import com.padelclubapp.databinding.ActivityModificaProfiloBinding;
import com.padelclubapp.dataclass.Campo;
import com.padelclubapp.dataclass.Prenotazione;
import com.padelclubapp.dataclass.PrenotazioneMaestro;
import com.padelclubapp.dataclass.Utente;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class ModificaProfiloActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;
    private ActivityModificaProfiloBinding binding;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private Dialog dialog;
    private Uri imageUri;
    private Utente utente;
    private Prenotazione prenotazione;
    private PrenotazioneMaestro prenotazioneMaestro;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityModificaProfiloBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setToolBar();
        setFirebase();
        goStorage();
        dialog = new Dialog(ModificaProfiloActivity.this);
        setDialog();
        utente = new Utente();
        prenotazione = new Prenotazione();
        prenotazioneMaestro = new PrenotazioneMaestro();
        getDatiUtente();
        binding.cancelImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.profileImage.setImageDrawable(getDrawable(R.drawable.account_circle));
                binding.cancelImage.setVisibility(View.INVISIBLE);
                imageUri = null;
            }
        });
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidateData(binding.nome.getText().toString(), imageUri, binding.cognome.getText().toString(), binding.email.getText().toString(), binding.bio.getText().toString()))
                    if (!imageUri.toString().equals(utente.getFotoUtente()))
                        uploadFirebase(imageUri);
                    else
                        salvaModifiche();
            }
        });
        binding.deletePrenotazioneCampo.setOnClickListener(v ->
                showDialogConfermaEliminazione("prenotazioneCampo", true));
        binding.deletePrenotazioneMaestro.setOnClickListener(v -> showDialogConfermaEliminazione("prenotazioneCampoMaestro", false));
    }

    private void salvaModifiche() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("nome", binding.nome.getText().toString());
        updates.put("cognome", binding.cognome.getText().toString());
        updates.put("email", binding.email.getText().toString());
        updates.put("bio", binding.bio.getText().toString());
        mDatabase.updateChildren(updates);
        finish();
    }

    private void setToolBar() {
        binding.toolbar.toolbarTitle.setVisibility(View.GONE);
        binding.toolbar.backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
        storageReference = FirebaseStorage.getInstance("gs://padelclubapp.appspot.com").getReference();
    }

    private void getDatiUtente() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                utente = snapshot.getValue(Utente.class);
                if (utente != null) {
                    Glide.with(ModificaProfiloActivity.this).load(utente.getFotoUtente()).into(binding.profileImage);
                    imageUri = Uri.parse(utente.getFotoUtente());
                    binding.cancelImage.setVisibility(View.VISIBLE);
                    binding.nome.setText(utente.getNome());
                    binding.cognome.setText(utente.getCognome());
                    binding.email.setText(utente.getEmail());
                    binding.bio.setText(utente.getBio());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        mDatabase.child("prenotazioneCampoMaestro").child("dettagli").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    prenotazioneMaestro = snapshot.getValue(PrenotazioneMaestro.class);
                    getDataCampo(prenotazioneMaestro.getIdCampo(), prenotazioneMaestro.getData(), prenotazioneMaestro.getOra(), true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        mDatabase.child("prenotazioneCampo").child("dettagli").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    prenotazione = snapshot.getValue(Prenotazione.class);
                    getDataCampo(prenotazione.getIdCampo(), prenotazione.getData(), prenotazione.getOra(), false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getDataCampo(String campoId, String data, String ora, boolean isLezione) {
        DatabaseReference dbCampo = FirebaseDatabase.getInstance().getReference().child("campi").child(campoId);
        dbCampo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Campo campo = snapshot.getValue(Campo.class);
                    if (isLezione) {
                        binding.txtPrenotazioneMaestro.setText("Nome Campo: " + campo.getNome() + "\n" + "Giorno: " + data + "\n" + "Ora: " + ora);
                        binding.deletePrenotazioneMaestro.setVisibility(View.VISIBLE);
                    } else {
                        binding.txtPrenotazioneCampo.setText("Nome Campo: " + campo.getNome() + "\n" + "Giorno: " + data + "\n" + "Ora: " + ora);
                        binding.deletePrenotazioneCampo.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private boolean checkNameSurname(String name) {
        String namePattern = "^[A-Za-zÀ-ÖØ-öø-ÿ' -]+$";
        Pattern pattern = Pattern.compile(namePattern);
        return !name.isEmpty() && pattern.matcher(name).matches();
    }

    private boolean checkEmail(String email) {
        return !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidateData(String name, Uri imageUri, String surname, String email, String bio) {
        return checkNameSurname(name) && (imageUri != null) && checkNameSurname(surname) && checkEmail(email) && (!bio.isEmpty());
    }

    private void uploadFirebase(Uri uri) {
        StorageReference fileRef = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("nome", binding.nome.getText().toString());
                        updates.put("cognome", binding.cognome.getText().toString());
                        updates.put("email", binding.email.getText().toString());
                        updates.put("bio", binding.bio.getText().toString());
                        updates.put("fotoUtente", uri.toString());
                        mDatabase.updateChildren(updates);
                        dialog.dismiss();
                        finish();
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                dialog.show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                dialog.dismiss();
            }
        });
    }

    private void goStorage() {
        ActivityResultLauncher<String[]> requestPermissionsLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                this::onPermissionsResult
        );

        binding.profileImage.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                requestPermissionsLauncher.launch(new String[]{
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VIDEO,
                        Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                });
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissionsLauncher.launch(new String[]{
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VIDEO
                });
            } else {
                requestPermissionsLauncher.launch(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE});
            }
        });
    }

    private void onPermissionsResult(Map<String, Boolean> results) {
        boolean allPermissionsGranted = true;
        for (Map.Entry<String, Boolean> entry : results.entrySet()) {
            if (!entry.getValue()) {
                allPermissionsGranted = false;
                break;
            }
        }

        if (allPermissionsGranted) {
            Toast.makeText(ModificaProfiloActivity.this, "All permissions granted", Toast.LENGTH_SHORT).show();
            openGallery();
        } else {
            Toast.makeText(ModificaProfiloActivity.this, "Permissions denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            binding.profileImage.setImageURI(imageUri);
            binding.cancelImage.setVisibility(View.VISIBLE);
        }
    }

    private void setDialog() {
        dialog.setContentView(R.layout.dialog_progress_bar);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    private void showDialogConfermaEliminazione(String path, boolean isPrenotazioneCampo) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_elimina_segnalazione);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(true);

        TextView message = dialog.findViewById(R.id.dialogMessage);
        Button btnAnnulla = dialog.findViewById(R.id.btnAnnulla);
        Button btnElimina = dialog.findViewById(R.id.btnElimina);

        message.setText("Sei sicuro di voler eliminare questa prenotazione?");

        btnAnnulla.setOnClickListener(v -> dialog.dismiss());

        btnElimina.setOnClickListener(v -> {
            if (isPrenotazioneCampo) {
                binding.deletePrenotazioneCampo.setVisibility(View.GONE);
                binding.txtPrenotazioneCampo.setText("Non ci sono prenotazioni da visualizzare");
                DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("prenotazioni").child(prenotazione.getIdCampo()).child(prenotazione.getData()).child(prenotazione.getOra());
                if (prenotazione.getnPosti() == 1) {
                    database.removeValue();
                } else
                    database.child("nPosti").setValue(prenotazione.getnPosti() - 1);
                database.child("utenti").child(mAuth.getCurrentUser().getUid()).removeValue();
            } else {
                binding.deletePrenotazioneMaestro.setVisibility(View.GONE);
                binding.txtPrenotazioneMaestro.setText("Non ci sono prenotazioni da visualizzare");
                FirebaseDatabase.getInstance().getReference().child("prenotazioniMaestri").child(prenotazioneMaestro.getIdMaestro()).child(prenotazioneMaestro.getIdCampo()).child(prenotazioneMaestro.getData()).child(prenotazioneMaestro.getOra()).removeValue();
            }
            mDatabase.child(path).child("dettagli").removeValue();
            dialog.dismiss();
        });

        dialog.show();
    }
}