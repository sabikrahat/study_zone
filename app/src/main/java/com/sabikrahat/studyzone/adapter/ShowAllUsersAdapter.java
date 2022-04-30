package com.sabikrahat.studyzone.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sabikrahat.studyzone.R;
import com.sabikrahat.studyzone.ViewProfile;
import com.sabikrahat.studyzone.models.User;

import java.util.List;

public class ShowAllUsersAdapter extends RecyclerView.Adapter<ShowAllUsersAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;

    public ShowAllUsersAdapter(Context mContext, List<User> mUsers) {
        this.mContext = mContext;
        this.mUsers = mUsers;
    }

    @NonNull
    @Override
    public ShowAllUsersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_show_list_item, parent, false);
        return new ShowAllUsersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowAllUsersAdapter.ViewHolder holder, int position) {
        final User user = mUsers.get(position);

        holder.name.setText(user.getName());
        holder.rid.setText("#" + user.getRid());
        try {
            Glide.with(mContext).load(user.getImageURL()).into(holder.image_profile);
        } catch (Exception e) {
            holder.image_profile.setImageResource(R.drawable.ic_person);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intentShowProfile = new Intent(mContext, ViewProfile.class);
            intentShowProfile.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intentShowProfile.putExtra("targetUID", user.getUid());
            mContext.startActivity(intentShowProfile);
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView image_profile;
        private TextView name, rid;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            name = itemView.findViewById(R.id.username_item);
            rid = itemView.findViewById(R.id.rid_number);
        }
    }
}