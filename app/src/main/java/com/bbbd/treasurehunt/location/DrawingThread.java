package com.bbbd.treasurehunt.location;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * Created by Daniel on 2015-03-25.
 */
public class DrawingThread extends Thread {
    private CompassView view;
    private boolean running = false;

    public void setRunning(boolean running) {
        this.running = running;
    }

    public DrawingThread(CompassView view) {
        super();
        this.view = view;

    }

    @Override
    public void run() {
        while (running) {
            SurfaceHolder holder = view.getHolder();
            Canvas canvas = holder.lockCanvas();
            if (canvas != null) {
                synchronized (holder) {
                    view.drawCompass(canvas);
                }
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }
}
