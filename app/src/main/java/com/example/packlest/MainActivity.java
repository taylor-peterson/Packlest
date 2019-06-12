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

public class MainActivity extends AppCompatActivity {
    ListView packingListView;
    private ArrayAdapter<String> arrayAdapter;
    ArrayList<String> packingLists = new ArrayList<>();

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
        } else if (id == R.id.create_packing_list_button) {
            Intent intent = new Intent(this, CreatePackingList.class);
            startActivityForResult(intent, 1);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                String newPackingListName = data.getStringExtra("packingListName");
                packingLists.add(newPackingListName);
                arrayAdapter.notifyDataSetChanged();
            }
        }
    }

    private void setListViewOnItemClickListener() {
        packingListView.setOnItemClickListener((parent, view, position, id) -> {
            String packingListName = arrayAdapter.getItem(position);
            Intent intent = new Intent(this, PackingList.class);
            intent.putExtra("packingListName", packingListName);
            startActivityForResult(intent, 2);
        });
    }
}
