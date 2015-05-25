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
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;

import com.gigamole.neon.R;


/**
 * Created by GIGAMOLE on 05.04.2015.
 */
public class SeaBreathVisualizerView extends VisualizerView {

    public SeaBreathVisualizerView(Context context) {
        super(context);
    }

    private Paint seaPaint = new Paint(Paint.ANTI_ALIAS_FLAG) {
        {
            setStyle(Style.STROKE);
            setStrokeCap(Cap.ROUND);
            setStrokeWidth(20);
            setMaskFilter(new BlurMaskFilter(25, BlurMaskFilter.Blur.NORMAL));
        }
    };

    private Paint sandPaint = new Paint(Paint.ANTI_ALIAS_FLAG) {
        {
            setColor(Color.argb(220, 192, 169, 128));
            setStyle(Style.STROKE);
            setStrokeCap(Cap.ROUND);
            setMaskFilter(new BlurMaskFilter(25, BlurMaskFilter.Blur.NORMAL));
        }
    };

    private Paint flowPaint = new Paint(Paint.ANTI_ALIAS_FLAG) {
        {
            setColor(Color.argb(200, 227, 242, 249));
            setStyle(Style.STROKE);
            setStrokeWidth(10);
            setStrokeCap(Cap.ROUND);
            setMaskFilter(new BlurMaskFilter(15, BlurMaskFilter.Blur.NORMAL));
        }
    };

    private Paint wetSandPaint = new Paint() {
        {
            setColor(Color.argb(200, 192, 169, 128));
            setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
        }
    };

    private Bitmap wetSandBitmap;
    private Canvas wetSandCanvas;
    private Bitmap sandBitmap;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (this.mainBytes == null) {
            return;
        }

        if (this.mainPoints == null || this.mainPoints.length < this.mainBytes.length * 4) {
            this.mainRect.set(0, 0, getWidth(), getHeight());

            this.mainPoints = new float[this.mainBytes.length * 4];
            this.sandBitmap = scaleCropToFit(
                    BitmapFactory.decodeResource(getResources(),
                            R.drawable.bg_sand),
                    this.mainRect.width(),
                    this.mainRect.height()
            );
        }

        if (this.wetSandBitmap == null) {
            this.wetSandBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
        }
        if (this.wetSandCanvas == null) {
            this.wetSandCanvas = new Canvas(this.wetSandBitmap);
        }

        // Draw sand bg and multiply other draws for all picture
        canvas.drawBitmap(this.sandBitmap, new Matrix(), null);

        final Path path = new Path();

        for (int i = 0; i < this.mainBytes.length / 4; i++) {
            this.mainPoints[i * 4] = i * 4 * 4 - 5;
            this.mainPoints[i * 4 + 2] = i * 4 * 4 + 5;
            this.mainPoints[i * 4 + 1] = this.mainRect.height();
            this.mainPoints[i * 4 + 3] =
                    this.mainRect.height() -
                            mainRect.height() / 2 +
                            ((byte) (mainBytes[i * 4] + 128)) * (mainRect.height() / 2) / 128;

            this.wetSandCanvas.drawRect(
                    this.mainPoints[i * 4],
                    this.mainPoints[i * 4 + 3],
                    this.mainPoints[i * 4 + 2],
                    this.mainPoints[i * 4 + 1],
                    this.sandPaint
            );
        }
        // Draw wet sand
        this.wetSandCanvas.drawPaint(this.wetSandPaint);
        canvas.drawBitmap(this.wetSandBitmap, new Matrix(), null);

        // Apply gradient to sea from dark blue to shine white flow
        for (int i = 0; i < this.mainBytes.length / 4; i++) {
            this.seaPaint.setShader(new LinearGradient(
                            0,
                            this.mainPoints[i * 4 + 3],
                            0,
                            this.mainPoints[i * 4 + 1],
                            new int[]{
                                    Color.argb(120, 227, 242, 249),
                                    Color.argb(120, 80, 202, 247),
                                    Color.argb(120, 0, 119, 242),
                                    Color.argb(120, 2, 51, 144),
                            },
                            null,
                            Shader.TileMode.MIRROR
                    )
            );

            canvas.drawRect(
                    this.mainPoints[i * 4],
                    this.mainPoints[i * 4 + 3],
                    this.mainPoints[i * 4 + 2],
                    this.mainPoints[i * 4 + 1],
                    this.seaPaint
            );

            // If path not moved need to move and then make line
            if (i == 0) {
                path.moveTo(this.mainPoints[i * 4] + 5, this.mainPoints[i * 4 + 3]);
            } else {
                path.lineTo(this.mainPoints[i * 4] + 5, this.mainPoints[i * 4 + 3]);
            }
        }

        canvas.drawPath(path, this.flowPaint);
    }

    // Get bitmap which scaled and fit to view size
    private Bitmap scaleCropToFit(Bitmap original, int targetWidth, int targetHeight) {
        // Need to scale the image, keeping the aspect ration first
        int width = original.getWidth();
        int height = original.getHeight();

        float widthScale = (float) targetWidth / (float) width;
        float heightScale = (float) targetHeight / (float) height;
        float scaledWidth;
        float scaledHeight;

        int startY = 0;
        int startX = 0;

        if (widthScale > heightScale) {
            scaledWidth = targetWidth;
            scaledHeight = height * widthScale;
            // Crop height by...
            startY = (int) ((scaledHeight - targetHeight) / 2);
        } else {
            scaledHeight = targetHeight;
            scaledWidth = width * heightScale;
            // Crop width by..
            startX = (int) ((scaledWidth - targetWidth) / 2);
        }

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(original, (int) scaledWidth, (int) scaledHeight, true);
        return Bitmap.createBitmap(scaledBitmap, startX, startY, targetWidth, targetHeight);
    }
}
