package com.example.packlest;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.EditText;

public class CreatePackingListActivity extends AppCompatActivity {

    private EditText editTextPackingListName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_packing_list);
        setSupportActionBar(findViewById(R.id.toolbar));
        findViewById(R.id.buttonSavePackingList).setOnClickListener(e -> onButtonSaveClick());

        editTextPackingListName = findViewById(R.id.editTextPackingListName);
    }

    private void onButtonSaveClick() {
        String packingListName = editTextPackingListName.getText().toString();
        if (packingListName.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Packing list requires name.")
                    .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss())
                    .setCancelable(false)
                    .create()
                    .show();
        } else {
            Intent intent = new Intent();
            PackingList packingList = new PackingList();
            packingList.name = editTextPackingListName.getText().toString();
            intent.putExtra("packingList", packingList);
            setResult(PacklestActivity.RESULT_CODES.PACKING_LIST_CREATED.ordinal(), intent);
            finish();
        }
    }
}
