package com.sabikrahat.studyzone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.sabikrahat.studyzone.authentication.LoginActivity;
import com.sabikrahat.studyzone.authentication.RegisterActivity;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // for disable force dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            //check user is available or disable
            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            finish();
        }

        findViewById(R.id.welcomeActivityLoginButtonId).setOnClickListener(view -> startActivity(new Intent(WelcomeActivity.this, LoginActivity.class)));

        findViewById(R.id.welcomeActivityRegisterButtonId).setOnClickListener(view -> startActivity(new Intent(WelcomeActivity.this, RegisterActivity.class)));
    }
}