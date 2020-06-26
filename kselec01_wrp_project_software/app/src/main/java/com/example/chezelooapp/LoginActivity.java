package com.example.chezelooapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "Login";
    //FIREBASE
    private FirebaseAuth mAuth;
    private Button mButton;
    private EditText mEmail;
    private EditText mPasssword;
    private TextView mRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
        // Displaying login screen
        setContentView(R.layout.activity_login);
        //FIREBASE
        mAuth = FirebaseAuth.getInstance();
        // Widgets
        mButton = findViewById(R.id.login_btn);
        mEmail = findViewById(R.id.login_email);
        mPasssword = findViewById(R.id.login__paswword);
        mRegister = findViewById(R.id.login_signup);
        //BUNDLE
        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null) {
            mEmail.setText(mBundle.getString("email"));
        }

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });


        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = mEmail.getText().toString();
                String password = mPasssword.getText().toString();
                if (!userName.isEmpty() && !password.isEmpty()) {
                    login(userName,password);
                } else {
                    Toast.makeText(getApplicationContext(), "Please compllete all fields", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Checks if the user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
           launchHome();
        }
    }


    private void login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            // Get User Object from DBon
                            FirebaseUser user = mAuth.getCurrentUser();
                            launchHome();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
    }

    // Starts Home Activity
    private void launchHome(){
        Intent intent=new Intent(this,HomeActivity.class);
        //TODO: put bundle
        startActivity(intent);
        finish();
    }
}


