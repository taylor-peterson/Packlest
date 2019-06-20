package com.example.packlest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ListIterator;

public class PackingListActivity extends AppCompatActivity {
    ListView itemListView;
    private ListViewItemCheckboxAdapter dataAdapter;
    PackingList packingList;
    private static final String TAG = "PackingListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.packing_list);
        setSupportActionBar(findViewById(R.id.toolbar));

        packingList = getIntent().getParcelableExtra("packingList");
        setTitle(packingList.name);

        itemListView = findViewById(R.id.listViewItems);
        dataAdapter = new ListViewItemCheckboxAdapter(this, packingList);
        itemListView.setAdapter(dataAdapter);

        setListViewOnItemClickListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_packing_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.create_item_button:
                Log.v(TAG, "Creating new item!");
                Intent intent = new Intent(this, CreateItemActivity.class);
                startActivityForResult(intent, MainActivity.REQUEST_CODES.CREATE_ITEM.ordinal());
                break;
            case R.id.delete_packing_list:
                Log.v(TAG, "Deleting packing list");
                Intent deleteIntent = new Intent();
                deleteIntent.putExtra("packingList", packingList);
                setResult(MainActivity.RESULT_CODES.PACKING_LIST_DELETED.ordinal(), deleteIntent);
                finish();
                break;
            case R.id.un_add_all_items:
                Log.v(TAG, "Un-adding all items");
                for (int i = 0; i < itemListView.getCount(); i++) {
                    View view = itemListView.getChildAt(i);

                    CheckBoxTriState itemCheckbox = view.findViewById(R.id.list_view_item_checkbox);
                    itemCheckbox.setState(CheckBoxTriState.CHECKBOX_STATE.UNADDED);

                    packingList.items.get(i).checkbox_state = itemCheckbox.getState();
                }
                break;
            case R.id.uncheck_all_items:
                Log.v(TAG, "Un-checking all checked items");
                for (int i = 0; i < itemListView.getCount(); i++) {
                    View view = itemListView.getChildAt(i);

                    CheckBoxTriState itemCheckbox = view.findViewById(R.id.list_view_item_checkbox);
                    if (itemCheckbox.getState() == CheckBoxTriState.CHECKBOX_STATE.CHECKED) {
                        itemCheckbox.setState(CheckBoxTriState.CHECKBOX_STATE.UNCHECKED);
                        packingList.items.get(i).checkbox_state = itemCheckbox.getState();
                    }

                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MainActivity.REQUEST_CODES.CREATE_ITEM.ordinal() && resultCode == MainActivity.RESULT_CODES.ITEM_MODIFIED.ordinal()) {
            Item newItem = data.getParcelableExtra("item");
            packingList.items.add(newItem);
            Log.v(TAG, "Added: " + newItem.name);
        } else if (requestCode == MainActivity.REQUEST_CODES.MODIFY_ITEM.ordinal()) {
            Item modifiedItem = data.getParcelableExtra("item");
            ListIterator<Item> iterator = packingList.items.listIterator();
            while (iterator.hasNext()) {
                Item itemEntry = iterator.next();
                if (itemEntry.uuid.equals(modifiedItem.uuid)) {
                    if (resultCode == MainActivity.RESULT_CODES.ITEM_MODIFIED.ordinal()) {
                        iterator.set(modifiedItem);
                        Log.v(TAG, "Modified: " + modifiedItem.name);
                    } else if (resultCode == MainActivity.RESULT_CODES.ITEM_DELETED.ordinal()) {
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
            startActivityForResult(intent, MainActivity.REQUEST_CODES.MODIFY_ITEM.ordinal());
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("packingList", packingList);
        setResult(MainActivity.RESULT_CODES.PACKING_LIST_MODIFIED.ordinal(), intent);
        finish();
    }
}
