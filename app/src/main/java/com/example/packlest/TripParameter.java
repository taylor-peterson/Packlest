package com.example.packlest;

import java.util.UUID;

class TripParameter {
    final UUID uuid;
    String name;

    TripParameter() {
        uuid = UUID.randomUUID();
        name = "";
    }

    @Override
    public String toString() {
        return name;
    }
}
