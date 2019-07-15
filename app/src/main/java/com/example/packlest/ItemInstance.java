package com.example.packlest;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

// Items may be used in multiple packing lists.
// Each packing list might have the item in a different state (unadded/unchecked/checked).
// This class tracks that state and links it to the global data for the item.
public class ItemInstance implements Parcelable{
    UUID uuid;
    UUID item_uuid;
    CHECKBOX_STATE checkbox_state;

    ItemInstance(UUID item_uuid) {
        uuid = UUID.randomUUID();
        this.item_uuid = item_uuid;
        checkbox_state = CHECKBOX_STATE.UNADDED;
    }

    public String getName() {
        return PacklestApplication.getInstance().packlestData.items.get(item_uuid).name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeSerializable(this.uuid);
        out.writeSerializable(this.item_uuid);
        out.writeInt(this.checkbox_state.ordinal());
    }

    public static final Parcelable.Creator<ItemInstance> CREATOR = new Parcelable.Creator<ItemInstance>() {
        public ItemInstance createFromParcel(Parcel in) {
            return new ItemInstance(in);
        }

        public ItemInstance[] newArray(int size) {
            return new ItemInstance[size];
        }
    };

    private ItemInstance(Parcel in) {
        uuid = (UUID)in.readSerializable();
        item_uuid = (UUID) in.readSerializable();
        checkbox_state = CHECKBOX_STATE.values()[in.readInt()];
    }
}
