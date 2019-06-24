package com.example.packlest;

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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PacklestData {
    private static final String TAG = "PacklestData";
    private Map<UUID, PackingList> packingLists;

    PacklestData() {
        packingLists = new HashMap<>();
    }

    public void loadPacklestDataFromFile(File file) {
        Log.v(TAG, "Loading packlest data from file");
        StringBuilder input = new StringBuilder();
        try {
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
        if (!inputJson.isEmpty()) {
            Gson gson = new Gson();
            Type type = new TypeToken<HashMap<UUID, PackingList>>() {}.getType();
            packingLists = gson.fromJson(inputJson, type);
        }
    }

    public void persistPacklestDataToFile(FileOutputStream outputStream) {
        Log.v(TAG, "Persisting packlest data to file");
        Gson gson = new Gson();
        String fileContents = gson.toJson(packingLists);
        Log.v(TAG, "Writing:" + fileContents);
        try {
            outputStream.write(fileContents.getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<PackingList> getPackingLists() {
        return new ArrayList<>(packingLists.values());
    }

    public void addPackingList(PackingList packingList) {
        Log.v(TAG, "Adding new packing list with name:" + packingList.name);
        packingLists.put(packingList.uuid, packingList);
    }

    public void addItemToPackingList(UUID packingListUuid, Item item) {
        packingLists.get(packingListUuid).items.add(item);
    }

    public void updateItemInPackingList(UUID packingListUuid, Item modifiedItem) {
        Log.v(TAG, "Modifying item: " + modifiedItem.name);
        for (int i = 0; i < packingLists.get(packingListUuid).items.size(); i++) {
            if (packingLists.get(packingListUuid).items.get(i).uuid.equals(modifiedItem.uuid)) {
                Log.v(TAG, "item replaced");
                packingLists.get(packingListUuid).items.set(i, modifiedItem);
            }
        }
    }

    public void deletePackingList(UUID uuid) {
        Log.v(TAG, "Removing packing list");
        packingLists.remove(uuid);
    }

    public void setCheckboxStateForAllItemsInPackingList(UUID uuid, CHECKBOX_STATE checkbox_state) {
        for (Item item : packingLists.get(uuid).items) {
            item.checkbox_state = checkbox_state;
        }
    }

    public void uncheckAllCheckedItemsInPackingList(UUID uuid) {
        for (Item item : packingLists.get(uuid).items) {
            if (item.checkbox_state == CHECKBOX_STATE.CHECKED) {
                item.checkbox_state = CHECKBOX_STATE.UNCHECKED;
            }
        }
    }

    public void removeItemFromPackingList(UUID packingListUuid, Item item) {
        Log.v(TAG, "Removing item:" + item.uuid);
        for (int i = 0; i < packingLists.get(packingListUuid).items.size(); i++) {
            if (packingLists.get(packingListUuid).items.get(i).uuid.equals(item.uuid)) {
                packingLists.get(packingListUuid).items.remove(i);
            }
        }
    }

    public PackingList getPackingListForUUID(UUID uuid) {
        return packingLists.get(uuid);
    }
}
