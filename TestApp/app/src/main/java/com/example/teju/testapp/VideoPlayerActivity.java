package com.example.teju.testapp;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import org.w3c.dom.Text;

public class VideoPlayerActivity extends Activity {
    boolean isPaused = false;
    VideoView vvView;
    Uri vidUri;
    SeekBar seekBar;
    int totalDuration = 0;
    Handler seekHandler = new Handler();

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

    public void playVideo(){

        vvView.setVideoURI(vidUri);
        vvView.requestFocus();
        vvView.start();

        isPaused = false;

        Log.d("<><><>","RunnableInplayVideo "+vvView.getDuration());
        seekBar.setEnabled(true);

        vvView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
             @Override
             public void onCompletion(MediaPlayer mediaPlayer) {
                 seekBar.setEnabled(false);
                 isPaused = true;
             }
         });

        runnable.run();
    }

        Runnable runnable=new Runnable() {
            int progress = 0;
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progress = vvView.getCurrentPosition();
                        Log.d("<><><>","progress"+progress);
                        seekBar.setProgress(progress);
                        TextView tvCurrentTime = findViewById(R.id.tvCurrentTime);
                        int minutes = (progress/1000)/60;
                        int seconds= (progress/1000)%60;
                        tvCurrentTime.setText(minutes+":"+seconds);
                    }
                });

                if(isPaused == false) {
                    seekHandler.postDelayed(runnable, 1000);
                }
            }
        };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
         vvView = findViewById(R.id.vvView);
         seekBar = findViewById(R.id.seekBar);
        seekBar.setProgress(0);
        if(vvView.isPlaying()==false){
            seekBar.setEnabled(false);
        }
        Intent intent= getIntent();
        vidUri = Uri.parse(intent.getExtras().getString("Video Uri"));
          playVideo();

          vvView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
              @Override
              public void onPrepared(MediaPlayer mediaPlayer) {
                  getTotalDuration();
              }
          });



        final ImageView ivPlay = (ImageView)findViewById(R.id.ivPlay);
        final ImageView ivPause = (ImageView)findViewById(R.id.ivPause);

        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivPlay.setVisibility(View.INVISIBLE);
                ivPause.setVisibility(View.VISIBLE);


                if(isPaused==true){
                    int length = vvView.getCurrentPosition();
                    vvView.seekTo(length);
                    vvView.start();
                    isPaused=false;
                }


            }
        });
        ivPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivPause.setVisibility(View.INVISIBLE);
                ivPlay.setVisibility(View.VISIBLE);
                vvView.pause();
                isPaused=true;
            }
        });

        ImageView ivBackArrow = (ImageView)findViewById(R.id.ivBackArrow);
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
                if(vvView!=null&& vvView.isPlaying()){
                    vvView.seekTo(progress);
                }
            }
        });

    }
}
