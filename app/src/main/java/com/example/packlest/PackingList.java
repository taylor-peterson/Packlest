package com.example.packlest;

import java.util.ArrayList;

class PackingList extends AbstractBaseObject {
    @SuppressWarnings("CanBeFinal")
    ArrayList<ItemInstance> itemInstances;

    PackingList() {
        super();
        itemInstances = new ArrayList<>();
    }

    PackingList(PackingList packingList) {
        super(packingList);
        itemInstances = new ArrayList<>(packingList.itemInstances);
    }
}
