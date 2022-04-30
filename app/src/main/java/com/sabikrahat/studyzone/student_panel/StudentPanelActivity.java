package com.sabikrahat.studyzone.student_panel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sabikrahat.studyzone.R;
import com.sabikrahat.studyzone.models.ClassLink;
import com.sabikrahat.studyzone.teacher_panel.TeacherPanelActivity;

public class StudentPanelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_panel);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());

        findViewById(R.id.classRecord).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StudentPanelActivity.this, VideoListShowActivity.class));
            }
        });

        findViewById(R.id.liveClass).setOnClickListener(view -> FirebaseDatabase.getInstance().getReference("ClassLink").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ClassLink classLink = snapshot.getValue(ClassLink.class);
                if (classLink != null) {
                    Uri classMeetLink = Uri.parse(classLink.getClassLink());
                    Intent intent = new Intent(Intent.ACTION_VIEW, classMeetLink);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StudentPanelActivity.this, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        }));
    }
}