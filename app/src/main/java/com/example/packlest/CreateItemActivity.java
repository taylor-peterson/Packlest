package com.example.packlest;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class CreateItemActivity extends AppCompatActivity {
    private EditText editTextItemName;
    Item item;
    private static final String TAG = "CreateItemActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_item);
        setSupportActionBar(findViewById(R.id.toolbar));
        findViewById(R.id.buttonSaveItem).setOnClickListener(this::onClickButtonSave);

        editTextItemName = findViewById(R.id.editTextItemName);

        item = getIntent().getParcelableExtra("item");
        if (item != null) {
            setTitle("Edit Item");
            editTextItemName.setText(item.name);
        } else {
            item = new Item();
            setTitle("Create Item");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_item, menu);
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        if (getTitle() == "Edit Item") {
            menu.findItem(R.id.delete_item).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.delete_item) {
            Log.v(TAG, "Deleting item");
            Intent deleteIntent = new Intent();
            deleteIntent.putExtra("item", item);
            setResult(PacklestActivity.RESULT_CODES.ITEM_DELETED.ordinal(), deleteIntent);
            finish();
        }

        return super.onOptionsItemSelected(menuItem);
    }

    private void onClickButtonSave(View e) {
        String itemName = editTextItemName.getText().toString();
        if (itemName.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Item requires name.")
                    .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss())
                    .setCancelable(false)
                    .create()
                    .show();
        } else {
            Intent intent = new Intent();
            item.name = editTextItemName.getText().toString();
            intent.putExtra("item", item);
            setResult(PacklestActivity.RESULT_CODES.ITEM_MODIFIED.ordinal(), intent);
            finish();
        }
    }

}
