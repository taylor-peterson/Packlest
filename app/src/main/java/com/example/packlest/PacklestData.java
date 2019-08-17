package com.example.packlest;

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
import java.util.Objects;
import java.util.UUID;

class PacklestData {
    private static final String OBJECT_DELIMITER = "OBJECT_DELIMITER";
    Map<UUID, PackingList> packingLists;
    Map<UUID, Item> items;
    Map<UUID, ItemCategory> itemCategories;
    Map<UUID, TripParameter> tripParameters;
    PacklestDataRelationships packlestDataRelationships;

    PacklestData() {
        packingLists = new HashMap<>();
        items = new HashMap<>();
        itemCategories = new HashMap<>();
        tripParameters = new HashMap<>();

        ItemCategory defaultItemCategory = new ItemCategory();
        defaultItemCategory.name = "Uncategorized";
        itemCategories.put(defaultItemCategory.uuid, defaultItemCategory);

        packlestDataRelationships = new PacklestDataRelationships(defaultItemCategory.uuid);
    }

    void loadPacklestDataFromFile(File file) {
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

        if (inputJson.length == 5) {
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

            String inputItemCategories = inputJson[2];
            if (!inputItemCategories.isEmpty()) {
                Type type = new TypeToken<HashMap<UUID, ItemCategory>>() {}.getType();
                itemCategories = gson.fromJson(inputItemCategories, type);
            }

            String inputTripParameters = inputJson[3];
            if (!inputTripParameters.isEmpty()) {
                Type type = new TypeToken<HashMap<UUID, TripParameter>>() {}.getType();
                tripParameters = gson.fromJson(inputTripParameters, type);
            }

            String inputDataRelationships = inputJson[4];
            if (!inputDataRelationships.isEmpty()) {
                Type type = new TypeToken<PacklestDataRelationships>() {}.getType();
                packlestDataRelationships = gson.fromJson(inputDataRelationships, type);
            }
        }
    }

    void persistPacklestDataToFile(FileOutputStream outputStream) {
        Gson gson = new Gson();
        String fileContents =
                gson.toJson(packingLists) + OBJECT_DELIMITER+
                gson.toJson(items) + OBJECT_DELIMITER +
                gson.toJson(itemCategories) + OBJECT_DELIMITER +
                gson.toJson(tripParameters) + OBJECT_DELIMITER +
                gson.toJson(packlestDataRelationships);
        try {
            outputStream.write(fileContents.getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    boolean doesNameExist(String name, Collection<? extends AbstractBaseObject> items) {
        for (AbstractBaseObject abstractBaseObject : items) {
            if (name.equals(abstractBaseObject.name)) {
                return true;
            }
        }
        return false;
    }

    void unaddAllItemsInPackingList(UUID packingListUuid) {
        for (ItemInstance itemInstance : Objects.requireNonNull(packingLists.get(packingListUuid)).itemInstances) {
            itemInstance.checkboxState = CHECKBOX_STATE.UNADDED;
        }
    }

    void uncheckAllCheckedItemsInPackingList(UUID packingListUuid) {
        for (ItemInstance itemInstance : Objects.requireNonNull(packingLists.get(packingListUuid)).itemInstances) {
            if (itemInstance.checkboxState == CHECKBOX_STATE.CHECKED) {
                itemInstance.checkboxState = CHECKBOX_STATE.UNCHECKED;
            }
        }
    }

    void addOrUpdateItem(Item item, HashSet<UUID> tripParametersInUse, ItemCategory itemCategory) {
        items.put(item.uuid, item);
        HashSet<UUID> packingListUuids = packlestDataRelationships.putItem(item.uuid, tripParametersInUse, itemCategory.uuid);

        for (UUID packingListUuid : packingListUuids) {
            addItemToPackingList(item.uuid, packingListUuid);
        }
    }
    void deleteItem(UUID itemUuid) {
        items.remove(itemUuid);

        HashSet<UUID> packingListUuidsToCleanup = packlestDataRelationships.removeItemUuid(itemUuid);
        if (packingListUuidsToCleanup != null) {
            for (UUID packingListUuid : packingListUuidsToCleanup) {
                removeItemFromPackingList(itemUuid, packingListUuid);
            }
        }
    }

    void addOrUpdateItemCategory(ItemCategory itemCategory) {
        itemCategories.put(itemCategory.uuid, itemCategory);
    }
    void deleteItemCategory(UUID itemCategoryUuid) {
        itemCategories.remove(itemCategoryUuid);
        packlestDataRelationships.removeItemCategoryUuid(itemCategoryUuid);
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

        HashSet<UUID> itemsFromTripParameters = new HashSet<>();
        for (UUID tripParameterUuid : tripParametersInUse) {
            itemsFromTripParameters.addAll(packlestDataRelationships.getItemUuidsForTripParameterUuid(tripParameterUuid));
        }

        // We use ArrayLists for the item instances to allow for ordering.
        // This means that we have to double check to make sure we don't introduce duplicates.
        for (UUID itemUuid : itemsFromTripParameters) {
            boolean alreadyPresentInPackingList = false;
            for (ItemInstance itemInstance : packingList.itemInstances) {
                if (itemInstance.itemUuid.equals(itemUuid)) {
                    alreadyPresentInPackingList = true;
                }
            }
            if (!alreadyPresentInPackingList) {
                ItemInstance itemInstance = new ItemInstance(itemUuid);
                packingList.itemInstances.add(itemInstance);
            }
        }

        packlestDataRelationships.putPackingList(packingList.uuid, tripParametersInUse, itemsFromTripParameters);
    }
    void deletePackingList(UUID packingListUuid) {
        packingLists.remove(packingListUuid);
        packlestDataRelationships.removePackingListUuid(packingListUuid);
    }
    void addItemToPackingList(UUID itemUuid, UUID packingListUuid) {
        // Create and add a new ItemInstance if one does not already exist.
        boolean newItem = true;
        for (int i = 0; i < Objects.requireNonNull(packingLists.get(packingListUuid)).itemInstances.size(); i++) {
            if (Objects.requireNonNull(packingLists.get(packingListUuid)).itemInstances.get(i).itemUuid.equals(itemUuid)) {
                newItem = false;
            }
        }
        if (newItem) {
            Objects.requireNonNull(packingLists.get(packingListUuid)).itemInstances.add(new ItemInstance(itemUuid));
        }

        packlestDataRelationships.relateItemToPackingList(itemUuid, packingListUuid);
    }
    private void removeItemFromPackingList(UUID itemUuid, UUID packingListUuid) {
        for (int i = 0; i < Objects.requireNonNull(packingLists.get(packingListUuid)).itemInstances.size(); i++) {
            if (Objects.requireNonNull(packingLists.get(packingListUuid)).itemInstances.get(i).itemUuid.equals(itemUuid)) {
                //noinspection SuspiciousListRemoveInLoop
                Objects.requireNonNull(packingLists.get(packingListUuid)).itemInstances.remove(i);
            }
        }
    }
    void updateItemInPackingList(UUID packingListUuid, ItemInstance modifiedItem) {
        // This is used to update the checkbox state of an existing item.
        for (int i = 0; i < Objects.requireNonNull(packingLists.get(packingListUuid)).itemInstances.size(); i++) {
            if (Objects.requireNonNull(packingLists.get(packingListUuid)).itemInstances.get(i).uuid.equals(modifiedItem.uuid)) {
                Objects.requireNonNull(packingLists.get(packingListUuid)).itemInstances.set(i, modifiedItem);
            }
        }
    }
}
