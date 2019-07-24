package com.example.packlest;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.UUID;

public class TripParameterEditorActivity extends AppCompatActivity {
    private EditText editTextTripParameterName;
    private TripParameter tripParameter;
    private static final String TAG = "TripParameterEditorActivity";
    private boolean newTripParameter = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_trip_paramter);
        setSupportActionBar(findViewById(R.id.toolbar));
        findViewById(R.id.buttonSaveTripParameter).setOnClickListener(e -> onClickButtonSave());

        editTextTripParameterName = findViewById(R.id.editTextTripParameterName);

        MultiAutoCompleteTextView tripParameters = findViewById(R.id.multiAutoCompleteTextViewTripParameter);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, PacklestApplication.getInstance().packlestData.getTripParameterNames());
        tripParameters.setAdapter(adapter);
        tripParameters.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        tripParameter = PacklestApplication.getInstance().packlestData.getTripParameterForUuid(
                (UUID) getIntent().getSerializableExtra("tripParameterUuid"));
        if (tripParameter != null) {
            setTitle("Edit Trip Parameter");
            newTripParameter = false;
            editTextTripParameterName.setText(tripParameter.name);
            //tripParameters.setText(tripParameter.getTripParameterNames());
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
        if (!newTripParameter) {
            menu.findItem(R.id.delete_item).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.delete_item) {
            Log.v(TAG, "Deleting Trip Parameter");
            PacklestApplication.getInstance().packlestData.deleteTripParameter(tripParameter);
            finish();
        }

        return super.onOptionsItemSelected(menuItem);
    }

    private void onClickButtonSave() {
        String tripParameterName = editTextTripParameterName.getText().toString();
        if (tripParameterName.isEmpty() ||
                (newTripParameter && PacklestApplication.getInstance().packlestData.doesTripParameterNameExist(tripParameterName))) {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Trip Parameter requires unique name.")
                    .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss())
                    .setCancelable(false)
                    .create()
                    .show();
        } else {
            tripParameter.name = editTextTripParameterName.getText().toString();
            //tripParameter.tripParameters = PacklestApplication.getInstance().packlestData.getTripParametersForNames(Arrays.asList(tripParameters.getText().toString().split("\\s*,\\s*")));

            if (newTripParameter) {
                PacklestApplication.getInstance().packlestData.addTripParameter(tripParameter);
            } else {
                PacklestApplication.getInstance().packlestData.updateTripParameter(tripParameter);
            }

            finish();
        }
    }

}
