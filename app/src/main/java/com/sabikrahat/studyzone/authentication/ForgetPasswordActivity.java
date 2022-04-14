package com.sabikrahat.studyzone.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sabikrahat.studyzone.R;

public class ForgetPasswordActivity extends AppCompatActivity {

    private EditText emailEditText;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize Firebase Auth user
        FirebaseUser mUser = mAuth.getCurrentUser();

        if (mUser != null) {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "You're signed out for some security issues.", Toast.LENGTH_SHORT).show();
        }

        emailEditText = findViewById(R.id.resetPasswordActivityEmailEditTextID);

        findViewById(R.id.resetPasswordActivitySignout_and_SigninTextViewID).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(ForgetPasswordActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        findViewById(R.id.resetPasswordActivitySendMailButtonID).setOnClickListener(view -> {
            String email = emailEditText.getText().toString();

            mAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    finish();
                    startActivity(new Intent(ForgetPasswordActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));

                    Toast.makeText(ForgetPasswordActivity.this, "Reset password link sent to your email.", Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(e -> Toast.makeText(ForgetPasswordActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
        });
    }
}