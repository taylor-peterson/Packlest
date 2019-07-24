package com.example.packlest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

// Derived from https://stackoverflow.com/questions/31498785/data-structure-to-represent-many-to-many-relationship
class ItemToTripParameterMap {
    private final Map<UUID, Set<UUID>> itemUuidToTripParameterUuidsMap = new HashMap<>();
    private final Map<UUID, Set<UUID>> tripParameterUuidToItemUuidsMap = new HashMap<>();

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

    void put(UUID itemUuid, ArrayList<UUID> tripParameterUuids) {
        removeItemUuid(itemUuid);
        for (UUID tripParameterUuid : tripParameterUuids) {
            put(itemUuid, tripParameterUuid);
        }
    }

    Set<UUID> getTripParameterUuidsForItemUuid(UUID itemUuid) {
        return itemUuidToTripParameterUuidsMap.get(itemUuid);
    }

    Set<UUID> getItemUuidsForTripParameterUuid(UUID tripParameterUuid) {
        return tripParameterUuidToItemUuidsMap.get(tripParameterUuid);
    }

    void removeItemUuid(UUID itemUuid) {
        Set<UUID> tripParameterUuidsToRemove = itemUuidToTripParameterUuidsMap.remove(itemUuid);
        if (tripParameterUuidsToRemove != null) {
            for (UUID tripParameterUuid : tripParameterUuidsToRemove) {
                tripParameterUuidToItemUuidsMap.get(tripParameterUuid).remove(itemUuid);
            }
        }
    }

    Set<UUID> removeTripParameterUuid(UUID tripParameterUuid) {
        Set<UUID> itemUuidsToRemove = tripParameterUuidToItemUuidsMap.remove(tripParameterUuid);
        if (itemUuidsToRemove != null) {
            for (UUID itemUuid : itemUuidsToRemove) {
                itemUuidToTripParameterUuidsMap.get(itemUuid).remove(tripParameterUuid);
            }
        }
        return itemUuidsToRemove;
    }
}
