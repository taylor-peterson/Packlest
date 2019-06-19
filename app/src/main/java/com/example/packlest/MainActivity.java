package com.example.packlest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.ListIterator;

/*
 * Design Thoughts:
 * - Item: a discrete thing to be brought on a trip
 * - Item Categories: general item classification (e.g. clothing, climbing gear, overnight gear)
 *   - Think of this as the logical grouping you'd use when laying everything out to pack.
 *   - Each item is associated with *one* category.
 * - Trip Parameters: parameters of a trip (e.g. activities, conditions, length of outing)
 *  - Each item can be associated with many trip parameters.
 *
 * TODO:
 * - Add back in ability to edit items (i.e. checkbox should only respond to presses on it, not the item name)
 * - Filter in menu to cycle through all/added+packed/added
 * - Database of items at the top-level
 * - When create a packing list, select the set of tags that apply
 * - Pre-populate packing lists with all items with the selected tags
 * - Display items based on category
 * - Reset/Hard Reset/Sync
 * - Edit trip parameters
 * - Packing list, item, and category names must be unique
 * - Ability to export/import data as json
 * - Populate missing UID from stored data (e.g. if hand-edited)
 * - Ability to share lists
 * - Standardize naming (e.g. buttons, codes, ids - camelCase vs snake_case?)
 * - Enhance logging
 * - Testing
 * - Documentation
 * - Address all warnings
 */

public class MainActivity extends AppCompatActivity {
    ListView packingListView;
    private ArrayAdapter<PackingList> arrayAdapter;
    ArrayList<PackingList> packingLists;
    private static final String TAG = "mainActivity";

    private static final String DATA_FILE = "themDatas.json";

    enum REQUEST_CODES {
        CREATE_PACKING_LIST,
        VIEW_PACKING_LIST,
        CREATE_ITEM,
        MODIFY_ITEM,
    }

    enum RESULT_CODES {
        ITEM_MODIFIED,
        ITEM_DELETED,
        PACKING_LIST_CREATED,
        PACKING_LIST_MODIFIED,
        PACKING_LIST_DELETED,
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setSupportActionBar(findViewById(R.id.toolbar));

        StringBuilder input = new StringBuilder();
        try {
            File file = new File(getFilesDir(), DATA_FILE);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                input.append(line);
                input.append('\n');
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String inputJson = input.toString();
        Log.v(TAG, inputJson);
        if (inputJson.isEmpty()) {
            packingLists = new ArrayList<>();
        } else {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<PackingList>>() {
            }.getType();
            packingLists = gson.fromJson(input.toString(), type);
        }

        packingListView = findViewById(R.id.packingListView);
        arrayAdapter = new ArrayAdapter<>(this, R.layout.listview, R.id.textView, packingLists);
        packingListView.setAdapter(arrayAdapter);

        setListViewOnItemClickListener();
    }

    @Override
    protected void onStop() {
        Gson gson = new Gson();
        String fileContents = gson.toJson(packingLists);
        Log.v(TAG, "Writing:" + fileContents);
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(DATA_FILE, Context.MODE_PRIVATE);
            outputStream.write(fileContents.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.create_item_button) {
            Intent intent = new Intent(this, CreatePackingListActivity.class);
            startActivityForResult(intent, REQUEST_CODES.CREATE_PACKING_LIST.ordinal());
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) return;
        PackingList packingList = data.getParcelableExtra("packingList");

        // TODO Currently, if the app quites while in the packing list view, any new items will not be saved.
        // This will require modifying how data is saved: https://developer.android.com/reference/android/app/Activity#SavingPersistentState
        // Instead of current procedure, write to file onPause in each activity.
        if (requestCode == REQUEST_CODES.CREATE_PACKING_LIST.ordinal() && resultCode == RESULT_CODES.PACKING_LIST_CREATED.ordinal()) {
            packingLists.add(packingList);
        } else if (requestCode == REQUEST_CODES.VIEW_PACKING_LIST.ordinal()) {
            ListIterator<PackingList> iterator = packingLists.listIterator();
            while (iterator.hasNext()) {
                PackingList packingListEntry = iterator.next();
                if (packingListEntry.uuid.equals(packingList.uuid)) {
                    if (resultCode == RESULT_CODES.PACKING_LIST_MODIFIED.ordinal()) {
                        iterator.set(packingList);
                    } else if (resultCode == RESULT_CODES.PACKING_LIST_DELETED.ordinal()) {
                        iterator.remove();
                    }
                }
            }
        }
        arrayAdapter.notifyDataSetChanged();
    }

    private void setListViewOnItemClickListener() {
        packingListView.setOnItemClickListener((parent, view, position, id) -> {
            PackingList packingList = arrayAdapter.getItem(position);
            Intent intent = new Intent(this, PackingListActivity.class);
            intent.putExtra("packingList", packingList);
            startActivityForResult(intent, REQUEST_CODES.VIEW_PACKING_LIST.ordinal());
        });
    }
}
