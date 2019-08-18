package com.example.packlest;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;

class PackingListAdapter extends BaseExpandableListAdapter implements Filterable {
    private final Context context;
    private ItemFilter filter;
    FILTER_STATE filter_state = FILTER_STATE.NONE;
    SortedMap<UUID, SortedSet<UUID>> listData;
    private UUID packingListUuid;

    // TODO persist group fold state
    PackingListAdapter(Context context, PackingList packingList) {
        this.context = context;
        listData = new TreeMap<>();
        packingListUuid = packingList.uuid;
        convertPackingListToListData(packingList);
    }

    void convertPackingListToListData(PackingList packingList) {
        for (ItemInstance itemInstance : packingList.itemInstances.values()) {
            UUID itemCategoryUUID = PacklestApplication.getInstance().packlestData.packlestDataRelationships.getItemCategoryUuidForItemUuid(itemInstance.itemUuid);
            if (!listData.containsKey(itemCategoryUUID)) {
                listData.put(itemCategoryUUID, new TreeSet<>());
            }
            listData.get(itemCategoryUUID).add(itemInstance.itemUuid);
        }
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listData.keySet().toArray()[groupPosition];
    }

    @Override
    public int getGroupCount() {
        return listData.keySet().size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isLastChild, View view, ViewGroup parent) {
        if (view == null) {
            view = View.inflate(context, R.layout.packing_list_category, null);
        }

        TextView textView = view.findViewById(R.id.list_view_category_name);
        textView.setText(PacklestApplication.getInstance().packlestData.itemCategories.get(getGroup(groupPosition)).name);

        return view;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listData.get(getGroup(groupPosition)).toArray()[childPosition];
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup parent) {
        ItemInstanceViewHolder viewHolder;

        if (view != null) {
            viewHolder = (ItemInstanceViewHolder) view.getTag();
        } else {
            view = View.inflate(context, R.layout.packing_list_item, null);

            CheckBoxTriState listItemCheckbox = view.findViewById(R.id.list_view_item_checkbox);
            listItemCheckbox.setOnClickListener(listItemCheckboxListener);

            viewHolder = new ItemInstanceViewHolder(view);
            viewHolder.setItemCheckbox(listItemCheckbox);
            viewHolder.setItemTextView(view.findViewById(R.id.list_view_item_text));

            view.setTag(viewHolder);
        }

        UUID itemUuid = (UUID) getChild(groupPosition, childPosition);
        PackingList packingList = PacklestApplication.getInstance().packlestData.packingLists.get(packingListUuid);
        ItemInstance itemInstance = packingList.itemInstances.get(itemUuid);
        viewHolder.getItemTextView().setText(itemInstance.getName());
        viewHolder.getItemCheckbox().setState(itemInstance.checkboxState);

        return view;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listData.get(getGroup(groupPosition)).size();
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private final View.OnClickListener listItemCheckboxListener = view -> {
        View parentRow = (View) view.getParent().getParent();
        ExpandableListView expandableListView = (ExpandableListView) parentRow.getParent();
        final int position = expandableListView.getPositionForView(parentRow);

        ItemInstance itemInstance = PacklestApplication.getInstance().packlestData.packingLists.get(packingListUuid).itemInstances.get(expandableListView.getItemAtPosition(position));
        CheckBoxTriState itemCheckbox = view.findViewById(R.id.list_view_item_checkbox);
        itemInstance.checkboxState = itemCheckbox.getState();
        PacklestApplication.getInstance().packlestData.updateItemInPackingList(packingListUuid, itemInstance);

        getFilter().filter(filter_state.name());

        if ((filter_state == FILTER_STATE.ADDED_ONLY && itemInstance.checkboxState == CHECKBOX_STATE.UNADDED)
                || (filter_state == FILTER_STATE.UNCHECKED_ONLY && itemInstance.checkboxState == CHECKBOX_STATE.CHECKED)) {
            Snackbar snackbar = Snackbar
                    .make(expandableListView, "Filtered item no longer visible...", Snackbar.LENGTH_LONG)
                    .setAction("UNDO", nested_lambda_view -> {
                        // At this point, itemCheckbox will point to a different ItemInstance and filtering will take
                        // care of updating the views, so only correct the ItemInstance itself.
                        itemInstance.checkboxState = CheckBoxTriState.reverseCycleButtonState(itemInstance.checkboxState);
                        PacklestApplication.getInstance().packlestData.updateItemInPackingList(packingListUuid, itemInstance);
                        getFilter().filter(filter_state.name());
                    });
            snackbar.show();
        }
        PacklestApplication.getInstance().persistData();
    };

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new ItemFilter(packingListUuid, this);
        }
        return filter;
    }
}