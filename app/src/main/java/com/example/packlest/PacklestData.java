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

class PacklestData {
    private static final String TAG = "PacklestData";
    private Map<UUID, PackingList> packingLists;
    Map<UUID, Item> items; // TODO probs make private again
    private Map<UUID, TripParameter> tripParameters;

    PacklestData() {
        packingLists = new HashMap<>();
        items = new HashMap<>();
        tripParameters = new HashMap<>();
    }

    String[] getTripParameterNames() {
        ArrayList<String> names = new ArrayList<>();
        for (TripParameter tripParameter : tripParameters.values()) {
            names.add(tripParameter.name);
        }
        return names.toArray(new String[0]);
    }

    ArrayList<TripParameter> getTripParametersForNames(List<String> names) {
        ArrayList<TripParameter> tripParametersToReturn = new ArrayList<>();
        for (String name : names) {
            boolean existingTripParameter = false; // TODO better way?
            for (TripParameter tripParameter : tripParameters.values()) {
                if (tripParameter.name.equals(name)) {
                    tripParametersToReturn.add(tripParameter);
                    existingTripParameter = true;
                }
            }

            if (existingTripParameter) {
                continue;
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

    void loadPacklestDataFromFile(File file) {
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

    void persistPacklestDataToFile(FileOutputStream outputStream) {
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

    ArrayList<PackingList> getPackingLists() {
        return new ArrayList<>(packingLists.values());
    }

    ArrayList<Item> getItems() {
        return new ArrayList<>(items.values());
    }

    ArrayList<TripParameter> getTripParameters() {
        return new ArrayList<>(tripParameters.values());
    }

    boolean doesPackingListNameExist(String name) {
        for (PackingList packingList : packingLists.values()) {
            if (packingList.name.equals(name)) {
                return true;
            }
        }
        return false;
    }

    boolean doesItemNameExist(String name) {
        for (Item item : items.values()) {
            if (item.name.equals(name)) {
                return true;
            }
        }
        return false;
    }

    // TODO for these methods repeated three times, abstract?
    boolean doesTripParameterNameExist(String name) {
        for (TripParameter tripParameter : tripParameters.values()) {
            if (tripParameter.name.equals(name)) {
                return true;
            }
        }
        return false;
    }

    void addPackingList(PackingList packingList) {
        Log.v(TAG, "Adding new packing list with name:" + packingList.name);
        packingLists.put(packingList.uuid, packingList);
    }

    void addItem(Item item) {
        items.put(item.uuid, item);
    }
    void addTripParameter(TripParameter tripParameter) {
        tripParameters.put(tripParameter.uuid, tripParameter);
    }

    void addItemToPackingList(UUID packingListUuid, ItemInstance item) {
        packingLists.get(packingListUuid).itemInstances.add(item);
    }

    void updateItem(Item modifiedItem) {
        Log.v(TAG, "Modifying item");
        items.put(modifiedItem.uuid, modifiedItem);
    }

    void updateTripParameter(TripParameter tripParameter) {
        tripParameters.put(tripParameter.uuid, tripParameter);
    }

    void updateItemInPackingList(UUID packingListUuid, ItemInstance modifiedItem) {
        Log.v(TAG, "Modifying item instance");
        for (int i = 0; i < packingLists.get(packingListUuid).itemInstances.size(); i++) {
            if (packingLists.get(packingListUuid).itemInstances.get(i).uuid.equals(modifiedItem.uuid)) {
                Log.v(TAG, "item replaced");
                packingLists.get(packingListUuid).itemInstances.set(i, modifiedItem);
            }
        }
    }

    void deleteItem(Item modifiedItem) {
        items.remove(modifiedItem.uuid);
        // TODO ensure consistency with associated packing lists and parameters
        // Note that doing so here will allow removing the packing list logic from the item activity
        // and might allow simplifying the fragment abstraction further
    }

    void deletePackingList(UUID uuid) {
        Log.v(TAG, "Removing packing list");
        packingLists.remove(uuid);
        // TODO will need to clean up any items that do not have parameters
        // i.e. those that would be orphaned by deleting this list
    }

    void deleteTripParameter(TripParameter tripParameter) {
        tripParameters.remove(tripParameter.uuid);
    }

    void setCheckboxStateForAllItemsInPackingList(UUID uuid, CHECKBOX_STATE checkbox_state) {
        for (ItemInstance itemInstance : packingLists.get(uuid).itemInstances) {
            itemInstance.checkbox_state = checkbox_state;
        }
    }

    void uncheckAllCheckedItemsInPackingList(UUID uuid) {
        for (ItemInstance itemInstance : packingLists.get(uuid).itemInstances) {
            if (itemInstance.checkbox_state == CHECKBOX_STATE.CHECKED) {
                itemInstance.checkbox_state = CHECKBOX_STATE.UNCHECKED;
            }
        }
    }

    void removeItemFromPackingList(UUID packingListUuid, Item item) {
        Log.v(TAG, "Removing item:" + item.uuid);
        for (int i = 0; i < packingLists.get(packingListUuid).itemInstances.size(); i++) {
            if (packingLists.get(packingListUuid).itemInstances.get(i).item_uuid.equals(item.uuid)) {
                packingLists.get(packingListUuid).itemInstances.remove(i);
            }
        }
    }

    PackingList getPackingListForUuid(UUID uuid) {
        return packingLists.get(uuid);
    }
    Item getItemForUuid(UUID uuid) {
        return items.get(uuid);
    }
    TripParameter getTripParameterForUuid(UUID uuid) {
        return tripParameters.get(uuid);
    }
}
