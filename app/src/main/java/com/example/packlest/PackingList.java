package com.example.packlest;

import java.util.ArrayList;
import java.util.UUID;

class PackingList {
    UUID uuid;
    public String name;
    final ArrayList<ItemInstance> itemInstances;
    ArrayList<TripParameter> tripParameters;

    PackingList() {
        uuid = UUID.randomUUID();
        name = "";
        itemInstances = new ArrayList<>();
        tripParameters = new ArrayList<>();
    }

    PackingList(PackingList packingList) {
        uuid = packingList.uuid;
        name = packingList.name;
        itemInstances = new ArrayList<>(packingList.itemInstances);
        tripParameters = new ArrayList<>(packingList.tripParameters);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
