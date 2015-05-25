package com.gigamole.neon.models;
/*
 * Copyright (C) 2015 Basil Miller
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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