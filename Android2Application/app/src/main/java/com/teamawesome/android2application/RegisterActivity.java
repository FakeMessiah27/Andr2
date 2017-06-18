package com.teamawesome.android2application;

import android.content.Intent;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    DatabaseReference RootRef;
    DatabaseReference usernameRef;

    TextView tvStatus;
    TextView tvDetails;
    EditText etEmail;
    EditText etPassword;
    EditText etUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        RootRef = FirebaseDatabase.getInstance().getReference();

        tvStatus = (TextView)findViewById(R.id.tvStatus);
        tvDetails = (TextView)findViewById(R.id.tvDetails);
        etEmail = (EditText) findViewById(R.id.tbRegisterEmail);
        etPassword = (EditText) findViewById(R.id.tbRegisterPassword);
        etUsername = (EditText) findViewById(R.id.tbUsername);

//        Button btnRegister = (Button)findViewById(R.id.btnRegister);
//        btnRegister.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                createAccount(etEmail.getText().toString(), etPassword.getText().toString());
//            }
//        });
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


//    public void updateUI(FirebaseUser user){
//        //hideProgressDialog();
//        if(user!= null)
//        {
//            tvStatus.setText("user is good " + user.getEmail() + " " + user.isEmailVerified());
//            tvDetails.setText("bueno " + user.getUid());
//        }
//        else
//        {
//            tvStatus.setText("usero logo outo");
//            tvDetails.setText(null);
//        }
//    }

    private void createAccount(String email, String password)
    {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.d("AccountCreation", "createUserWithEmail:onComplete " + task.isSuccessful());
                if(task.isSuccessful())
                {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            usernameRef = RootRef.child("locations").child(auth.getCurrentUser().getUid()).child("username");
                            usernameRef.setValue(etUsername.getText().toString());
                        }
                    }).start();
                    startMain();
                }
                else
                {
                    tvStatus.setText("Something went wrong, please try again.");
                }
            }
        });
    }

    private void startMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("username", etUsername.getText().toString());
        startActivity(intent);
    }

    public void registerButtonClicked(View v) {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String username = etUsername.getText().toString();

        if (!email.contains("@")){
            tvStatus.setText("Email address not valid!");
        }
        else if (TextUtils.isEmpty(password) || password.length() < 7) {
            tvStatus.setText("Password must be at least 7 characters long!");
        }
        else if (TextUtils.isEmpty(email)) {
            tvStatus.setText("Email field cannot be empty!");
        }
        else if (TextUtils.isEmpty(username) || username.length() < 7) {
            tvStatus.setText("Username must be at least 7 characters long!");
        }
        else {
            createAccount(email, password);
        }
    }
}




























