package com.example.packlest;

import android.content.Intent;

import java.util.ArrayList;

class ItemFragment extends PacklestFragment<Item> {
    @Override
    ArrayList<Item> getArrayListItems() {
        return PacklestApplication.getInstance().packlestData.getItems();
    }

    @Override
    void startCreateActivity() {
        Intent intent = new Intent(getActivity(), CreateItemActivity.class);
        startActivityForResult(intent, PacklestApplication.IGNORED_REQUEST_CODE);
    }

    @Override
    void setListViewOnItemClickListener() {
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Item item = arrayAdapter.getItem(position);
            Intent intent = new Intent(getActivity(), CreateItemActivity.class);
            intent.putExtra("itemUuid", item.uuid);
            startActivityForResult(intent, PacklestApplication.IGNORED_REQUEST_CODE);
        });
    }
}