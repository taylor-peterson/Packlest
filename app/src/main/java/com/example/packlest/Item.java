package com.example.packlest;

import java.util.UUID;

class Item {
    final UUID uuid;
    String name;

    Item() {
        uuid = UUID.randomUUID();
        name = "";
    }

    @Override
    public String toString() {
        return this.name;
    }
}
