package com.example.packlest;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import java.util.UUID;

public class ItemEditorActivity extends AbstractEditorActivity {
    private Item item;
    private UUID packingListUuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID itemUuid = (UUID) getIntent().getSerializableExtra("itemUuid");
        createBaseItemOrPackingListEditor(PacklestApplication.getInstance().packlestData.packlestDataRelationships.getTripParameterUuidsForItemUuid(itemUuid));

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
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.delete_item) {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm Deletion")
                    .setMessage("Do you really want to delete: " + item.name + "?")
                    .setIcon(android.R.drawable.ic_menu_delete)
                    .setPositiveButton(android.R.string.yes, (dialog, button) -> {
                        PacklestApplication.getInstance().packlestData.deleteItem(item.uuid);
                        finish();
                    })
                    .setNegativeButton(android.R.string.cancel, null).show();
        }

        return super.onOptionsItemSelected(menuItem);
    }

    void onClickButtonSave() {
        if (showAlertDialogIfNeeded(item.name, PacklestApplication.getInstance().packlestData.items.values())) {
            return; // Alert dialog shown by call above; nothing to do.
        }

        item.name = editText.getText().toString();
        PacklestApplication.getInstance().packlestData.addOrUpdateItem(item, tripParameterRecyclerViewAdapter.getTripParametersSelectedForUse());

        if (!editing && packingListUuid != null) {
            // In this case, you're creating an ad-hoc item from the packing list activity.
            // This item may not be associated with any Trip Parameters and should be added to
            // that packing list directly.
            PacklestApplication.getInstance().packlestData.addItemToPackingList(item.uuid, packingListUuid);
        }
        finish();
    }
}
