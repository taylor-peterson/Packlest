package com.example.packlest;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ListViewItemViewHolder extends RecyclerView.ViewHolder {
    private CheckBox itemCheckbox;
    private TextView itemTextView;

    ListViewItemViewHolder(View itemView) {
        super(itemView);
    }

    public CheckBox getItemCheckbox() {
        return itemCheckbox;
    }

    void setItemCheckbox(CheckBox itemCheckbox) {
        this.itemCheckbox = itemCheckbox;
    }

    TextView getItemTextView() {
        return itemTextView;
    }

    void setItemTextView(TextView itemTextView) {
        this.itemTextView = itemTextView;
    }
}
