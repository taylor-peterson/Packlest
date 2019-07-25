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

        item = PacklestApplication.getInstance().packlestData.getItemForUuid((UUID) getIntent().getSerializableExtra("itemUuid"));
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
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        tripParameterRecyclerViewAdapter = new TripParameterRecyclerViewAdapter(this, PacklestApplication.getInstance().packlestData.getTripParameterUuidsForItemUuid(item.uuid));
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
            if (packingListUuid != null) { // TODO move this into PacklestData - will need to delete from all packing lists (will require multimap)
                PacklestApplication.getInstance().packlestData.removeItemFromPackingList(packingListUuid, item);
            }
            PacklestApplication.getInstance().packlestData.deleteItem(item);
            finish();
        }

        return super.onOptionsItemSelected(menuItem);
    }

    private void onClickButtonSave() {
        String name = editText.getText().toString();
        if (name.isEmpty() ||
                (!editing && PacklestApplication.getInstance().packlestData.doesItemNameExist(name))) {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Item requires unique name.")
                    .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss())
                    .setCancelable(false)
                    .create()
                    .show();
        } else {
            item.name = editText.getText().toString();
            PacklestApplication.getInstance().packlestData.updateTripParametersForItem(item.uuid, tripParameterRecyclerViewAdapter.getTripParametersInUse());

            if (!editing) {
                ItemInstance itemInstance = new ItemInstance(item.uuid);

                PacklestApplication.getInstance().packlestData.addItem(item);
                if (packingListUuid != null) {
                    PacklestApplication.getInstance().packlestData.addItemToPackingList(packingListUuid, itemInstance);
                }
            } else {
                PacklestApplication.getInstance().packlestData.updateItem(item);
            }

            finish();
        }
    }

}
