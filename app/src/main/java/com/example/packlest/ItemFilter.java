package com.example.packlest;

import android.widget.Filter;

import java.util.Objects;
import java.util.UUID;

class ItemFilter extends Filter {
    private final UUID packingListUuid;
    private final PackingListAdapter adapter;

    ItemFilter(UUID packingListUuid, PackingListAdapter adapter) {
        this.packingListUuid = packingListUuid;
        this.adapter = adapter;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        if(constraint != null && constraint.length()>0)
        {
            constraint = constraint.toString();

            PackingList filteredPackingList = new PackingList();

            filteredPackingList.uuid = packingListUuid;
            for (ItemInstance itemInstance: Objects.requireNonNull(PacklestApplication.getInstance().packlestData.packingLists.get(packingListUuid)).itemInstances.values()) {
                if (constraint == FILTER_STATE.ADDED_ONLY.name() && itemInstance.checkboxState != CHECKBOX_STATE.UNADDED) {
                    filteredPackingList.itemInstances.put(itemInstance.itemUuid, itemInstance);
                } else if (constraint == FILTER_STATE.UNCHECKED_ONLY.name() && itemInstance.checkboxState == CHECKBOX_STATE.UNCHECKED) {
                    filteredPackingList.itemInstances.put(itemInstance.itemUuid, itemInstance);
                } else if (constraint == FILTER_STATE.NONE.name()) {
                    filteredPackingList.itemInstances.put(itemInstance.itemUuid, itemInstance);
                }
            }

            results.count=1;
            results.values=filteredPackingList;
        } else {
            results.count=1;
            results.values= packingListUuid;
        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.convertPackingListToListData((PackingList) results.values);
    }
}
