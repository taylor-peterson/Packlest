package com.example.packlest;

import android.content.Intent;

import java.util.ArrayList;
import java.util.Objects;

class ItemFragment extends AbstractFragment {
    @Override
    ArrayList<AbstractBaseObject> getArrayListItems() {
        return new ArrayList<>(PacklestApplication.getInstance().packlestData.items.values());
    }

    @Override
    void startCreateActivity() {
        Intent intent = new Intent(getActivity(), ItemEditorActivity.class);
        startActivityForResult(intent, PacklestApplication.IGNORED_REQUEST_CODE);
    }

    @Override
    void setListViewOnItemClickListener() {
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Item item = (Item) arrayAdapter.getItem(position);
            Intent intent = new Intent(getActivity(), ItemEditorActivity.class);
            intent.putExtra("itemUuid", Objects.requireNonNull(item).uuid);
            startActivityForResult(intent, PacklestApplication.IGNORED_REQUEST_CODE);
        });
    }
}