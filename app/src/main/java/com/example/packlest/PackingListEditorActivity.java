package com.example.packlest;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import java.util.UUID;

public class PackingListEditorActivity extends AbstractEditorActivity {
    private PackingList packingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID packingListUuid = (UUID) getIntent().getSerializableExtra("packingListUuid");
        createBaseEditor();
        addTripParameterSelector(
                PacklestApplication.getInstance().packlestData.packlestDataRelationships.getTripParameterUuidsForPackingListUuid(packingListUuid));

        packingList = PacklestApplication.getInstance().packlestData.packingLists.get(packingListUuid);
        if (packingList != null) {
            setTitle("Edit Packing List");
            editing = true;
            editText.setText(packingList.name);
        } else {
            packingList = new PackingList();
            setTitle("Create Packing List");
            findViewById(R.id.button_save_and_create_another).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.delete_item) {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm Deletion")
                    .setMessage("Do you really want to delete: " + packingList.name + "?")
                    .setIcon(android.R.drawable.ic_menu_delete)
                    .setPositiveButton(android.R.string.yes, (dialog, button) -> {
                        PacklestApplication.getInstance().packlestData.deletePackingList(packingList.uuid);
                        finish();
                    })
                    .setNegativeButton(android.R.string.cancel, null).show();
        }

        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    boolean onClickButtonSave() {
        if (showAlertDialogIfNeeded(packingList.name, PacklestApplication.getInstance().packlestData.packingLists.values())) {
            return false; // Alert dialog shown by call above; nothing to do.
        }

        packingList.name = editText.getText().toString();
        PacklestApplication.getInstance().packlestData.addOrUpdatePackingList(packingList, tripParameterRecyclerViewAdapter.getTripParametersSelectedForUse());
        finish();
        return true;
    }
}
