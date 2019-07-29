package com.example.packlest;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import java.util.UUID;

public class PackingListEditorActivity extends AbstractEditorActivity {
    private PackingList packingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID packingListUuid = (UUID) getIntent().getSerializableExtra("packingListUuid");
        createBaseItemOrPackingListEditor(
                PacklestApplication.getInstance().packlestData.packlestDataRelationships.getTripParameterUuidsForPackingListUuid(packingListUuid));

        packingList = PacklestApplication.getInstance().packlestData.packingLists.get(packingListUuid);
        if (packingList != null) {
            setTitle("Edit Packing List");
            editing = true;
            editText.setText(packingList.name);
        } else {
            packingList = new PackingList();
            setTitle("Create Packing List");
        }
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

    void onClickButtonSave() {
        if (showAlertDialogIfNeeded(packingList.name, PacklestApplication.getInstance().packlestData.packingLists.values())) {
        } else {
            packingList.name = editText.getText().toString();
            PacklestApplication.getInstance().packlestData.addOrUpdatePackingList(packingList, tripParameterRecyclerViewAdapter.getTripParametersSelectedForUse());
            finish();
        }
    }
}
