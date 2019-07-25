package com.example.packlest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

class PackingList {
    UUID uuid;
    public String name;
    final ArrayList<ItemInstance> itemInstances;
    Set<UUID> tripParameterUuids;

    PackingList() {
        uuid = UUID.randomUUID();
        name = "";
        itemInstances = new ArrayList<>();
        tripParameterUuids = new HashSet<>();
    }

    PackingList(PackingList packingList) {
        uuid = packingList.uuid;
        name = packingList.name;
        itemInstances = new ArrayList<>(packingList.itemInstances);
        tripParameterUuids = new HashSet<>(packingList.tripParameterUuids);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
