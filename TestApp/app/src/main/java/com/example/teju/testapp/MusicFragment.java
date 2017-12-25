package com.example.teju.testapp;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 */
public class MusicFragment extends Fragment{
    ListView listView;
    MusicFragmentAdapter adapter;
    SeekBar seek_bar;
    boolean isPaused = false;
       TextView tvTotalTime,tvCurrentTime;
    int lastSongIndex = -1;

    MediaPlayer mediaPlayer = new MediaPlayer();
    Handler seekHandler = new Handler();

    Cursor cursor;
    ArrayList<songItems> mSongsList;




    public MusicFragment() {
        // Required empty public constructor
    }

    public void setTotalDuration(){
        long totalDuration = mediaPlayer.getDuration();
        int minutes = (int)(totalDuration/1000)/60;
        int seconds= (int)(totalDuration/1000)%60;
        tvTotalTime.setText(minutes+":"+seconds);

    }


    public void playNextSong(){
        int nextSongIndex = lastSongIndex + 1;

        if(nextSongIndex == mSongsList.size()) return;

        lastSongIndex = nextSongIndex;

        Log.d("<>", "next song: "+nextSongIndex);
        songItems item = mSongsList.get(nextSongIndex);
        playSong(item.getUri());



    }

    public void playSong(Uri uri){
        try {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.setDataSource(getActivity().getApplicationContext(), uri);
            mediaPlayer.prepare();
            mediaPlayer.start();
            seek_bar.setMax(mediaPlayer.getDuration());

            runnable.run();
            adapter.notifyDataSetChanged();
            seek_bar.setEnabled(true);

            setTotalDuration();




            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    seek_bar.setEnabled(false);
                    playNextSong();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d("<>", "onRequestPermissionsResult");

        for (int i = 0; i < grantResults.length; i++) {
            String permission = permissions[i];

            if (permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    getSongsList();
                } else {
                    requestPermission();
                }
                return;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestPermission() {

        int result = getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);

        if (result == PackageManager.PERMISSION_GRANTED) {
            getSongsList();
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    0);
        }

    }

    public void getSongsList() {

        Log.d("<>", "getSongsList");

        new Thread(new Runnable() {
            @Override
            public void run() {

                Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                cursor = getActivity().getContentResolver().query(musicUri, null, null, null, null);
                int titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int idColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
                int artistColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);

                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        long id = cursor.getLong(idColumn);
                        String title = cursor.getString(titleColumn);
                        String artist = cursor.getString(artistColumn);

                        final songItems items = new songItems();
                        items.setId(id);
                        items.setTitle(title);
                        items.setArtist(artist);
                        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                        Uri uri = Uri.fromFile(new File(path));
                        items.setUri(uri);

                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mSongsList.add(items);
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }

                    }
                }
                cursor.close();
            }
        }).start();


    }

    final Runnable runnable= new Runnable() {
        @Override
        public void run() {
            if(getActivity()!= null)
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int progress = mediaPlayer.getCurrentPosition();
                    Log.d("<>", "progress:"+progress);
                    seek_bar.setProgress(progress);

                    int minutes = (int)(progress/1000)/60;
                    int seconds= (int)(progress/1000)%60;
                    tvCurrentTime.setText(minutes+":"+seconds);

                }
            });

            if(mediaPlayer.isPlaying()==true) {
                seekHandler.postDelayed(runnable, 1000);
            }

            Log.d("<>", mediaPlayer.getDuration()+", "+mediaPlayer.getCurrentPosition());
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View frview = inflater.inflate(R.layout.fragment_music, container, false);
        mSongsList = new ArrayList<songItems>();


        tvTotalTime = frview.findViewById(R.id.tvTotalTime);
        tvCurrentTime=frview.findViewById(R.id.tvCurrentTime);


        seek_bar = frview.findViewById(R.id.seekBar);
        seek_bar.setProgress(0);
        if(mediaPlayer.isPlaying()==false){
            seek_bar.setEnabled(false);
        }

        listView = (ListView) frview.findViewById(R.id.lvSong_list);
        if (getActivity() != null) {
            adapter = new MusicFragment.MusicFragmentAdapter(this.getActivity());
            listView.setAdapter(adapter);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                songItems items = mSongsList.get(i);
                lastSongIndex = i;
                playSong(items.getUri());

                final ImageView ivPause = frview.findViewById(R.id.ivPause);
                final ImageView ivPlay = frview.findViewById(R.id.ivPlay);

                ivPause.setVisibility(View.VISIBLE);
                ivPlay.setVisibility(View.INVISIBLE);

            }
        });

        final ImageView ivPause = frview.findViewById(R.id.ivPause);
        final ImageView ivPlay = frview.findViewById(R.id.ivPlay);

        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(View.INVISIBLE);
                ivPause.setVisibility(View.VISIBLE);

                if(isPaused == true){
                    int length = mediaPlayer.getCurrentPosition();
                    mediaPlayer.seekTo(length);
                    mediaPlayer.start();
                    isPaused = false;
                }else{
                    playNextSong();
                }

                runnable.run();
            }
        });

        ivPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(View.INVISIBLE);
                ivPlay.setVisibility(View.VISIBLE);
                mediaPlayer.pause();
                isPaused = true;
            }
        });


        final int[] dragPos = {0};

        seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                dragPos[0] = i;


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(dragPos[0]);

            }
        });


//        Collections.sort(mSongsList, new Comparator<songItems>(){
//            @Override
//            public int compare(songItems a, songItems b) {
//                return a.getTitle().compareTo(b.getTitle());
//            }
//        });

        requestPermission();

        return frview;
    }



    public class MusicFragmentAdapter extends BaseAdapter {
        Context mContext;

        MusicFragmentAdapter(Context c) {
            mContext = c;
        }

        @Override
        public int getCount() {
            return mSongsList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View list = view;
            songItems items = mSongsList.get(i);
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (list == null) {
                list = inflater.inflate(R.layout.songs, null);
            }

            if(lastSongIndex == i){
                list.setBackgroundColor(Color.parseColor("#dddd00"));
            }else{
                list.setBackgroundColor(Color.parseColor("#eeeeee"));
            }

            TextView tvSongName = (TextView) list.findViewById(R.id.tvSongName);
            TextView tvArtistName = (TextView) list.findViewById(R.id.tvArtistName);

            tvSongName.setText(items.getTitle());
            tvArtistName.setText(items.getArtist());

            return list;
        }
    }
}


