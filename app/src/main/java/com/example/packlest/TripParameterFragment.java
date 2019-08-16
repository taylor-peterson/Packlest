package com.example.packlest;

import android.content.Intent;

import java.util.ArrayList;
import java.util.Objects;

class TripParameterFragment extends AbstractFragment {
    @Override
    ArrayList<AbstractBaseObject> getArrayListItems() {
        return new ArrayList<>(PacklestApplication.getInstance().packlestData.tripParameters.values());
    }

    @Override
    public void startCreateActivity() {
        Intent intent = new Intent(getActivity(), TripParameterEditorActivity.class);
        startActivityForResult(intent, PacklestApplication.IGNORED_REQUEST_CODE);
    }


    @Override
    void setListViewOnItemClickListener() {
        listView.setOnItemClickListener((parent, view, position, id) -> {
            TripParameter tripParameter = (TripParameter) arrayAdapter.getItem(position);
            Intent intent = new Intent(getActivity(), TripParameterEditorActivity.class);
            intent.putExtra("tripParameterUuid", Objects.requireNonNull(tripParameter).uuid);
            startActivityForResult(intent, PacklestApplication.IGNORED_REQUEST_CODE);
        });
    }
}

