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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;

@SuppressWarnings("SuspiciousMethodCalls")
class PackingListAdapter extends BaseExpandableListAdapter implements Filterable {
    private final Context context;
    private ItemFilter filter;
    FILTER_STATE filter_state = FILTER_STATE.NONE;
    private final SortedMap<UUID, SortedSet<UUID>> listData;
    private final Map<UUID, Boolean> groupCollapseState;
    @SuppressWarnings("CanBeFinal")
    private UUID packingListUuid;

    PackingListAdapter(Context context, PackingList packingList) {
        this.context = context;
        listData = new TreeMap<>((first, second) -> Objects.requireNonNull(PacklestApplication.getInstance().packlestData.itemCategories.get(first)).name.compareTo(
                Objects.requireNonNull(PacklestApplication.getInstance().packlestData.itemCategories.get(second)).name));

        packingListUuid = packingList.uuid;
        convertPackingListToListData(packingList);

        groupCollapseState = new HashMap<>();
        for (UUID uuid : listData.keySet()) {
            groupCollapseState.put(uuid, false);
        }
    }

    void convertPackingListToListData(PackingList packingList) {
        listData.clear();
        for (ItemInstance itemInstance : packingList.itemInstances.values()) {
            UUID itemCategoryUuid = PacklestApplication.getInstance().packlestData.packlestDataRelationships.getItemCategoryUuidForItemUuid(itemInstance.itemUuid);
            if (!listData.containsKey(itemCategoryUuid)) {
                listData.put(itemCategoryUuid, new TreeSet<>((first, second) -> Objects.requireNonNull(PacklestApplication.getInstance().packlestData.items.get(first)).name.compareTo(
                        Objects.requireNonNull(PacklestApplication.getInstance().packlestData.items.get(second)).name)));
            }
            Objects.requireNonNull(listData.get(itemCategoryUuid)).add(itemInstance.itemUuid);
        }
        notifyDataSetChanged();
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
        groupCollapseState.put((UUID) getGroup(groupPosition), false);
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
        groupCollapseState.put((UUID) getGroup(groupPosition), true);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return Objects.requireNonNull(listData.keySet().toArray())[groupPosition];
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

        UUID itemCategoryUuid = (UUID) getGroup(groupPosition);

        if (!groupCollapseState.containsKey(itemCategoryUuid)) {
            // Item Categories are initialized in onCreate; if a new item is added that introduces
            // a new item category, we need to make sure it gets added to the groupCollapseState object.
            groupCollapseState.put(itemCategoryUuid, false);
        }

        //noinspection ConstantConditions
        if (groupCollapseState.get(itemCategoryUuid)) {
            ((ExpandableListView) parent).collapseGroup(groupPosition);
        } else {
            ((ExpandableListView) parent).expandGroup(groupPosition);
        }

        TextView textView = view.findViewById(R.id.list_view_category_name);
        //noinspection SuspiciousMethodCalls
        textView.setText(Objects.requireNonNull(PacklestApplication.getInstance().packlestData.itemCategories.get(getGroup(groupPosition))).name);

        return view;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return Objects.requireNonNull(Objects.requireNonNull(listData.get(getGroup(groupPosition))).toArray())[childPosition];
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
        ItemInstance itemInstance = Objects.requireNonNull(packingList).itemInstances.get(itemUuid);
        viewHolder.getItemTextView().setText(Objects.requireNonNull(itemInstance).getName());
        viewHolder.getItemCheckbox().setState(itemInstance.checkboxState);

        return view;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return Objects.requireNonNull(listData.get(getGroup(groupPosition))).size();
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private final View.OnClickListener listItemCheckboxListener = view -> {
        View parentRow = (View) view.getParent().getParent();
        ExpandableListView expandableListView = (ExpandableListView) parentRow.getParent();
        final int position = expandableListView.getPositionForView(parentRow);

        ItemInstance itemInstance = Objects.requireNonNull(PacklestApplication.getInstance().packlestData.packingLists.get(packingListUuid)).itemInstances.get(expandableListView.getItemAtPosition(position));
        CheckBoxTriState itemCheckbox = view.findViewById(R.id.list_view_item_checkbox);
        Objects.requireNonNull(itemInstance).checkboxState = itemCheckbox.getState();
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