package com.example.packlest;

import android.os.Bundle;
import android.text.Html;
import android.view.Menu;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class PacklestActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setSupportActionBar(findViewById(R.id.toolbar));

        ViewPager viewPager = findViewById(R.id.view_pager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        checkFirstRun();
    }

    private void checkFirstRun() {
        boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true);
        if (isFirstRun){
            showFirstRunIntroDialog();

            getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .edit()
                    .putBoolean("isFirstRun", false)
                    .apply();
        }
    }

    private void showFirstRunIntroDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Greetings and Salutations!")
                .setIcon(android.R.drawable.ic_menu_info_details)
                .setMessage(Html.fromHtml("Here's a quick overview of the objects you'll be dealing with:" +
                        "<br>" +
                        "<br>" +
                        "<b>Trips</b> are, well, trips!" +
                        "<br>" +
                        "<br>" +
                        "<b>Items</b> are discrete things to be brought on a trip." +
                        "<br>" +
                        "<br>" +
                        "<b>Item Categories</b> are general item classifications (e.g. clothing, climbing gear, overnight gear). " +
                        "Think of these as the logical groupings you'd use when laying everything out to pack. " +
                        "Each item is associated with precisely one category." +
                        "<br>" +
                        "<br>" +
                        "<b>Trip Params</b> are characteristics of a trip (e.g. activities, conditions, length of outing). " +
                        "Each item can be associated with many trip parameters.", Html.FROM_HTML_MODE_LEGACY))
                .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                .setNeutralButton("Tell me more...", (dialog, listener) -> {
                    dialog.dismiss();
                    showDataPersistenceDialog();
                })
                .setCancelable(false)
                .create()
                .show();
    }

    private void showDataPersistenceDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Data Persistence")
                .setIcon(android.R.drawable.ic_menu_info_details)
                .setMessage("Deleting an item/trip parameter/packing list only removes that item and associations to it. " +
                            "It does not cleanup other objects that then have nothing associated with them." +
                            "\n\n" +
                            "Removing a trip parameter from an activity similarly does not remove items only associated with " +
                            "that trip parameter. Rather, it will only result in future items added to that trip parameter " +
                            "not being added to said trip." +
                            "\n\n" +
                            "We utilize \"Save\" buttons whenever editing primitives to provide a logical place for validation.")
                .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                .setNeutralButton("What's in the future...", (dialog, innerListener) -> {
                    dialog.dismiss();
                    showFuturePossibilitiesDialog();
                })
                .setCancelable(false)
                .create()
                .show();
    }

    private void showFuturePossibilitiesDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Future possibilities")
                .setIcon(android.R.drawable.ic_menu_info_details)
                .setMessage("Drag & drop? Weights/quantities? Export/import?")
                .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .create()
                .show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        PacklestApplication.getInstance().persistData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}
