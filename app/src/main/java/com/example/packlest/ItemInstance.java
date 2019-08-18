package com.example.packlest;

import androidx.annotation.NonNull;

import java.util.Objects;
import java.util.UUID;

// Items may be used in multiple packing lists.
// Each packing list might have the item in a different state (unadded/unchecked/checked).
// This class tracks that state and links it to the global data for the item.
class ItemInstance {
    final UUID itemUuid;
    CHECKBOX_STATE checkboxState;

    ItemInstance(UUID item_uuid) {
        this.itemUuid = item_uuid;
        checkboxState = CHECKBOX_STATE.UNADDED;
    }

    @NonNull
    @Override
    public String toString() {
        return getName();
    }

    String getName() {
        return Objects.requireNonNull(PacklestApplication.getInstance().packlestData.items.get(itemUuid)).name;
    }
}
