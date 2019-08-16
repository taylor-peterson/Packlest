package com.example.packlest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

// Derived from https://stackoverflow.com/questions/31498785/data-structure-to-represent-many-to-many-relationship
class PacklestDataRelationships {
    private final Map<UUID, HashSet<UUID>> itemUuidToTripParameterUuidsMap = new HashMap<>();
    private final Map<UUID, HashSet<UUID>> itemUuidToPackingListUuidsMap = new HashMap<>();
    private final Map<UUID, UUID> itemUuidToItemCategoryUuidMap = new HashMap<>();
    private final Map<UUID, HashSet<UUID>> itemCategoryUuidToItemUuidsMap = new HashMap<>();
    private final Map<UUID, HashSet<UUID>> tripParameterUuidToItemUuidsMap = new HashMap<>();
    private final Map<UUID, HashSet<UUID>> tripParameterUuidToPackingListUuidsMap = new HashMap<>();
    private final Map<UUID, HashSet<UUID>> packingListUuidToItemUuidsMap = new HashMap<>();
    private final Map<UUID, HashSet<UUID>> packingListUuidToTripParameterUuidsMap = new HashMap<>();
    private final UUID defaultItemCategoryUuid;

    PacklestDataRelationships(UUID itemCategoryUuid) {
        defaultItemCategoryUuid = itemCategoryUuid;
    }

    // Returns the set of PackingLists that need to contain this Item.
    HashSet<UUID> putItem(UUID itemUuid, HashSet<UUID> tripParameterUuids, UUID itemCategoryUuid) {
        relateItemToItemCategory(itemUuid, itemCategoryUuid);

        removeItemToTripParameterAssociations(itemUuid); // The old parameter set might differ from the new one.
        HashSet<UUID> packingListsToUpdate = new HashSet<>();
        for (UUID tripParameterUuid : tripParameterUuids) {
            packingListsToUpdate.addAll(relateItemToTripParameter(itemUuid, tripParameterUuid));
        }
        return packingListsToUpdate;
    }

    // Returns the set of PackingLists associated with this TripParameter that need to contain this Item.
    private HashSet<UUID> relateItemToTripParameter(UUID itemUuid, UUID tripParameterUuid) {
        if (!itemUuidToTripParameterUuidsMap.containsKey(itemUuid)) {
            itemUuidToTripParameterUuidsMap.put(itemUuid, new HashSet<>());
        }
        itemUuidToTripParameterUuidsMap.get(itemUuid).add(tripParameterUuid);

        if (!tripParameterUuidToItemUuidsMap.containsKey(tripParameterUuid)) {
            tripParameterUuidToItemUuidsMap.put(tripParameterUuid, new HashSet<>());
        }
        tripParameterUuidToItemUuidsMap.get(tripParameterUuid).add(itemUuid);

        return getPackingListUuidsForTripParameterUuid(tripParameterUuid);
    }

    private void relateItemToItemCategory(UUID itemUuid, UUID itemCategoryUuid) {
        itemUuidToItemCategoryUuidMap.put(itemUuid, itemCategoryUuid);

        if (!itemCategoryUuidToItemUuidsMap.containsKey(itemCategoryUuid)) {
            itemCategoryUuidToItemUuidsMap.put(itemCategoryUuid, new HashSet<>());
        }
        itemCategoryUuidToItemUuidsMap.get(itemCategoryUuid).add(itemUuid);
    }

    void relateItemToPackingList(UUID itemUuid, UUID packingListUuid) {
        if (!itemUuidToPackingListUuidsMap.containsKey(itemUuid)) {
            itemUuidToPackingListUuidsMap.put(itemUuid, new HashSet<>());
        }
        itemUuidToPackingListUuidsMap.get(itemUuid).add(packingListUuid);

        if (!packingListUuidToItemUuidsMap.containsKey(packingListUuid)) {
            packingListUuidToItemUuidsMap.put(packingListUuid, new HashSet<>());
        }
        packingListUuidToItemUuidsMap.get(packingListUuid).add(itemUuid);
    }

