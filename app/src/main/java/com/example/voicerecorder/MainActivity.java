package com.example.voicerecorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity implements View.OnClickListener, Timer.OnTimerTickListener {

    private String recordPermission = Manifest.permission.RECORD_AUDIO;
    private int PERMISSION_CODE = 200;
    private ImageButton btnRecord;
    private MediaRecorder mediaRecorder;
    private String dirPath = "";
    private String fileName = "";
    private boolean isRecording = false;
    private boolean isPaused = false;
    private Timer timer;
    private TextView tvTimer;
    private Vibrator vibrator;
    private WaveformView waveFormView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timer = new Timer(this::onTimerTick);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        btnRecord = (ImageButton) findViewById(R.id.btnRecord);
        btnRecord.setOnClickListener(this);
        tvTimer = (TextView) findViewById(R.id.tvTimer);
        waveFormView = (WaveformView) findViewById(R.id.waveFormView);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRecord:
                if (isPaused) {
                    resumeRecorder();
                } else if (isRecording) {
                    pauseRecorder();
                } else {
                    if (checkPermissions()) {
                        startRecording();
                        btnRecord.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause, null));
                        isRecording = true;
                        isPaused = false;
                        timer.start();
                    }
                }
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                break;
        }

    }

    private boolean checkPermissions() {
        //Check permission
        if (ActivityCompat.checkSelfPermission(this, recordPermission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{recordPermission}, PERMISSION_CODE);
            return false;
        }
    }

    private void startRecording() {

        dirPath = this.getExternalFilesDir("/").getAbsolutePath();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.ENGLISH);
        Date now = new Date();
        fileName = "voice_record_" + formatter.format(now) + ".mp3";

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setOutputFile(dirPath + "/" + fileName);
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Start Recording
        mediaRecorder.start();
    }

    private void pauseRecorder() {
        mediaRecorder.pause();
        isPaused = true;
        btnRecord.setImageDrawable(getResources().getDrawable(R.drawable.ic_record, null));
        timer.pause();
    }

    private void resumeRecorder() {
        mediaRecorder.resume();
        isPaused = false;
        btnRecord.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause, null));
        timer.start();
    }

    private void stopRecorder() {
        timer.stop();
    }

    @Override
    public void onTimerTick(String duration) {
        tvTimer.setText(duration);
        waveFormView.addAmplitude((float) mediaRecorder.getMaxAmplitude());
    }
}