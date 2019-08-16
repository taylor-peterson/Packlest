package com.example.packlest;

import android.content.Intent;

import java.util.ArrayList;
import java.util.Objects;

class PackingListFragment extends AbstractFragment {
    @Override
    ArrayList<AbstractBaseObject> getArrayListItems() {
        return new ArrayList<>(PacklestApplication.getInstance().packlestData.packingLists.values());
    }

    @Override
    void startCreateActivity() {
        Intent intent = new Intent(getActivity(), PackingListEditorActivity.class);
        startActivityForResult(intent, PacklestApplication.IGNORED_REQUEST_CODE);
    }

    @Override
    void setListViewOnItemClickListener() {
        listView.setOnItemClickListener((parent, view, position, id) -> {
            PackingList packingList = (PackingList) arrayAdapter.getItem(position);
            Intent intent = new Intent(getActivity(), PackingListActivity.class);
            intent.putExtra("packingListUuid", Objects.requireNonNull(packingList).uuid);
            startActivityForResult(intent, PacklestApplication.IGNORED_REQUEST_CODE);
        });
    }
}

