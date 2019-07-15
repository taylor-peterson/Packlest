package com.example.packlest;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.UUID;

public class PackingList implements Parcelable {
    UUID uuid;
    public String name;
    ArrayList<ItemInstance> itemInstances;

    PackingList() {
        uuid = UUID.randomUUID();
        name = "";
        itemInstances = new ArrayList<>();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeSerializable(this.uuid);
        out.writeString(this.name);
        out.writeList(this.itemInstances);
    }

    public static final Parcelable.Creator<PackingList> CREATOR = new Parcelable.Creator<PackingList>() {
        public PackingList createFromParcel(Parcel in) {
            return new PackingList(in);
        }

        public PackingList[] newArray(int size) {
            return new PackingList[size];
        }
    };

    private PackingList(Parcel in) {
        uuid = (UUID)in.readSerializable();
        name = in.readString();
        itemInstances = new ArrayList<>();
        in.readList(itemInstances, Item.class.getClassLoader());
    }

    @Override
    public String toString() {
        return this.name;
    }
}
