package com.example.packlest;

import android.content.Intent;

import java.util.ArrayList;
import java.util.Objects;

class PackingListFragment extends PacklestFragment<PackingList> {
    @Override
    ArrayList<PackingList> getArrayListItems() {
        return PacklestApplication.getInstance().packlestData.getPackingLists();
    }

    @Override
    void startCreateActivity() {
        Intent intent = new Intent(getActivity(), PackingListEditorActivity.class);
        startActivityForResult(intent, PacklestApplication.IGNORED_REQUEST_CODE);
    }

    @Override
    void setListViewOnItemClickListener() {
        listView.setOnItemClickListener((parent, view, position, id) -> {
            PackingList packingList = arrayAdapter.getItem(position);
            Intent intent = new Intent(getActivity(), PackingListActivity.class);
            intent.putExtra("packingListUuid", Objects.requireNonNull(packingList).uuid);
            startActivityForResult(intent, PacklestApplication.IGNORED_REQUEST_CODE);
        });
    }
}

