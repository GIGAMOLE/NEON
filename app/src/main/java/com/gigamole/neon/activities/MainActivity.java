package com.gigamole.neon.activities;
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

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Visualizer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gigamole.millspinners.lib.MultiArcSpinner;
import com.gigamole.neon.R;
import com.gigamole.neon.adapters.PlayerPagerAdapter;
import com.gigamole.neon.adapters.SongListAdapter;
import com.gigamole.neon.models.SongModel;
import com.gigamole.neon.utils.Constants;
import com.gigamole.neon.utils.RadioGroup;
import com.gigamole.neon.utils.SongsManager;
import com.gigamole.neon.utils.Utilities;
import com.gigamole.neon.views.StaticViewPager;
import com.gigamole.neon.visualizers.BarVisualizerView;
import com.gigamole.neon.visualizers.GravityDefiedVisualizerView;
import com.gigamole.neon.visualizers.PulseVisualizerView;
import com.gigamole.neon.visualizers.SeaBreathVisualizerView;
import com.gigamole.neon.visualizers.VisualizerView;
import com.gigamole.slideimageview.lib.SlideImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by GIGAMOLE on 17.05.2015.
 */
public class MainActivity extends Activity implements OnCompletionListener, SeekBar.OnSeekBarChangeListener {

    // Header views
    private ImageButton btnPlay;

    private TextView songArtist;
    private TextView songTitle;
    private TextView songCurrentDuration;

    private SeekBar songSeekBar;
    private ProgressBar songProgressBar;
    private View tintProgressBar;

    private StaticViewPager playerPager;
    private View viewStub;

    // Bottom views
    private ListView songsListView;

    // Swipe layout views
    private CheckBox cbLoop;
    private CheckBox cbRandom;

    private RadioGroup radioGroup;
    private RadioButton rbCover;
    private RadioButton rbBar;
    private RadioButton rbGd;
    private RadioButton rbSea;
    private RadioButton rbPulse;

    private SeekBar vsb_1;
    private SeekBar vsb_2;
    private SeekBar vsb_3;
    private SeekBar vsb_4;
    private SeekBar vsb_5;

    private TextView txtVsb_1;
    private TextView txtVsb_2;
    private TextView txtVsb_3;
    private TextView txtVsb_4;
    private TextView txtVsb_5;

    // Media Player
    private MediaPlayer mediaPlayer;
    private Visualizer visualizer;
    private Equalizer equalizer;

    // Handler to update UI timer, song list, equalizer positions etc,.
    private Handler mainHandler = new Handler();
    private UpdateCurrentStateTask updateCurrentStateTask = new UpdateCurrentStateTask();

    private ArrayList<SongModel> songsList = new ArrayList<>();
    private int[] equalizerPositions = new int[Constants.EQUALIZER_SEEKBAR_COUNT];

    // Global variables
    private int screenWidth;
    private int screenHeight;

    private int currentSongIndex;
    private boolean doubleExitState;
    private Bitmap stubBitmap;

