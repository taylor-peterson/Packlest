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
import java.util.ListIterator;
import java.util.UUID;

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

    public PackingList getpackingListForUUID(UUID uuid) {
        for (PackingList packingList : packingLists) {
            if (packingList.uuid == uuid) {
                return packingList;
            }
        }
        return new PackingList();
    }

    public void updatePackingList(PackingList packingList) {
        ListIterator<PackingList> iterator = packingLists.listIterator();
        while (iterator.hasNext()) {
            PackingList packingListEntry = iterator.next();
            if (packingListEntry.uuid.equals(packingList.uuid)) {
                iterator.set(packingList);
                Log.v(TAG, "Modified globally: " + packingList.name);
            }
        }
    }

    public void updateItem(PackingList packingList, Item item) {
        int packingListCounter = 0;
        for (PackingList packingListEntry : packingLists) {
            if (packingListEntry.uuid.equals(packingList.uuid)) {
                Gson gson = new Gson();
                Log.v(TAG, gson.toJson(packingLists));
                int itemCounter = 0;
                for (Item itemEntry : packingList.items) {
                    if (itemEntry.uuid.equals(item.uuid)) {
                        itemEntry.checkbox_state = item.checkbox_state;
                        itemEntry.name = item.name;
                        packingLists.get(packingListCounter).items.get(itemCounter).checkbox_state = itemEntry.checkbox_state;
                        Log.v(TAG, "Modified globally: " + item.name);
                        Log.v(TAG, "Modified globally: " + item.checkbox_state.toString());

                    }
                }
                itemCounter++;
                Log.v(TAG, gson.toJson(packingLists));
            }
            packingListCounter++;
        }
    }

    public PackingList getUpdatedPackingList(PackingList packingList) {
        for (PackingList packingListEntry : packingLists) {
            if (packingListEntry.uuid.equals(packingList.uuid)) {
                return packingListEntry;
            }
        }
        return new PackingList();
    }
}
