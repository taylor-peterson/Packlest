package com.example.packlest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import java.lang.reflect.Array;
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
 * - Populate missing UID from stored data (e.g. if hand-edited)
 * - Delete lists/items
 * - Database of items at the top-level
 * - When create a packing list, select the set of tags that apply
 * - Pre-populate packing lists with all items with the selected tags
 * - Every item in a packing list has three states: unadded, added, packed
 * - Cycle through states with check-boxes/swipes
 * - Filter in menu to cycle through all/added+packed/added
 * - Display items based on category
 * - Reset/Hard Reset/Sync
 * - Edit trip parameters
 * - Ability to export/inport data as json/toml/etc.
 * - Ability to share lists
 */

public class MainActivity extends AppCompatActivity {
    ListView packingListView;
    private ArrayAdapter<PackingList> arrayAdapter;
    ArrayList<PackingList> packingLists;

    enum REQUEST_CODES {
        CREATE_PACKING_LIST,
        VIEW_PACKING_LIST,
        CREATE_ITEM,
        MODIFY_ITEM
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        StringBuilder input = new StringBuilder();
        try {
            File file = new File(getFilesDir(), "themDatas.json");
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
        String filename = "themDatas.json";
        String fileContents = gson.toJson(packingLists);
        FileOutputStream outputStream;

        try {
            Log.e("print", fileContents);
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
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

        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.create_item_button) {
            Intent intent = new Intent(this, CreatePackingListActivity.class);
            startActivityForResult(intent, REQUEST_CODES.CREATE_PACKING_LIST.ordinal());
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODES.CREATE_PACKING_LIST.ordinal()) {
                PackingList newPackingList = data.getParcelableExtra("packingList");
                packingLists.add(newPackingList);
            } else if (requestCode == REQUEST_CODES.VIEW_PACKING_LIST.ordinal()) {
                PackingList packingList = data.getParcelableExtra("packingList");
                ListIterator<PackingList> iterator= packingLists.listIterator();
                while (iterator.hasNext()) {
                    PackingList packingListEntry = iterator.next();
                    if (packingListEntry.uuid.equals(packingList.uuid)) {
                        iterator.set(packingList);
                    }
                }
            }
            arrayAdapter.notifyDataSetChanged();
        }
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
