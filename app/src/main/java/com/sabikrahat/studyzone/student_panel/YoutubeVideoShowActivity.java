package com.sabikrahat.studyzone.student_panel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.sabikrahat.studyzone.R;
import com.sabikrahat.studyzone.adapter.VideoListShowAdapter;
import com.sabikrahat.studyzone.models.VideoModel;

import java.util.ArrayList;

public class YoutubeVideoShowActivity extends AppCompatActivity {

    private String title, url;
    private TextView details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_video_show);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        url = intent.getStringExtra("link");

        details = findViewById(R.id.textDetails);
        details.setText(title);
        String videoId;
        if(url.contains("watch?v=")){
            videoId = url.split("watch?v=")[1];
        } else if(url.contains("youtu.be/")){
            videoId = url.split("youtu.be/")[1];
        } else {
            videoId = url;
        }


        YouTubePlayerView youTubePlayerView = findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(youTubePlayerView);

        try {
            youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                    youTubePlayer.loadVideo(videoId, 0);
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Url expired! Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}