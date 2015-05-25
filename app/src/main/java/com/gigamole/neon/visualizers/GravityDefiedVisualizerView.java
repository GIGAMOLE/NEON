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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.animation.AnimationUtils;

import com.gigamole.neon.utils.Dynamics;

/**
 * Created by GIGAMOLE on 05.04.2015.
 */
public class GravityDefiedVisualizerView extends VisualizerView {

    public GravityDefiedVisualizerView(Context context) {
        super(context);
    }

    // Upper line and merge line (merge main and upper)
    private float[] upperPoints;
    private float[] mergePoints;

    // Our variables for calculating offset between neighbor points
    private int halfSize;
    private float offset = 0.8f;
    private float offsetCounter;

    private Paint linePaint = new Paint() {
        {
            setStyle(Style.FILL_AND_STROKE);
            setStrokeWidth(1.5f);
            setAntiAlias(true);
            setDither(true);
            setColor(Color.GREEN);
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

            this.mainPoints = new float[this.mainBytes.length * 4];
            this.upperPoints = new float[this.mainBytes.length * 4];
            this.mergePoints = new float[this.mainBytes.length * 4];

            // 30 it`s our distance between merge lines
            this.halfSize = ((this.mainBytes.length) / 30) / 2;
            this.offsetCounter = this.offset * this.halfSize;
        }

        for (int i = 0; i < this.mainBytes.length; i += 30) {
            if (i == 0) {
                // Get main line points
                this.mainPoints[i * 4] =
                        this.mainRect.width() * i / (this.mainBytes.length - 1);
                this.mainPoints[i * 4 + 1] =
                        this.mainRect.height() / 2 +
                                ((byte) (this.mainBytes[i] + 128)) * (this.mainRect.height() / 2) / 128;

                // Get upper line points
                this.upperPoints[i * 4] =
                        this.mainRect.width() * i / (this.mainBytes.length - 1) +
                                this.offsetCounter;
                this.upperPoints[i * 4 + 1] =
                        (this.mainRect.height() / 2 +
                                ((byte) (this.mainBytes[i] + 128)) *
                                        (this.mainRect.height() / 2) / 128) - 35;
            } else {
                this.mainPoints[i * 4] = this.mainPoints[(i - 30) * 4 + 2];
                this.mainPoints[i * 4 + 1] = this.mainPoints[(i - 30) * 4 + 3];

                this.upperPoints[i * 4] = this.upperPoints[(i - 30) * 4 + 2];
                this.upperPoints[i * 4 + 1] = this.upperPoints[(i - 30) * 4 + 3];
            }

            //
            this.mainPoints[i * 4 + 2] =
                    this.mainRect.width() * (i + 1) / (this.mainBytes.length - 1);
            this.mainPoints[i * 4 + 3] =
                    this.mainRect.height() / 2 +
                            ((byte) (this.mainBytes[i + 1] + 128)) * (this.mainRect.height() / 2) / 128;

            this.upperPoints[i * 4 + 2] =
                    this.mainRect.width() * (i + 1) / (this.mainBytes.length - 1) + this.offsetCounter;
            this.upperPoints[i * 4 + 3] =
                    (this.mainRect.height() / 2 +
                            ((byte) (this.mainBytes[i + 1] + 128)) * (this.mainRect.height() / 2) / 128) - 35;

            // Get merge line points
            this.mergePoints[i * 4] = this.mainPoints[i * 4];
            this.mergePoints[i * 4 + 1] = this.mainPoints[i * 4 + 1];
            this.mergePoints[i * 4 + 2] = this.upperPoints[i * 4];
            this.mergePoints[i * 4 + 3] = this.upperPoints[i * 4 + 1];

            this.offsetCounter -= this.offset;
        }

        // Refresh offset counter for next draws
        this.offsetCounter = this.halfSize * this.offset;

        // Set data for dynamics and float track changing
        setData(this.mainPoints);
        setUpperData(this.upperPoints);
        setMergeData(this.mergePoints);

        // Get dynamics points
        for (int i = 0; i < this.mainBytes.length; i += 30) {
            this.mainPoints[i * 4] = this.data[i * 4].getPosition();
            this.mainPoints[i * 4 + 1] = this.data[i * 4 + 1].getPosition();
            this.mainPoints[i * 4 + 2] = this.data[i * 4 + 2].getPosition();
            this.mainPoints[i * 4 + 3] = this.data[i * 4 + 3].getPosition();

            this.upperPoints[i * 4] = this.upperData[i * 4].getPosition();
            this.upperPoints[i * 4 + 1] = this.upperData[i * 4 + 1].getPosition();
            this.upperPoints[i * 4 + 2] = this.upperData[i * 4 + 2].getPosition();
            this.upperPoints[i * 4 + 3] = this.upperData[i * 4 + 3].getPosition();

            this.mergePoints[i * 4] = this.mergeData[i * 4].getPosition();
            this.mergePoints[i * 4 + 1] = this.mergeData[i * 4 + 1].getPosition();
            this.mergePoints[i * 4 + 2] = this.mergeData[i * 4 + 2].getPosition();
            this.mergePoints[i * 4 + 3] = this.mergeData[i * 4 + 3].getPosition();
        }

        // Draw out track
        canvas.drawLines(this.mainPoints, this.linePaint);
        canvas.drawLines(this.upperPoints, this.linePaint);
        canvas.drawLines(this.mergePoints, this.linePaint);
    }

