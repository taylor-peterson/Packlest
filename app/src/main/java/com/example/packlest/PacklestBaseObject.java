package com.example.packlest;

import java.util.UUID;

abstract class PacklestBaseObject {
    UUID uuid;
    String name;

    PacklestBaseObject() {
        uuid = UUID.randomUUID();
        name = "";
    }

    PacklestBaseObject(PacklestBaseObject packlestBaseObject) {
        uuid = packlestBaseObject.uuid;
        name = packlestBaseObject.name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}

