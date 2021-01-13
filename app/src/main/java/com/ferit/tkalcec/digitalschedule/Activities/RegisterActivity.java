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

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etRegEmail;
    private EditText etRegPassword;
    private EditText etRegConfirmPassword;
    private Button btnRegister;
    private Button btnRegLogin;
    private ProgressBar registerProgress;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        setUpUI();
    }

    private void setUpUI() {
        this.etRegEmail = (EditText) findViewById(R.id.etRegEmail);
        this.etRegPassword = (EditText) findViewById(R.id.etRegPassword);
        this.etRegConfirmPassword = (EditText) findViewById(R.id.etRegConfirmPassword);
        this.btnRegister = (Button) findViewById(R.id.btnRegister);
        this.btnRegLogin = (Button) findViewById(R.id.btnRegisterLogin);
        this.registerProgress = (ProgressBar) findViewById(R.id.regProgress);

        this.btnRegLogin.setOnClickListener(this);
        this.btnRegister.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            sendToFaculty();
        }
    }

    private void sendToFaculty() {
        Intent facultyIntent = new Intent(RegisterActivity.this, FacultyActivity.class);
        startActivity(facultyIntent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case (R.id.btnRegister):
                String regEmail = etRegEmail.getText().toString();
                String regPassword = etRegPassword.getText().toString();
                String regConfPassword = etRegConfirmPassword.getText().toString();

                if (!TextUtils.isEmpty(regEmail) && !TextUtils.isEmpty(regPassword) && !TextUtils.isEmpty(regConfPassword)) {
                    if (regPassword.equals(regConfPassword)) {
                        registerProgress.setVisibility(View.VISIBLE);

                        mAuth.createUserWithEmailAndPassword(regEmail, regPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    sendToFaculty();
                                }
                                else {
                                    String errorMsg = task.getException().getMessage();
                                    Toast.makeText(RegisterActivity.this, "Error: " + errorMsg, Toast.LENGTH_SHORT).show();
                                }
                                registerProgress.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                    else {
                        Toast.makeText(RegisterActivity.this, "Lozinke se ne podudaraju.", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(RegisterActivity.this, "Molimo popunite sva polja.", Toast.LENGTH_SHORT).show();
                }
                break;
            case (R.id.btnRegisterLogin):
                Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
                break;
        }
    }
}
