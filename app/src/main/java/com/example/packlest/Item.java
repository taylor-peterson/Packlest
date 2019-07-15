package com.example.packlest;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

public class Item implements Parcelable {
    UUID uuid;
    public String name;

    Item() {
        uuid = UUID.randomUUID();
        name = "";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeSerializable(this.uuid);
        out.writeString(this.name);
    }

    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    private Item(Parcel in) {
        uuid = (UUID)in.readSerializable();
        name = in.readString();
    }

    @Override
    public String toString() {
        return this.name;
    }
}
