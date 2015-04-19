package com.bbbd.treasurehunt.location;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.location.Location;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.bbbd.treasurehunt.CompassActivity;

/**
 * Created by Daniel on 2015-03-25.
 */
public class CompassView extends SurfaceView implements SurfaceHolder.Callback {

    private Path path;
    private SurfaceHolder holder;
    private DrawingThread thread;
    private Paint pathPaint, borderPaint;

    private Location target;
    private CompassActivity activity;
    private Compass compass;
    private float width, height, arrowHeight, arrowWidth;

    public CompassView(CompassActivity activity, Compass compass, Location target) {
        super(activity);
        this.activity = activity;
        this.compass = compass;
        this.target = target;
        init();
    }

    public void setTargetLocation(Location location) {
        this.target = location;
    }

    private void init() {
        holder = getHolder();
        holder.addCallback(this);
        setFocusable(true);

        setBackgroundColor(Color.TRANSPARENT);
        setZOrderOnTop(true);
        holder.setFormat(PixelFormat.TRANSPARENT);
    }

    private void initGraphical() {
        width = getWidth();
        height = getHeight();
        arrowWidth = width / 4f;
        arrowHeight = width / 3f;

        pathPaint = new Paint();
        pathPaint.setColor(Color.RED);
        pathPaint.setStyle(Paint.Style.FILL);

        borderPaint = new Paint();
        borderPaint.setColor(Color.BLACK);
        borderPaint.setStrokeWidth(6f);
        borderPaint.setStyle(Paint.Style.STROKE);

        path = new Path();
        path.moveTo(width / 2f, height / 2f - arrowHeight);
        path.lineTo(width / 2f + arrowWidth, height * 0.55f);
        path.lineTo(width / 2f + arrowWidth / 3f, height * 0.55f);

        path.lineTo(width / 2f + arrowWidth / 3f, height * 0.7f);
        path.lineTo(width / 2f - arrowWidth / 3f, height * 0.7f);

        path.lineTo(width / 2f - arrowWidth / 3f, height * 0.55f);

        path.lineTo(width / 2f - arrowWidth / 3f, height * 0.55f);
        path.lineTo(width / 2f - arrowWidth, height * 0.55f);
        path.lineTo(width / 2f, height / 2f - arrowHeight);
        path.close();
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
        float bearing = activity.getLastLocation().bearingTo(target);
        float deg = bearing - compass.getDegrees();
        canvas.save();
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        canvas.rotate(deg, width / 2f, height / 2f);
        canvas.drawPath(path, pathPaint);
        canvas.drawPath(path, borderPaint);
        canvas.restore();
    }
}
