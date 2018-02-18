package com.example.teju.u_and_e;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

public class VideoPlayerActivity extends Activity {
    boolean isPaused = false;
    VideoView vvView;
    Uri vidUri;
    SeekBar seekBar;
    int totalDuration = 0;
    Handler seekHandler = new Handler();

    boolean musicControlButtonDisplayed = false;
    ImageView ivPlay;
    ImageView ivPause;

    public void getTotalDuration() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                totalDuration = vvView.getDuration();
                seekBar.setMax(totalDuration);
                Log.d("<><><>", "total duration" + totalDuration);
                TextView tvTotalTime = findViewById(R.id.tvTotalTime);
                int minutes = (int) ((totalDuration / 1000) / 60);
                int seconds = (int) ((totalDuration / 1000) % 60);
                Log.d("<><><>", "Minutes" + minutes + "seconds" + seconds);
                tvTotalTime.setText(minutes + ":" + seconds);
            }
        });
    }

    public void playVideo() {

        vvView.setVideoURI(vidUri);
        vvView.requestFocus();
        vvView.start();

        isPaused = false;

        Log.d("<><><>", "RunnableInplayVideo " + vvView.getDuration());
        seekBar.setEnabled(true);

        vvView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                seekBar.setEnabled(false);
                isPaused = true;
                ivPause.setVisibility(View.INVISIBLE);
                ivPlay.setVisibility(View.VISIBLE);
                finish();
            }
        });

        runnable.run();
    }

    Runnable runnable = new Runnable() {
        int progress = 0;

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progress = vvView.getCurrentPosition();
                    Log.d("<><><>", "progress" + progress);
                    seekBar.setProgress(progress);
                    TextView tvCurrentTime = findViewById(R.id.tvCurrentTime);
                    int minutes = (progress / 1000) / 60;
                    int seconds = (progress / 1000) % 60;
                    tvCurrentTime.setText(minutes + ":" + seconds);
                }
            });

            if (isPaused == false) {
                seekHandler.postDelayed(runnable, 1000);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(seekHandler != null){
            seekHandler.removeCallbacks(runnable);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        vvView = findViewById(R.id.vvView);

        seekBar = findViewById(R.id.seekBar);
        seekBar.setProgress(0);

        if (vvView.isPlaying() == false) {
            seekBar.setEnabled(false);
        }

        Intent intent = getIntent();
        vidUri = Uri.parse(intent.getExtras().getString("Video Uri"));
        playVideo();

        vvView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                getTotalDuration();
            }
        });

        ivPlay = (ImageView) findViewById(R.id.ivPlay);
        ivPause = (ImageView) findViewById(R.id.ivPause);

        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivPlay.setVisibility(View.INVISIBLE);
                ivPause.setVisibility(View.VISIBLE);

                if (isPaused == true) {
                    int length = vvView.getCurrentPosition();
                    vvView.seekTo(length);
                    vvView.start();
                    isPaused = false;
                    runnable.run();
                }

                seekBar.setEnabled(true);
            }
        });

        ivPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivPause.setVisibility(View.INVISIBLE);
                ivPlay.setVisibility(View.VISIBLE);
                vvView.pause();
                if(seekHandler != null){
                    seekHandler.removeCallbacks(runnable);
                }
                isPaused = true;

                seekBar.setEnabled(false);
            }
        });

        ImageView ivBackArrow = (ImageView) findViewById(R.id.ivBackArrow);
        ivBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                if (vvView != null && isPaused == false) {
                    vvView.seekTo(progress);
                }
            }
        });

        VideoView vvView = findViewById(R.id.vvView);
        vvView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                Log.d("TOUCH", "onTouch");

                RelativeLayout vwMusicButtons = findViewById(R.id.vwMusicButtons);

                if(musicControlButtonDisplayed == true){
                    int eventY = (int) motionEvent.getY();
                    int seekBarY = (int) vwMusicButtons.getY();

                    Log.d("TOUCH", ""+eventY+", "+seekBarY);

                    if(eventY > seekBarY){
                        return false;
                    }

                    vwMusicButtons.setVisibility(View.GONE);
                    musicControlButtonDisplayed = false;

                }else{
                    vwMusicButtons.setVisibility(View.VISIBLE);
                    musicControlButtonDisplayed = true;
                }


                return false;
            }
        });

        vvView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RelativeLayout vwMusicButtons = findViewById(R.id.vwMusicButtons);

                if(musicControlButtonDisplayed == true){
                    vwMusicButtons.setVisibility(View.GONE);
                    musicControlButtonDisplayed = false;
                }else{
                    vwMusicButtons.setVisibility(View.VISIBLE);
                    musicControlButtonDisplayed = true;
                }

            }
        });

    }
}
