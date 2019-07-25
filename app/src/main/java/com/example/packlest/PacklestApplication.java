package com.example.packlest;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

@SuppressWarnings("WeakerAccess") // Needs to be public or the manifest complains.
public class PacklestApplication extends Application {
    private static PacklestApplication singleton;
    private static final String TAG = "PacklestApplication";
    private static final String DATA_FILE = "packlest_data.json";
    static final int IGNORED_REQUEST_CODE = 0; // Request codes are required to get responses, but are not used by application logic.
    static final int TRIP_PARAMETER_COLUMN_COUNT = 3; // Number of columns of trip parameters to display.

    PacklestData packlestData;

    static PacklestApplication getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;

        packlestData = new PacklestData();
        File packlestDataFile = new File(getFilesDir(), DATA_FILE);
        packlestData.loadPacklestDataFromFile(packlestDataFile);
    }

    // All activities should call persistData to persist data.
    void persistData() {
        Log.v(TAG, "Persisting application data.");
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput(DATA_FILE, Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        packlestData.persistPacklestDataToFile(outputStream);
    }
}
