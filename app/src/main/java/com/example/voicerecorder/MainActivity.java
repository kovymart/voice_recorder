package com.example.voicerecorder;

import androidx.core.app.ActivityCompat;
import androidx.room.Room;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private ImageButton btnList;
    private ImageButton btnDone;
    private ImageButton btnDelete;
    private ArrayList<Float> amplitudes = new ArrayList<Float>();
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    private LinearLayout bottomSheet;
    private View bottomSheetBg;
    private TextInputEditText fileNameInput;
    private MaterialButton btnCancel;
    private MaterialButton btnSave;
    private AppDatabase db;
    private String duration = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "audioRecords").allowMainThreadQueries().build();

        bottomSheet = (LinearLayout) findViewById(R.id.bottomSheet);
        bottomSheetBg = (View) findViewById(R.id.bottomSheetBg);

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setPeekHeight(0);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        timer = new Timer(this::onTimerTick);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        tvTimer = (TextView) findViewById(R.id.tvTimer);
        waveFormView = (WaveformView) findViewById(R.id.waveFormView);

        btnRecord = (ImageButton) findViewById(R.id.btnRecord);
        btnRecord.setOnClickListener(this);
        btnList = (ImageButton) findViewById(R.id.btnList);
        btnList.setOnClickListener(this);
        btnDone = (ImageButton) findViewById(R.id.btnDone);
        btnDone.setOnClickListener(this);
        btnDelete = (ImageButton) findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(this);
        btnDelete.setEnabled(false);

        fileNameInput = (TextInputEditText) findViewById(R.id.fileNameInput);
        btnCancel = (MaterialButton) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);
        btnSave = (MaterialButton) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);

        bottomSheetBg.setOnClickListener(this);
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
                        btnDelete.setEnabled(true);
                        btnDelete.setImageDrawable(getResources().getDrawable(R.drawable.ic_delete, null));
                        btnList.setVisibility(View.GONE);
                        btnDone.setVisibility(View.VISIBLE);
                    }
                }
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                break;
            case R.id.btnList:
                startActivity(new Intent(this, GalleryActivity.class));
                break;
            case R.id.btnDone:
                stopRecorder();
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                bottomSheetBg.setVisibility(View.VISIBLE);
                fileNameInput.setText(fileName);
                break;
            case R.id.btnDelete:
                stopRecorder();
                new File(dirPath + "/" + fileName).delete();
                break;
            case R.id.btnCancel:
            case R.id.bottomSheetBg:
                new File(dirPath + "/" + fileName).delete();
                dismiss();
                break;
            case R.id.btnSave:
                dismiss();
                save();
                break;
        }

    }

    private void save() {
        String newFileName = fileNameInput.getText().toString();
        if (newFileName != fileName) {
            File newFile = new File(dirPath + "/" + newFileName + ".mp3");
            new File(dirPath + "/" + fileName).renameTo(newFile);
        }

        String filePath = dirPath + "/" + newFileName + ".mp3";
        long timestamp = new Date().getTime();
        String ampsPath = dirPath + "/" + newFileName;

        try {
            FileOutputStream fos = new FileOutputStream(ampsPath);
            ObjectOutputStream outputStream = new ObjectOutputStream(fos);
            outputStream.writeObject(amplitudes);
            fos.close();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        AudioRecord record = new AudioRecord(newFileName, filePath, timestamp, duration, ampsPath);
        db.audioRecordDao().insert(record);
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

    private void hideKeyboard(View view) {
        InputMethodManager iMM = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        iMM.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void dismiss() {
        bottomSheetBg.setVisibility(View.GONE);
        hideKeyboard(fileNameInput);

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED), 100);

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
        mediaRecorder.stop();
        mediaRecorder.release();
        isPaused = false;
        isRecording = false;
        btnList.setVisibility(View.VISIBLE);
        btnDone.setVisibility(View.GONE);
        btnDelete.setEnabled(false);
        btnDelete.setImageDrawable(getResources().getDrawable(R.drawable.ic_delete_disable, null));
        btnRecord.setImageDrawable(getResources().getDrawable(R.drawable.ic_record, null));
        tvTimer.setText("00:00.00");
        amplitudes = waveFormView.clear();
    }

    @Override
    public void onTimerTick(String duration) {
        tvTimer.setText(duration);
        this.duration = duration.substring(0, duration.length() - 3);
        waveFormView.addAmplitude((float) mediaRecorder.getMaxAmplitude());
    }
}