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
