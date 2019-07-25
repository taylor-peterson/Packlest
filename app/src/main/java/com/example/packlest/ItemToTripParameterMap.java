package com.example.packlest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

// Derived from https://stackoverflow.com/questions/31498785/data-structure-to-represent-many-to-many-relationship
class ItemToTripParameterMap {
    private final Map<UUID, HashSet<UUID>> itemUuidToTripParameterUuidsMap = new HashMap<>();
    private final Map<UUID, HashSet<UUID>> tripParameterUuidToItemUuidsMap = new HashMap<>();

    void put(UUID itemUuid, UUID tripParameterUuid) {
        if (!itemUuidToTripParameterUuidsMap.containsKey(itemUuid)) {
            itemUuidToTripParameterUuidsMap.put(itemUuid, new HashSet<>());
        }
        itemUuidToTripParameterUuidsMap.get(itemUuid).add(tripParameterUuid);

        if (!tripParameterUuidToItemUuidsMap.containsKey(tripParameterUuid)) {
            tripParameterUuidToItemUuidsMap.put(tripParameterUuid, new HashSet<>());
        }
        tripParameterUuidToItemUuidsMap.get(tripParameterUuid).add(itemUuid);
    }

    void put(UUID itemUuid, HashSet<UUID> tripParameterUuids) {
        removeItemUuid(itemUuid);
        for (UUID tripParameterUuid : tripParameterUuids) {
            put(itemUuid, tripParameterUuid);
        }
    }

    HashSet<UUID> getTripParameterUuidsForItemUuid(UUID itemUuid) {
        HashSet<UUID> tripParameterUuidsForItemUuid = itemUuidToTripParameterUuidsMap.get(itemUuid);
        if (tripParameterUuidsForItemUuid != null) {
            return tripParameterUuidsForItemUuid;
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
        HashSet<UUID> tripParameterUuidsToRemove = itemUuidToTripParameterUuidsMap.remove(itemUuid);
        if (tripParameterUuidsToRemove != null) {
            for (UUID tripParameterUuid : tripParameterUuidsToRemove) {
                tripParameterUuidToItemUuidsMap.get(tripParameterUuid).remove(itemUuid);
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
    }
}
