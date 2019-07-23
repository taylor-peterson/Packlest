package com.example.packlest;

import java.util.ArrayList;
import java.util.UUID;

class Item {
    final UUID uuid;
    String name;
    ArrayList<TripParameter> tripParameters;

    Item() {
        uuid = UUID.randomUUID();
        name = "";
        tripParameters = new ArrayList<>();
    }

    @Override
    public String toString() {
        return this.name;
    }

    String getTripParameterNames() {
        ArrayList<String> names = new ArrayList<>();

        if (tripParameters != null) {
            for (TripParameter tripParameter : tripParameters) {
                names.add(tripParameter.name);
            }
        }
        return String.join(",", names);
    }
}
