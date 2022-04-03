package com.sabikrahat.studyzone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sabikrahat.studyzone.models.User;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private View mView;
    private ImageView drawerProfileImage;
    private TextView drawerTextView;

    private FirebaseAuth mAuth;

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

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        mAuth = FirebaseAuth.getInstance();

        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
//                if (!(user.getRole().equals("Admin"))) {
//                    //hide or show
//                    Menu menu = navigationView.getMenu();
//                    menu.findItem(R.id.admin_panel).setVisible(false);
//                    scrollView.setVisibility(View.GONE);
//                    postWritten.setVisibility(View.GONE);
//                    postButton.setVisibility(View.GONE);
//                    if ((user.getStatus().equals("Student")) || ((user.getStatus().equals("Guest")))) {
//                        //hide or show
//                        menu.findItem(R.id.teacher_panel).setVisible(false);
//                    }
//                }
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

                item.setCheckable(false);
                break;

            case R.id.teacher_panel:
                //TODO: Teacher Panel

                item.setCheckable(false);
                break;

            case R.id.admin_panel:
                //TODO: Admin Panel

                item.setCheckable(false);
                break;

            case R.id.my_profile:
                //TODO: My Profile
                Intent intentShowProfile = new Intent(MainActivity.this, ViewProfile.class);
                intentShowProfile.putExtra("targetUID", mAuth.getCurrentUser().getUid());
                startActivity(intentShowProfile);

                item.setCheckable(false);
                break;

            case R.id.forgetPassword:
                //TODO: Reset Password
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

                item.setCheckable(false);
                break;

            case R.id.feedback:
                //TODO: Share

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
