package com.example.teju.u_and_e;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MusicFragment extends Fragment {
    FastScrollRecyclerView listView;
    MusicFragmentAdapter adapter;
    SeekBar seek_bar;
    boolean isPaused = false;
    TextView tvTotalTime, tvCurrentTime;
    int lastSongIndex = -1;

    Handler seekHandler = new Handler();
    TestAppSharedPreferences preferences = TestAppSharedPreferences.getInstance(getActivity());

    Cursor cursor;
    ArrayList<songItems> mSongsList;
    View frview;

    public MusicFragment() {
        // Required empty public constructor
    }

    public void setTotalDuration() {
        long totalDuration = MainActivity.mediaPlayer.getDuration();
        int minutes = (int) (totalDuration / 1000) / 60;
        int seconds = (int) (totalDuration / 1000) % 60;
        tvTotalTime.setText(minutes + ":" + seconds);
    }

    public void playPrevSong() {
        int nextSongIndex = lastSongIndex - 1;

        if (nextSongIndex == -1) nextSongIndex = 0;

        lastSongIndex = nextSongIndex;

        Log.d("<>", "next song: " + nextSongIndex);
        songItems item = mSongsList.get(nextSongIndex);
        playSong(item);

        listView.smoothScrollToPosition(nextSongIndex);
    }

    public void playNextSong() {
        int nextSongIndex = lastSongIndex + 1;

        if (nextSongIndex == mSongsList.size()) return;

        lastSongIndex = nextSongIndex;

        Log.d("<>", "next song: " + nextSongIndex);
        songItems item = mSongsList.get(nextSongIndex);
        playSong(item);

        listView.smoothScrollToPosition(nextSongIndex);
    }

    public void loadBottomCurrSongView(final Uri thumbUri, final String title) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    final ImageView ivPause = frview.findViewById(R.id.ivPause);
                    final ImageView ivPlay = frview.findViewById(R.id.ivPlay);

                    ivPlay.setVisibility(View.INVISIBLE);
                    ivPause.setVisibility(View.VISIBLE);

                    RelativeLayout rlProgressBarView = frview.findViewById(R.id.rlProgressBarView);

                    ImageView ivCurrSongsPic = frview.findViewById(R.id.ivCurrSongsPic);
                    TextView tvCurrSongTitle = frview.findViewById(R.id.tvCurrSongTitle);

                    Glide.with(getActivity())
                            .load(thumbUri)
                            .placeholder(R.drawable.song)
                            .override(100, 100)
                            .into(ivCurrSongsPic);

                    tvCurrSongTitle.setText(title);

                    runnable.run();

                    rlProgressBarView.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    public void stopSong(){

        if(MainActivity.mediaPlayer.isPlaying() == true){
            MainActivity.mediaPlayer.stop();
            seek_bar.setMax(0);
            seek_bar.setProgress(0);
        }

        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    RelativeLayout rlProgressBarView = frview.findViewById(R.id.rlProgressBarView);
                    TextView tvCurrSongTitle = frview.findViewById(R.id.tvCurrSongTitle);
                    tvCurrSongTitle.setText("");
                    rlProgressBarView.setVisibility(View.GONE);
                }
            });
        }

    }

    public void playSong(final songItems item) {
        try {
            Uri uri = item.getUri();
            MainActivity.mediaPlayer.stop();
            MainActivity.mediaPlayer.reset();
            MainActivity.mediaPlayer.setDataSource(getActivity().getApplicationContext(), uri);
            MainActivity.mediaPlayer.prepare();
            MainActivity.mediaPlayer.start();
            seek_bar.setMax(MainActivity.mediaPlayer.getDuration());

            preferences.setLastPlayedSongPath(item.getUri().toString());
            preferences.setLastPlayedSongThumbUri(item.getThumbUri().toString());
            preferences.setLastPlayedSongTitle(item.getTitle());

            runnable.run();
            adapter.notifyDataSetChanged();
            seek_bar.setEnabled(true);

            setTotalDuration();
            MainActivity.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    seek_bar.setEnabled(false);
                    playNextSong();
                }
            });

            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        RelativeLayout rlProgressBarView = frview.findViewById(R.id.rlProgressBarView);

                        ImageView ivCurrSongsPic = frview.findViewById(R.id.ivCurrSongsPic);
                        TextView tvCurrSongTitle = frview.findViewById(R.id.tvCurrSongTitle);

                        Glide.with(getActivity())
                                .load(item.getThumbUri())
                                .placeholder(R.drawable.song)
                                .override(100, 100)
                                .into(ivCurrSongsPic);

                        tvCurrSongTitle.setText(item.getTitle());

                        rlProgressBarView.setVisibility(View.VISIBLE);
                    }
                });
            }
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
                cursor = getActivity().getContentResolver().query(musicUri, null, null, null,
                        MediaStore.Audio.Media.TITLE + " COLLATE NOCASE ASC");

                int titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int idColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
                int artistColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);

                String oldTitle, oldPath, oldThumbUri;

                oldTitle = preferences.getLastPlayedSongTitle();
                oldPath = preferences.getLastPlayedSongPath();
                oldThumbUri = preferences.getLastPlayedSongThumbUri();

                Log.d(MusicFragment.class.getSimpleName(), "oldTitle: "+oldTitle+", oldPath: "+oldPath+", oldThumbUri: "+oldThumbUri);

                int index = 0;
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

                        long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                        Uri thumbUri = Uri.parse("content://media/external/audio/albumart");
                        Uri imgUri = ContentUris.withAppendedId(thumbUri,
                                albumId);
                        Log.d(MusicFragment.class.getSimpleName(), "Fetched Song Path: " + imgUri + "");
                        items.setThumbUri(imgUri);

                        if (oldPath.trim().equals(uri.toString().trim()) == true) {
                            if (MainActivity.mediaPlayer.isPlaying() == true) {
                                loadBottomCurrSongView(Uri.parse(oldThumbUri), oldTitle);
                                seek_bar.setMax(MainActivity.mediaPlayer.getDuration());

                                Log.d(MusicFragment.class.getSimpleName(), "oldPath: "+oldPath + ", uriPath: "+uri.toString()+
                                        ", isMediaPlayerPlaying: "+MainActivity.mediaPlayer.isPlaying());

                                lastSongIndex = index;
                                Log.d(MusicFragment.class.getSimpleName(), "index:"+index+", lastSongIndex: "+lastSongIndex);
                                setTotalDuration();
                            }
                        }

                        if (getActivity() != null) {
                            final int finalIndex = index;
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mSongsList.add(items);
                                    adapter.notifyDataSetChanged();
                                    if(lastSongIndex == finalIndex){
                                        listView.smoothScrollToPosition(finalIndex);
                                    }
                                }
                            });
                        }
                        index++;
                    }
                }
                cursor.close();
            }
        }).start();
    }

    final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (getActivity() != null)
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int progress = MainActivity.mediaPlayer.getCurrentPosition();
                        Log.d("<>", "progress:" + progress);
                        seek_bar.setProgress(progress);

                        int minutes = (int) (progress / 1000) / 60;
                        int seconds = (int) (progress / 1000) % 60;
                        tvCurrentTime.setText(minutes + ":" + seconds);

                    }
                });

            if (MainActivity.mediaPlayer.isPlaying() == true) {
                seekHandler.postDelayed(runnable, 1000);
            }

            Log.d("<>", MainActivity.mediaPlayer.getDuration() + ", " + MainActivity.mediaPlayer.getCurrentPosition());
        }
    };


    public void setRecyclerViewLayoutManager(RecyclerView recyclerView) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (recyclerView.getLayoutManager() != null) {
            scrollPosition =
                    ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.scrollToPosition(scrollPosition);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        frview = inflater.inflate(R.layout.fragment_music, container, false);
        mSongsList = new ArrayList<songItems>();

        tvTotalTime = frview.findViewById(R.id.tvTotalTime);
        tvCurrentTime = frview.findViewById(R.id.tvCurrentTime);

        seek_bar = frview.findViewById(R.id.seekBar);
        seek_bar.setProgress(0);

        listView = frview.findViewById(R.id.lvSong_list);
        if (getActivity() != null) {
            adapter = new MusicFragment.MusicFragmentAdapter(this.getActivity());
            listView.setAdapter(adapter);
        }

        final ImageView ivPause = frview.findViewById(R.id.ivPause);
        final ImageView ivPlay = frview.findViewById(R.id.ivPlay);
        final ImageView ivPrev = frview.findViewById(R.id.ivPrev);
        final ImageView ivNext = frview.findViewById(R.id.ivNext);
        final ImageView ivStop = frview.findViewById(R.id.ivStop);

        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(View.INVISIBLE);
                ivPause.setVisibility(View.VISIBLE);

                if (isPaused == true) {
                    int length = MainActivity.mediaPlayer.getCurrentPosition();
                    MainActivity.mediaPlayer.seekTo(length);
                    MainActivity.mediaPlayer.start();
                    isPaused = false;
                } else {
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
                MainActivity.mediaPlayer.pause();
                isPaused = true;
            }
        });

        ivPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playPrevSong();
            }
        });

        ivNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playNextSong();
            }
        });

        ivStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopSong();
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
                MainActivity.mediaPlayer.seekTo(dragPos[0]);

            }
        });

        requestPermission();
        setRecyclerViewLayoutManager(listView);

        if (MainActivity.mediaPlayer.isPlaying() == false) {
            seek_bar.setEnabled(false);
        } else {
            Log.d(MusicFragment.class.getSimpleName(), "Song is still running");
        }

        return frview;
    }

    public class MusicFragmentAdapter extends RecyclerView.Adapter implements FastScrollRecyclerView.SectionedAdapter, FastScrollRecyclerView.MeasurableAdapter {
        Context mContext;

        MusicFragmentAdapter(Context c) {
            mContext = c;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup v, int viewType) {
            View row = LayoutInflater.from(v.getContext()).inflate(R.layout.songs, v, false);
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    songItems items = mSongsList.get(position);
                    lastSongIndex = position;
                    playSong(items);

                    final ImageView ivPause = frview.findViewById(R.id.ivPause);
                    final ImageView ivPlay = frview.findViewById(R.id.ivPlay);

                    ivPause.setVisibility(View.VISIBLE);
                    ivPlay.setVisibility(View.INVISIBLE);
                }
            });

            return new myViewHolder(row);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            songItems items = mSongsList.get(position);

            ((myViewHolder) holder).getTvSongName().setText(items.getTitle());
            ((myViewHolder) holder).getTvArtistName().setText(items.getArtist());

            if (getActivity() != null) {
                Glide.with(getActivity())
                        .load(items.getThumbUri())
                        .placeholder(R.drawable.song)
                        .override(100, 100)
                        .into(((myViewHolder) holder).getIvMusic());
            }

            if (lastSongIndex == position) {
                ((myViewHolder) holder).setBackgroundColor("#464648");
            } else {
                ((myViewHolder) holder).setBackgroundColor("#2d3e52");
            }

            ((myViewHolder) holder).setRootViewPositionTag(position);
        }

        @Override
        public int getItemCount() {
            return mSongsList.size();
        }

        @Override
        public int getViewTypeHeight(RecyclerView recyclerView, int viewType) {
            return 55;
        }

        @NonNull
        @Override
        public String getSectionName(int position) {
            songItems items = mSongsList.get(position);
            String songName = items.getTitle();
            return ("" + songName.charAt(0)).toUpperCase();
        }
    }

    public class myViewHolder extends RecyclerView.ViewHolder {

        private TextView tvSongName;
        private TextView tvArtistName;
        private ImageView ivMusic;
        private View row;

        public View getRow() {
            return row;
        }

        public TextView getTvSongName() {
            return tvSongName;
        }

        public TextView getTvArtistName() {
            return tvArtistName;
        }

        public void setBackgroundColor(String color) {
            row.setBackgroundColor(Color.parseColor(color));
        }

        public ImageView getIvMusic() {
            return ivMusic;
        }

        public void setRootViewPositionTag(int position) {
            row.setTag(position);
        }


        public myViewHolder(View v) {
            super(v);

            tvSongName = (TextView) v.findViewById(R.id.tvSongName);
            tvArtistName = (TextView) v.findViewById(R.id.tvArtistName);
            ivMusic = (ImageView) v.findViewById(R.id.ivMusic);

            row = v;
        }
    }
}


