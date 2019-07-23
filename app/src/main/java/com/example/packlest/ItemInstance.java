package com.example.packlest;

import java.util.UUID;

// Items may be used in multiple packing lists.
// Each packing list might have the item in a different state (unadded/unchecked/checked).
// This class tracks that state and links it to the global data for the item.
class ItemInstance {
    final UUID uuid;
    final UUID item_uuid;
    CHECKBOX_STATE checkbox_state;

    ItemInstance(UUID item_uuid) {
        uuid = UUID.randomUUID();
        this.item_uuid = item_uuid;
        checkbox_state = CHECKBOX_STATE.UNADDED;
    }

    @Override
    public String toString() {
        return getName();
    }

    String getName() {
        return PacklestApplication.getInstance().packlestData.items.get(item_uuid).name;
    }
}
