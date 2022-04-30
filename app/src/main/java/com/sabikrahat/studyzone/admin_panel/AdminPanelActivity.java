package com.sabikrahat.studyzone.admin_panel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.sabikrahat.studyzone.R;
import com.sabikrahat.studyzone.models.User;

import java.util.HashMap;

public class AdminPanelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());

        findViewById(R.id.adminPanelSearchAllUsers).setOnClickListener(view -> startActivity(new Intent(AdminPanelActivity.this, SeeAllUsersActivity.class)));

        findViewById(R.id.adminPanelAddAdminID).setOnClickListener(v -> showChangeRolePopup("Admin"));

        findViewById(R.id.adminPanelMakeGuestID).setOnClickListener(view -> showChangeRolePopup("Guest"));

        findViewById(R.id.adminPanelAddTeacherID).setOnClickListener(view -> showChangeRolePopup("Teacher"));

        findViewById(R.id.adminPanelAddStudent).setOnClickListener(view -> showChangeRolePopup("Student"));

        findViewById(R.id.adminPanelChangeClassLink).setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(AdminPanelActivity.this);
            View v = getLayoutInflater().inflate(R.layout.admin_custom_popup_layout, null);

            final EditText link = v.findViewById(R.id.popupUserID);
            Button cancelButton = v.findViewById(R.id.popupCancelButtonID);
            Button goToButton = v.findViewById(R.id.popupGotoButtonID);

            link.setHint("Class Link");
            link.setInputType(InputType.TYPE_CLASS_TEXT);

            builder.setView(v);

            final AlertDialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });

            goToButton.setOnClickListener(view1 -> {
                String link_text = link.getText().toString().trim();

                if (link_text.isEmpty()) {
                    link.setError("Enter class join link");
                    link.requestFocus();
                    return;
                }

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("ClassLink");

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("classLink", link_text);

                ref.updateChildren(hashMap);

                Toast.makeText(AdminPanelActivity.this, "Class link updated", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            });
            alertDialog.show();
        });
    }

    private void showChangeRolePopup(String updatedRole) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminPanelActivity.this);
        View view = getLayoutInflater().inflate(R.layout.admin_custom_popup_layout, null);

        final EditText user_id = view.findViewById(R.id.popupUserID);
        Button cancelButton = view.findViewById(R.id.popupCancelButtonID);
        Button goToButton = view.findViewById(R.id.popupGotoButtonID);

        user_id.setHint("Enter User Id");
        user_id.setInputType(InputType.TYPE_CLASS_NUMBER);

        builder.setView(view);

        final AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);

        cancelButton.setOnClickListener(view12 -> alertDialog.dismiss());

        goToButton.setOnClickListener(view1 -> {
            String temp_id = user_id.getText().toString().trim();

            if (temp_id.isEmpty()) {
                user_id.setError("Enter the user id");
                user_id.requestFocus();
                return;
            }

            FirebaseDatabase.getInstance().getReference("Users").orderByChild("rid").equalTo(temp_id).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        user.setRole(updatedRole);
                        FirebaseDatabase.getInstance().getReference("Users").child(snapshot.getKey()).setValue(user).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(AdminPanelActivity.this, user.getName() + " added as " + updatedRole, Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            } else {
                                Toast.makeText(AdminPanelActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        });
        alertDialog.show();
    }
}