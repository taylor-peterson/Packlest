package com.example.packlest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.UUID;

public class PackingListActivity extends AppCompatActivity {
    private ListView listView;
    private ListViewItemCheckboxAdapter dataAdapter;
    private PackingList filteredPackingList;
    private static final String TAG = "PackingListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.packing_list);
        setSupportActionBar(findViewById(R.id.toolbar));

        UUID packingListUuid = (UUID) getIntent().getSerializableExtra("packingListUuid");
        filteredPackingList = new PackingList(PacklestApplication.getInstance().packlestData.packingLists.get(packingListUuid));
        setTitle(filteredPackingList.name);

        listView = findViewById(R.id.listViewItems);
        dataAdapter = new ListViewItemCheckboxAdapter(this, filteredPackingList);
        listView.setAdapter(dataAdapter);

        setListViewOnItemClickListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_packing_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.create_item_button:
                Log.v(TAG, "Creating new item");
                Intent intent = new Intent(this, ItemEditorActivity.class);
                intent.putExtra("packingListUuid", filteredPackingList.uuid);
                startActivityForResult(intent, PacklestApplication.IGNORED_REQUEST_CODE);
                break;
            case R.id.filter_items_button:
                if (dataAdapter.filter_state == FILTER_STATE.NONE) {
                    Log.v(TAG, "Filtering out un-added itemInstances");
                    menuItem.setIcon(R.drawable.ic_filter);
                    dataAdapter.filter_state = FILTER_STATE.ADDED_ONLY;
                } else if (dataAdapter.filter_state == FILTER_STATE.ADDED_ONLY) {
                    Log.v(TAG, "Filtering out checked-itemInstances too");
                    dataAdapter.filter_state = FILTER_STATE.UNCHECKED_ONLY;
                    menuItem.setIcon(R.drawable.ic_filter_remove);
                } else if (dataAdapter.filter_state == FILTER_STATE.UNCHECKED_ONLY){
                    Log.v(TAG, "Resetting the filter");
                    dataAdapter.filter_state = FILTER_STATE.NONE;
                    menuItem.setIcon(R.drawable.ic_filter_outline);
                }
                dataAdapter.getFilter().filter(dataAdapter.filter_state.name());
                dataAdapter.notifyDataSetChanged();
                break;
            case R.id.edit_packing_list:
                Log.v(TAG, "Editing packing list");
                intent = new Intent(this, PackingListEditorActivity.class);
                intent.putExtra("packingListUuid", filteredPackingList.uuid);
                startActivityForResult(intent, PacklestApplication.IGNORED_REQUEST_CODE);
                break;
            case R.id.delete_packing_list:
                Log.v(TAG, "Deleting packing list");
                new AlertDialog.Builder(this)
                        .setTitle("Confirm Deletion")
                        .setMessage("Do you really want to delete: " + filteredPackingList.name + "?")
                        .setIcon(android.R.drawable.ic_menu_delete)
                        .setPositiveButton(android.R.string.yes, (dialog, button) -> {
                            PacklestApplication.getInstance().packlestData.deletePackingList(filteredPackingList.uuid);
                            finish();
                        })
                        .setNegativeButton(android.R.string.cancel, null).show();
                break;
            case R.id.un_add_all_items:
                Log.v(TAG, "Un-adding all itemInstances");
                PacklestApplication.getInstance().packlestData.unaddAllItemsInPackingList(filteredPackingList.uuid);
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
        listView.setOnItemClickListener((parent, view, position, id) -> {
            ItemInstance itemInstance = dataAdapter.getItem(position);
            Item item = PacklestApplication.getInstance().packlestData.items.get(itemInstance.itemUuid);

            Intent intent = new Intent(this, ItemEditorActivity.class);
            intent.putExtra("itemUuid", item.uuid);
            intent.putExtra("packingListUuid", filteredPackingList.uuid);
            startActivityForResult(intent, PacklestApplication.IGNORED_REQUEST_CODE);
        });
    }

    @Override
    public void onPause() {
        Log.v(TAG, "Paused");
        super.onPause();
        PacklestApplication.getInstance().persistData();
    }

    private void syncFilteredPackingList() {
        Log.v(TAG, "Syncing filtered packing list");
        PackingList fullPackingList = PacklestApplication.getInstance().packlestData.packingLists.get(filteredPackingList.uuid);
        filteredPackingList.name = fullPackingList.name;
        filteredPackingList.itemInstances.clear();
        filteredPackingList.itemInstances.addAll(fullPackingList.itemInstances);
        setTitle(filteredPackingList.name);
        dataAdapter.notifyDataSetChanged();
        PacklestApplication.getInstance().persistData();
    }

}
