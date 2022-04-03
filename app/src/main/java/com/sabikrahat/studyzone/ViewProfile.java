package com.sabikrahat.studyzone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sabikrahat.studyzone.adapter.ViewProfilePostAdapter;
import com.sabikrahat.studyzone.models.Post;
import com.sabikrahat.studyzone.models.User;

import java.util.ArrayList;
import java.util.List;

public class ViewProfile extends AppCompatActivity {

    private ImageView profilePic;
    private TextView name, email, phone, id, batch, available;
    private RecyclerView recyclerView;

    private String targetUID;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private ViewProfilePostAdapter viewProfilePostAdapter;
    private List<Post> postLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());

        Intent intent = getIntent();
        targetUID = intent.getStringExtra("targetUID");

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        profilePic = findViewById(R.id.userProfilePicID);
        name = findViewById(R.id.userProfileNameID);
        email = findViewById(R.id.userProfileEmailID);
        phone = findViewById(R.id.userProfilePhoneID);
        id = findViewById(R.id.userProfileStudentIdID);
        batch = findViewById(R.id.userProfileBatchID);
        available = findViewById(R.id.availableTextID);
        recyclerView = findViewById(R.id.userProfileRecyclerViewID);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(targetUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                try {
                    Glide.with(ViewProfile.this).load(user.getImageURL()).into(profilePic);
                } catch (Exception e) {
                    profilePic.setImageResource(R.drawable.ic_person);
                }
                name.setText(user.getName());
                email.setText(user.getEmail());
                phone.setText(user.getPhone());
                id.setText(user.getRid() + " (App id)");
                batch.setText(user.getBatch());
                if ((user.getStatus()).equals("true")) {
                    available.setText("Active");
                    available.setVisibility(View.GONE);
                } else {
                    available.setText("Disable");
                    available.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewProfile.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        phone.setOnClickListener(v -> {
            if (!mUser.getUid().equals(targetUID)) {
                Intent intent1 = new Intent(Intent.ACTION_DIAL);
                intent1.setData(Uri.parse("tel:" + phone.getText().toString()));
                if (intent1.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent1);
                }
            }
        });

        if (!mUser.getUid().equals(targetUID)) {
            available.setVisibility(View.VISIBLE);
        } else {
            available.setVisibility(View.GONE);
        }

        //My Posts
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ViewProfile.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        postLists = new ArrayList<>();
        viewProfilePostAdapter = new ViewProfilePostAdapter(getApplicationContext(), postLists);
        recyclerView.setAdapter(viewProfilePostAdapter);

        readPosts();
    }

    private void readPosts() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postLists.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    if ((post.getPublisherId()).equals(targetUID)) {
                        postLists.add(post);
                    }
                }
                viewProfilePostAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_profile_menu, menu);

        if (!mUser.getUid().equals(targetUID)) {
            MenuItem item = menu.findItem(R.id.user_profile_edit);
            item.setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.user_profile_edit) {
            //TODO: Edit your profile access
            Intent intentShowProfile = new Intent(ViewProfile.this, EditProfile.class);
            startActivity(intentShowProfile);
        }
        return super.onOptionsItemSelected(item);
    }
}