package com.example.teju.testapp;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Created by Teju on 12/18/2017.
 */

public class VideoItems {
   private String title;
    private String artist;
    private Uri uri;
    private Bitmap imageBitmap;
    private Uri thumbUri;


    public Uri getThumbUri() {
        return thumbUri;
    }

    public void setThumbUri(Uri thumbUri) {
        this.thumbUri = thumbUri;
    }

    public String getTitle() {
        return title;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
