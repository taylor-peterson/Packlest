package com.example.packlest;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashSet;
import java.util.UUID;

public class ItemEditorActivity extends AppCompatActivity {
    private EditText editText;
    private boolean editing = false;
    private static final String TAG = "EditorActivity";
    private Item item;
    private UUID packingListUuid;
    private TripParameterRecyclerViewAdapter tripParameterRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor);
        setSupportActionBar(findViewById(R.id.toolbar));
        findViewById(R.id.buttonSave).setOnClickListener(e -> onClickButtonSave());

        editText = findViewById(R.id.editTextEditeeName);

        UUID itemUuid = (UUID) getIntent().getSerializableExtra("itemUuid");
        item = PacklestApplication.getInstance().packlestData.items.get(itemUuid);
        if (item != null) {
            setTitle("Edit Item");
            editing = true;
            editText.setText(item.name);
        } else {
            item = new Item();
            setTitle("Create Item");
        }

        packingListUuid = (UUID) getIntent().getSerializableExtra("packingListUuid");

        RecyclerView recyclerView = findViewById(R.id.recyclerViewPackingListTripParameters);
        recyclerView.setLayoutManager(new GridLayoutManager(this, PacklestApplication.TRIP_PARAMETER_COLUMN_COUNT));
        tripParameterRecyclerViewAdapter = new TripParameterRecyclerViewAdapter(
                this, PacklestApplication.getInstance().packlestData.packlestDataRelationships.getTripParameterUuidsForItemUuid(item.uuid));
        recyclerView.setAdapter(tripParameterRecyclerViewAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_item, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (editing) {
            menu.findItem(R.id.delete_item).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.delete_item) {
            Log.v(TAG, "Deleting item");
            PacklestApplication.getInstance().packlestData.deleteItem(item.uuid);
            finish();
        }

        return super.onOptionsItemSelected(menuItem);
    }

    private void onClickButtonSave() {
        String name = editText.getText().toString();
        boolean duplicateName = PacklestApplication.getInstance().packlestData.doesNameExist(name, PacklestApplication.getInstance().packlestData.items.values());
        boolean renamed = (!name.equals(item.name));
        if (name.isEmpty() ||
                (!editing && duplicateName) ||
                (editing && renamed && duplicateName)) {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Item requires unique name.")
                    .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss())
                    .setCancelable(false)
                    .create()
                    .show();
        } else {
            item.name = editText.getText().toString();
            HashSet<UUID> tripParametersInUse = tripParameterRecyclerViewAdapter.getTripParametersSelectedForUse();
            PacklestApplication.getInstance().packlestData.addOrUpdateItem(item, tripParametersInUse);

            if (!editing && packingListUuid != null) {
                    // In this case, you're creating an ad-hoc item from the packing list activity.
                    // This item may not be associated with any Trip Parameters and should be added to
                    // that packing list directly.
                    PacklestApplication.getInstance().packlestData.addItemToPackingList(item.uuid, packingListUuid);
            }
            finish();
        }
    }

}
