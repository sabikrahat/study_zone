package com.sabikrahat.studyzone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.sabikrahat.studyzone.R;
import com.sabikrahat.studyzone.models.VideoModel;

import java.util.ArrayList;

public class VideoListShowAdapter extends ArrayAdapter<VideoModel> {

    private final Context context;
    private final ArrayList<VideoModel> values;


    public VideoListShowAdapter(@NonNull Context context, @NonNull ArrayList<VideoModel> objects) {
        super(context, -1, objects);
        this.context = context;
        this.values = objects;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.video_list_item, parent, false);

        TextView vTitle = rowView.findViewById(R.id.videoTitle);
        TextView vUrl = rowView.findViewById(R.id.videoUrl);

        vTitle.setText(values.get(position).getTitle());
        vUrl.setText(values.get(position).getLink());
        return rowView;
    }
}
