package com.sabikrahat.studyzone.authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.sabikrahat.studyzone.R;
import com.sabikrahat.studyzone.models.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText, confirmPassEditText, nameEditText, phoneEditText;

    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.registerActivityLoginTextID).setOnClickListener(view -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));

        emailEditText = findViewById(R.id.registerActivityEmailID);
        passwordEditText = findViewById(R.id.registerActivityPasswordID);
        confirmPassEditText = findViewById(R.id.registerActivityConfirmPasswordID);
        nameEditText = findViewById(R.id.registerActivityNameID);
        phoneEditText = findViewById(R.id.registerActivityPhoneID);

        findViewById(R.id.registerActivityRegisterButtonID).setOnClickListener(view -> registerUser());
    }

    private void registerUser() {
        String email = emailEditText.getText().toString().trim();
        String pwd = passwordEditText.getText().toString();
        String confirmPwd = confirmPassEditText.getText().toString();
        String name = nameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();

        //checking the validity of the email
        if (email.isEmpty()) {
            emailEditText.setError("Enter your email address");
            emailEditText.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Enter a valid email address");
            emailEditText.requestFocus();
            return;
        }

        //checking the validity of the password
        if (pwd.isEmpty()) {
            passwordEditText.setError("Enter a password");
            passwordEditText.requestFocus();
            return;
        }

        if (confirmPwd.isEmpty()) {
            confirmPassEditText.setError("Enter a password");
            confirmPassEditText.requestFocus();
            return;
        }

        if (pwd.length() < 6) {
            passwordEditText.setError("Minimum length of a password should be 6");
            passwordEditText.requestFocus();
            confirmPassEditText.setError("Minimum length of a password should be 6");
            confirmPassEditText.requestFocus();
            passwordEditText.setText("");
            confirmPassEditText.setText("");
            return;
        }

        if (confirmPwd.length() < 6) {
            passwordEditText.setError("Minimum length of a password should be 6");
            passwordEditText.requestFocus();
            confirmPassEditText.setError("Minimum length of a password should be 6");
            confirmPassEditText.requestFocus();
            passwordEditText.setText("");
            confirmPassEditText.setText("");
            return;
        }

        if (!pwd.equals(confirmPwd)) {
            passwordEditText.setError("Password doesn't match");
            passwordEditText.requestFocus();
            confirmPassEditText.setError("Password doesn't match");
            confirmPassEditText.requestFocus();
            passwordEditText.setText("");
            confirmPassEditText.setText("");
            return;
        }


        //checking the validity of the full name
        if (name.isEmpty()) {
            nameEditText.setError("Enter your name");
            nameEditText.requestFocus();
            return;
        }

        //checking the validity of the phone number
        if (phone.isEmpty()) {
            phoneEditText.setError("Enter your phone number");
            phoneEditText.requestFocus();
            return;
        }

        if (!Patterns.PHONE.matcher(phone).matches()) {
            phoneEditText.setError("Enter a valid phone number");
            phoneEditText.requestFocus();
            return;
        }

        if (phone.length() < 9 || phone.length() > 12) {
            phoneEditText.setError("Enter a valid phone number");
            phoneEditText.requestFocus();
            return;
        }

        progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setMessage("Creating your Account. Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                String imageUrl = "https://firebasestorage.googleapis.com/v0/b/study-zone-sabikrahat-72428.appspot.com/o/sample_pic.jpg?alt=media&token=59eaf687-01cf-42ce-9791-53fb445c2f03";

                Random rnd = new Random();
                int tempId = rnd.nextInt(999999);
                String rid = String.valueOf(tempId);

                SimpleDateFormat sdf = new SimpleDateFormat("E, dd MMM yyyy 'at' hh:mm a");
                String currentDateAndTime = sdf.format(new Date());

                User user = new User(mAuth.getCurrentUser().getUid(), rid, email, name, phone, imageUrl, "Guest", "true", currentDateAndTime);

                FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid()).setValue(user).addOnCompleteListener(task12 -> {
                    FirebaseUser mUser = mAuth.getCurrentUser();

                    //set display name
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                    mUser.updateProfile(profileUpdates).addOnCompleteListener(task1 -> {
                        if (!task1.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Error: " + task1.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                    emailEditText.setText("");
                    passwordEditText.setText("");
                    confirmPassEditText.setText("");
                    nameEditText.setText("");
                    phoneEditText.setText("");

                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Register completed.", Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                });
            } else {
                // If sign in fails, display a message to the user.
                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                    //If email already registered.
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Email already registered.", Toast.LENGTH_LONG).show();
                } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                    //If email are in incorrect format
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Email format incorrect.", Toast.LENGTH_LONG).show();
                } else if (task.getException() instanceof FirebaseAuthWeakPasswordException) {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Password is too weak.", Toast.LENGTH_LONG).show();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}