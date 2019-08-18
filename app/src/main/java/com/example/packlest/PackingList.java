package com.example.packlest;


import java.util.HashMap;
import java.util.UUID;

class PackingList extends AbstractBaseObject {
    @SuppressWarnings("CanBeFinal")
    HashMap<UUID, ItemInstance> itemInstances;

    PackingList() {
        super();
        itemInstances = new HashMap<>();
    }

    PackingList(PackingList packingList) {
        super(packingList);
        itemInstances = new HashMap<>(packingList.itemInstances);
    }
}
