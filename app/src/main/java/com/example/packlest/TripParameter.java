package com.example.packlest;

import java.util.UUID;

public class TripParameter {
    UUID uuid;
    String name;
    //Set<Item> items;

    TripParameter() {
        uuid = UUID.randomUUID();
        name = "";
        //items = new HashSet();
    }
}
