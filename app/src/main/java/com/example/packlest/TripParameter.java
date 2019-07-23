package com.example.packlest;

import java.util.UUID;

class TripParameter {
    final UUID uuid;
    String name;
    //Set<Item> items;

    TripParameter() {
        uuid = UUID.randomUUID();
        name = "";
        //items = new HashSet();
    }

    @Override
    public String toString() {
        return name;
    }
}
