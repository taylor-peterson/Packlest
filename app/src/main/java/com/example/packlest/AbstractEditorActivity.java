package com.example.packlest;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

abstract class AbstractEditorActivity extends AppCompatActivity {
    EditText editText;
    boolean editing = false;
    TripParameterRecyclerViewAdapter tripParameterRecyclerViewAdapter;
    Spinner spinner;

    void createBaseEditor() {
        setContentView(R.layout.editor);
        setSupportActionBar(findViewById(R.id.toolbar));
        findViewById(R.id.button_save).setOnClickListener(e -> onClickButtonSave());
        findViewById(R.id.button_save_and_create_another).setOnClickListener(e -> onClickButtonSaveAndCreateAnother());

        editText = findViewById(R.id.edit_text_editee_name);
    }

    void addTripParameterSelector(HashSet<UUID> tripParameterUuids) {
        LinearLayout linearLayout = findViewById(R.id.layout_trip_parameters);
        linearLayout.setVisibility(View.VISIBLE);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_packing_list_trip_parameters);
        recyclerView.setLayoutManager(new GridLayoutManager(this, PacklestApplication.TRIP_PARAMETER_COLUMN_COUNT));
        tripParameterRecyclerViewAdapter = new TripParameterRecyclerViewAdapter(this, tripParameterUuids);
        recyclerView.setAdapter(tripParameterRecyclerViewAdapter);
    }

    void addItemCategorySelector(UUID itemCategoryUuid) {
        RelativeLayout relativeLayout = findViewById(R.id.layout_spinner_item_categories);
        relativeLayout.setVisibility(View.VISIBLE);

        spinner = findViewById(R.id.spinner_item_categories);
        ArrayList<ItemCategory> itemCategories = new ArrayList<>(PacklestApplication.getInstance().packlestData.itemCategories.values());
        Collections.sort(itemCategories);
        ArrayAdapter<ItemCategory> arrayAdapter = new ArrayAdapter<>(
                this,
                R.layout.support_simple_spinner_dropdown_item,
                itemCategories);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        ItemCategory itemCategory = PacklestApplication.getInstance().packlestData.itemCategories.get(itemCategoryUuid);
        spinner.setSelection(arrayAdapter.getPosition(itemCategory));
    }

    abstract boolean onClickButtonSave(); // true if successful; false if validation failed.

    private void onClickButtonSaveAndCreateAnother() {
        if (onClickButtonSave()) {
            startActivity(getIntent());
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

    boolean showAlertDialogIfNeeded(String originalName, Collection<? extends AbstractBaseObject> existingObjects) {
        String newName = editText.getText().toString();
        boolean duplicateName = PacklestApplication.getInstance().packlestData.doesNameExist(newName, existingObjects);
        boolean renamed = (!newName.equals(originalName));
        if (newName.isEmpty() ||
                (!editing && duplicateName) ||
                (editing && renamed && duplicateName)) {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Unique name required.")
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                    .setCancelable(false)
                    .create()
                    .show();
            return true;
        } else {
            return false;
        }
    }
}
