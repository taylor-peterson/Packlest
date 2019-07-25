package com.example.packlest;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

abstract class PacklestFragment<I> extends Fragment {
    ListView listView;
    ArrayAdapter<I> arrayAdapter;
    private ArrayList<I> arrayListItems;

    abstract void startCreateActivity();
    abstract ArrayList<I> getArrayListItems();
    abstract void setListViewOnItemClickListener();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        arrayListItems = getArrayListItems();
        arrayAdapter = new ArrayAdapter<>(view.getContext(), R.layout.listview, R.id.textView, arrayListItems);
        listView = view.findViewById(R.id.fragmentList);
        listView.setAdapter(arrayAdapter);

        setHasOptionsMenu(true);
        setListViewOnItemClickListener();

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.create_item_button) {
            startCreateActivity();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        updateArrayListItems();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateArrayListItems();
    }

    private void updateArrayListItems() {
        arrayListItems.clear();
        arrayListItems.addAll(getArrayListItems());
        arrayAdapter.notifyDataSetChanged();
        PacklestApplication.getInstance().persistData();
    }
}

