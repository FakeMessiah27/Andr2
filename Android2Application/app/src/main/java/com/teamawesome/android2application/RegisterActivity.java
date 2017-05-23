package com.teamawesome.android2application;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    TextView tvStatus;
    TextView tvDetails;
    EditText etEmail;
    EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        auth = FirebaseAuth.getInstance();

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

        FirebaseUser currentUser = auth.getCurrentUser();
        updateUI(currentUser);
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
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    FirebaseUser user = auth.getCurrentUser();
                    updateUI(user);
                }
                else
                {
                    updateUI(null);
                }
            }
        });
    }

    private void signIn(String email, String password)
    {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    FirebaseUser user = auth.getCurrentUser();
                    updateUI(user);
                }
                else
                {
                    updateUI(null);
                    tvStatus.setText("user no bueno");
                }

            }
        });
    }

    private void signOut()
    {
        auth.signOut();
        updateUI(null);
    }
}




























