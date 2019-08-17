package com.example.packlest;

import androidx.annotation.NonNull;

import java.util.UUID;

abstract class AbstractBaseObject implements Comparable<AbstractBaseObject> {
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

    @NonNull
    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public int compareTo(@NonNull AbstractBaseObject abstractBaseObject) {
        return this.name.compareTo(abstractBaseObject.name);
    }
}

