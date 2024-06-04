package com.padelclubapp.activity;

import android.Manifest;
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
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.padelclubapp.databinding.ActivityLogoSetUpBinding;
import com.padelclubapp.dataclass.InfoApp;

import java.util.Map;

public class LogoSetUpActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;
    private ActivityLogoSetUpBinding binding;
    private DatabaseReference mDatabase;
    private StorageReference storageReference;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLogoSetUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setUpToolbar();
        goStorage();
        saveData();

    }

    private void saveData() {
        mDatabase = FirebaseDatabase.getInstance("https://padelclubapp-default-rtdb.firebaseio.com").getReference();
        storageReference = FirebaseStorage.getInstance("gs://padelclubapp.appspot.com").getReference();

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageUri != null) {
                    uploadFirebase(imageUri);
                } else {
                    Toast.makeText(LogoSetUpActivity.this, "selezionare l'immaggine", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(LogoSetUpActivity.this, "apposto", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
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
            binding.logo.setImageURI(imageUri);
        }
    }
}
