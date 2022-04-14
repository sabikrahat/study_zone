package com.sabikrahat.studyzone.adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sabikrahat.studyzone.EditPostActivity;
import com.sabikrahat.studyzone.R;
import com.sabikrahat.studyzone.ViewProfile;
import com.sabikrahat.studyzone.models.Post;
import com.sabikrahat.studyzone.models.User;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private Context mContext;
    private List<Post> mPost;

    private FirebaseUser firebaseUser;
    private String role;

    public PostAdapter(Context mContext, List<Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Post post = mPost.get(position);

        holder.description.setText(post.getDescription());
        holder.dateTime.setText(post.getDateTime());

        FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("role")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        role = snapshot.getValue().toString();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(mContext, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        publisherInfo(holder.image_profile, holder.username, post.getPublisherId());

        holder.image_profile.setOnClickListener(v -> {
            if (role.equalsIgnoreCase("Admin")) {
                Intent intentShowProfile = new Intent(mContext, ViewProfile.class);
                intentShowProfile.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intentShowProfile.putExtra("targetUID", post.getPublisherId());
                mContext.startActivity(intentShowProfile);
            }
        });

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (role.equalsIgnoreCase("Admin")) {
                    Intent intentShowProfile = new Intent(mContext, ViewProfile.class);
                    intentShowProfile.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intentShowProfile.putExtra("targetUID", post.getPublisherId());
                    mContext.startActivity(intentShowProfile);
                }
            }
        });

        holder.dateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (role.equalsIgnoreCase("Admin")) {
                    Intent intentShowProfile = new Intent(mContext, ViewProfile.class);
                    intentShowProfile.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intentShowProfile.putExtra("targetUID", post.getPublisherId());
                    mContext.startActivity(intentShowProfile);
                }
            }
        });

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(mContext, view);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {

                            case R.id.postEdit:
                                Intent intentPost = new Intent(mContext, EditPostActivity.class);
                                intentPost.putExtra("postID", post.getPostId());
                                intentPost.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                mContext.startActivity(intentPost);
                                return true;

                            case R.id.delete:
                                FirebaseDatabase.getInstance().getReference("Posts").child(post.getPostId())
                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(mContext, "Post deleted!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                return true;

                            case R.id.report:
                                Toast.makeText(mContext, "Will update this feature soon.", Toast.LENGTH_SHORT).show();
                                return true;

                            default:
                                return false;
                        }
                    }
                });
                popupMenu.inflate(R.menu.post_menu);
                if (!post.getPublisherId().equals(firebaseUser.getUid())) {
                    if (!role.equalsIgnoreCase("Admin")) {
                        popupMenu.getMenu().findItem(R.id.postEdit).setVisible(false);
                        popupMenu.getMenu().findItem(R.id.delete).setVisible(false);
                    }
                }
                popupMenu.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView image_profile, like, comment, more;
        private TextView username, dateTime, description, likes, comments;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            more = itemView.findViewById(R.id.more);
            username = itemView.findViewById(R.id.username);
            dateTime = itemView.findViewById(R.id.dateTime);
            description = itemView.findViewById(R.id.description);
        }
    }

    private void publisherInfo(final ImageView image_profile, final TextView username, String userid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(mContext).load(user.getImageURL()).into(image_profile);
                username.setText(user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}