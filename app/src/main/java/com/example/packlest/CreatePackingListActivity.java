package com.example.packlest;

import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CreatePackingListActivity extends AppCompatActivity {

    private EditText editTextPackingListName;
    TripParameterRecyclerViewAdapter tripParameterRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_packing_list);
        setSupportActionBar(findViewById(R.id.toolbar));
        findViewById(R.id.buttonSavePackingList).setOnClickListener(e -> onButtonSaveClick());

        editTextPackingListName = findViewById(R.id.editTextPackingListName);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewPackingListTripParameters);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        tripParameterRecyclerViewAdapter = new TripParameterRecyclerViewAdapter(this);
        recyclerView.setAdapter(tripParameterRecyclerViewAdapter);
    }

    private void onButtonSaveClick() {
        String packingListName = editTextPackingListName.getText().toString();
        if (packingListName.isEmpty() || PacklestApplication.getInstance().packlestData.doesPackingListNameExist(packingListName)) {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Packing list requires unique name.")
                    .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss())
                    .setCancelable(false)
                    .create()
                    .show();
        } else {
            PackingList packingList = new PackingList();
            packingList.name = editTextPackingListName.getText().toString();
            PacklestApplication.getInstance().packlestData.addPackingList(packingList);
            finish();
        }
    }
}
