package com.example.packlest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class PackingListActivity extends AppCompatActivity {
    private ListView itemListView;
    private ListViewItemCheckboxAdapter dataAdapter;
    private PackingList filteredPackingList;
    private static final String TAG = "PackingListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.packing_list);
        setSupportActionBar(findViewById(R.id.toolbar));

        filteredPackingList = getIntent().getParcelableExtra("packingList");
        setTitle(filteredPackingList.name);

        itemListView = findViewById(R.id.listViewItems);
        dataAdapter = new ListViewItemCheckboxAdapter(this, filteredPackingList);
        itemListView.setAdapter(dataAdapter);

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
                Log.v(TAG, "Creating new item");
                Intent intent = new Intent(this, CreateItemActivity.class);
                startActivityForResult(intent, REQUEST_CODES.CREATE_ITEM.ordinal());
                break;
            case R.id.filter_items_button:
                if (dataAdapter.filter_state == FILTER_STATE.NONE) {
                    Log.v(TAG, "Filtering out un-added itemInstances");
                    menuItem.setIcon(R.drawable.ic_filter);
                    dataAdapter.getFilter().filter(FILTER_STATE.ADDED_ONLY.name());
                    dataAdapter.filter_state = FILTER_STATE.ADDED_ONLY;
                } else if (dataAdapter.filter_state == FILTER_STATE.ADDED_ONLY) {
                    Log.v(TAG, "Filtering out checked-itemInstances too");
                    dataAdapter.getFilter().filter(FILTER_STATE.UNCHECKED_ONLY.name());
                    dataAdapter.filter_state = FILTER_STATE.UNCHECKED_ONLY;
                    menuItem.setIcon(R.drawable.ic_filter_remove);
                } else if (dataAdapter.filter_state == FILTER_STATE.UNCHECKED_ONLY){
                    Log.v(TAG, "Resetting the filter");
                    dataAdapter.getFilter().filter(FILTER_STATE.NONE.name());
                    dataAdapter.filter_state = FILTER_STATE.NONE;
                    menuItem.setIcon(R.drawable.ic_filter_outline);
                }
                dataAdapter.notifyDataSetChanged();
                break;
            case R.id.delete_packing_list:
                Log.v(TAG, "Deleting packing list");
                PacklestApplication.getInstance().packlestData.deletePackingList(filteredPackingList.uuid);
                finish();
                break;
            case R.id.un_add_all_items:
                Log.v(TAG, "Un-adding all itemInstances");
                PacklestApplication.getInstance().packlestData.setCheckboxStateForAllItemsInPackingList(
                        filteredPackingList.uuid, CHECKBOX_STATE.UNADDED);
                syncFilteredPackingList();
                break;
            case R.id.uncheck_all_items:
                Log.v(TAG, "Un-checking all checked itemInstances");
                PacklestApplication.getInstance().packlestData.uncheckAllCheckedItemsInPackingList(filteredPackingList.uuid);
                syncFilteredPackingList();
                break;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(TAG, "Activity result");
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODES.CREATE_ITEM.ordinal() && resultCode == RESULT_CODES.ITEM_MODIFIED.ordinal()) {
            Item newItem = data.getParcelableExtra("item");
            ItemInstance itemInstance = new ItemInstance(newItem.uuid);
            Log.v(TAG, "Adding: " + newItem.name);
            PacklestApplication.getInstance().packlestData.addItem(newItem);
            PacklestApplication.getInstance().packlestData.addItemToPackingList(filteredPackingList.uuid, itemInstance);
        } else if (requestCode == REQUEST_CODES.MODIFY_ITEM.ordinal() && resultCode != RESULT_CODES.BACK_BUTTON.ordinal()) {
            Item modifiedItem = data.getParcelableExtra("item");

            if (resultCode == RESULT_CODES.ITEM_MODIFIED.ordinal()) {
                Log.v(TAG, "Modifying: " + modifiedItem.name);
                PacklestApplication.getInstance().packlestData.updateItem(modifiedItem);
            } else if (resultCode == RESULT_CODES.ITEM_DELETED.ordinal()) {
                Log.v(TAG, "Deleting: " + modifiedItem.name);
                PacklestApplication.getInstance().packlestData.removeItemFromPackingList(filteredPackingList.uuid, modifiedItem);
                PacklestApplication.getInstance().packlestData.removeItem(modifiedItem);
            }
        } else {
            Log.v(TAG, "Back button pressed");
        }

        syncFilteredPackingList();
    }

    private void setListViewOnItemClickListener() {
        Log.v(TAG, "Item Clicked");
        // TODO first click triggers this?
        itemListView.setOnItemClickListener((parent, view, position, id) -> {
            ItemInstance itemInstance = dataAdapter.getItem(position);
            Item item = PacklestApplication.getInstance().packlestData.items.get(itemInstance.item_uuid);

            Intent intent = new Intent(this, CreateItemActivity.class);
            intent.putExtra("item", item);
            startActivityForResult(intent, REQUEST_CODES.MODIFY_ITEM.ordinal());
        });
    }

    @Override
    public void onBackPressed() {
        Log.v(TAG, "Back Pressed");
        finish();
    }

    @Override
    public void onPause() {
        Log.v(TAG, "Paused");
        super.onPause();
        PacklestApplication.getInstance().persistData();
    }

    private void syncFilteredPackingList() {
        Log.v(TAG, "Syncing filtered packing list");
        PackingList fullPackingList = PacklestApplication.getInstance().packlestData.getPackingListForUUID(filteredPackingList.uuid);
        filteredPackingList.name = fullPackingList.name;
        filteredPackingList.itemInstances.clear();
        filteredPackingList.itemInstances.addAll(fullPackingList.itemInstances);
        dataAdapter.notifyDataSetChanged();
        PacklestApplication.getInstance().persistData();
    }

}
