package com.example.voicerecorder;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;


public class WaveformView extends View {

    private Paint paint = new Paint();

    public WaveformView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setColor(Color.rgb(244, 81, 30));
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawRoundRect(new RectF(20f, 30f, 20 + 30f, 30f + 60f), 6f, 6f, paint);
    }
}
