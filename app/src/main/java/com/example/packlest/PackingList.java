package com.example.packlest;

import java.util.ArrayList;
import java.util.UUID;

public class PackingList {
    UUID uuid;
    public String name;
    ArrayList<ItemInstance> itemInstances;

    PackingList() {
        uuid = UUID.randomUUID();
        name = "";
        itemInstances = new ArrayList<>();
    }

    PackingList(PackingList packingList) {
        uuid = packingList.uuid;
        name = packingList.name;
        itemInstances = new ArrayList<>(packingList.itemInstances);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
