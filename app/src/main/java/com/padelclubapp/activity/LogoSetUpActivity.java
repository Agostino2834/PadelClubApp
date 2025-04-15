package com.padelclubapp.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.padelclubapp.databinding.ActivityLogoSetUpBinding;
import com.padelclubapp.dataclass.InfoApp;

import java.util.Map;
import java.util.Objects;

public class LogoSetUpActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;
    private ActivityLogoSetUpBinding binding;
    private DatabaseReference mDatabase;
    private StorageReference storageReference;
    private Uri imageUri;
    private Dialog dialog;
    private boolean isImageChange = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLogoSetUpBinding.inflate(getLayoutInflater());
        mDatabase = FirebaseDatabase.getInstance("https://padelclubapp-default-rtdb.firebaseio.com").getReference();
        storageReference = FirebaseStorage.getInstance("gs://padelclubapp.appspot.com").getReference();
        dialog = new Dialog(LogoSetUpActivity.this);
        setDialog();
        setUpToolbar();
        setupUi();
        goStorage();
        saveData();
        setContentView(binding.getRoot());
    }

    private void setupUi() {
        mDatabase.child("infoApp").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    InfoApp infoApp = dataSnapshot.getValue(InfoApp.class);
                    binding.nomeClub.setText(infoApp.getNome());
                    if (!infoApp.getLogo().isEmpty()) {
                        Glide.with(LogoSetUpActivity.this)
                                .load(infoApp.getLogo())
                                .into(binding.logo);
                        imageUri = Uri.parse(infoApp.getLogo());
                        binding.cancelLogo.setVisibility(View.VISIBLE);
                    } else
                        binding.logo.setImageDrawable(getDrawable(R.drawable.logo_default));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.cancelLogo.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View view) {
                binding.logo.setImageDrawable(getDrawable(R.drawable.logo_default));
                binding.cancelLogo.setVisibility(View.INVISIBLE);
                isImageChange = false;
                imageUri = null;
            }
        });
    }

    private void saveData() {
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageUri != null && !binding.nomeClub.getText().toString().isEmpty()) {
                    if (!isImageChange) {
                        mDatabase.child("infoApp").child("nome").setValue(binding.nomeClub.getText().toString());
                    } else
                        uploadFirebase(imageUri);
                    Intent intent = new Intent(LogoSetUpActivity.this, GestioneCampiActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(LogoSetUpActivity.this, "selezionare l'immaggine e il nome", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void uploadFirebase(Uri uri) {
        StorageReference fileRef = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        InfoApp infoApp = new InfoApp(uri.toString(), binding.nomeClub.getText().toString(), 0);
                        mDatabase.child("infoApp").setValue(infoApp);
                        dialog.dismiss();
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

    private void setUpToolbar() {
        binding.toolbar.backIcon.setVisibility(View.GONE);
        binding.toolbar.toolbarTitle.setText("Personalizza la tua app");
    }

    private void goStorage() {
        ActivityResultLauncher<String[]> requestPermissionsLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                this::onPermissionsResult
        );

        binding.logo.setOnClickListener(view -> {
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
            Toast.makeText(LogoSetUpActivity.this, "All permissions granted", Toast.LENGTH_SHORT).show();
            openGallery();
        } else {
            Toast.makeText(LogoSetUpActivity.this, "Permissions denied", Toast.LENGTH_SHORT).show();
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
            Glide.with(this).load(imageUri).into(binding.logo);
            binding.cancelLogo.setVisibility(View.VISIBLE);
            isImageChange = true;
        }
    }
}
