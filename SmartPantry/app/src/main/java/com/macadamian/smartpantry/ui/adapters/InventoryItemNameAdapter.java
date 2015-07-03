package com.macadamian.smartpantry.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.google.common.collect.Lists;

import java.util.ArrayList;

public class InventoryItemNameAdapter extends BaseAdapter implements ListAdapter, Filterable {

    private ArrayList<String> mData = Lists.newArrayList();
    private ArrayList<String> mFilteredData = Lists.newArrayList();

    public InventoryItemNameAdapter() {
        super();
    }

    @Override
    public int getCount() {
        return mFilteredData.size();
    }

    @Override
    public Object getItem(int position) {
        return mFilteredData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = new ViewHolder();
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.activity_list_item, parent, false);
            vh.name = (TextView)convertView.findViewById(android.R.id.text1);
            convertView.setTag(vh);
        }
        else {
            vh = (ViewHolder)convertView.getTag();
        }
        vh.name.setText(mFilteredData.get(position));

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults results = new FilterResults();
                if (constraint != null) {
                    results.values = getFilteredList(constraint.toString());
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.values == null)
                    return;
                mFilteredData = (ArrayList<String>) results.values;
                notifyDataSetChanged();
            }



            private ArrayList<String> getFilteredList(final String filter) {
                final ArrayList<String> filteredList = Lists.newArrayList();
                for(final String item : mData) {
                    if (item.toUpperCase().startsWith(filter.toUpperCase())) {
                        filteredList.add(item);
                    }
                }
                return filteredList;
            }
        };
    }

    class ViewHolder {
        public TextView name;
    }

    public void setData(final ArrayList<String> data) {
        mData = data;
    }
}
