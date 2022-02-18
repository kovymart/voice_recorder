package com.example.voicerecorder;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.chip.Chip;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class AudioPlayerActivity extends AppCompatActivity implements View.OnClickListener {

    private MediaPlayer mediaPlayer;
    private ImageButton btnPlay;
    private ImageButton btnBackward;
    private ImageButton btnForward;
    private Chip speedChip;
    private SeekBar seekBar;
    private Runnable runnable;
    private Handler handler;
    private final long delay = 1000L;
    private int jumpVal = 1000;
    private float playBackSpeed = 1.0f;
    private TextView tvFilename;
    private TextView tvTrackProgress;
    private TextView tvTrackDuration;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);

        String filePath = getIntent().getStringExtra("filePath");
        String fileName = getIntent().getStringExtra("fileName");

        btnPlay = (ImageButton) findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(this);

        btnBackward = (ImageButton) findViewById(R.id.btnBackward);
        btnBackward.setOnClickListener(this);

        btnForward = (ImageButton) findViewById(R.id.btnForward);
        btnForward.setOnClickListener(this);

        speedChip = (Chip) findViewById(R.id.chip);
        speedChip.setOnClickListener(this);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        tvFilename = (TextView) findViewById(R.id.tvFilename);
        tvFilename.setText(fileName);

        toolbar = (MaterialToolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        tvTrackProgress = (TextView) findViewById(R.id.tvTrackProgress);
        tvTrackDuration = (TextView) findViewById(R.id.tvTrackDuration);

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        tvTrackDuration.setText(dateFormat(mediaPlayer.getDuration()));

        handler = new Handler(Looper.getMainLooper());
        runnable = () -> {
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            tvTrackProgress.setText(dateFormat(mediaPlayer.getCurrentPosition()));
            handler.postDelayed(runnable, delay);
        };
        playPausePlayer();
        seekBar.setMax(mediaPlayer.getDuration());

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                btnPlay.setBackground(getResources().getDrawable(R.drawable.ic_play_circle, null));
                handler.removeCallbacks(runnable);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPlay:
                playPausePlayer();
                break;
            case R.id.btnForward:
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + jumpVal);
                seekBar.setProgress(seekBar.getProgress() + jumpVal);
                break;
            case R.id.btnBackward:
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - jumpVal);
                seekBar.setProgress(seekBar.getProgress() - jumpVal);
                break;
            case R.id.chip:
                if (playBackSpeed != 2f) {
                    playBackSpeed += 0.5f;
                } else {
                    playBackSpeed = 0.5f;
                }
                mediaPlayer.setPlaybackParams(new PlaybackParams().setSpeed(playBackSpeed));
                speedChip.setText("x " + playBackSpeed);
                break;

        }
    }

    private void playPausePlayer() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();

            btnPlay.setBackground(getResources().getDrawable(R.drawable.ic_pause_circle, null));
            handler.postDelayed(runnable, delay);
        } else {
            mediaPlayer.pause();
            btnPlay.setBackground(getResources().getDrawable(R.drawable.ic_play_circle, null));
            handler.removeCallbacks(runnable);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mediaPlayer.stop();
        mediaPlayer.release();
        handler.removeCallbacks(runnable);
    }

    private String dateFormat(int duration) {
        int d = duration / 1000;
        int s = d % 60;
        int m = ((d / 60) % 60);
        int h = (int) ((d - (m * 60)) / 360);

        NumberFormat f = new DecimalFormat("00");
        String str = String.format("%s:%s", m, f.format(s));
        if (h > 0) {
            str = String.format("%s: %s", h, str);
        }
        return str;
    }
}