package com.teamawesome.android2application;

import android.content.Intent;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;

    private boolean logInSuccesfull;

    TextView tvStatus;
    TextView tvDetails;
    EditText etEmail;
    EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logInSuccesfull = false;
        setContentView(R.layout.activity_register);
        auth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener(){
          @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
//              if(currentUser!=null){
//
//              } else {
//
//              }
          }
        };

        tvStatus = (TextView)findViewById(R.id.tvStatus);
        tvDetails = (TextView)findViewById(R.id.tvDetails);
        etEmail = (EditText) findViewById(R.id.tbRegisterEmail);
        etPassword = (EditText) findViewById(R.id.tbRegisterPassword);

        Button btnRegister = (Button)findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount(etEmail.getText().toString(), etPassword.getText().toString());
            }
        });
    }




    @Override
    public void onStart(){
        super.onStart();
        auth.addAuthStateListener(authListener);
        FirebaseUser currentUser = auth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    public void onStop(){
        super.onStop();
        if(auth != null){
            auth.removeAuthStateListener(authListener);
        }
    }


    public void updateUI(FirebaseUser user){
        //hideProgressDialog();
        if(user!= null)
        {
            tvStatus.setText("user is good " + user.getEmail() + " " + user.isEmailVerified());
            tvDetails.setText("bueno " + user.getUid());
        }
        else
        {

            tvStatus.setText("usero logo outo");
            tvDetails.setText(null);
        }
    }

    private void createAccount(String email, String password)
    {
        Log.d("AccountCreation", "MethodFired");
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.d("AccountCreation", "createUserWithEmail:onComplete " + task.isSuccessful());
                if(task.isSuccessful())
                {
                    FirebaseUser user = auth.getCurrentUser();
                    updateUI(user);
                    logInSuccesfull = true;
                    startMain();
                }
                else
                {
                    updateUI(null);
                }
            }
        });
    }

    private void startMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void registerButtonClicked(){
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        if (!email.contains("@")){
            tvStatus.setText("Email address not valid!");
        } else{
            tvStatus.setText("Something went wrong, please try again");
        }


        createAccount(email, password);
        if (!logInSuccesfull){

        } else{
            Log.d("Account", "start activity ");

        }
    }


}




























