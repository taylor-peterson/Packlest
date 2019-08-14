package com.example.packlest;

import android.view.Menu;
import android.view.MenuInflater;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

abstract class AbstractEditorActivity extends AppCompatActivity {
    EditText editText;
    boolean editing = false;
    TripParameterRecyclerViewAdapter tripParameterRecyclerViewAdapter;

    // TODO use visibility to get rid of the second layout
    void createBaseItemCategoryOrTripParameterEditor() {
        setContentView(R.layout.item_category_and_trip_parameter_editor);
        setSupportActionBar(findViewById(R.id.toolbar));
        findViewById(R.id.button_save).setOnClickListener(e -> onClickButtonSave());

        editText = findViewById(R.id.edit_text_editee_name);
    }

    void createBaseItemOrPackingListEditor(HashSet<UUID> tripParameterUuids) {
        setContentView(R.layout.item_and_packing_list_editor);
        setSupportActionBar(findViewById(R.id.toolbar));
        findViewById(R.id.button_save).setOnClickListener(e -> onClickButtonSave());

        editText = findViewById(R.id.edit_test_editee_name);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_packing_list_trip_parameters);
        recyclerView.setLayoutManager(new GridLayoutManager(this, PacklestApplication.TRIP_PARAMETER_COLUMN_COUNT));
        tripParameterRecyclerViewAdapter = new TripParameterRecyclerViewAdapter(this, tripParameterUuids);
        recyclerView.setAdapter(tripParameterRecyclerViewAdapter);
    }

    abstract void onClickButtonSave();

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
                    .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss())
                    .setCancelable(false)
                    .create()
                    .show();
            return true;
        } else {
            return false;
        }
    }
}
