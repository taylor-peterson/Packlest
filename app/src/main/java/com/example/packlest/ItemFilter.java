package com.example.packlest;

import android.widget.Filter;

class ItemFilter extends Filter {
    private final PackingList filterlist;
    private final ListViewItemCheckboxAdapter adapter;

    ItemFilter(PackingList filterList, ListViewItemCheckboxAdapter adapter) {
        this.filterlist = filterList;
        this.adapter = adapter;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        if(constraint != null && constraint.length()>0)
        {
            constraint=constraint.toString();

            PackingList filteredPackingList = new PackingList();

            filteredPackingList.uuid = filterlist.uuid;
            filteredPackingList.name = filterlist.name;
            for (ItemInstance itemInstance: PacklestApplication.getInstance().packlestData.packingLists.get(filteredPackingList.uuid).itemInstances) {
                if (constraint == FILTER_STATE.ADDED_ONLY.name() && itemInstance.checkbox_state != CHECKBOX_STATE.UNADDED) {
                    filteredPackingList.itemInstances.add(itemInstance);
                } else if (constraint == FILTER_STATE.UNCHECKED_ONLY.name() && itemInstance.checkbox_state == CHECKBOX_STATE.UNCHECKED) {
                    filteredPackingList.itemInstances.add(itemInstance);
                } else if (constraint == FILTER_STATE.NONE.name()) {
                    filteredPackingList.itemInstances.add(itemInstance);
                }
            }

            results.count=1;
            results.values=filteredPackingList;
        } else {
            results.count=1;
            results.values=filterlist;
        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        PackingList filteredPackingList = (PackingList) results.values;
        adapter.packingList.itemInstances.clear();
        adapter.packingList.itemInstances.addAll(filteredPackingList.itemInstances);
        adapter.notifyDataSetChanged();
    }
}
