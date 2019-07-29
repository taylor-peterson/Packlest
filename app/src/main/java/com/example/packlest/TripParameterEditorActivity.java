package com.example.packlest;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import java.util.UUID;

public class TripParameterEditorActivity extends AbstractEditorActivity {
    private TripParameter tripParameter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_trip_parameter);
        setSupportActionBar(findViewById(R.id.toolbar));
        findViewById(R.id.buttonSaveTripParameter).setOnClickListener(e -> onClickButtonSave());

        editText = findViewById(R.id.editTextTripParameterName);

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
            Log.v(TAG, "Deleting Trip Parameter");
            PacklestApplication.getInstance().packlestData.deleteTripParameter(tripParameter.uuid);
            finish();
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
