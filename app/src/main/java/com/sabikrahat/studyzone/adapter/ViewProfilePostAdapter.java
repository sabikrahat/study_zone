package com.sabikrahat.studyzone.adapter;

import android.app.AlertDialog;
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
import com.sabikrahat.studyzone.models.Post;
import com.sabikrahat.studyzone.models.User;

import java.util.List;

public class ViewProfilePostAdapter extends RecyclerView.Adapter<ViewProfilePostAdapter.ViewHolder>  {
    private Context mContext;
    private List<Post> mPost;

    private FirebaseUser firebaseUser;
    private String role;

    public ViewProfilePostAdapter(Context mContext, List<Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public ViewProfilePostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);
        return new ViewProfilePostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewProfilePostAdapter.ViewHolder holder, int position) {
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


        holder.more.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(mContext, view);
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {

                    case R.id.postEdit:
                        Intent intenPost = new Intent(mContext, EditPostActivity.class);
                        intenPost.putExtra("postID", post.getPostId());
                        intenPost.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intenPost);
                        return true;

                    case R.id.delete:
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setMessage("Are you sure you want to delete this post?")
                                .setNegativeButton("No", null)
                                .setPositiveButton("Yes", (dialogInterface, i) -> {
                                    FirebaseDatabase.getInstance().getReference("Posts").child(post.getPostId())
                                            .removeValue().addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(mContext, "Post deleted!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }).show();

                        return true;

                    case R.id.report:
                        Toast.makeText(mContext, "Will update this feature soon.", Toast.LENGTH_SHORT).show();
                        return true;

                    default:
                        return false;
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
        });
    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView image_profile, more;
        private TextView username, dateTime, description;

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

