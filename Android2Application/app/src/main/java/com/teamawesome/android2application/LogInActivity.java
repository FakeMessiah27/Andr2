package com.teamawesome.android2application;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.teamawesome.android2application.R.id.tvStatus;

public class LogInActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;

    EditText etEmail;
    EditText etPassword;
    TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        auth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
//                if(currentUser!=null){
//
//                } else {
//
//                }
            }
        };

        etEmail = (EditText) findViewById(R.id.etLoginEmail);
        etPassword = (EditText) findViewById(R.id.etLoginPassword);
        tvStatus = (TextView) findViewById(R.id.tvStatus);
    }

    @Override
    public void onStart(){
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop(){
        super.onStop();
        if(auth != null){
            auth.removeAuthStateListener(authListener);
        }
    }

    public void logIn(View v) {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if (!email.contains("@")){
            tvStatus.setText("Email address not valid!");
        }
        else {
            signIn(email, password);
        }
    }

    public void register(View v){
        Intent intent = new Intent(LogInActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    private void signIn(String email, String password)
    {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("AuthSingIn", "singInWithEmail:onComplete" + task.isSuccessful());
                if(task.isSuccessful())
                {
                    Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                else
                {
                    tvStatus.setText("Login failed. Please try again.");
                }
            }
        });
    }
}
