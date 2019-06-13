package com.example.packlest;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CreateItemActivity extends AppCompatActivity {

    private EditText editText;
    Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_item);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button button_save = findViewById(R.id.buttonSaveCreateItem);
        button_save.setOnClickListener(this::onClick);

        editText = findViewById(R.id.editTextCreateItem);

        Intent intent = getIntent();
        item = intent.getParcelableExtra("item");
        if (item != null) {
            setTitle("Edit Item");
            editText.setText(item.name);
        } else {
            item = new Item();
            setTitle("Create Item");
        }
    }

    private void onClick(View e) {
        String itemName = editText.getText().toString();
        if (itemName.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Item requires name.")
                    .setPositiveButton("ok", (dialog, which) -> dialog.dismiss())
                    .setCancelable(false)
                    .create()
                    .show();
        } else {
            Intent result_intent = new Intent();
            item.name = editText.getText().toString();
            result_intent.putExtra("item", item);
            setResult(RESULT_OK, result_intent);
            finish();
        }
    }

}
