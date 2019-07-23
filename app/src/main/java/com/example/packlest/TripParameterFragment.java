package com.example.packlest;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

// TODO extract code common to this and item/packing list fragments
// They only vary in the arraylist/adapter type, what method to call to get list items, the activity to start when selecting an item, and that item's type

public class TripParameterFragment extends Fragment {
    private ListView listView;
    private ArrayAdapter<TripParameter> arrayAdapter;
    private ArrayList<TripParameter> tripParameters;

    public TripParameterFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        tripParameters = PacklestApplication.getInstance().packlestData.getTripParameters();
        arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.listview, R.id.textView, tripParameters);
        listView = view.findViewById(R.id.fragmentList);
        listView.setAdapter(arrayAdapter);

        setHasOptionsMenu(true);
        setListViewOnItemClickListener();

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.create_item_button) {
            Intent intent = new Intent(getActivity(), CreateItemActivity.class); // TODO separate activity to edit trip parameters
            startActivityForResult(intent, REQUEST_CODES.CREATE_ITEM.ordinal());
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        tripParameters.clear();
        tripParameters.addAll(PacklestApplication.getInstance().packlestData.getTripParameters());
        arrayAdapter.notifyDataSetChanged();
        PacklestApplication.getInstance().persistData();
    }

    private void setListViewOnItemClickListener() {
        listView.setOnItemClickListener((parent, view, position, id) -> {
            TripParameter tripParameter = arrayAdapter.getItem(position);
            Intent intent = new Intent(getActivity(), CreateItemActivity.class);
            intent.putExtra("tripParameterUuid", tripParameter.uuid);
            startActivityForResult(intent, REQUEST_CODES.MODIFY_ITEM.ordinal());
        });
    }
}

