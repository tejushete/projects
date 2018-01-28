package com.example.teju.testapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.io.File;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoFragment extends Fragment {
    FastScrollRecyclerView listView;
    VideoFragmentAdapter adapter;
    ArrayList<VideoItems> mVideoList;
    Cursor cursor;

    public VideoFragment() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d("<>", "onRequestPermissionsResult");

        for (int i = 0; i < grantResults.length; i++) {
            String permission = permissions[i];

            if (permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    getVideoList();
                } else {
                    requestPermission();
                }
                return;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestPermission() {
        int result = getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            getVideoList();
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }
    }

    public void getVideoList() {

        Log.d("<>", "getvideolist");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Uri videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                cursor = getActivity().getContentResolver().query(videoUri,
                        null,
                        null,
                        null,
                        MediaStore.Video.Media.TITLE+" COLLATE NOCASE ASC");

                int titleColumn = cursor.getColumnIndex(MediaStore.Video.Media.TITLE);
                int artistColumn = cursor.getColumnIndex(MediaStore.Video.Media.ARTIST);
                int data = cursor.getColumnIndex(MediaStore.Video.Media.DATA);
                int thumbData = cursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA);

                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        String title = cursor.getString(titleColumn);
                        String artist = cursor.getString(artistColumn);
                        String path = cursor.getString(data);
                        String thumbPath = cursor.getString(thumbData);

                        final VideoItems items = new VideoItems();
                        items.setTitle(title);
                        items.setArtist(artist);
                        Uri uri = Uri.fromFile(new File(path));
                        items.setUri(uri);

                        Uri thumbUri = Uri.fromFile(new File(thumbPath));
                        items.setThumbUri(thumbUri);

                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mVideoList.add(items);
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                }
            }
        }).start();
    }

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

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vFrView = inflater.inflate(R.layout.fragment_video, container, false);
        mVideoList = new ArrayList<VideoItems>();
        listView = vFrView.findViewById(R.id.llVideo_list);
        if (getActivity() != null) {
            adapter = new VideoFragment.VideoFragmentAdapter(this.getActivity());
            listView.setAdapter(adapter);
        }

        requestPermission();
        setRecyclerViewLayoutManager(listView);
        return vFrView;
    }

    public class VideoFragmentAdapter extends RecyclerView.Adapter implements FastScrollRecyclerView.SectionedAdapter,FastScrollRecyclerView.MeasurableAdapter {
        Context mContext;

        VideoFragmentAdapter(Context c) {
            mContext = c;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup v, int viewType) {
            View row = LayoutInflater.from(v.getContext()).inflate(R.layout.video,v,false);
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    VideoItems items = mVideoList.get(position);
                    Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);
                    intent.putExtra("Video Uri", items.getUri().toString());
                    startActivity(intent);

                }
            });
            return new myViewHolder(row);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            VideoItems items = mVideoList.get(position);
            ((myViewHolder) holder).getTvVideoName().setText(items.getTitle());
            ((myViewHolder) holder).getTvArtistName().setText(items.getArtist());
            if (getActivity() != null) {
                Glide.with(getActivity())
                        .load(items.getThumbUri())
                        .override(100,100)
                        .placeholder(R.drawable.video_placeholder)
                        .into(((myViewHolder) holder).getIvVideo());
            }
             ((myViewHolder) holder).setRootViewPositionTag(position);
        }

        @Override
        public int getItemCount() {
            return mVideoList.size();
        }

        @Override
        public int getViewTypeHeight(RecyclerView recyclerView, int viewType) {
            return 55;
        }

        @NonNull
        @Override
        public String getSectionName(int position) {
            VideoItems items = mVideoList.get(position);
            String videoname = items.getTitle();

            return(""+videoname.charAt(0)).toUpperCase();
        }
    }

    public class myViewHolder extends RecyclerView.ViewHolder {

        private TextView tvVideoName;
        private TextView tvArtistName;
        private ImageView ivVideo;
        private View row;

        public View getRow(){
            return row;
        }

        public TextView getTvVideoName() {
            return tvVideoName;
        }

        public TextView getTvArtistName() {
            return tvArtistName;
        }


        public ImageView getIvVideo() {
            return ivVideo;
        }

        public void setRootViewPositionTag(int position) {
            row.setTag(position);
        }


        public myViewHolder(View v) {
            super(v);

            tvVideoName = (TextView) v.findViewById(R.id.tvVideoName);
            tvArtistName = (TextView) v.findViewById(R.id.tvArtistName);
            ivVideo = (ImageView) v.findViewById(R.id.ivVideo);

            row = v;
        }
    }
}
