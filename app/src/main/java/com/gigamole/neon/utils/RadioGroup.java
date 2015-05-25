package com.gigamole.neon.utils;

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