    private boolean isRandom = false;
    private boolean isLoop = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new GetSongsListTask().execute(this.songsList);
    }

    private void initUI() {
        this.screenWidth = this.getWindowManager().getDefaultDisplay().getWidth();
        this.screenHeight = this.getWindowManager().getDefaultDisplay().getHeight();

        hideSplashView();

        initHeader();
        initBottom();
    }

    private void hideSplashView() {
        // Hide splash view
        final View splashView = findViewById(R.id.splash_view);

        final Animation animation = new TranslateAnimation(0f, 0f, 0f, -this.screenHeight);
        animation.setDuration(Constants.SPLASH_VIEW_ANIMATION_DURATION);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                splashView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        splashView.startAnimation(animation);
    }

    private void initHeader() {
        // Stub image when cover is empty
        this.stubBitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.stub)).getBitmap();

        this.playerPager = (StaticViewPager) findViewById(R.id.player_view_pager);

        // Set media visualizer pager and set first type (cover) by default
        final PlayerPagerAdapter playerPagerAdapter = new PlayerPagerAdapter(getBaseContext());
        this.playerPager.setAdapter(playerPagerAdapter);
        this.playerPager.setTag(Constants.COVER_PAGE);

        this.btnPlay = (ImageButton) findViewById(R.id.player_btn_play);
        this.btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // check for already playing
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    // Changing button image to play button
                    btnPlay.setImageResource(R.drawable.play);
                } else {
                    // Resume song
                    mediaPlayer.start();
                    // Changing button image to pause button
                    btnPlay.setImageResource(R.drawable.stop);
                }

                updateEqualizer();
            }
        });

        this.songSeekBar = (SeekBar) findViewById(R.id.player_song_seek_bar);
        this.songSeekBar.setOnSeekBarChangeListener(this);

        this.songProgressBar = (ProgressBar) findViewById(R.id.player_song_progress_bar);
        this.tintProgressBar = findViewById(R.id.player_tint_progress_bar);

        this.songTitle = (TextView) findViewById(R.id.player_song_title);
        this.songArtist = (TextView) findViewById(R.id.player_song_author);
        this.songCurrentDuration = (TextView) findViewById(R.id.player_song_current_time);

        this.viewStub = findViewById(R.id.view_stub);
    }

    private void initBottom() {
        initSongList();
        initSwipeLayout();
    }

    public void initSongList() {
        final SongListAdapter songListAdapter = new SongListAdapter(songsList, this, updateCurrentStateTask);

        this.songsListView = (ListView) findViewById(R.id.player_song_list);
        this.songsListView.setAdapter(songListAdapter);
        this.songsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (btnPlay.getVisibility() == View.INVISIBLE) {
                    // At start just header visible without child
                    // But when click on list item they become visible
                    btnPlay.setVisibility(View.VISIBLE);
                    viewStub.setClickable(false);
                }

                // Get current song position
                currentSongIndex = position;

                // Update current state
                updateListSong((ListView) parent, position);
                playSong(position);
            }
        });
    }

    public void initSwipeLayout() {
        initCheckBoxes();
        initRadioGroup();
        initEqualizerBars();
    }

    private void initCheckBoxes() {
        this.cbLoop = (CheckBox) findViewById(R.id.rb_loop);
        this.cbRandom = (CheckBox) findViewById(R.id.rb_random);

        // Prevent from two check boxes checked
        // Only one can be enabled and both of them can turned off

        this.cbLoop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLoop) {
                    isLoop = false;

                    cbLoop.setChecked(false);
                } else {
                    isLoop = true;
                    isRandom = false;

                    cbLoop.setChecked(true);
                    cbRandom.setChecked(false);
                }
            }
        });

        this.cbRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRandom) {
                    isRandom = false;

                    cbRandom.setChecked(false);
                } else {
                    isRandom = true;
                    isLoop = false;

                    cbRandom.setChecked(true);
                    cbLoop.setChecked(false);
                }
            }
        });
    }

    private void initRadioGroup() {
        this.rbCover = (RadioButton) findViewById(R.id.rb_cover);
        this.rbBar = (RadioButton) findViewById(R.id.rb_bar);
        this.rbGd = (RadioButton) findViewById(R.id.rb_gd);
        this.rbSea = (RadioButton) findViewById(R.id.rb_sea);
        this.rbPulse = (RadioButton) findViewById(R.id.rb_pulse);

        this.rbCover.setTag(Constants.COVER_PAGE);
        this.rbBar.setTag(Constants.BAR_PAGE);
        this.rbGd.setTag(Constants.GD_PAGE);
        this.rbSea.setTag(Constants.SEA_PAGE);
        this.rbPulse.setTag(Constants.PULSE_PAGE);

        final ArrayList<RadioButton> radioButtons = new ArrayList<>();
        radioButtons.add(this.rbCover);
        radioButtons.add(this.rbBar);
        radioButtons.add(this.rbGd);
        radioButtons.add(this.rbSea);
        radioButtons.add(this.rbPulse);

        this.radioGroup = new RadioGroup(radioButtons, this, new RadioGroup.CustomRadioGroupListener() {
            @Override
            public void onClick(View v) {
                final int index = (int) v.getTag();
                playerPager.setTag(index);
                updatePager();
            }
        });
    }

    private void initEqualizerBars() {
        this.vsb_1 = (SeekBar) findViewById(R.id.vsb_1);
        this.vsb_2 = (SeekBar) findViewById(R.id.vsb_2);
        this.vsb_3 = (SeekBar) findViewById(R.id.vsb_3);
        this.vsb_4 = (SeekBar) findViewById(R.id.vsb_4);
        this.vsb_5 = (SeekBar) findViewById(R.id.vsb_5);

        this.txtVsb_1 = (TextView) findViewById(R.id.txt_vsb_1);
        this.txtVsb_2 = (TextView) findViewById(R.id.txt_vsb_2);
        this.txtVsb_3 = (TextView) findViewById(R.id.txt_vsb_3);
        this.txtVsb_4 = (TextView) findViewById(R.id.txt_vsb_4);
        this.txtVsb_5 = (TextView) findViewById(R.id.txt_vsb_5);
    }

    private void initMP() {
        this.mediaPlayer = new MediaPlayer();
        this.mediaPlayer.setOnCompletionListener(this);
    }

    private void updateEqualizer() {
        this.equalizer = new Equalizer(0, this.mediaPlayer.getAudioSessionId());
        this.equalizer.setEnabled(true);

        updateFrequencyBars(this.txtVsb_1, this.vsb_1, (short) 0);
        updateFrequencyBars(this.txtVsb_2, this.vsb_2, (short) 1);
        updateFrequencyBars(this.txtVsb_3, this.vsb_3, (short) 2);
        updateFrequencyBars(this.txtVsb_4, this.vsb_4, (short) 3);
        updateFrequencyBars(this.txtVsb_5, this.vsb_5, (short) 4);
    }

    private void updateFrequencyBars(TextView frequency, SeekBar seekBar, final short band) {
        frequency.setText(String.format("%d Hz", this.equalizer.getCenterFreq(band) / 1000));

        final short minEQLevel = this.equalizer.getBandLevelRange()[0];
        final short maxEQLevel = this.equalizer.getBandLevelRange()[1];

        seekBar.setMax(maxEQLevel - minEQLevel);
        seekBar.setProgress(this.equalizerPositions[band]);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                equalizer.setBandLevel(band, (short) (progress + minEQLevel));
                equalizerPositions[band] = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void updateVisualizer(final VisualizerView visualizerView) {
        if (this.visualizer != null) {
            this.visualizer.release();
        }

        this.visualizer = new Visualizer(this.mediaPlayer.getAudioSessionId());
        this.visualizer.setEnabled(false);
        this.visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        this.visualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
                visualizerView.updateVisualizer(bytes);
            }

            public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
            }
        }, Visualizer.getMaxCaptureRate() / 2, true, false);

        this.visualizer.setEnabled(true);
    }

    private void updateListSong(ListView parent, int position) {
        final SongListAdapter adapter = (SongListAdapter) parent.getAdapter();
        adapter.playingPosition = position;
        adapter.notifyDataSetChanged();
    }

    public void playSong(int songIndex) {
        // Play song
        try {
            final SongModel songModel = this.songsList.get(songIndex);
            this.mediaPlayer.reset();
            this.mediaPlayer.setAudioSessionId(songIndex);
            this.mediaPlayer.setDataSource(songModel.getPath());

            // Displaying Song title
            this.songTitle.setText(songModel.getTitle());
            this.songArtist.setText(songModel.getArtist());
            updatePager();

            // Changing Button Image to pause image
            this.btnPlay.setImageResource(R.drawable.stop);

            // set Progress bar values
            this.songSeekBar.setProgress(0);
            this.songSeekBar.setMax(100);

            this.mediaPlayer.prepare();
            this.mediaPlayer.start();
            // Updating progress bar
            updateBars();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updatePager() {
        final int index = (int) this.playerPager.getTag();

        this.playerPager.setCurrentItem(index, true);

        switch (index) {
            case Constants.COVER_PAGE:
                final Bitmap tempBitmap = ((SongModel) this.songsListView.getAdapter().getItem(this.currentSongIndex)).getAlbum();

                if (tempBitmap != null) {
                    ((SlideImageView) this.playerPager.findViewWithTag(Constants.COVER_PAGE_TAG)).setSource(tempBitmap);
                } else {
                    ((SlideImageView) this.playerPager.findViewWithTag(Constants.COVER_PAGE_TAG)).setSource(this.stubBitmap);
                }

                break;
            case Constants.BAR_PAGE:
                updateVisualizer(((BarVisualizerView) this.playerPager.findViewWithTag(Constants.BAR_PAGE_TAG)));
                break;
            case Constants.GD_PAGE:
                updateVisualizer(((GravityDefiedVisualizerView) this.playerPager.findViewWithTag(Constants.GD_PAGE_TAG)));
                break;
            case Constants.SEA_PAGE:
                updateVisualizer(((SeaBreathVisualizerView) this.playerPager.findViewWithTag(Constants.SEA_PAGE_TAG)));
                break;
            case Constants.PULSE_PAGE:
                updateVisualizer(((PulseVisualizerView) this.playerPager.findViewWithTag(Constants.PULSE_PAGE_TAG)));
                break;
        }

        updateEqualizer();
    }

    public void updateBars() {
        this.mainHandler.post(this.updateCurrentStateTask);
    }

    public class UpdateCurrentStateTask implements Runnable {
        public ProgressBar songItemProgressBar = null;

        public void run() {
            try {
                final long totalDuration = mediaPlayer.getDuration();
                final long currentDuration = mediaPlayer.getCurrentPosition();

                // Displaying time completed playing
                songCurrentDuration.setText(Utilities.milliSecondsToTimer(currentDuration));

                // Updating progress bar
                int progress = Utilities.getProgressPercentage(currentDuration, totalDuration);
                songSeekBar.setProgress(progress);
                songProgressBar.setProgress(progress);
                tintProgressBar.setLayoutParams(new RelativeLayout.LayoutParams((int) ((progress / 100f) * screenWidth), ViewGroup.LayoutParams.MATCH_PARENT));

                if ((((SongListAdapter) songsListView.getAdapter()).playingPosition != SongListAdapter.NOT_PLAYING) && (null != songItemProgressBar)) {
                    songItemProgressBar.setProgress(progress);
                }

                // Running this thread after 100 milliseconds
                mainHandler.post(this);
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
        if (fromTouch) {
            updateGlobalBar(seekBar);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // remove message Handler from updating progress bar
        mainHandler.removeCallbacks(updateCurrentStateTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        updateGlobalBar(seekBar);
    }

    private void updateGlobalBar(SeekBar seekBar) {
        this.mainHandler.removeCallbacks(this.updateCurrentStateTask);

        final int totalDuration = this.mediaPlayer.getDuration();
        final int currentPosition = Utilities.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        this.mediaPlayer.seekTo(currentPosition);

        // update timer progress again
        updateBars();
    }

    @Override
    public void onCompletion(MediaPlayer arg0) {

        // check for repeat is ON or OFF
        if (this.isLoop) {
            // repeat is on play same song again
            playSong(this.currentSongIndex);
        } else if (this.isRandom) {
            // shuffle is on - play a random song
            final Random rand = new Random();
            this.currentSongIndex = rand.nextInt((this.songsList.size() - 1) + 1);
            playSong(this.currentSongIndex);
        } else {
            // no repeat or shuffle ON - play next song
            if (this.currentSongIndex < (this.songsList.size() - 1)) {
                playSong(this.currentSongIndex + 1);
                this.currentSongIndex = this.currentSongIndex + 1;
            } else {
                // play first song
                playSong(0);
                this.currentSongIndex = 0;
            }
        }

        updateListSong(this.songsListView, this.currentSongIndex);
    }

    @Override
    public void onBackPressed() {
        if (this.doubleExitState) {
            super.onBackPressed();
            return;
        }

        this.doubleExitState = true;
        Toast.makeText(this, Constants.DOUBLE_EXIT_TITLE, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleExitState = false;
            }
        }, Constants.DOUBLE_EXIT_DURATION);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (this.mediaPlayer != null) {
            this.mediaPlayer.release();
        }

        if (this.visualizer != null) {
            this.visualizer.release();
        }

        if (this.equalizer != null) {
            this.equalizer.release();
        }
    }

    private class GetSongsListTask extends AsyncTask<ArrayList<SongModel>, Void, Void> {
        private TextView txtLoadedSongs;
        private MultiArcSpinner multiArcSpinner;

        private void initSplashUI() {
            this.txtLoadedSongs = (TextView) findViewById(R.id.txt_loaded_songs);

            this.multiArcSpinner = (MultiArcSpinner) findViewById(R.id.multi_arc_spinner);
            this.multiArcSpinner.setColors(getResources().getIntArray(R.array.neon_colors), false);
        }

        @Override
        protected void onPreExecute() {
            initSplashUI();
        }

        @Override
        protected Void doInBackground(ArrayList<SongModel>... songsList) {
            final SongsManager songsManager = new SongsManager();
            final ArrayList<String> pathsList = new ArrayList<>();

            songsManager.getPlayList(pathsList);
            songsManager.initPlayList(pathsList, songsList[0], this.txtLoadedSongs);

            return null;
        }

        @Override
        protected void onPostExecute(Void value) {
            initUI();
            initMP();
        }
    }

}