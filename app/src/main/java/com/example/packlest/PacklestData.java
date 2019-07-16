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
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PacklestData {
    private static final String TAG = "PacklestData";
    private Map<UUID, PackingList> packingLists;
    Map<UUID, Item> items; // TODO probs make private again
    Map<UUID, TripParameter> tripParameters;

    PacklestData() {
        packingLists = new HashMap<>();
        items = new HashMap<>();
        tripParameters = new HashMap<>();
    }

    public String[] getTripParameterNames() {
        ArrayList<String> names = new ArrayList<>();
        for (TripParameter tripParameter : tripParameters.values()) {
            names.add(tripParameter.name);
        }
        return names.toArray(new String[0]);
    }

    public ArrayList<TripParameter> getTripParametersForNames(List<String> names) {
        ArrayList<TripParameter> tripParametersToReturn = new ArrayList<>();
        for (String name : names) {
            for (TripParameter tripParameter : tripParameters.values()) {
                if (tripParameter.name.equals(name)) {
                    tripParametersToReturn.add(tripParameter);
                    continue;
                }
            }
            TripParameter newTripParameter = new TripParameter();
            newTripParameter.name = name;
            // TODO items
            // TODO if there are no items for a trip parameter, clean it up? Or add different interface instead of text field?
            tripParameters.put(newTripParameter.uuid, newTripParameter);
            tripParametersToReturn.add(newTripParameter);
        }
        return tripParametersToReturn;
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

        String[] inputJson = input.toString().split("NEXT_HASH_MAP");
        Gson gson = new Gson();

        if (inputJson.length == 3) {
            String inputPackingLists = inputJson[0];
            if (!inputPackingLists.isEmpty()) {
                Type type = new TypeToken<HashMap<UUID, PackingList>>() {}.getType();
                packingLists = gson.fromJson(inputPackingLists, type);
            }

            String inputItems = inputJson[1];
            if (!inputItems.isEmpty()) {
                Type type = new TypeToken<HashMap<UUID, Item>>() {}.getType();
                items = gson.fromJson(inputItems, type);
            }

            String inputTripParameters = inputJson[2];
            if (!inputTripParameters.isEmpty()) {
                Type type = new TypeToken<HashMap<UUID, TripParameter>>() {}.getType();
                tripParameters = gson.fromJson(inputTripParameters, type);
            }
        }
    }

    public void persistPacklestDataToFile(FileOutputStream outputStream) {
        Log.v(TAG, "Persisting packlest data to file");
        Gson gson = new Gson();
        String fileContents =
                gson.toJson(packingLists) + "NEXT_HASH_MAP" +
                gson.toJson(items) + "NEXT_HASH_MAP" +
                gson.toJson(tripParameters);
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

    public boolean doesPackingListNameExist(String name) {
        for (PackingList packingList : packingLists.values()) {
            if (packingList.name.equals(name)) {
                return true;
            }
        }
        return false;
    }

    public boolean doesItemNameExist(String name) {
        for (Item item : items.values()) {
            if (item.name.equals(name)) {
                return true;
            }
        }
        return false;
    }

    public void addPackingList(PackingList packingList) {
        Log.v(TAG, "Adding new packing list with name:" + packingList.name);
        packingLists.put(packingList.uuid, packingList);
    }

    public void addItem(Item item) {
        items.put(item.uuid, item);
    }

    public void addItemToPackingList(UUID packingListUuid, ItemInstance item) {
        packingLists.get(packingListUuid).itemInstances.add(item);
    }

    public void updateItem(Item modifiedItem) {
        Log.v(TAG, "Modifying item");
        items.put(modifiedItem.uuid, modifiedItem);
    }

    public void updateItemInPackingList(UUID packingListUuid, ItemInstance modifiedItem) {
        Log.v(TAG, "Modifying item instance");
        for (int i = 0; i < packingLists.get(packingListUuid).itemInstances.size(); i++) {
            if (packingLists.get(packingListUuid).itemInstances.get(i).uuid.equals(modifiedItem.uuid)) {
                Log.v(TAG, "item replaced");
                packingLists.get(packingListUuid).itemInstances.set(i, modifiedItem);
            }
        }
    }

    public void removeItem(Item modifiedItem) {
        items.remove(modifiedItem);
    }

    public void deletePackingList(UUID uuid) {
        Log.v(TAG, "Removing packing list");
        packingLists.remove(uuid);
        // TODO will need to clean up any items that do not have parameters
        // i.e. those that would be orphaned by deleting this list
    }

    public void setCheckboxStateForAllItemsInPackingList(UUID uuid, CHECKBOX_STATE checkbox_state) {
        for (ItemInstance itemInstance : packingLists.get(uuid).itemInstances) {
            itemInstance.checkbox_state = checkbox_state;
        }
    }

    public void uncheckAllCheckedItemsInPackingList(UUID uuid) {
        for (ItemInstance itemInstance : packingLists.get(uuid).itemInstances) {
            if (itemInstance.checkbox_state == CHECKBOX_STATE.CHECKED) {
                itemInstance.checkbox_state = CHECKBOX_STATE.UNCHECKED;
            }
        }
    }

    public void removeItemFromPackingList(UUID packingListUuid, Item item) {
        Log.v(TAG, "Removing item:" + item.uuid);
        for (int i = 0; i < packingLists.get(packingListUuid).itemInstances.size(); i++) {
            if (packingLists.get(packingListUuid).itemInstances.get(i).item_uuid.equals(item.uuid)) {
                packingLists.get(packingListUuid).itemInstances.remove(i);
            }
        }
    }

    public PackingList getPackingListForUuid(UUID uuid) {
        return packingLists.get(uuid);
    }

    public Item getItemForUuid(UUID uuid) {
        return items.get(uuid);
    }
}
