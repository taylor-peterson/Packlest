package com.example.packlest;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatCheckBox;

// Code modified from https://stackoverflow.com/questions/16511535/creating-a-three-states-checkbox-on-android.
public class CheckBoxTriState extends AppCompatCheckBox {
    enum CHECKBOX_STATE {
        CHECKED,
        UNCHECKED,
        UNADDED,
    }

    private CHECKBOX_STATE state;

    public CheckBoxTriState(Context context) {
        super(context);
        init();
    }

    public CheckBoxTriState(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CheckBoxTriState(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        state = CHECKBOX_STATE.UNADDED;
        updateButton();

        setOnCheckedChangeListener((buttonView, isChecked) -> {
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
        });

    }

    private void updateButton() {
        int btnDrawable = R.drawable.ic_baseline_indeterminate_check_box_24px;
        switch (state) {
            case UNADDED:
                btnDrawable = R.drawable.ic_baseline_indeterminate_check_box_24px;
                break;
            case UNCHECKED:
                btnDrawable = R.drawable.ic_baseline_check_box_outline_blank_24px;
                break;
            case CHECKED:
                btnDrawable = R.drawable.ic_baseline_check_box_24px;
                break;
        }
        setButtonDrawable(btnDrawable);
    }

    public CHECKBOX_STATE getState() {
        return state;
    }

    public void setState(CHECKBOX_STATE newState) {
        state = newState;
        updateButton();
    }
}