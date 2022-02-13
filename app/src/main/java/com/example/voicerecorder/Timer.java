package com.example.voicerecorder;

import android.os.Handler;

public class Timer {

    interface OnTimerTickListener {
        void onTimerTick(String duration);
    }

    private Long duration = 0L;
    private final Long delay = 100L;
    private Handler myHandler = new Handler();
    private Runnable runnable;

    public Timer(OnTimerTickListener listener) {
        runnable = () -> {
            duration += delay;
            myHandler.postDelayed(runnable, delay);
            listener.onTimerTick(format());
        };

    }

    public void start() {
        myHandler.postDelayed(runnable, delay);
    }

    public void pause() {
        myHandler.removeCallbacks(runnable);
    }

    public void stop() {
        myHandler.removeCallbacks(runnable);
        duration = 0L;
    }

    public String format() {
        Long millis = duration % 1000;
        Long seconds = (duration / 1000) % 60;
        Long minutes = (duration / (1000 * 60)) % 60;
        Long hours = (duration / (1000 * 60 * 60));
        String formatted = "";
        if (hours > 0) {
            formatted = String.format("%02d:%02d:%02d.%02d", hours, minutes, seconds, millis / 10);
        } else {
            formatted = String.format("%02d:%02d.%02d", minutes, seconds, millis / 10);
        }

        return formatted;
    }
}
