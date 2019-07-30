package com.example.packlest;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;

import com.google.android.material.snackbar.Snackbar;

class ListViewItemCheckboxAdapter extends BaseAdapter implements Filterable {
    private static final String TAG = "PackLestListViewItemCheckboxAdapter";
    PackingList packingList;
    private final Context context;
    private ItemFilter filter;
    FILTER_STATE filter_state = FILTER_STATE.NONE;

    ListViewItemCheckboxAdapter(Context context, PackingList packingList) {
        this.context = context;
        this.packingList = packingList;
    }

    @Override
    public int getCount() {
        return packingList.itemInstances.size();
    }

    @Override
    public ItemInstance getItem(int itemIndex) {
        return packingList.itemInstances.get(itemIndex);
    }

    @Override
    public long getItemId(int itemIndex) {
        return itemIndex;
    }

    @Override
    public View getView(int itemIndex, View convertView, ViewGroup viewGroup) {
        ListViewItemViewHolder viewHolder;

        if (convertView != null) {
            viewHolder = (ListViewItemViewHolder) convertView.getTag();
        } else {
            convertView = View.inflate(context, R.layout.packing_list_item, null);

            CheckBoxTriState listItemCheckbox = convertView.findViewById(R.id.list_view_item_checkbox);
            listItemCheckbox.setOnClickListener(listItemCheckboxListener);

            viewHolder = new ListViewItemViewHolder(convertView);
            viewHolder.setItemCheckbox(listItemCheckbox);
            viewHolder.setItemTextView(convertView.findViewById(R.id.list_view_item_text));

            convertView.setTag(viewHolder);
        }

        ItemInstance itemInstance = packingList.itemInstances.get(itemIndex);
        viewHolder.getItemTextView().setText(itemInstance.getName());
        viewHolder.getItemCheckbox().setState(itemInstance.checkboxState);

        return convertView;
    }

    private final View.OnClickListener listItemCheckboxListener = view -> {
        Log.v(TAG, "Item clicked");
        View parentRow = (View) view.getParent().getParent();
        ListView listView = (ListView) parentRow.getParent();
        final int position = listView.getPositionForView(parentRow);

        ItemInstance itemInstance = getItem(position);
        CheckBoxTriState itemCheckbox = view.findViewById(R.id.list_view_item_checkbox);
        itemInstance.checkboxState = itemCheckbox.getState();
        PacklestApplication.getInstance().packlestData.updateItemInPackingList(packingList.uuid, itemInstance);

        getFilter().filter(filter_state.name());

        if ((filter_state == FILTER_STATE.ADDED_ONLY && itemInstance.checkboxState == CHECKBOX_STATE.UNADDED)
                || (filter_state == FILTER_STATE.UNCHECKED_ONLY && itemInstance.checkboxState == CHECKBOX_STATE.CHECKED)) {
            Snackbar snackbar = Snackbar
                    .make(listView, "Filtered item no longer visible...", Snackbar.LENGTH_SHORT)
                    .setAction("UNDO", nested_lambda_view -> {
                        // At this point, itemCheckbox will point to a different ItemInstance and filtering will take
                        // care of updating the views, so only correct the ItemInstance itself.
                        itemInstance.checkboxState = CheckBoxTriState.reverseCycleButtonState(itemInstance.checkboxState);
                        PacklestApplication.getInstance().packlestData.updateItemInPackingList(packingList.uuid, itemInstance);
                        getFilter().filter(filter_state.name());
                    });
            snackbar.show();
        }
        PacklestApplication.getInstance().persistData();
    };

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new ItemFilter(packingList, this);
        }
        return filter;
    }
}