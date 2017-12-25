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

import java.io.File;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class VideoFragment extends Fragment  {
    ListView listView;
    VideoFragmentAdapter adapter;
    ArrayList<VideoItems>mVideoList;
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
    public void requestPermission(){
        int result = getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        if(result== PackageManager.PERMISSION_GRANTED){
            getVideoList();
        }else{
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);

        }
    }

    public void getVideoList(){

        Log.d("<>", "getvideolist");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Uri videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                cursor= getActivity().getContentResolver().query(videoUri,null,null,null,null);
                int titleColumn =cursor.getColumnIndex(MediaStore.Video.Media.TITLE);
                int artistColumn = cursor.getColumnIndex(MediaStore.Video.Media.ARTIST);
                int data = cursor.getColumnIndex(MediaStore.Video.Media.DATA);
               // long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media._ID));
               int thumbData= cursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA);

               // BitmapFactory.Options options = new BitmapFactory.Options();
               // options.inSampleSize=1;
                //Bitmap thumb =MediaStore.Video.Thumbnails.getThumbnail(getActivity().getContentResolver(),id,MediaStore.Video.Thumbnails.MICRO_KIND,options);



                if(cursor!=null){
                    while(cursor.moveToNext()){
                        String title = cursor.getString(titleColumn);
                        String artist = cursor.getString(artistColumn);
                        String path  =cursor.getString(data);
                        String thumbPath = cursor.getString(thumbData);

                        final VideoItems items = new VideoItems();
                        items.setTitle(title);
                        items.setArtist(artist);
                        Uri uri =  Uri.fromFile(new File(path));
                        items.setUri(uri);

                       Uri thumbUri =  Uri.fromFile(new File(thumbPath));
                        items.setThumbUri(thumbUri);
                        //items.setImageBitmap(thumb);

                        if(getActivity()!=null){

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


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vFrView = inflater.inflate(R.layout.fragment_video, container, false);
        mVideoList=new ArrayList<VideoItems>();
        listView = (ListView) vFrView.findViewById(R.id.llVideo_list);
        if (getActivity() != null) {
            adapter = new VideoFragment.VideoFragmentAdapter(this.getActivity());
            listView.setAdapter(adapter);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                VideoItems items= mVideoList.get(i);
                Intent intent = new Intent(getActivity(),VideoPlayerActivity.class);
                intent.putExtra("Video Uri",items.getUri().toString());
                startActivity(intent);
            }
        });

        requestPermission();
        return vFrView;

    }



    public class VideoFragmentAdapter extends BaseAdapter {
        Context mContext;

        VideoFragmentAdapter(Context c) {
            mContext = c;
        }

        @Override
        public int getCount() {
            return mVideoList.size();
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
            VideoItems items = mVideoList.get(i);
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (list == null) {
                list = inflater.inflate(R.layout.video, null);
            }


            TextView tvVideoName = (TextView) list.findViewById(R.id.tvVideoName);
            TextView tvArtistName = (TextView) list.findViewById(R.id.tvArtistName);
            ImageView ivVideo = (ImageView) list.findViewById(R.id.ivVideo);

            tvVideoName.setText(items.getTitle());
            tvArtistName.setText(items.getArtist());
              // ivVideo.setImageBitmap(items.getImageBitmap());
            if(getActivity() != null) {
                Glide.with(getActivity()).load(items.getThumbUri()).into(ivVideo);
            }


            return list;
        }


    }
}
