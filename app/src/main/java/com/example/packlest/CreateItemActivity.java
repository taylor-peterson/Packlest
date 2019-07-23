package com.example.packlest;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.UUID;

public class CreateItemActivity extends AppCompatActivity {
    private EditText editTextItemName;
    private MultiAutoCompleteTextView tripParameters;
    private Item item;
    private static final String TAG = "CreateItemActivity";
    private UUID packingListUuid;
    private boolean newItem = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_item);
        setSupportActionBar(findViewById(R.id.toolbar));
        findViewById(R.id.buttonSaveItem).setOnClickListener(this::onClickButtonSave);

        editTextItemName = findViewById(R.id.editTextItemName);

        tripParameters = findViewById(R.id.multiAutoCompleteTextView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, PacklestApplication.getInstance().packlestData.getTripParameterNames());
        tripParameters.setAdapter(adapter);
        tripParameters.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        item = PacklestApplication.getInstance().packlestData.getItemForUuid(
                (UUID) getIntent().getSerializableExtra("itemUuid"));
        if (item != null) {
            setTitle("Edit Item");
            newItem = false;
            editTextItemName.setText(item.name);
            tripParameters.setText(item.getTripParameterNames());
        } else {
            item = new Item();
            setTitle("Create Item");
        }

        packingListUuid = (UUID) getIntent().getSerializableExtra("packingListUuid");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_item, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!newItem) {
            menu.findItem(R.id.delete_item).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.delete_item) {
            Log.v(TAG, "Deleting item");
            if (packingListUuid != null) {
                PacklestApplication.getInstance().packlestData.removeItemFromPackingList(packingListUuid, item);
            }
            PacklestApplication.getInstance().packlestData.deleteItem(item);
            finish();
        }

        return super.onOptionsItemSelected(menuItem);
    }

    private void onClickButtonSave(View e) {
        String itemName = editTextItemName.getText().toString();
        if (itemName.isEmpty() ||
                (newItem && PacklestApplication.getInstance().packlestData.doesItemNameExist(itemName))) {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Item requires unique name.")
                    .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss())
                    .setCancelable(false)
                    .create()
                    .show();
        } else {
            item.name = editTextItemName.getText().toString();
            item.tripParameters = PacklestApplication.getInstance().packlestData.getTripParametersForNames(Arrays.asList(tripParameters.getText().toString().split("\\s*,\\s*")));

            if (newItem) {
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
