package com.example.voicerecorder;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


public class WaveformView extends View {

    private Paint paint = new Paint();
    private ArrayList<Float> amplitudes = new ArrayList<Float>();
    private ArrayList<RectF> spikes = new ArrayList<RectF>();

    private float radius = 6f;
    private float spikeWidth = 9f;
    private float screenWidth;
    private float screenHeight = 400f;
    private float d = 6f;
    private int maxSpikes = 0;

    public WaveformView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setColor(Color.rgb(244, 81, 30));
        screenWidth = (float) (getResources().getDisplayMetrics().widthPixels);
        maxSpikes = (int) (screenWidth / (spikeWidth + d));
    }

    public void addAmplitude(float amp) {
        Float norm = (float) Math.min(((int) amp) / 7, 400);
        amplitudes.add(norm);
        spikes.clear();
        List<Float> amps = amplitudes.subList(Math.max(amplitudes.size() - maxSpikes, 0), amplitudes.size());
        for (int i = 0; i < amps.size(); i++) {
            float left = screenWidth - (i * (spikeWidth + d));
            float top = ((screenHeight / 2) - (amps.get(i) / 2));
            float right = left + spikeWidth;
            float bottom = top + amps.get(i);
            spikes.add(new RectF(left, top, right, bottom));
        }
        invalidate();
    }

    public ArrayList<Float> clear() {
        ArrayList<Float> amps = (ArrayList<Float>) amplitudes.clone();
        amplitudes.clear();
        spikes.clear();
        invalidate();

        return amps;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        spikes.forEach((item) -> canvas.drawRoundRect(item, radius, radius, paint));
    }
}
