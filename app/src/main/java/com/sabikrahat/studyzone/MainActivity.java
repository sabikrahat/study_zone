package com.sabikrahat.studyzone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sabikrahat.studyzone.adapter.PostAdapter;
import com.sabikrahat.studyzone.authentication.ForgetPasswordActivity;
import com.sabikrahat.studyzone.models.Post;
import com.sabikrahat.studyzone.models.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private View mView;
    private ImageView drawerProfileImage;
    private TextView drawerTextView;

    private FirebaseAuth mAuth;
    private ScrollView scrollView;
    private EditText postWritten;
    private Button postButton;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private PostAdapter postAdapter;
    private List<Post> postLists;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.application_nav_view_ID);

        toolbar = findViewById(R.id.toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mView = navigationView.getHeaderView(0);
        drawerProfileImage = mView.findViewById(R.id.drawer_profile);
        drawerTextView = mView.findViewById(R.id.drawer_profile_name);

        scrollView = findViewById(R.id.ScrollID);
        postWritten = findViewById(R.id.applicationPostEditTextID);
        postButton = findViewById(R.id.applicationPostButtonID);
        recyclerView = findViewById(R.id.applicationRecyclerViewID);
        progressBar = findViewById(R.id.progress_circular);

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        mAuth = FirebaseAuth.getInstance();

        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                Menu menu = navigationView.getMenu();
                if (!(user.getRole().equals("Admin")) && !(user.getRole().equals("Teacher"))) {
                    //hide or show
                    menu.findItem(R.id.admin_panel).setVisible(false);
                    scrollView.setVisibility(View.GONE);
                    postWritten.setVisibility(View.GONE);
                    postButton.setVisibility(View.GONE);
                    if ((user.getRole().equals("Student")) || ((user.getRole().equals("Guest")))) {
                        //hide or show
                        menu.findItem(R.id.teacher_panel).setVisible(false);
                    }
                }
                if (user.getRole().equalsIgnoreCase("Teacher")) {
                    //hide or show
                    menu.findItem(R.id.admin_panel).setVisible(false);
                }
                try {
                    Glide.with(MainActivity.this).load(user.getImageURL()).into(drawerProfileImage);
                } catch (Exception e) {
                    drawerProfileImage.setImageResource(R.drawable.ic_person);
                }
                drawerTextView.setText(user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        drawerProfileImage.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, ViewProfile.class).putExtra("targetUID", mAuth.getCurrentUser().getUid())));

        drawerTextView.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, ViewProfile.class).putExtra("targetUID", mAuth.getCurrentUser().getUid())));

        postButton.setOnClickListener(v -> {
            if (postWritten.getText().toString().equals("")) {
                Toast.makeText(MainActivity.this, "You can't post an empty content.", Toast.LENGTH_SHORT).show();
                return;
            } else {
                postContent();
            }
        });

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        postLists = new ArrayList<>();
        postAdapter = new PostAdapter(getApplicationContext(), postLists);
        recyclerView.setAdapter(postAdapter);

        readPosts();
    }

    private void postContent() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Posting");
        progressDialog.setCancelable(false);
        progressDialog.show();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

        SimpleDateFormat sdf = new SimpleDateFormat("E, dd MMM yyyy 'at' hh:mm a");
        String currentDateAndTime = sdf.format(new Date());

        String postId = reference.push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("postId", postId);
        hashMap.put("dateTime", currentDateAndTime);
        hashMap.put("description", postWritten.getText().toString());
        hashMap.put("publisherId", FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.child(postId).setValue(hashMap);

        progressDialog.dismiss();
        postWritten.setText("");
        Toast.makeText(MainActivity.this, "Posted", Toast.LENGTH_LONG).show();
    }

    private void readPosts() {
        progressBar.setVisibility(View.VISIBLE);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postLists.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    postLists.add(post);
                }
                postAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                //TODO: Home

                item.setCheckable(false);
                break;

            case R.id.student_panel:
                //TODO: Student Panel
                Toast.makeText(this, "Student Panel", Toast.LENGTH_SHORT).show();
                item.setCheckable(false);
                break;

            case R.id.teacher_panel:
                //TODO: Teacher Panel
                Toast.makeText(this, "Teacher Panel", Toast.LENGTH_SHORT).show();
                item.setCheckable(false);
                break;

            case R.id.admin_panel:
                //TODO: Admin Panel
                Toast.makeText(this, "Admin Panel", Toast.LENGTH_SHORT).show();
                item.setCheckable(false);
                break;

            case R.id.my_profile:
                //TODO: My Profile
                startActivity(new Intent(MainActivity.this, ViewProfile.class).putExtra("targetUID", mAuth.getCurrentUser().getUid()));

                item.setCheckable(false);
                break;

            case R.id.forgetPassword:
                //TODO: Reset Password
                finish();
                startActivity(new Intent(MainActivity.this, ForgetPasswordActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));

                item.setCheckable(false);
                break;

            case R.id.logout:
                //TODO: Logout
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, WelcomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));

                item.setCheckable(false);
                break;

            case R.id.contact:
                //TODO: Contact Us
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                phoneIntent.setData(Uri.parse("tel:" + "+8801647629698"));
                if (phoneIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(phoneIntent);
                }

                item.setCheckable(false);
                break;

            case R.id.feedback:
                //TODO: Share
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("plain/text");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"sabikrahat72428@gmail.com"});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Study Zone Feedback. [Student id:" + user.getRid() + "]");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Name: " + user.getName());
                startActivity(Intent.createChooser(emailIntent, ""));

                item.setCheckable(false);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to Exit?")
                    .setNegativeButton("No", null)
                    .setPositiveButton("Yes", (dialogInterface, i) -> finishAffinity()).show();
        }
    }
}
