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

public class PackingListFragment extends Fragment {
    private ListView packingListView;
    private ArrayAdapter<PackingList> arrayAdapter;
    private ArrayList<PackingList> packingLists;

    public PackingListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        packingLists = PacklestApplication.getInstance().packlestData.getPackingLists();
        arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.listview, R.id.textView, packingLists);
        packingListView = view.findViewById(R.id.fragmentList);
        packingListView.setAdapter(arrayAdapter);

        setHasOptionsMenu(true);
        setListViewOnItemClickListener();

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.create_item_button) {
            Intent intent = new Intent(getActivity(), CreatePackingListActivity.class);
            startActivityForResult(intent, REQUEST_CODES.CREATE_PACKING_LIST.ordinal());
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        packingLists.clear();
        packingLists.addAll(PacklestApplication.getInstance().packlestData.getPackingLists());
        arrayAdapter.notifyDataSetChanged();
        PacklestApplication.getInstance().persistData();
    }

    private void setListViewOnItemClickListener() {
        packingListView.setOnItemClickListener((parent, view, position, id) -> {
            PackingList packingList = arrayAdapter.getItem(position);
            Intent intent = new Intent(getActivity(), PackingListActivity.class);
            intent.putExtra("packingListUuid", packingList.uuid);
            startActivityForResult(intent, REQUEST_CODES.VIEW_PACKING_LIST.ordinal());
        });
    }
}

