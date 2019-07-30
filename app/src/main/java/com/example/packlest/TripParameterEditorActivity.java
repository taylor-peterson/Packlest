package com.example.packlest;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import java.util.UUID;

public class TripParameterEditorActivity extends AbstractEditorActivity {
    private TripParameter tripParameter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_trip_parameter);
        setSupportActionBar(findViewById(R.id.toolbar));
        findViewById(R.id.button_save_trip_parameter).setOnClickListener(e -> onClickButtonSave());

        editText = findViewById(R.id.edit_text_trip_parameter_name);

        UUID tripParameterUuid = (UUID) getIntent().getSerializableExtra("tripParameterUuid");
        tripParameter = PacklestApplication.getInstance().packlestData.tripParameters.get(tripParameterUuid);
        if (tripParameter != null) {
            setTitle("Edit Trip Parameter");
            editing = true;
            editText.setText(tripParameter.name);
        } else {
            tripParameter = new TripParameter();
            setTitle("Create Trip Parameter");
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.delete_item) {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm Deletion")
                    .setMessage("Do you really want to delete: " + tripParameter.name + "?")
                    .setIcon(android.R.drawable.ic_menu_delete)
                    .setPositiveButton(android.R.string.yes, (dialog, button) -> {
                        PacklestApplication.getInstance().packlestData.deleteTripParameter(tripParameter.uuid);
                        finish();
                    })
                    .setNegativeButton(android.R.string.cancel, null).show();
        }

        return super.onOptionsItemSelected(menuItem);
    }

    void onClickButtonSave() {
        if (showAlertDialogIfNeeded(tripParameter.name, PacklestApplication.getInstance().packlestData.tripParameters.values())) {
           return; // Alert dialog shown by call above; nothing to do.
        }

        tripParameter.name = editText.getText().toString();
        PacklestApplication.getInstance().packlestData.addOrUpdateTripParameter(tripParameter);
        finish();
    }

}
