package com.example.packlest;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.ListIterator;

public class MainActivity extends AppCompatActivity {
    ListView packingListView;
    private ArrayAdapter<PackingList> arrayAdapter;
    ArrayList<PackingList> packingLists = new ArrayList<>();

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

        packingListView = findViewById(R.id.packingListView);
        arrayAdapter = new ArrayAdapter<>(this, R.layout.listview, R.id.textView, packingLists);
        packingListView.setAdapter(arrayAdapter);

        setListViewOnItemClickListener();
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
