package com.example.packlest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.UUID;

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

        filteredPackingList = new PackingList(PacklestApplication.getInstance().packlestData.getPackingListForUuid(
                (UUID) getIntent().getSerializableExtra("packingListUuid")));
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
                intent.putExtra("packingListUuid", filteredPackingList.uuid);
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
        syncFilteredPackingList();
    }

    private void setListViewOnItemClickListener() {
        Log.v(TAG, "Item Clicked");
        // TODO first click triggers this?
        itemListView.setOnItemClickListener((parent, view, position, id) -> {
            ItemInstance itemInstance = dataAdapter.getItem(position);
            Item item = PacklestApplication.getInstance().packlestData.items.get(itemInstance.item_uuid);

            Intent intent = new Intent(this, CreateItemActivity.class);
            intent.putExtra("itemUuid", item.uuid);
            intent.putExtra("packingListUuid", filteredPackingList.uuid);
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
        PackingList fullPackingList = PacklestApplication.getInstance().packlestData.getPackingListForUuid(filteredPackingList.uuid);
        filteredPackingList.name = fullPackingList.name;
        filteredPackingList.itemInstances.clear();
        filteredPackingList.itemInstances.addAll(fullPackingList.itemInstances);
        dataAdapter.notifyDataSetChanged();
        PacklestApplication.getInstance().persistData();
    }

}
