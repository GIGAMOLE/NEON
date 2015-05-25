package com.gigamole.neon.utils;
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

import android.app.Activity;
import android.view.View;
import android.widget.RadioButton;

import java.util.List;

/**
 * Created by GIGAMOLE on 11.05.2015.
 */

// This class for allowing only one checked radio button in group
public class RadioGroup implements View.OnClickListener {

    List<RadioButton> radios;
    RadioButton selectedButton;
    Activity activity;
    CustomRadioGroupListener customListener;

    public RadioGroup(List<RadioButton> radios, Activity activity) {
        this.radios = radios;
        this.activity = activity;
        for (RadioButton rb : radios) {
            rb.setOnClickListener(this);
        }
    }

    public RadioGroup(List<RadioButton> radios, Activity activity, CustomRadioGroupListener prgl) {
        this.radios = radios;
        this.activity = activity;
        this.customListener = prgl;
        for (RadioButton rb : radios) {
            rb.setOnClickListener(this);
        }
    }

    public void setCustomListener(CustomRadioGroupListener customListener) {
        this.customListener = customListener;
    }

    public boolean isAnyButtonSelected() {
        for (RadioButton rb : this.radios) {
            if (rb.isSelected()) {
                return true;
            }
        }
        return false;
    }

    public RadioButton getSelectedButton() {
        return this.selectedButton;
    }

    @Override
    public void onClick(View v) {
        this.selectedButton = (RadioButton) this.activity.findViewById(v.getId());
        for (RadioButton rb : this.radios) {
            if (rb != this.selectedButton) {
                rb.setChecked(false);
            }
        }
        this.customListener.onClick(v);
    }

    public interface CustomRadioGroupListener {

        public void onClick(View v);

    }
}