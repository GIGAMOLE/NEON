package com.gigamole.neon.adapters;
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

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gigamole.neon.R;
import com.gigamole.neon.activities.MainActivity;
import com.gigamole.neon.models.SongModel;

import java.util.ArrayList;

/**
 * Created by GIGAMOLE on 16.05.2015.
 */
public class SongListAdapter extends BaseAdapter {

    // Our song list
    private ArrayList<SongModel> songsList = new ArrayList<>();

    private Context context;
    private MainActivity activity;
    private Resources resources;
    private LayoutInflater layoutInflater;

    // Define current position and state
    public static final int NOT_PLAYING = -1;
    public int playingPosition = NOT_PLAYING;

    // Our watchers for song item progress bar
    private Handler itemHandler = new Handler();
    private MainActivity.UpdateCurrentStateTask itemProgressUpdater;

    public SongListAdapter(ArrayList<SongModel> songsList, MainActivity activity, MainActivity.UpdateCurrentStateTask updateTask) {
        this.songsList = songsList;

        this.activity = activity;
        this.context = activity.getBaseContext();
        this.resources = activity.getResources();
        this.layoutInflater = LayoutInflater.from(activity);
        this.itemProgressUpdater = updateTask;
    }

    @Override
    public int getCount() {
        return this.songsList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.songsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        SongModel songModel = (SongModel) getItem(position);

        if (convertView == null) {
            convertView = this.layoutInflater.inflate(R.layout.item_song_list, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.title.setText(songModel.getTitle());
        viewHolder.duration.setText(songModel.getFormattedDuration());

        if (position == this.playingPosition) {
            viewHolder.progressBar.setVisibility(View.VISIBLE);
            viewHolder.headphone.setVisibility(View.VISIBLE);

            this.itemProgressUpdater.songItemProgressBar = viewHolder.progressBar;
            this.itemHandler.post(this.itemProgressUpdater);
        } else {
            viewHolder.progressBar.setVisibility(View.GONE);
            viewHolder.progressBar.setProgress(0);
            viewHolder.headphone.setVisibility(View.GONE);

            if (this.itemProgressUpdater.songItemProgressBar == viewHolder.progressBar) {
                this.itemProgressUpdater.songItemProgressBar = null;
            }
        }

        return convertView;
    }

    public class ViewHolder {
        public final ProgressBar progressBar;
        public final View headphone;
        public final TextView title;
        public final TextView duration;

        public ViewHolder(View view) {
            this.progressBar = (ProgressBar) view.findViewById(R.id.song_item_progress_bar);
            this.headphone = view.findViewById(R.id.song_item_img_headphone);
            this.title = (TextView) view.findViewById(R.id.song_item_title);
            this.duration = (TextView) view.findViewById(R.id.song_item_duration);
        }
    }
}
