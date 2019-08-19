package com.example.packlest;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;
import java.util.UUID;

public class PackingListActivity extends AppCompatActivity {
    private ExpandableListView expandableListView;
    private PackingListAdapter dataAdapter;
    private String packingListName;
    private UUID packingListUuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.packing_list);
        setSupportActionBar(findViewById(R.id.toolbar));

        packingListUuid = (UUID) getIntent().getSerializableExtra("packingListUuid");
        PackingList packingList = new PackingList(PacklestApplication.getInstance().packlestData.packingLists.get(packingListUuid));
        packingListName = packingList.name;
        setTitle(packingListName);

        expandableListView = findViewById(R.id.list_view_items);
        dataAdapter = new PackingListAdapter(this, packingList);
        expandableListView.setAdapter(dataAdapter);

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
                intent.putExtra("packingListUuid", packingListUuid);
                startActivityForResult(intent, PacklestApplication.IGNORED_REQUEST_CODE);
                break;
            case R.id.filter_items_button:
                if (dataAdapter.filter_state == FILTER_STATE.NONE) {
                    menuItem.setIcon(R.drawable.ic_filter);
                    dataAdapter.filter_state = FILTER_STATE.ADDED_ONLY;
                    Toast.makeText(this, "Filtering out un-added items.", Toast.LENGTH_SHORT).show();
                } else if (dataAdapter.filter_state == FILTER_STATE.ADDED_ONLY) {
                    dataAdapter.filter_state = FILTER_STATE.UNCHECKED_ONLY;
                    menuItem.setIcon(R.drawable.ic_filter_remove);
                    Toast.makeText(this, "Filtering out packed items.", Toast.LENGTH_SHORT).show();
                } else if (dataAdapter.filter_state == FILTER_STATE.UNCHECKED_ONLY){
                    dataAdapter.filter_state = FILTER_STATE.NONE;
                    menuItem.setIcon(R.drawable.ic_filter_outline);
                    Toast.makeText(this, "Removing filter.", Toast.LENGTH_SHORT).show();
                }
                dataAdapter.getFilter().filter(dataAdapter.filter_state.name());
                break;
            case R.id.edit_packing_list:
                intent = new Intent(this, PackingListEditorActivity.class);
                intent.putExtra("packingListUuid", packingListUuid);
                startActivityForResult(intent, PacklestApplication.IGNORED_REQUEST_CODE);
                break;
            case R.id.delete_packing_list:
                new AlertDialog.Builder(this)
                        .setTitle("Confirm Deletion")
                        .setMessage("Do you really want to delete: " + packingListName + "?")
                        .setIcon(android.R.drawable.ic_menu_delete)
                        .setPositiveButton(android.R.string.yes, (dialog, button) -> {
                            PacklestApplication.getInstance().packlestData.deletePackingList(packingListUuid);
                            finish();
                        })
                        .setNegativeButton(android.R.string.cancel, null).show();
                break;
            case R.id.un_add_all_items:
                PacklestApplication.getInstance().packlestData.unaddAllItemsInPackingList(packingListUuid);
                syncPackingList();
                break;
            case R.id.uncheck_all_items:
                PacklestApplication.getInstance().packlestData.uncheckAllCheckedItemsInPackingList(packingListUuid);
                syncPackingList();
                break;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        syncPackingList();
    }

    private void setListViewOnItemClickListener() {
        expandableListView.setOnChildClickListener((parent, view, groupPosition, childPosition, id) -> {
            UUID itemInstanceUuid = (UUID) dataAdapter.getChild(groupPosition, childPosition);
            Item item = PacklestApplication.getInstance().packlestData.items.get(itemInstanceUuid);

            Intent intent = new Intent(this, ItemEditorActivity.class);
            intent.putExtra("itemUuid", Objects.requireNonNull(item).uuid);
            intent.putExtra("packingListUuid", packingListUuid);
            startActivityForResult(intent, PacklestApplication.IGNORED_REQUEST_CODE);

            return false;
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        PacklestApplication.getInstance().persistData();
    }

    private void syncPackingList() {
        packingListName = Objects.requireNonNull(PacklestApplication.getInstance().packlestData.packingLists.get(packingListUuid)).name;
        setTitle(packingListName);
        dataAdapter.getFilter().filter(dataAdapter.filter_state.name());
        PacklestApplication.getInstance().persistData();
    }

}
