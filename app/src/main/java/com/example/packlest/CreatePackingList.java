package com.example.packlest;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CreatePackingList extends AppCompatActivity {

    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_packing_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button button_save = findViewById(R.id.button_save);
        button_save.setOnClickListener(this::onClick);

        editText = findViewById(R.id.editText);
    }

    private void onClick(View e) {
        String packingListName = editText.getText().toString();
        if (packingListName.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Packing list requires name.")
                    .setPositiveButton("ok", (dialog, which) -> dialog.dismiss())
                    .setCancelable(false)
                    .create()
                    .show();
        } else {
            Intent result_intent = new Intent();
            PackingList packingList = new PackingList();
            packingList.name = editText.getText().toString();
            result_intent.putExtra("packingListName", packingList);
            setResult(RESULT_OK, result_intent);
            finish();
        }
    }
}