    public GravityDefiedDynamics[] data;
    public GravityDefiedDynamics[] upperData;
    public GravityDefiedDynamics[] mergeData;

    public Runnable upperAnimator = new Runnable() {

        @Override
        public void run() {
            long now = AnimationUtils.currentAnimationTimeMillis();
            boolean needNewFrame = false;
            for (Dynamics dynamics : upperData) {
                dynamics.update(now);
                if (!dynamics.isAtRest()) {
                    needNewFrame = true;
                }
            }
            if (needNewFrame) {
                postDelayed(this, 5);
            }
            invalidate();
        }
    };

    public Runnable animator = new Runnable() {

        @Override
        public void run() {
            long now = AnimationUtils.currentAnimationTimeMillis();
            boolean needNewFrame = false;
            for (Dynamics dynamics : data) {
                dynamics.update(now);
                if (!dynamics.isAtRest()) {
                    needNewFrame = true;
                }
            }
            if (needNewFrame) {
                postDelayed(this, 5);
            }
            invalidate();
        }
    };

    public Runnable mergeAnimator = new Runnable() {

        @Override
        public void run() {
            long now = AnimationUtils.currentAnimationTimeMillis();
            boolean needNewFrame = false;
            for (Dynamics dynamics : mergeData) {
                dynamics.update(now);
                if (!dynamics.isAtRest()) {
                    needNewFrame = true;
                }
            }
            if (needNewFrame) {
                postDelayed(this, 5);
            }
            invalidate();
        }
    };

    public void setData(float[] newData) {
        long now = AnimationUtils.currentAnimationTimeMillis();
        if (this.data == null || this.data.length != newData.length) {
            this.data = new GravityDefiedDynamics[newData.length];

            for (int i = 0; i < newData.length; i++) {
                GravityDefiedDynamics dynamics = new GravityDefiedDynamics(this.mainRect.height() / 2, 0.8f);
                dynamics.setPosition(newData[i], now);
                dynamics.setTargetPosition(newData[i], now);

                this.data[i] = dynamics;
            }
            invalidate();
        } else {
            for (int i = 0; i < this.data.length; i++) {
                this.data[i].setTargetPosition(newData[i], now);
            }
        }

        removeCallbacks(this.animator);
        post(this.animator);
    }

    public void setUpperData(float[] newData) {
        long now = AnimationUtils.currentAnimationTimeMillis();
        if (this.upperData == null || this.upperData.length != newData.length) {
            this.upperData = new GravityDefiedDynamics[newData.length];

            for (int i = 0; i < newData.length; i++) {
                GravityDefiedDynamics dynamics = new GravityDefiedDynamics(this.mainRect.height() / 2, 0.8f);
                dynamics.setPosition(newData[i], now);
                dynamics.setTargetPosition(newData[i], now);

                this.upperData[i] = dynamics;
            }
            invalidate();
        } else {
            for (int i = 0; i < this.upperData.length; i++) {
                this.upperData[i].setTargetPosition(newData[i], now);
            }
        }

        removeCallbacks(this.upperAnimator);
        post(this.upperAnimator);
    }

    public void setMergeData(float[] newData) {
        long now = AnimationUtils.currentAnimationTimeMillis();
        if (this.mergeData == null || this.mergeData.length != newData.length) {
            this.mergeData = new GravityDefiedDynamics[newData.length];

            for (int i = 0; i < newData.length; i++) {
                GravityDefiedDynamics dynamics = new GravityDefiedDynamics(this.mainRect.height() / 2, 0.8f);
                dynamics.setPosition(newData[i], now);
                dynamics.setTargetPosition(newData[i], now);

                this.mergeData[i] = dynamics;
            }
            invalidate();
        } else {
            for (int i = 0; i < this.mergeData.length; i++) {
                this.mergeData[i].setTargetPosition(newData[i], now);
            }
        }

        removeCallbacks(this.mergeAnimator);
        post(this.mergeAnimator);
    }


    // This class for fastest track point changing
    private class GravityDefiedDynamics extends Dynamics {

        public GravityDefiedDynamics(float springiness, float dampingRatio) {
            super(springiness, dampingRatio);
            setSpeed(500f);
        }

    }
}
