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

// TODO obviously, there's a lot of duplicated code between the editor activities...
// try to abstract that away.
public class PackingListEditorActivity extends AppCompatActivity {
    private EditText editTextPackingListName;
    private boolean editing = false;
    private PackingList packingList;
    private static final String TAG = "PackingListEditorActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_packing_list);
        setSupportActionBar(findViewById(R.id.toolbar));
        findViewById(R.id.buttonSavePackingList).setOnClickListener(e -> onButtonSaveClick());

        editTextPackingListName = findViewById(R.id.editTextPackingListName);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewPackingListTripParameters);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        TripParameterRecyclerViewAdapter tripParameterRecyclerViewAdapter = new TripParameterRecyclerViewAdapter(this);
        recyclerView.setAdapter(tripParameterRecyclerViewAdapter);

        packingList = PacklestApplication.getInstance().packlestData.getPackingListForUuid(
                (UUID) getIntent().getSerializableExtra("packingListUuid"));
        if (packingList != null) {
            setTitle("Edit Packing List");
            editing = true;
            editTextPackingListName.setText(packingList.name);
        } else {
            packingList = new PackingList();
        }
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
        String packingListName = editTextPackingListName.getText().toString();
        if (packingListName.isEmpty() ||
                (!editing && PacklestApplication.getInstance().packlestData.doesPackingListNameExist(packingListName))) {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Packing list requires unique name.")
                    .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss())
                    .setCancelable(false)
                    .create()
                    .show();
        } else {
            packingList.name = editTextPackingListName.getText().toString();

            if (editing) {
                PacklestApplication.getInstance().packlestData.updatePackingList(packingList);
            } else {
                PacklestApplication.getInstance().packlestData.addPackingList(packingList);
            }
            finish();
        }
    }
}
