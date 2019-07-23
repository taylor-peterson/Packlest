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

public class ItemFragment extends Fragment {
    private ListView listView;
    private ArrayAdapter<Item> arrayAdapter;
    private ArrayList<Item> items;

    public ItemFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        items = PacklestApplication.getInstance().packlestData.getItems();
        arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.listview, R.id.textView, items);
        listView = view.findViewById(R.id.fragmentList);
        listView.setAdapter(arrayAdapter);

        setHasOptionsMenu(true);
        setListViewOnItemClickListener();

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.create_item_button) {
            Intent intent = new Intent(getActivity(), CreateItemActivity.class);
            startActivityForResult(intent, REQUEST_CODES.CREATE_ITEM.ordinal());
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        items.clear();
        items.addAll(PacklestApplication.getInstance().packlestData.getItems());
        arrayAdapter.notifyDataSetChanged();
        PacklestApplication.getInstance().persistData();
    }

    private void setListViewOnItemClickListener() {
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Item item = arrayAdapter.getItem(position);
            Intent intent = new Intent(getActivity(), CreateItemActivity.class);
            intent.putExtra("itemUuid", item.uuid);
            startActivityForResult(intent, REQUEST_CODES.MODIFY_ITEM.ordinal());
        });
    }
}

