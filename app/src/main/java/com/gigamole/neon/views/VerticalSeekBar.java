package com.gigamole.neon.views;
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
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

/**
 * Created by GIGAMOLE on 17.05.2015.
 */
public class VerticalSeekBar extends SeekBar {

    protected OnSeekBarChangeListener changeListener;
    protected int x, y, z, w;

    public VerticalSeekBar(Context context) {
        super(context);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected synchronized void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);

        this.x = w;
        this.y = h;
        this.z = oldw;
        this.w = oldh;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Swap height and width
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    @Override
    protected void onDraw(Canvas c) {
        // Rotate our seek bar drawable
        c.rotate(-90);
        c.translate(-getHeight(), 0);

        super.onDraw(c);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setSelected(true);
                setPressed(true);
                if (this.changeListener != null) this.changeListener.onStartTrackingTouch(this);
                break;
            case MotionEvent.ACTION_UP:
                setSelected(false);
                setPressed(false);
                if (this.changeListener != null) this.changeListener.onStopTrackingTouch(this);
                break;
            case MotionEvent.ACTION_MOVE:
                int progress = getMax() - (int) (getMax() * event.getY() / getHeight());
                setProgress(progress);
                onSizeChanged(getWidth(), getHeight(), 0, 0);
                if (this.changeListener != null)
                    this.changeListener.onProgressChanged(this, progress, true);
                break;

            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

    @Override
    public synchronized void setOnSeekBarChangeListener(OnSeekBarChangeListener listener) {
        this.changeListener = listener;
    }

    @Override
    public synchronized void setProgress(int progress) {
        if (progress >= 0) {
            super.setProgress(progress);
        } else {
            super.setProgress(0);
        }

        onSizeChanged(this.x, this.y, this.z, this.w);

        if (this.changeListener != null) {
            this.changeListener.onProgressChanged(this, progress, false);
        }
        invalidate();
    }
}
