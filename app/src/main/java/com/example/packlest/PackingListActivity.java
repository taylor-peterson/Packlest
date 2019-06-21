package com.example.packlest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.ListIterator;

public class PackingListActivity extends AppCompatActivity {
    ListView itemListView;
    private ListViewItemCheckboxAdapter dataAdapter;
    PackingList packingList;
    PackingList filteredPackingList;
    private static final String TAG = "PackingListActivity";
    FILTER_STATE filter_state;
    Gson gson;

    enum FILTER_STATE {
        NONE,
        ADDED_ONLY,
        UNCHECKED_ONLY
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.packing_list);
        setSupportActionBar(findViewById(R.id.toolbar));

        packingList = getIntent().getParcelableExtra("packingList");
        gson = new Gson(); // We use this to (de)serialize to make deep copies.
        filteredPackingList = gson.fromJson(gson.toJson(packingList), PackingList.class);
        setTitle(packingList.name);

        itemListView = findViewById(R.id.listViewItems);
        dataAdapter = new ListViewItemCheckboxAdapter(this, filteredPackingList);
        itemListView.setAdapter(dataAdapter);

        filter_state = FILTER_STATE.NONE;

        setListViewOnItemClickListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_packing_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.create_item_button:
                Log.v(TAG, "Creating new item!");
                Intent intent = new Intent(this, CreateItemActivity.class);
                startActivityForResult(intent, PacklestActivity.REQUEST_CODES.CREATE_ITEM.ordinal());
                break;
            case R.id.filter_items_button:
                if (filter_state == FILTER_STATE.NONE) {
                    Log.v(TAG, "Filtering out un-added items");
                    filteredPackingList = new PackingList();
                    for (Item item : packingList.items ) {
                        Log.v(TAG, item.name);
                        if (item.checkbox_state != CheckBoxTriState.CHECKBOX_STATE.UNADDED) {
                            Log.v(TAG, "Adding item:" + item.name);
                            filteredPackingList.items.add(item);
                        }
                    }
                    menuItem.setIcon(R.drawable.ic_filter);
                    filter_state = FILTER_STATE.ADDED_ONLY;
                } else if (filter_state == FILTER_STATE.ADDED_ONLY) {
                    Log.v(TAG, "Filtering out checked-items too");
                    filteredPackingList = new PackingList();
                    for (Item item : packingList.items ) {
                        if (item.checkbox_state == CheckBoxTriState.CHECKBOX_STATE.UNCHECKED) {
                            Log.v(TAG, "Adding item:" + item.name);
                            filteredPackingList.items.add(item);
                        }
                    }
                    menuItem.setIcon(R.drawable.ic_filter_remove);
                    filter_state = FILTER_STATE.UNCHECKED_ONLY;
                } else if (filter_state == FILTER_STATE.UNCHECKED_ONLY){
                    Log.v(TAG, "Resetting the filter");
                    filteredPackingList = gson.fromJson(gson.toJson(packingList), PackingList.class);
                    menuItem.setIcon(R.drawable.ic_filter_outline);
                    filter_state = FILTER_STATE.NONE;
                }
                Log.v(TAG, filteredPackingList.items.toString());
                dataAdapter.updatePackingList(filteredPackingList);
                break;
            case R.id.delete_packing_list:
                Log.v(TAG, "Deleting packing list");
                Intent deleteIntent = new Intent();
                deleteIntent.putExtra("packingList", packingList);
                setResult(PacklestActivity.RESULT_CODES.PACKING_LIST_DELETED.ordinal(), deleteIntent);
                finish();
                break;
            case R.id.un_add_all_items:
                Log.v(TAG, "Un-adding all items");
                for (Item packingListItem : packingList.items) {
                    packingListItem.checkbox_state = CheckBoxTriState.CHECKBOX_STATE.UNADDED;
                }
                dataAdapter.notifyDataSetChanged();
                break;
            case R.id.uncheck_all_items:
                Log.v(TAG, "Un-checking all checked items");
                for (Item packingListItem : packingList.items) {
                    if (packingListItem.checkbox_state == CheckBoxTriState.CHECKBOX_STATE.CHECKED){
                        packingListItem.checkbox_state = CheckBoxTriState.CHECKBOX_STATE.UNCHECKED;
                    }
                }
                dataAdapter.notifyDataSetChanged();
                break;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PacklestActivity.REQUEST_CODES.CREATE_ITEM.ordinal() && resultCode == PacklestActivity.RESULT_CODES.ITEM_MODIFIED.ordinal()) {
            Item newItem = data.getParcelableExtra("item");
            packingList.items.add(newItem);
            Log.v(TAG, "Added: " + newItem.name);
        } else if (requestCode == PacklestActivity.REQUEST_CODES.MODIFY_ITEM.ordinal()) {
            Item modifiedItem = data.getParcelableExtra("item");
            ListIterator<Item> iterator = packingList.items.listIterator();
            while (iterator.hasNext()) {
                Item itemEntry = iterator.next();
                if (itemEntry.uuid.equals(modifiedItem.uuid)) {
                    if (resultCode == PacklestActivity.RESULT_CODES.ITEM_MODIFIED.ordinal()) {
                        iterator.set(modifiedItem);
                        Log.v(TAG, "Modified: " + modifiedItem.name);
                    } else if (resultCode == PacklestActivity.RESULT_CODES.ITEM_DELETED.ordinal()) {
                        iterator.remove();
                        Log.v(TAG, "Deleted: " + modifiedItem.name);
                    }
                }
            }
        }
        dataAdapter.notifyDataSetChanged();
    }

    private void setListViewOnItemClickListener() {
        itemListView.setOnItemClickListener((parent, view, position, id) -> {
            Item item = dataAdapter.getItem(position);

            Intent intent = new Intent(this, CreateItemActivity.class);
            intent.putExtra("item", item);
            startActivityForResult(intent, PacklestActivity.REQUEST_CODES.MODIFY_ITEM.ordinal());
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("packingList", packingList);
        setResult(PacklestActivity.RESULT_CODES.PACKING_LIST_MODIFIED.ordinal(), intent);
        finish();
    }

    @Override
    public void onPause() {
        super.onPause();
        PacklestApplication.getInstance().onPause();
    }
}
