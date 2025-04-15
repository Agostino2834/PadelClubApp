package com.padelclubapp.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.padelclubapp.R;
import com.padelclubapp.databinding.ActivityRegistrationBinding;
import com.padelclubapp.dataclass.Utente;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;
    private final Handler handler = new Handler();
    private ActivityRegistrationBinding binding;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private static final boolean isAdmin = false;
    private static final boolean isFirstAccess = true;
    private Uri imageUri;
    private Dialog dialog;
    private Double ranking = 0.0;
    private Double reputazione = 5.0;
    private boolean isImageChange = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        storageReference = FirebaseStorage.getInstance("gs://padelclubapp.appspot.com").getReference();
        dialog = new Dialog(RegistrationActivity.this);
        setContentView(binding.getRoot());
        setToolBar();
        setFirebase();
        setDialog();
        goStorage();

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isValidateData(
                        binding.nome.getText().toString(),
                        imageUri,
                        binding.cognome.getText().toString(),
                        binding.email.getText().toString(),
                        binding.password.getText().toString(),
                        binding.confirmPassword.getText().toString(),
                        binding.bio.getText().toString())) {

                    createUser(binding.email.getText().toString(), binding.password.getText().toString());


                } else {
                    binding.errorMessage.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.cancelImage.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View view) {
                binding.profileImage.setImageDrawable(getDrawable(R.drawable.account_circle));
                binding.cancelImage.setVisibility(View.INVISIBLE);
                isImageChange = false;
                imageUri = null;
            }
        });
    }

    private void createUser(String email, String password) {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            uploadFirebase(imageUri);

                        } else {
                        }
                    }
                });
    }

    private void setToolBar() {
        binding.toolbar.toolbarTitle.setText("REGISTRAZIONE");
        binding.toolbar.backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://padelclubapp-default-rtdb.firebaseio.com").getReference();
    }

    private void deleteFields() {
        binding.nome.setText("");
        binding.cognome.setText("");
        binding.email.setText("");
        binding.password.setText("");
        binding.confirmPassword.setText("");
    }

    private boolean checkNameSurname(String name) {
        String namePattern = "^[A-Za-zÀ-ÖØ-öø-ÿ' -]+$";
        Pattern pattern = Pattern.compile(namePattern);
        return !name.isEmpty() && pattern.matcher(name).matches();
    }

    private boolean checkEmail(String email) {
        return !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean checkPassword(String password) {
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#%^&*()\\-_=+])[A-Za-z\\d!@#%^&*()\\-_=+]{8,}$";
        Pattern pattern = Pattern.compile(passwordRegex);
        return !password.isEmpty() && pattern.matcher(password).matches();
    }

    private boolean isEqualPassword(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }

    private boolean isValidateData(String name, Uri imageUri, String surname, String email, String password, String confirmPassword, String bio) {
        return checkNameSurname(name) && (imageUri != null) && checkNameSurname(surname) && checkEmail(email) && checkPassword(password) && checkPassword(confirmPassword) && isEqualPassword(password, confirmPassword) && (!bio.isEmpty());
    }

    private void uploadFirebase(Uri uri) {
        StorageReference fileRef = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String userId = mAuth.getCurrentUser().getUid();

                        Utente user = new Utente(
                                userId,
                                uri.toString(),
                                binding.nome.getText().toString(),
                                binding.cognome.getText().toString(),
                                binding.email.getText().toString(),
                                binding.password.getText().toString(),
                                binding.bio.getText().toString(),
                                ranking,
                                reputazione
                        );
                        mDatabase.child("users").child(userId).setValue(user, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError != null) {
                                    mostraEsitoRegistrazione(false);
                                } else {
                                    mostraEsitoRegistrazione(true);
                                    deleteFields();
                                }
                            }
                        });
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
            Toast.makeText(RegistrationActivity.this, "All permissions granted", Toast.LENGTH_SHORT).show();
            openGallery();
        } else {
            Toast.makeText(RegistrationActivity.this, "Permissions denied", Toast.LENGTH_SHORT).show();
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
            isImageChange = true;
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
    private void mostraEsitoRegistrazione(boolean isSuccesso) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_result);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        ImageView image = dialog.findViewById(R.id.image_result);
        TextView text = dialog.findViewById(R.id.text_result);

        if (isSuccesso) {
            image.setImageResource(R.drawable.success_icon);
            text.setText("Registrazione avvenuta con successo!");
        } else {
            image.setImageResource(R.drawable.error_icon);
            text.setText("Si è verificato un errore durante la registrazione.");
        }

        dialog.show();


        new Handler().postDelayed(() -> {
            dialog.dismiss();
            Intent intent;
            if (isSuccesso)
                intent = new Intent(RegistrationActivity.this, MainActivity.class);
            else
                intent = new Intent(RegistrationActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }, 3000);
    }

}