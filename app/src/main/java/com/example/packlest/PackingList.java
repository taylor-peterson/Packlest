package com.example.packlest;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class PackingList implements Parcelable {
    public String name;
    public ArrayList<String> items;

    public PackingList() {}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeSerializable(items);
    }

    public static final Parcelable.Creator<PackingList> CREATOR = new Parcelable.Creator<PackingList>() {
        public PackingList createFromParcel(Parcel in) {
            return new PackingList(in);
        }

        public PackingList[] newArray(int size) {
            return new PackingList[][size];
        }
    };

    private PackingList(Parcel in) {
        name = in.readString();
        items = (ArrayList<String>)in.readSerializable();
    }
}
