package com.example.packlest;

import android.widget.Filter;

public class ItemFilter extends Filter {
    PackingList filterlist;
    ListViewItemCheckboxAdapter adapter;

    public ItemFilter(PackingList filterList, ListViewItemCheckboxAdapter adapter) {
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
            for (Item item : filterlist.items) {
                if (constraint == PackingListActivity.FILTER_STATE.ADDED_ONLY.name() && item.checkbox_state != CHECKBOX_STATE.UNADDED) {
                    filteredPackingList.items.add(item);
                } else if (constraint == PackingListActivity.FILTER_STATE.UNCHECKED_ONLY.name() && item.checkbox_state == CHECKBOX_STATE.UNCHECKED) {
                    filteredPackingList.items.add(item);
                } else if (constraint == PackingListActivity.FILTER_STATE.NONE.name()) {
                    filteredPackingList.items.add(item);
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
        adapter.packingList = (PackingList) results.values;
        adapter.notifyDataSetChanged();
    }
}
