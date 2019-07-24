package com.example.packlest;

import java.util.ArrayList;
import java.util.UUID;

class PackingList {
    UUID uuid;
    public String name;
    final ArrayList<ItemInstance> itemInstances;
    ArrayList<UUID> tripParameterUuids;

    PackingList() {
        uuid = UUID.randomUUID();
        name = "";
        itemInstances = new ArrayList<>();
        tripParameterUuids = new ArrayList<>();
    }

    PackingList(PackingList packingList) {
        uuid = packingList.uuid;
        name = packingList.name;
        itemInstances = new ArrayList<>(packingList.itemInstances);
        tripParameterUuids = new ArrayList<>(packingList.tripParameterUuids);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
