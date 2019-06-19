package com.example.packlest;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListViewItemCheckboxAdapter extends BaseAdapter {
    private PackingList packingList;
    private Context context;

    public ListViewItemCheckboxAdapter(Context context, PackingList packingList) {
        this.context = context;
        this.packingList = packingList;
    }

    @Override
    public int getCount() {
        int ret = 0;
        if (packingList != null) {
            ret = packingList.items.size();
        }
        return ret;
    }

    @Override
    public Item getItem(int itemIndex) {
        Item item = null;
        if (packingList != null) {
            item = packingList.items.get(itemIndex);
        }
        return item;
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

            TextView listItemText = convertView.findViewById(R.id.list_view_item_text);

            viewHolder = new ListViewItemViewHolder(convertView);

            viewHolder.setItemCheckbox(listItemCheckbox);

            viewHolder.setItemTextView(listItemText);

            convertView.setTag(viewHolder);
        }

        Item item = packingList.items.get(itemIndex);
        CheckBoxTriState listItemCheckbox = convertView.findViewById(R.id.list_view_item_checkbox);
        listItemCheckbox.setState(item.checkbox_state);
        viewHolder.getItemTextView().setText(item.name);

        return convertView;
    }
}
