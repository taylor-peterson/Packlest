package com.example.packlest;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import java.util.UUID;

public class ItemCategoryEditorActivity extends AbstractEditorActivity {
        private ItemCategory itemCategory;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            UUID itemCategoryUuid = (UUID) getIntent().getSerializableExtra("itemCategoryUuid");
            createBaseEditor();

            itemCategory = PacklestApplication.getInstance().packlestData.itemCategories.get(itemCategoryUuid);
            if (itemCategory != null) {
                setTitle("Edit Item Category");
                editing = true;
                editText.setText(itemCategory.name);
            } else {
                itemCategory = new ItemCategory();
                setTitle("Create Item Category");
            }
        }

        @Override
        public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
            if (menuItem.getItemId() == R.id.delete_item) {
                if (itemCategory.name.equals("Uncategorized")) {
                    new AlertDialog.Builder(this)
                            .setTitle("Deletion Error")
                            .setIcon(android.R.drawable.ic_menu_info_details)
                            .setMessage("'Uncategorized' cannot be deleted.")
                            .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                            .setCancelable(false)
                            .create()
                            .show();
                } else {
                    new AlertDialog.Builder(this)
                            .setTitle("Confirm Deletion")
                            .setMessage("Do you really want to delete: " + itemCategory.name + "?")
                            .setIcon(android.R.drawable.ic_menu_delete)
                            .setPositiveButton(android.R.string.yes, (dialog, button) -> {
                                PacklestApplication.getInstance().packlestData.deleteItemCategory(itemCategory.uuid);
                                finish();
                            })
                            .setNegativeButton(android.R.string.cancel, null).show();
                }
            }

            return super.onOptionsItemSelected(menuItem);
        }

        void onClickButtonSave() {
            if (showAlertDialogIfNeeded(itemCategory.name, PacklestApplication.getInstance().packlestData.itemCategories.values())) {
                return; // Alert dialog shown by call above; nothing to do.
            }

            itemCategory.name = editText.getText().toString();
            PacklestApplication.getInstance().packlestData.addOrUpdateItemCategory(itemCategory);
            finish();
        }
    }
