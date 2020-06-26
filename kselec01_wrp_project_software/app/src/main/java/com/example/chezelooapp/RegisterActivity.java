package com.example.chezelooapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.chezelooapp.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    //FIREBASE
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private StorageReference mStorageRef;
    private String mImageUrl;
    private Bitmap mUserImageBitmap = null;

    private static final String TAG = "SignIn";
    private ImageView mUserImg;
    private Button mBack;
    private Button mRegister;
    private EditText mFirstName;
    private EditText mLastName;
    private EditText mEmail;
    private EditText mPassword;
    ProgressBar mProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //STORAGE
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        //FIELDS
        mUserImg = findViewById(R.id.sign_image);
        mFirstName = findViewById(R.id.reg_fName);
        mLastName = findViewById(R.id.reg_lName);
        mEmail = findViewById(R.id.reg_email);
        mPassword = findViewById(R.id.reg__password);
        mRegister = findViewById(R.id.reg_button);
        mProgress = findViewById(R.id.signIn_progressBar);
        mBack = findViewById(R.id.reg_back);

        mUserImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
            }
        });
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //Register
        mRegister.setOnClickListener(v -> {
            //TODO: name in realDb
            String fName = mFirstName.getText().toString();
            String lName = mLastName.getText().toString();
            String email = mEmail.getText().toString();
            String password = mPassword.getText().toString();
            String name = fName + " " + lName;
            if (!fName.isEmpty() && !lName.isEmpty() && !password.isEmpty() && !email.isEmpty() && mUserImageBitmap != null) {
                signIn(name, email, password);
            } else {
                Toast.makeText(getApplicationContext(), "Please complete all field an add a user Image", Toast.LENGTH_LONG).show();
            }
        });
    }


    //Register
    private void signIn(String name, String email, String password) {
        final String e = email;
        mProgress.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mProgress.setVisibility(View.GONE);
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(getApplicationContext(), "Succesfully register", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "createUserWithEmail:success");
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            intent.putExtra("email", e);

                            FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            String UID;
                            try {
                                UID = currentFirebaseUser.getUid();
                                uploadImgToStorage(name, email, UID);
                            } catch (Exception e) {
                                Log.e(TAG, "onComplete: Exception UID " + e.getMessage());
                            }
                            startActivity(intent);
                            finish();
                        } else {
                            mProgress.setVisibility(View.GONE);
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getApplicationContext(), "Login failed" + task.getException() + String.valueOf(task.getException()), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    //CHECK PERMISSION
    private static final int PERMISSION_CODE = 1000;

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //permission already granted
                loadImage();
            } else {
                String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
                //permission not enable, request it
                requestPermissions(permission, PERMISSION_CODE);

            }
        } else {
            //system os < marshmallow
            loadImage();
        }
    }

    //ON PERMISSION REQUEST
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadImage();
                } else {
                    Toast.makeText(this, "Cannot access your images", Toast.LENGTH_LONG).show();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //LOAD IMAGE
    private final int PICK_IMAGE_CODE = 2222;

    private void loadImage() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(galleryIntent, PICK_IMAGE_CODE);
    }

    //ONACTIVITY RESULT
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_CODE && resultCode == RESULT_OK && data != null) {
            try {
                Uri imgUri = data.getData();
                mUserImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgUri);
                mUserImg.setImageBitmap(mUserImageBitmap);
            } catch (Exception exception) {
                Log.d(TAG, "onActivityResult: " + exception.toString());
            }
        }
    }

    //UPLOAD IMG TO FIREBASE STORAGE and FIELD TO DATABSE
    private void uploadImgToStorage(String name, String email, String imgUrlUID) {
        if (mUserImageBitmap != null) {
            byte[] byteImg = convertToByte(mUserImageBitmap);
            StorageReference mReference = mStorageRef.child("users/" + imgUrlUID);
            Log.d(TAG, "uploadImgToStorage: before");
            mReference.putBytes(byteImg).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task downloadUrl = Objects.requireNonNull(Objects.requireNonNull(taskSnapshot.getMetadata()).getReference()).getDownloadUrl();
                    final Task task = downloadUrl.addOnSuccessListener(o -> {
                        mImageUrl = o.toString();
                        myRef.child("Users").push().setValue(new User(name,email, mImageUrl,imgUrlUID));
                    });
                }
            });
        }
    }

    public byte[] convertToByte(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

}
