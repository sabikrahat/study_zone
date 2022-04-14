package com.sabikrahat.studyzone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sabikrahat.studyzone.models.Post;

import java.util.HashMap;

public class EditPostActivity extends AppCompatActivity {


    private EditText edittext;
    private Button done;

    private String PostID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());

        Intent intent = getIntent();
        PostID = intent.getStringExtra("postID");

        edittext = findViewById(R.id.editPostEditText);
        done = findViewById(R.id.editPostEditdDone);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(PostID);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                edittext.setText(snapshot.getValue(Post.class).getDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditPostActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        done.setOnClickListener(v -> {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(PostID);

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("description", edittext.getText().toString());

            ref.updateChildren(hashMap);
            Toast.makeText(EditPostActivity.this, "Post edited!", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(EditPostActivity.this, MainActivity.class));
        });
    }
}