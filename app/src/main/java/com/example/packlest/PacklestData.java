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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

class PacklestData {
    private static final String TAG = "PacklestData";
    private static final String OBJECT_DELIMITER = "OBJECT_DELIMITER";
    Map<UUID, PackingList> packingLists;
    Map<UUID, Item> items;
    Map<UUID, TripParameter> tripParameters;
    PacklestDataRelationships packlestDataRelationships;

    PacklestData() {
        packingLists = new HashMap<>();
        items = new HashMap<>();
        tripParameters = new HashMap<>();
        packlestDataRelationships = new PacklestDataRelationships();
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

        String[] inputJson = input.toString().split(OBJECT_DELIMITER);
        Gson gson = new Gson();

        if (inputJson.length == 4) {
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

            String inputDataRelationships = inputJson[3];
            if (!inputDataRelationships.isEmpty()) {
                Type type = new TypeToken<PacklestDataRelationships>() {}.getType();
                packlestDataRelationships = gson.fromJson(inputDataRelationships, type);
            }
        }
    }

    void persistPacklestDataToFile(FileOutputStream outputStream) {
        Log.v(TAG, "Persisting packlest data to file");
        Gson gson = new Gson();
        String fileContents =
                gson.toJson(packingLists) + OBJECT_DELIMITER+
                gson.toJson(items) + OBJECT_DELIMITER +
                gson.toJson(tripParameters) + OBJECT_DELIMITER +
                gson.toJson(packlestDataRelationships);
        Log.v(TAG, "Writing:" + fileContents);
        try {
            outputStream.write(fileContents.getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    boolean doesNameExist(String name, Collection<? extends PacklestBaseObject> items) {
        for (PacklestBaseObject packlestBaseObject : items) {
            if (name.equals(packlestBaseObject.name)) {
                return true;
            }
        }
        return false;
    }

    void unaddAllItemsInPackingList(UUID packingListUuid) {
        for (ItemInstance itemInstance : packingLists.get(packingListUuid).itemInstances) {
            itemInstance.checkbox_state = CHECKBOX_STATE.UNADDED;
        }
    }

    void uncheckAllCheckedItemsInPackingList(UUID packingListUuid) {
        for (ItemInstance itemInstance : packingLists.get(packingListUuid).itemInstances) {
            if (itemInstance.checkbox_state == CHECKBOX_STATE.CHECKED) {
                itemInstance.checkbox_state = CHECKBOX_STATE.UNCHECKED;
            }
        }
    }

    void addOrUpdateItem(Item item, HashSet<UUID> tripParametersInUse) {
        items.put(item.uuid, item);
        packlestDataRelationships.putItem(item.uuid, tripParametersInUse);
        // TODO need to then go back and update packing list item instances?
        // Or just have the packing list check to see if there are new items and then create new instances as needed?
    }
    void deleteItem(UUID itemUuid) {
        items.remove(itemUuid);
        packlestDataRelationships.removeItemUuid(itemUuid);
    }

    void addOrUpdateTripParameter(TripParameter tripParameter) {
        tripParameters.put(tripParameter.uuid, tripParameter);
    }
    void deleteTripParameter(UUID tripParameterUuid) {
        tripParameters.remove(tripParameterUuid);
        packlestDataRelationships.removeTripParameterUuid(tripParameterUuid);
    }

    void addOrUpdatePackingList(PackingList packingList, HashSet<UUID> tripParametersInUse) {
        packingLists.put(packingList.uuid, packingList);

        HashSet<UUID> itemUuids = new HashSet<>();
        for (UUID tripParameterUuid : tripParametersInUse) {
            itemUuids.addAll(packlestDataRelationships.getItemUuidsForTripParameterUuid(tripParameterUuid));
        }

        // TODO UGGGGGGGLLLLLYYYYY
        for (UUID itemUuid : itemUuids) {
            boolean repeated = false;
            for (ItemInstance itemInstance : packingList.itemInstances) {
                if (itemInstance.item_uuid.equals(itemUuid)) {
                    repeated = true;
                }
            }
            if (!repeated) {
                ItemInstance itemInstance = new ItemInstance(itemUuid);
                packingList.itemInstances.add(itemInstance);
            }
        }

        // TODO remove item associated only with removed parameters
        // Something like foreach item, if has parameter(s) and none in list, remove

        packlestDataRelationships.putPackingList(packingList.uuid, tripParametersInUse);
    }
    void deletePackingList(UUID packingListUuid) {
        packingLists.remove(packingListUuid);
        packlestDataRelationships.removePackingListUuid(packingListUuid);

        // Delete any items that have no parameters (i.e. that were created solely for this list).
        for (ItemInstance itemInstance : packingLists.get(packingListUuid).itemInstances) {
            if (packlestDataRelationships.getTripParameterUuidsForItemUuid(itemInstance.item_uuid).isEmpty()) {
                deleteItem(itemInstance.item_uuid);
            }
        }
    }
    void addItemToPackingList(UUID packingListUuid, ItemInstance item) {
        packingLists.get(packingListUuid).itemInstances.add(item);
    }
    void updateItemInPackingList(UUID packingListUuid, ItemInstance modifiedItem) {
        for (int i = 0; i < packingLists.get(packingListUuid).itemInstances.size(); i++) {
            if (packingLists.get(packingListUuid).itemInstances.get(i).uuid.equals(modifiedItem.uuid)) {
                packingLists.get(packingListUuid).itemInstances.set(i, modifiedItem);
            }
        }
    }

    // Derived from https://stackoverflow.com/questions/31498785/data-structure-to-represent-many-to-many-relationship
    class PacklestDataRelationships {
        private final Map<UUID, HashSet<UUID>> itemUuidToTripParameterUuidsMap = new HashMap<>();
        private final Map<UUID, HashSet<UUID>> itemUuidToPackingListUuidsMap = new HashMap<>();
        private final Map<UUID, HashSet<UUID>> tripParameterUuidToItemUuidsMap = new HashMap<>();
        private final Map<UUID, HashSet<UUID>> tripParameterUuidToPackingListUuidsMap = new HashMap<>();
        private final Map<UUID, HashSet<UUID>> packingListUuidToItemUuidsMap = new HashMap<>();
        private final Map<UUID, HashSet<UUID>> packingListUuidToTripParameterUuidsMap = new HashMap<>();

        void putItem(UUID itemUuid, HashSet<UUID> tripParameterUuids) {
            removeItemUuid(itemUuid); // The old parameter set might differ from the new one.
            for (UUID tripParameterUuid : tripParameterUuids) {
                putItemRelationship(itemUuid, tripParameterUuid);
            }
        }
        void putItemRelationship(UUID itemUuid, UUID tripParameterUuid) {
            if (!itemUuidToTripParameterUuidsMap.containsKey(itemUuid)) {
                itemUuidToTripParameterUuidsMap.put(itemUuid, new HashSet<>());
            }
            itemUuidToTripParameterUuidsMap.get(itemUuid).add(tripParameterUuid);

            if (!tripParameterUuidToItemUuidsMap.containsKey(tripParameterUuid)) {
                tripParameterUuidToItemUuidsMap.put(tripParameterUuid, new HashSet<>());
            }
            tripParameterUuidToItemUuidsMap.get(tripParameterUuid).add(itemUuid);
        }

        void putPackingList(UUID packingListUuid, HashSet<UUID> tripParameterUuids) {
            removePackingListUuid(packingListUuid); // The old parameter set might differ from the new one.
            for (UUID tripParameterUuid : tripParameterUuids) {
                putPackingListRelationship(packingListUuid, tripParameterUuid);
            }
        }
        void putPackingListRelationship(UUID packingListUuid, UUID tripParameterUuid) {
            if (!packingListUuidToTripParameterUuidsMap.containsKey(packingListUuid)) {
                packingListUuidToTripParameterUuidsMap.put(packingListUuid, new HashSet<>());
            }
            packingListUuidToTripParameterUuidsMap.get(packingListUuid).add(tripParameterUuid);

            if (!tripParameterUuidToPackingListUuidsMap.containsKey(tripParameterUuid)) {
                tripParameterUuidToPackingListUuidsMap.put(tripParameterUuid, new HashSet<>());
            }
            tripParameterUuidToPackingListUuidsMap.get(tripParameterUuid).add(packingListUuid);
        }

        HashSet<UUID> getTripParameterUuidsForItemUuid(UUID itemUuid) {
            HashSet<UUID> tripParameterUuidsForItemUuid = itemUuidToTripParameterUuidsMap.get(itemUuid);
            if (tripParameterUuidsForItemUuid != null) {
                return tripParameterUuidsForItemUuid;
            }
            return new HashSet<>();
        }

        HashSet<UUID> getTripParameterUuidsForPackingListUuid(UUID packingListUuid) {
            HashSet<UUID> tripParameterUuidsForPackingListUuid = packingListUuidToTripParameterUuidsMap.get(packingListUuid);
            if (tripParameterUuidsForPackingListUuid != null) {
                return tripParameterUuidsForPackingListUuid;
            }
            return new HashSet<>();
        }

        HashSet<UUID> getItemUuidsForTripParameterUuid(UUID tripParameterUuid) {
            HashSet<UUID> itemUuidsForTripParameterUuid = tripParameterUuidToItemUuidsMap.get(tripParameterUuid);
            if (itemUuidsForTripParameterUuid != null) {
                return itemUuidsForTripParameterUuid;
            }
            return new HashSet<>();
        }

        void removeItemUuid(UUID itemUuid) {
            HashSet<UUID> tripParameterUuidsToCleanup = itemUuidToTripParameterUuidsMap.remove(itemUuid);
            if (tripParameterUuidsToCleanup != null) {
                for (UUID tripParameterUuid : tripParameterUuidsToCleanup) {
                    tripParameterUuidToItemUuidsMap.get(tripParameterUuid).remove(itemUuid);
                }
            }

            HashSet<UUID> packingListUuidsToCleanup = itemUuidToPackingListUuidsMap.remove(itemUuid);
            if (packingListUuidsToCleanup != null) {
                for (UUID packingListUuid : packingListUuidsToCleanup) {
                    packingLists.get(packingListUuid).itemInstances.remove(itemUuid);
                    packingListUuidToItemUuidsMap.get(packingListUuid).remove(itemUuid);
                }
            }
        }

        void removeTripParameterUuid(UUID tripParameterUuid) {
            HashSet<UUID> itemUuidsToRemove = tripParameterUuidToItemUuidsMap.remove(tripParameterUuid);
            if (itemUuidsToRemove != null) {
                for (UUID itemUuid : itemUuidsToRemove) {
                    // TODO remove item if no other trip parameters?
                    itemUuidToTripParameterUuidsMap.get(itemUuid).remove(tripParameterUuid);
                }
            }

            HashSet<UUID> packingListUuidsToCleanup = tripParameterUuidToPackingListUuidsMap.remove(tripParameterUuid);
            if (packingListUuidsToCleanup != null) {
                for (UUID packingListUuid : packingListUuidsToCleanup) {
                    // TODO remove items only associated with trip parameter from list
                    packingListUuidToTripParameterUuidsMap.get(packingListUuid).remove(tripParameterUuid);
                }
            }
        }

        void removePackingListUuid(UUID packingListUuid) {
            HashSet<UUID> tripParameterUuidsToCleanup = packingListUuidToTripParameterUuidsMap.remove(packingListUuid);
            if (tripParameterUuidsToCleanup != null) {
                for (UUID tripParameterUuid : tripParameterUuidsToCleanup) {
                    tripParameterUuidToPackingListUuidsMap.get(tripParameterUuid).remove(packingListUuid);
                }
            }

            HashSet<UUID> itemUuidsToRemove = packingListUuidToItemUuidsMap.remove(packingListUuid);
            if (itemUuidsToRemove != null) {
                for (UUID itemUuid : itemUuidsToRemove) {
                    itemUuidToPackingListUuidsMap.get(itemUuid).remove(packingListUuid);
                    // TODO remove item if it does not have trip parameters?
                }
            }
        }
    }
}
