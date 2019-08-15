package com.example.packlest;

import android.content.Intent;

import java.util.ArrayList;
import java.util.Objects;

class ItemCategoryFragment extends AbstractFragment<ItemCategory> {
    @Override
    ArrayList<ItemCategory> getArrayListItems() {
        return new ArrayList<>(PacklestApplication.getInstance().packlestData.itemCategories.values());
    }

    @Override
    void startCreateActivity() {
        Intent intent = new Intent(getActivity(), ItemCategoryEditorActivity.class);
        startActivityForResult(intent, PacklestApplication.IGNORED_REQUEST_CODE);
    }

    @Override
    void setListViewOnItemClickListener() {
        listView.setOnItemClickListener((parent, view, position, id) -> {
            ItemCategory itemCategory = arrayAdapter.getItem(position);
            Intent intent = new Intent(getActivity(), ItemCategoryEditorActivity.class);
            intent.putExtra("itemCategoryUuid", Objects.requireNonNull(itemCategory).uuid);
            startActivityForResult(intent, PacklestApplication.IGNORED_REQUEST_CODE);
        });
    }
}