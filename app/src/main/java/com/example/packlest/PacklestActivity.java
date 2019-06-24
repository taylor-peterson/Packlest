package com.example.packlest;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/*
 * Design Thoughts:
 * - Item: a discrete thing to be brought on a trip
 * - Item Categories: general item classification (e.g. clothing, climbing gear, overnight gear)
 *   - Think of this as the logical grouping you'd use when laying everything out to pack.
 *   - Each item is associated with *one* category.
 * - Trip Parameters: parameters of a trip (e.g. activities, conditions, length of outing)
 *  - Each item can be associated with many trip parameters.
 * - We utilize "Save" buttons whenever editing primitives to provide a logical place for validation.
 * - Activities with a "Save" button do not have knowledge of global state and just create an object.
 *
 * TODO:
 * - Filter in menu to cycle through all/added+packed/added
 * - Reconsider how to uncheck items without cyclng through unadded - e.g. oops clicked wrong thing but have filter on - long-press to reverse?
 * - Database of items/parameters/lists at the top-level
 * - enums in their own files
 * - Packing list, item, and category names must be unique
 * - When create a packing list, select the set of parameters that apply
 * - Edit trip parameters
 * - Pre-populate packing lists with all items with the selected parameters
 * - Display items based on category
 * - Sync
 * - Ability to export/import data as json
 *  ==========
 * - Populate missing UID from stored data (e.g. if hand-edited)
 * - Ability to share lists
 * - Standardize naming (e.g. buttons, codes, ids - camelCase vs snake_case?)
 * - Enhance logging
 * - Testing
 * - Documentation
 * - Address all warnings
 */

public class PacklestActivity extends AppCompatActivity {
    ListView packingListView;
    private ArrayAdapter<PackingList> arrayAdapter;
    private static final String TAG = "PacklestActivity";

    enum REQUEST_CODES {
        CREATE_PACKING_LIST,
        VIEW_PACKING_LIST,
        CREATE_ITEM,
        MODIFY_ITEM,
    }

    enum RESULT_CODES {
        BACK_BUTTON,
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

        arrayAdapter = new ArrayAdapter<>(this, R.layout.listview, R.id.textView, PacklestApplication.getInstance().packingLists);
        packingListView = findViewById(R.id.packingListView);
        packingListView.setAdapter(arrayAdapter);

        setListViewOnItemClickListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        PacklestApplication.getInstance().onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.create_item_button) {
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

        if (requestCode == REQUEST_CODES.CREATE_PACKING_LIST.ordinal() && resultCode == RESULT_CODES.PACKING_LIST_CREATED.ordinal()) {
            PacklestApplication.getInstance().packingLists.add(packingList);
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