    void putPackingList(UUID packingListUuid, HashSet<UUID> tripParameterUuids, HashSet<UUID> itemUuidsFromTripParameters) {
        removePackingListUuid(packingListUuid); // The old parameter set might differ from the new one.
        for (UUID tripParameterUuid : tripParameterUuids) {
            relatePackingListToTripParameter(packingListUuid, tripParameterUuid);
        }
        for (UUID itemUuid : itemUuidsFromTripParameters) {
            relateItemToPackingList(itemUuid, packingListUuid);
        }
    }
    private void relatePackingListToTripParameter(UUID packingListUuid, UUID tripParameterUuid) {
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

    private HashSet<UUID> getPackingListUuidsForTripParameterUuid(UUID tripParameterUuid) {
        HashSet<UUID> packingListUuidsForTripParameterUuid = tripParameterUuidToPackingListUuidsMap.get(tripParameterUuid);
        if (packingListUuidsForTripParameterUuid != null) {
            return packingListUuidsForTripParameterUuid;
        }
        return new HashSet<>();
    }

    UUID getItemCategoryUuidForItemUuid(UUID itemUuid) {
        if (itemUuidToItemCategoryUuidMap.containsKey(itemUuid)) {
            return itemUuidToItemCategoryUuidMap.get(itemUuid);
        } else {
            relateItemToItemCategory(itemUuid, defaultItemCategoryUuid);
            return defaultItemCategoryUuid;
        }
    }

    // Returns the set of packing lists containing the deleted item.
    HashSet<UUID> removeItemUuid(UUID itemUuid) {
        removeItemToTripParameterAssociations(itemUuid);

        HashSet<UUID> packingListUuidsToCleanup = itemUuidToPackingListUuidsMap.remove(itemUuid);
        if (packingListUuidsToCleanup != null) {
            for (UUID packingListUuid : packingListUuidsToCleanup) {
                packingListUuidToItemUuidsMap.get(packingListUuid).remove(itemUuid);
            }
        }
        return packingListUuidsToCleanup;
    }

    private void removeItemToTripParameterAssociations(UUID itemUuid) {
        HashSet<UUID> tripParameterUuidsToCleanup = itemUuidToTripParameterUuidsMap.remove(itemUuid);
        if (tripParameterUuidsToCleanup != null) {
            for (UUID tripParameterUuid : tripParameterUuidsToCleanup) {
                tripParameterUuidToItemUuidsMap.get(tripParameterUuid).remove(itemUuid);
            }
        }
    }

    void removeItemCategoryUuid(UUID itemCategoryUuid) {
        HashSet<UUID> itemUuidsToRemove = itemCategoryUuidToItemUuidsMap.remove(itemCategoryUuid);
        if (itemUuidsToRemove != null) {
            for (UUID itemUuid : itemUuidsToRemove) {
                itemUuidToItemCategoryUuidMap.remove(itemUuid);
                relateItemToItemCategory(itemUuid, defaultItemCategoryUuid);
            }
        }
    }

    void removeTripParameterUuid(UUID tripParameterUuid) {
        HashSet<UUID> itemUuidsToRemove = tripParameterUuidToItemUuidsMap.remove(tripParameterUuid);
        if (itemUuidsToRemove != null) {
            for (UUID itemUuid : itemUuidsToRemove) {
                itemUuidToTripParameterUuidsMap.get(itemUuid).remove(tripParameterUuid);
            }
        }

        HashSet<UUID> packingListUuidsToCleanup = tripParameterUuidToPackingListUuidsMap.remove(tripParameterUuid);
        if (packingListUuidsToCleanup != null) {
            for (UUID packingListUuid : packingListUuidsToCleanup) {
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
            }
        }
    }
}
