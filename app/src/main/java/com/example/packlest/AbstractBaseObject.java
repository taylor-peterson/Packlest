package com.example.packlest;

import java.util.UUID;

abstract class AbstractBaseObject {
    UUID uuid;
    String name;

    AbstractBaseObject() {
        uuid = UUID.randomUUID();
        name = "";
    }

    AbstractBaseObject(AbstractBaseObject abstractBaseObject) {
        uuid = abstractBaseObject.uuid;
        name = abstractBaseObject.name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}

