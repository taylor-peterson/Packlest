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

import java.util.ArrayList;
import java.util.UUID;

public class PackingListEditorActivity extends AppCompatActivity {
    private EditText editText;
    private boolean editing = false;
    private static final String TAG = "EditorActivity";
    private PackingList packingList;
    private TripParameterRecyclerViewAdapter tripParameterRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor);
        setSupportActionBar(findViewById(R.id.toolbar));
        findViewById(R.id.buttonSave).setOnClickListener(e -> onButtonSaveClick());

        editText = findViewById(R.id.editTextEditeeName);

        packingList = PacklestApplication.getInstance().packlestData.getPackingListForUuid(
                (UUID) getIntent().getSerializableExtra("packingListUuid"));
        if (packingList != null) {
            setTitle("Edit Packing List");
            editing = true;
            editText.setText(packingList.name);
        } else {
            packingList = new PackingList();
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerViewPackingListTripParameters);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        tripParameterRecyclerViewAdapter = new TripParameterRecyclerViewAdapter(this, packingList.tripParameterUuids);
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
            Log.v(TAG, "Deleting Packing List");
            PacklestApplication.getInstance().packlestData.deletePackingList(packingList.uuid);
            finish();
        }

        return super.onOptionsItemSelected(menuItem);
    }

    private void onButtonSaveClick() {
        String name = editText.getText().toString();
        if (name.isEmpty() ||
                (!editing && PacklestApplication.getInstance().packlestData.doesPackingListNameExist(name))) {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Packing list requires unique name.")
                    .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss())
                    .setCancelable(false)
                    .create()
                    .show();
        } else {
            packingList.name = editText.getText().toString();
            packingList.tripParameterUuids = tripParameterRecyclerViewAdapter.getTripParametersSelectedForUse();

            if (editing) {
                PacklestApplication.getInstance().packlestData.updatePackingList(packingList);
                // TODO add all new items affiliated with new trip parameters
            } else {
                ArrayList<UUID> itemUuids = new ArrayList<>();
                for (UUID tripParameterUuid : packingList.tripParameterUuids) {
                    itemUuids.addAll(PacklestApplication.getInstance().packlestData.getItemUuidsForTripParameterUuid(tripParameterUuid));
                }
                for (UUID itemUuid : itemUuids) {
                    ItemInstance itemInstance = new ItemInstance(itemUuid);
                    // TODO only add unique items - change to set?
                    packingList.itemInstances.add(itemInstance);
                }

                PacklestApplication.getInstance().packlestData.addPackingList(packingList);
            }
            finish();
        }
    }
}
