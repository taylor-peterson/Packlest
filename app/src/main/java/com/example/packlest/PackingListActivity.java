package com.example.packlest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ListIterator;

public class PackingListActivity extends AppCompatActivity {
    ListView itemListView;
    private ArrayAdapter<Item> arrayAdapter;
    PackingList packingList;
    private static final String TAG = "PackingListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.packing_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        packingList = intent.getParcelableExtra("packingList");
        setTitle(packingList.name);

        itemListView = findViewById(R.id.listViewItems);
        arrayAdapter = new ArrayAdapter<>(this, R.layout.listview, R.id.textView, packingList.items);
        itemListView.setAdapter(arrayAdapter);

        setListViewOnItemClickListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_packing_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.create_item_button) {
            Log.v(TAG, "Creating new item!");
            Intent intent = new Intent(this, CreateItemActivity.class);
            startActivityForResult(intent, MainActivity.REQUEST_CODES.CREATE_ITEM.ordinal());
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == MainActivity.REQUEST_CODES.CREATE_ITEM.ordinal()) {
                Item newItem = data.getParcelableExtra("item");
                packingList.items.add(newItem);
                Log.v(TAG, "Added: " + newItem.name);
            } else if (requestCode == MainActivity.REQUEST_CODES.MODIFY_ITEM.ordinal()) {
                Item modifiedItem = data.getParcelableExtra("item");
                ListIterator<Item> iterator = packingList.items.listIterator();
                while (iterator.hasNext()) {
                    Item itemEntry = iterator.next();
                    if (itemEntry.uuid.equals(modifiedItem.uuid)) {
                        iterator.set(modifiedItem);
                        Log.v(TAG, "Modified: " + modifiedItem.name);
                    }
                }
            }
            arrayAdapter.notifyDataSetChanged();
        }
    }

    private void setListViewOnItemClickListener() {
        itemListView.setOnItemClickListener((parent, view, position, id) -> {
            Item item = arrayAdapter.getItem(position);
            Intent intent = new Intent(this, CreateItemActivity.class);
            intent.putExtra("item", item);
            startActivityForResult(intent, MainActivity.REQUEST_CODES.MODIFY_ITEM.ordinal());
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("packingList", packingList);
        setResult(RESULT_OK, intent);
        finish();
    }
}
