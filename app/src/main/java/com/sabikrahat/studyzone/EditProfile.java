package com.sabikrahat.studyzone;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sabikrahat.studyzone.models.User;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity {

    private CircleImageView profilePic;
    private TextView editPic;
    private EditText name, phone;
    private Button save;

    private FirebaseAuth mAuth;
    private String RID = "";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());

        profilePic = findViewById(R.id.image_profile);
        name = findViewById(R.id.fullname);
        phone = findViewById(R.id.phone_number);
        save = findViewById(R.id.save_changes);

        mAuth = FirebaseAuth.getInstance();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                RID = user.getRid();
                try {
                    Glide.with(EditProfile.this).load(user.getImageURL()).into(profilePic);
                } catch (Exception e) {
                    profilePic.setImageResource(R.drawable.ic_person);
                }
                name.setText(user.getName());
                phone.setText(user.getPhone());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditProfile.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.tv_change).setOnClickListener(view -> imagePickAndProcess());

        save.setOnClickListener(v -> saveData());
    }
    
    public void saveData() {
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", name.getText().toString());
        hashMap.put("phone", phone.getText().toString());

        databaseReference1.updateChildren(hashMap);

//                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Rid").child(RID);
//
//                HashMap<String, Object> hashMap_R = new HashMap<>();
//                hashMap_R.put("name", name.getText().toString());
//                hashMap_R.put("phoneNumber", phone.getText().toString());
//
//                databaseRef.updateChildren(hashMap_R);
        Toast.makeText(EditProfile.this, "Data updated successfully.", Toast.LENGTH_SHORT).show();

        finish();
        startActivity(new Intent(EditProfile.this, MainActivity.class));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void imagePickAndProcess() {
        boolean pick = true;
        if (pick) {
            if (!checkCameraPermission()) {
                System.out.println("Camera permission not granted");
                requestCameraPermission();
            } else {
                pickImage();
            }
        } else {
            if (!checkStoragePermission()) {
                requestStoragePermission();
            } else {
                pickImage();
            }
        }
    }

    private boolean checkCameraPermission() {
        boolean cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean writeStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        System.out.println("Camera permission: " + cameraPermission);
        return cameraPermission && writeStoragePermission;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestCameraPermission() {
        System.out.println("Requesting camera permission");
        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
    }

    private boolean checkStoragePermission() {
        System.out.println("Checking storage permission");
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestStoragePermission() {
        System.out.println("Requesting storage permission");
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
    }

    private void pickImage() {
        Toast.makeText(this, "Will updated later.", Toast.LENGTH_SHORT).show();
    }
}