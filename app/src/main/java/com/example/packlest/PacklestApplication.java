package com.example.packlest;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.UUID;

public class PacklestApplication extends Application {
    private static PacklestApplication singleton;
    private static final String TAG = "PacklestApplication";
    private static final String DATA_FILE = "packlest_data.json";

    public PacklestData packlestData;

    public static PacklestApplication getInstance() {
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

    // All activities should call onPause to persist data.
    public void onPause() {
        Log.v(TAG, "Application pausing");
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
