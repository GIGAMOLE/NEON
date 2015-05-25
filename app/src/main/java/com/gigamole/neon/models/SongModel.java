package com.gigamole.neon.models;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.gigamole.neon.utils.Utilities;

/**
 * Created by GIGAMOLE on 16.05.2015.
 */
public class SongModel {

    // Song title
    private String title;

    // Song artist
    private String artist;

    // Song duration like 12:40
    private String formattedDuration;

    // Song path on SD card
    private String path;

    // Song album cover
    private Bitmap album;

    public SongModel(String title, String artist, String duration, String path, Bitmap album) {
        setTitle(title);
        setArtist(artist);
        setFormattedDuration(duration);
        setPath(path);
        setAlbum(album);
    }

    public void setTitle(String title) {
        if (TextUtils.isEmpty(title)) {
            this.title = "Unknown Title";
        } else {
            this.title = title;
        }
    }

    public void setArtist(String artist) {
        if (TextUtils.isEmpty(artist)) {
            this.artist = "UNKNOWN ARTIST";
        } else {
            this.artist = artist.toUpperCase();
        }
    }

    public void setDuration(String duration) {
        this.formattedDuration = duration;
    }

    public void setFormattedDuration(String formattedDuration) {
        this.formattedDuration = Utilities.milliSecondsToTimer(Long.valueOf(formattedDuration));
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setAlbum(Bitmap album) {
        this.album = album;
    }

    public String getFormattedDuration() {
        return this.formattedDuration;
    }

    public String getTitle() {
        return this.title;
    }

    public String getArtist() {
        return this.artist;
    }

    public String getPath() {
        return this.path;
    }

    public Bitmap getAlbum() {
        return this.album;
    }
}