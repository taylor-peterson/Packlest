package com.example.packlest;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatCheckBox;

// Code modified from https://stackoverflow.com/questions/16511535/creating-a-three-states-checkbox-on-android.
class CheckBoxTriState extends AppCompatCheckBox {
    private CHECKBOX_STATE state;

    CheckBoxTriState(Context context, AttributeSet attrs) {
        super(context, attrs);

        state = CHECKBOX_STATE.UNADDED;
        updateButton();

        setOnCheckedChangeListener((buttonView, isChecked) -> cycleButtonState());
    }

    private void cycleButtonState() {
        switch (state) {
            case UNADDED:
                state = CHECKBOX_STATE.UNCHECKED;
                break;
            case UNCHECKED:
                state = CHECKBOX_STATE.CHECKED;
                break;
            case CHECKED:
                state = CHECKBOX_STATE.UNADDED;
                break;
        }
        updateButton();
    }

    static CHECKBOX_STATE reverseCycleButtonState(CHECKBOX_STATE checkboxState) {
        switch (checkboxState) {
            case UNADDED:
                checkboxState = CHECKBOX_STATE.CHECKED;
                break;
            case UNCHECKED:
                checkboxState = CHECKBOX_STATE.UNADDED;
                break;
            case CHECKED:
                checkboxState = CHECKBOX_STATE.UNCHECKED;
                break;
        }
        return checkboxState;
    }

    private void updateButton() {
        int btnDrawable = R.drawable.ic_baseline_indeterminate_check_box_24px;
        switch (state) {
            // case UNADDED handled by initialization.
            case UNCHECKED:
                btnDrawable = R.drawable.ic_baseline_check_box_outline_blank_24px;
                break;
            case CHECKED:
                btnDrawable = R.drawable.ic_baseline_check_box_24px;
                break;
        }
        setButtonDrawable(btnDrawable);
    }

    CHECKBOX_STATE getState() {
        return state;
    }

    void setState(CHECKBOX_STATE newState) {
        state = newState;
        updateButton();
    }
}