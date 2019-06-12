package com.example.packlest;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

public class PackingListActivity extends AppCompatActivity {
    ListView itemListView;
    private ArrayAdapter<String> arrayAdapter;
    ArrayList<String> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.packing_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        itemListView = findViewById(R.id.listViewItems);
        arrayAdapter = new ArrayAdapter<>(this, R.layout.listview, R.id.textView, items);
        itemListView.setAdapter(arrayAdapter);

        setListViewOnItemClickListener();

        Intent intent = getIntent();
        PackingList packingList = intent.getParcelableExtra("packingList");
        setTitle(packingList.name);
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
        } else if (id == R.id.create_packing_list_button) {
            Intent intent = new Intent(this, CreateItem.class);
            startActivityForResult(intent, 3);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 3) {
                String newItemName = data.getStringExtra("itemName");
                items.add(newItemName);
                arrayAdapter.notifyDataSetChanged();
            }
        }
    }

    private void setListViewOnItemClickListener() {
        itemListView.setOnItemClickListener((parent, view, position, id) -> {
            String itemName = arrayAdapter.getItem(position);
            Intent intent = new Intent(this, CreateItem.class);
            intent.putExtra("itemName", itemName);
            startActivityForResult(intent, 4);
        });
    }
}
