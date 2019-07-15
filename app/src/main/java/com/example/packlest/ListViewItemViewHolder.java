package com.example.packlest;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ListViewItemViewHolder extends RecyclerView.ViewHolder {
    private CheckBoxTriState itemCheckbox;
    private TextView itemTextView;

    ListViewItemViewHolder(View itemView) {
        super(itemView);
    }

    CheckBoxTriState getItemCheckbox() {
        return itemCheckbox;
    }

    void setItemCheckbox(CheckBoxTriState itemCheckbox) {
        this.itemCheckbox = itemCheckbox;
    }

    TextView getItemTextView() {
        return itemTextView;
    }

    void setItemTextView(TextView itemTextView) {
        this.itemTextView = itemTextView;
    }
}
