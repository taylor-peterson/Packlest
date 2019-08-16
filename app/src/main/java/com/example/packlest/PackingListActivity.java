package com.example.packlest;

import android.content.Intent;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.packing_list);
        setSupportActionBar(findViewById(R.id.toolbar));

        UUID packingListUuid = (UUID) getIntent().getSerializableExtra("packingListUuid");
        filteredPackingList = new PackingList(PacklestApplication.getInstance().packlestData.packingLists.get(packingListUuid));
        setTitle(filteredPackingList.name);

        // TODO: Display items based on category
        listView = findViewById(R.id.list_view_items);
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
                Intent intent = new Intent(this, ItemEditorActivity.class);
                intent.putExtra("packingListUuid", filteredPackingList.uuid);
                startActivityForResult(intent, PacklestApplication.IGNORED_REQUEST_CODE);
                break;
            case R.id.filter_items_button:
                if (dataAdapter.filter_state == FILTER_STATE.NONE) {
                    menuItem.setIcon(R.drawable.ic_filter);
                    dataAdapter.filter_state = FILTER_STATE.ADDED_ONLY;
                } else if (dataAdapter.filter_state == FILTER_STATE.ADDED_ONLY) {
                    dataAdapter.filter_state = FILTER_STATE.UNCHECKED_ONLY;
                    menuItem.setIcon(R.drawable.ic_filter_remove);
                } else if (dataAdapter.filter_state == FILTER_STATE.UNCHECKED_ONLY){
                    dataAdapter.filter_state = FILTER_STATE.NONE;
                    menuItem.setIcon(R.drawable.ic_filter_outline);
                }
                dataAdapter.getFilter().filter(dataAdapter.filter_state.name());
                dataAdapter.notifyDataSetChanged();
                break;
            case R.id.edit_packing_list:
                intent = new Intent(this, PackingListEditorActivity.class);
                intent.putExtra("packingListUuid", filteredPackingList.uuid);
                startActivityForResult(intent, PacklestApplication.IGNORED_REQUEST_CODE);
                break;
            case R.id.delete_packing_list:
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
                PacklestApplication.getInstance().packlestData.unaddAllItemsInPackingList(filteredPackingList.uuid);
                syncFilteredPackingList();
                break;
            case R.id.uncheck_all_items:
                PacklestApplication.getInstance().packlestData.uncheckAllCheckedItemsInPackingList(filteredPackingList.uuid);
                syncFilteredPackingList();
                break;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        syncFilteredPackingList();
    }

    private void setListViewOnItemClickListener() {
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
        super.onPause();
        PacklestApplication.getInstance().persistData();
    }

    private void syncFilteredPackingList() {
        PackingList fullPackingList = PacklestApplication.getInstance().packlestData.packingLists.get(filteredPackingList.uuid);
        filteredPackingList.name = fullPackingList.name;
        filteredPackingList.itemInstances.clear();
        filteredPackingList.itemInstances.addAll(fullPackingList.itemInstances);
        setTitle(filteredPackingList.name);
        dataAdapter.notifyDataSetChanged();
        PacklestApplication.getInstance().persistData();
    }

}
