package com.example.packlest;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class PacklestApplication extends Application {
    private static PacklestApplication singleton;
    private static final String TAG = "PacklestApplication";
    private static final String DATA_FILE = "themDatas.json";

    public ArrayList<PackingList> packingLists;

    public static PacklestApplication getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;

        StringBuilder input = new StringBuilder();
        try {
            File file = new File(getFilesDir(), DATA_FILE);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                input.append(line);
                input.append('\n');
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String inputJson = input.toString();
        Log.v(TAG, inputJson); // TODO Remove
        if (inputJson.isEmpty()) {
            packingLists = new ArrayList<>();
        } else {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<PackingList>>() {
            }.getType();
            packingLists = gson.fromJson(input.toString(), type);
        }
    }

    public void onPause() {
        Log.v(TAG, "Application pausing");
        Gson gson = new Gson();
        String fileContents = gson.toJson(packingLists);
        Log.v(TAG, "Writing:" + fileContents);
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(DATA_FILE, Context.MODE_PRIVATE);
            outputStream.write(fileContents.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
