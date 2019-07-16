package com.example.packlest;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class PacklestActivity extends AppCompatActivity {
    private ListView packingListView;
    private ArrayAdapter<PackingList> arrayAdapter;
    private ArrayList<PackingList> packingLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setSupportActionBar(findViewById(R.id.toolbar));

        packingLists = PacklestApplication.getInstance().packlestData.getPackingLists();
        arrayAdapter = new ArrayAdapter<>(this, R.layout.listview, R.id.textView, packingLists);
        packingListView = findViewById(R.id.packingListView);
        packingListView.setAdapter(arrayAdapter);

        setListViewOnItemClickListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        PacklestApplication.getInstance().persistData();
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
        packingLists.clear();
        packingLists.addAll(PacklestApplication.getInstance().packlestData.getPackingLists());
        arrayAdapter.notifyDataSetChanged();
        PacklestApplication.getInstance().persistData();
    }

    private void setListViewOnItemClickListener() {
        packingListView.setOnItemClickListener((parent, view, position, id) -> {
            PackingList packingList = arrayAdapter.getItem(position);
            Intent intent = new Intent(this, PackingListActivity.class);
            intent.putExtra("packingListUuid", packingList.uuid);
            startActivityForResult(intent, REQUEST_CODES.VIEW_PACKING_LIST.ordinal());
        });
    }
}
