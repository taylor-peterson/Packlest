package com.example.packlest;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

class ItemInstanceViewHolder extends RecyclerView.ViewHolder {
    private CheckBoxTriState itemCheckbox;
    private TextView itemTextView;

    ItemInstanceViewHolder(View itemView) {
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
