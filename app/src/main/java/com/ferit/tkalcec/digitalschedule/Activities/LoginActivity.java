package com.ferit.tkalcec.digitalschedule.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ferit.tkalcec.digitalschedule.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText etLoginEmail;
    private EditText etLoginPassword;
    private Button btnLogin;
    private Button btnLoginRegister;
    private ProgressBar loginProgress;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        setUpUI();
    }

    private void setUpUI() {
        this.etLoginEmail = (EditText) findViewById(R.id.etLoginEmail);
        this.etLoginPassword = (EditText) findViewById(R.id.etLoginPassword);
        this.btnLogin = (Button) findViewById(R.id.btnLogin);
        this.btnLoginRegister = (Button) findViewById(R.id.btnLoginRegister);
        this.loginProgress = (ProgressBar) findViewById(R.id.loginProgress);

        this.btnLogin.setOnClickListener(this);
        this.btnLoginRegister.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null) {
            sendToFaculty();
        }
    }

    private void sendToFaculty() {
        Intent facultyIntent = new Intent(LoginActivity.this, FacultyActivity.class);
        startActivity(facultyIntent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case (R.id.btnLogin):
                String loginEmail = etLoginEmail.getText().toString();
                String loginPassword = etLoginPassword.getText().toString();

                if(!TextUtils.isEmpty(loginEmail) && !TextUtils.isEmpty((loginPassword))) {
                    loginProgress.setVisibility(View.VISIBLE);

                    mAuth.signInWithEmailAndPassword(loginEmail, loginPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                sendToFaculty();
                            }
                            else {
                                String errorMsg = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this, "Error: " + errorMsg, Toast.LENGTH_SHORT).show();
                            }

                            loginProgress.setVisibility(View.INVISIBLE);
                        }
                    });
                }
                else {
                    Toast.makeText(LoginActivity.this, "Unesite email adresu i lozinku.", Toast.LENGTH_SHORT).show();
                }

                break;

            case (R.id.btnLoginRegister):
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
                finish();
            break;

        }
    }
}
