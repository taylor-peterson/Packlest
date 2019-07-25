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

import java.util.UUID;

public class TripParameterEditorActivity extends AppCompatActivity {
    private EditText editTextTripParameterName;
    private TripParameter tripParameter;
    private static final String TAG = "TripParameterEditorActivity";
    private boolean editing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_trip_parameter);
        setSupportActionBar(findViewById(R.id.toolbar));
        findViewById(R.id.buttonSaveTripParameter).setOnClickListener(e -> onClickButtonSave());

        editTextTripParameterName = findViewById(R.id.editTextTripParameterName);

        UUID tripParameterUuid = (UUID) getIntent().getSerializableExtra("tripParameterUuid");
        tripParameter = PacklestApplication.getInstance().packlestData.tripParameters.get(tripParameterUuid);
        if (tripParameter != null) {
            setTitle("Edit Trip Parameter");
            editing = true;
            editTextTripParameterName.setText(tripParameter.name);
        } else {
            tripParameter = new TripParameter();
            setTitle("Create Trip Parameter");
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
            Log.v(TAG, "Deleting Trip Parameter");
            PacklestApplication.getInstance().packlestData.deleteTripParameter(tripParameter.uuid);
            finish();
        }

        return super.onOptionsItemSelected(menuItem);
    }

    private void onClickButtonSave() {
        String name = editTextTripParameterName.getText().toString();
        boolean duplicateName = PacklestApplication.getInstance().packlestData.doesNameExist(name, PacklestApplication.getInstance().packlestData.tripParameters.values());
        boolean renamed = (!name.equals(tripParameter.name));
        if (name.isEmpty() ||
                (!editing && duplicateName) ||
                (editing && renamed && duplicateName)) {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Trip Parameter requires unique name.")
                    .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss())
                    .setCancelable(false)
                    .create()
                    .show();
        } else {
            tripParameter.name = editTextTripParameterName.getText().toString();

            PacklestApplication.getInstance().packlestData.addOrUpdateTripParameter(tripParameter);
            finish();
        }
    }

}
