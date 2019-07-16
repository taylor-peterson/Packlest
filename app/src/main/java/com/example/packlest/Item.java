package com.example.packlest;

import java.util.ArrayList;
import java.util.UUID;

public class Item {
    UUID uuid;
    public String name;
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

    public String getTripParameterNames() {
        ArrayList<String> names = new ArrayList<>();

        if (tripParameters != null) {
            for (TripParameter tripParameter : tripParameters) {
                names.add(tripParameter.name);
            }
        }
        return String.join(",", names);
    }
}
