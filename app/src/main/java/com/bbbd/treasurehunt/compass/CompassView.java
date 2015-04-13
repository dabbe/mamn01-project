package com.bbbd.treasurehunt.compass;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Daniel on 2015-03-25.
 */
public class CompassView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String[] cardinal = {"North", "Northeast", "East", "Southeast", "South", "Southwest", "West", "Northwest"};
    private static final String[] cardinalShort = {"N", "E", "S", "W"};
    private Path topPath, botPath;
    private SurfaceHolder holder;
    private DrawingThread thread;
    private Paint paint, topPathPaint, botPathPaint, middlePaint, backgroundPaint, linePaint;

    private Compass compass;
    private float width, height, arrowHeight, arrowWidth, radius;


    public CompassView(Context context, Compass compass) {
        super(context);
        this.compass = compass;
        init();
    }

    private void init() {
        holder = getHolder();
        holder.addCallback(this);
        setFocusable(true);
    }

    private void initGraphical() {
        width = getWidth();
        height = getHeight();
        arrowWidth = width / 15f;
        arrowHeight = width / 2.3f;
        radius = width / 2.1f;

        paint = new Paint();
        paint.setTextSize(150);
        paint.setColor(Color.RED);

        topPathPaint = new Paint();
        topPathPaint.setColor(Color.RED);
        topPathPaint.setStyle(Paint.Style.FILL);

        botPathPaint = new Paint();
        botPathPaint.setColor(Color.WHITE);
        botPathPaint.setStyle(Paint.Style.FILL);

        middlePaint = new Paint();
        middlePaint.setColor(Color.BLACK);
        middlePaint.setStyle(Paint.Style.FILL);
        middlePaint.setStrokeWidth(width * 0.008f);
        middlePaint.setTextSize(120);

        linePaint = new Paint();
        linePaint.setColor(Color.BLACK);
        linePaint.setStrokeWidth(width * 0.015f);

        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.LTGRAY);
        backgroundPaint.setStyle(Paint.Style.FILL);


        topPath = new Path();
        topPath.moveTo(width / 2f, height / 2f - arrowHeight);
        topPath.lineTo(width / 2f + arrowWidth, height / 2f);
        topPath.lineTo(width / 2f - arrowWidth, height / 2f);
        topPath.lineTo(width / 2f, height / 2f - arrowHeight);
        topPath.close();

        botPath = new Path();
        botPath.moveTo(width / 2f, height / 2f + arrowHeight);
        botPath.lineTo(width / 2f + arrowWidth, height / 2f);
        botPath.lineTo(width / 2f - arrowWidth, height / 2f);
        botPath.lineTo(width / 2f, height / 2f + arrowHeight);
        botPath.close();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        initGraphical();

        thread = new DrawingThread(this);
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
                compass.stop();
            } catch (InterruptedException e) {
            }
        }
    }

    public void drawCompass(Canvas canvas) {
       /* float oldDeg = deg;
        float tmp = compass.getDegrees() - oldDeg;
        if (Math.abs(tmp) < 1.5f) tmp = 0;
        deg = (oldDeg + (tmp) * 0.1f)%360;*/
        float deg = compass.getDegrees();
        canvas.save();
        canvas.drawColor(Color.BLACK);
        canvas.drawCircle(width / 2f, height / 2f, radius, backgroundPaint);
        canvas.rotate(-deg, width / 2f, height / 2f);
        for (int i = 0; i < 4; i++) {
            canvas.drawLine(width / 2f, height / 2f - radius, width / 2f, height / 2f - radius + height * 0.05f, linePaint);
            canvas.drawText(cardinalShort[i], width / 2f - middlePaint.measureText(cardinalShort[i]) / 2, height / 2f - radius + height * 0.08f, middlePaint);
            canvas.rotate(360 / 4, width / 2f, height / 2f);
        }
        for (int i = 0; i < 32; i++) {
            canvas.rotate(360 / 32f, width / 2f, height / 2f);
            canvas.drawLine(width / 2f, height / 2f + radius, width / 2f, height / 2f + radius - height * 0.03f, middlePaint);
        }
        canvas.restore();

        canvas.drawPath(topPath, topPathPaint);
        canvas.drawPath(botPath, botPathPaint);
        canvas.drawCircle(width / 2f, height / 2f, width / 25f, middlePaint);
    }
}
