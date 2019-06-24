package com.example.packlest;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;

public class ListViewItemCheckboxAdapter extends BaseAdapter implements Filterable {
    PackingList packingList;
    private Context context;
    ItemFilter filter;
    PackingListActivity.FILTER_STATE filter_state = PackingListActivity.FILTER_STATE.NONE;

    public ListViewItemCheckboxAdapter(Context context, PackingList packingList) {
        this.context = context;
        this.packingList = packingList;
    }

    @Override
    public int getCount() {
        return packingList.items.size();
    }

    @Override
    public Item getItem(int itemIndex) {
        return packingList.items.get(itemIndex);
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

        Item item = packingList.items.get(itemIndex);
        CheckBoxTriState listItemCheckbox = convertView.findViewById(R.id.list_view_item_checkbox);
        listItemCheckbox.setState(item.checkbox_state);
        viewHolder.getItemTextView().setText(item.name);

        return convertView;
    }

    private View.OnClickListener listItemCheckboxListener = view -> {
        View parentRow = (View) view.getParent().getParent();
        ListView listView = (ListView) parentRow.getParent();
        final int position = listView.getPositionForView(parentRow);

        Item item = getItem(position);
        CheckBoxTriState itemCheckbox = view.findViewById(R.id.list_view_item_checkbox);
        item.checkbox_state = itemCheckbox.getState();
        PacklestApplication.getInstance().updateItem(packingList, item);

        getFilter().filter(filter_state.name());
    };

    public void updatePackingList(PackingList packingList) {
        this.packingList = packingList;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new ItemFilter(packingList, this);
        }
        return filter;
    }
}