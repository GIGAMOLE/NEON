package com.gigamole.neon.visualizers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.Shader;
import android.view.View;

/**
 * Created by GIGAMOLE on 05.04.2015.
 */
public class BarVisualizerView extends VisualizerView {

    public BarVisualizerView(Context context) {
        super(context);

        // Used for DashPathEffect
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    private float[] barBgPoints;

    // Set main bar paint
    private Paint barPaint = new Paint() {
        {
            setStyle(Style.STROKE);
            setStrokeWidth(14);
            setDither(true);
            setAntiAlias(true);
            PathEffect effects = new DashPathEffect(new float[]{3f, 3f}, 0);
            setPathEffect(effects);
        }
    };

    // Set paint for background bar
    private Paint barBgPaint = new Paint() {
        {
            setColor(Color.DKGRAY);
            setStyle(Style.STROKE);
            setStrokeWidth(14);
            setDither(true);
            setAntiAlias(true);
            PathEffect effects = new DashPathEffect(new float[]{3f, 3f}, 0);
            setPathEffect(effects);
        }
    };

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (this.mainBytes == null) {
            return;
        }

        if (this.mainPoints == null || this.mainPoints.length < this.mainBytes.length * 4) {
            this.mainRect.set(0, 0, getWidth(), getHeight());

            // Set linear gradient for song level simulation from green to red
            this.barPaint.setShader(new LinearGradient(
                            0,
                            0,
                            0,
                            this.mainRect.height(),
                            new int[]{Color.RED, Color.YELLOW, Color.GREEN},
                            null,
                            Shader.TileMode.MIRROR)
            );

            this.mainPoints = new float[this.mainBytes.length * 4];
            this.barBgPoints = new float[this.mainBytes.length * 4];
        }

        // Divide bytes array by 4 to low dividers count
        for (int i = 0; i < this.mainBytes.length / 4; i++) {
            this.mainPoints[i * 4] = i * 4 * 4;
            this.mainPoints[i * 4 + 2] = i * 4 * 4;
            this.mainPoints[i * 4 + 1] = this.mainRect.height();
            this.mainPoints[i * 4 + 3] =
                    this.mainRect.height() -
                            this.mainRect.height() / 2 +
                            ((byte) (this.mainBytes[i * 4] + 128)) * (this.mainRect.height() / 2) / 128;

            // Fill lines like | | | | |
            this.barBgPoints[i * 4] = i * 4 * 4;
            this.barBgPoints[i * 4 + 2] = i * 4 * 4;
            this.barBgPoints[i * 4 + 1] = this.mainRect.height();
            this.barBgPoints[i * 4 + 3] = 0;
        }

        // Draw our bars and background
        canvas.drawLines(this.barBgPoints, this.barBgPaint);
        canvas.drawLines(this.mainPoints, this.barPaint);
    }
}
