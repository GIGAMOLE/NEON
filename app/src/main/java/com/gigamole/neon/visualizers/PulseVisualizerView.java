package com.gigamole.neon.visualizers;
/*
 * Copyright (C) 2015 Basil Miller
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import java.util.Random;

/**
 * Created by GIGAMOLE on 05.04.2015.
 */
public class PulseVisualizerView extends VisualizerView {

    public PulseVisualizerView(Context context) {
        super(context);
    }

    private Bitmap fadedPulseBitmap;
    private Canvas fadedPulseCanvas;

    private float amplitude = 0;

    private Paint pulsePaint = new Paint() {
        {
            setStrokeWidth(1f);
            setAntiAlias(true);
        }
    };

    private Paint blurPaint = new Paint() {
        {
            setStrokeWidth(3f);
            setMaskFilter(new BlurMaskFilter(5, BlurMaskFilter.Blur.NORMAL));
        }
    };

    private Paint flashPaint = new Paint() {
        {
            setStrokeWidth(5f);
            setAntiAlias(true);
        }
    };

    private Paint fadePaint = new Paint() {
        {
            setColor(Color.argb(150, 255, 255, 255));
            setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
        }
    };

    // Get random color in argb
    private int randomColor() {
        final Random r = new Random();
        return Color.argb(r.nextInt(255 - 120 + 1) + 120, r.nextInt(255), r.nextInt(255), r.nextInt(255));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (this.mainBytes == null) {
            return;
        }

        if (this.mainPoints == null || this.mainPoints.length < this.mainBytes.length * 4) {
            this.mainRect.set(0, 0, getWidth(), getHeight());
            this.mainPoints = new float[this.mainBytes.length * 4];
        }

        if (this.fadedPulseBitmap == null) {
            this.fadedPulseBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
        }
        if (this.fadedPulseCanvas == null) {
            this.fadedPulseCanvas = new Canvas(this.fadedPulseBitmap);
        }

        // Set random colors
        this.flashPaint.setColor(randomColor());
        this.blurPaint.setColor(randomColor());
        this.pulsePaint.setColor(randomColor());

        for (int i = 0; i < this.mainBytes.length - 1; i++) {
            this.mainPoints[i * 4] =
                    this.mainRect.width() * i / (this.mainBytes.length - 1);
            this.mainPoints[i * 4 + 1] =
                    this.mainRect.height() / 2 +
                            ((byte) (this.mainBytes[i] + 128)) * (this.mainRect.height() / 2) / 128;
            this.mainPoints[i * 4 + 2] =
                    this.mainRect.width() * (i + 1) / (this.mainBytes.length - 1);
            this.mainPoints[i * 4 + 3] =
                    this.mainRect.height() / 2 +
                            ((byte) (this.mainBytes[i + 1] + 128)) * (this.mainRect.height() / 2) / 128;
        }

        float accumulator = 0;
        for (int i = 0; i < this.mainBytes.length - 1; i++) {
            accumulator += Math.abs(this.mainBytes[i]);
        }

        float amp = accumulator / (128 * this.mainBytes.length);
        if (amp > this.amplitude) {
            // Amplitude is bigger than normal, make a prominent line
            this.amplitude = amp;
            this.fadedPulseCanvas.drawLines(this.mainPoints, this.flashPaint);
        } else {
            // Amplitude is nothing special, reduce the amplitude
            this.amplitude *= 0.99;
            this.fadedPulseCanvas.drawLines(this.mainPoints, this.blurPaint);
            this.fadedPulseCanvas.drawLines(this.mainPoints, this.pulsePaint);
        }

        this.fadedPulseCanvas.drawPaint(this.fadePaint);

        canvas.drawBitmap(this.fadedPulseBitmap, new Matrix(), null);
    }
}


//---------------------------------------------------------------------------------------
//Bezier curve wave
//        final Path path = new Path();
//        final Point mid = new Point();
//
//        path.reset();
//
//        for (int i = 0; i < mainBytes.length - 1; i += 50) {
//
//            if (i == 0) {
//                mainPoints[i * 4] = mainRect.width() * i / (mainBytes.length - 1);
//                mainPoints[i * 4 + 1] = mainRect.height() / 2 + ((byte) (mainBytes[i] + 128)) * (mainRect.height() / 2) / 128;
//
//                path.moveTo(mainPoints[i * 4], mainPoints[i * 4 + 1]);
//            } else {
//                mainPoints[i * 4] = mainPoints[(i - 50) * 4 + 2];
//                mainPoints[i * 4 + 1] = mainPoints[(i - 50) * 4 + 3];
//            }
//
//            mainPoints[i * 4 + 2] = mainRect.width() * (i + 1) / (mainBytes.length - 1);
//            mainPoints[i * 4 + 3] = mainRect.height() / 2 + ((byte) (mainBytes[i + 1] + 128)) * (mainRect.height() / 2) / 128;
//
//            mid.set((int) (mainPoints[i * 4] + mainPoints[i * 4 + 2]) / 2, (int) (mainPoints[i * 4 + 1] + mainPoints[i * 4 + 3]) / 2);
//            path.quadTo((mainPoints[i * 4] + mid.x) / 2, mainPoints[i * 4 + 1], mid.x, mid.y);
//            path.quadTo((mid.x + mainPoints[i * 4 + 2]) / 2, mainPoints[i * 4 + 3], mainPoints[i * 4 + 2], mainPoints[i * 4 + 3]);
//        }
