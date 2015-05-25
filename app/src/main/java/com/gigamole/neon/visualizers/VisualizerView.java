package com.gigamole.neon.visualizers;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

/**
 * Created by GIGAMOLE on 05.04.2015.
 */
public class VisualizerView extends View {

    // Our main bytes and points array
    public byte[] mainBytes;
    public float[] mainPoints;

    // Main rectangle for draw
    public Rect mainRect = new Rect();

    public VisualizerView(Context context) {
        super(context);
        init();
    }

    public void init() {
        this.mainBytes = null;
    }

    // Visualizer updater
    public void updateVisualizer(byte[] bytes) {
        this.mainBytes = bytes;
        invalidate();
    }
}
