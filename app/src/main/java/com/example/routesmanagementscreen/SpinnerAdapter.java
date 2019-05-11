package com.example.routesmanagementscreen;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class SpinnerAdapter extends ArrayAdapter
{
    private Context context; //context
    private ArrayList<SpinnerItem> items; //data source of the array adapter

    //public constructor
    public SpinnerAdapter(Context i_context)
    {
        super(i_context, 0);
        context = i_context;
        items = new ArrayList<>();
    }

    public ArrayList<SpinnerItem> getItems() { return items; }

    public void add(SpinnerItem i_itemToAdd)
    {
        items.add(i_itemToAdd);
        addAll(getItems());
        notifyDataSetChanged();
    }

    @Override
    public int getCount()
    {
        return items.size(); //returns total of items in the list
    }

    public String getItem(int position)
    {
        SpinnerItem currentItem = new SpinnerItem(null);
        String returnedValue = "";
        if (items.size() != 0)
        {
            currentItem = items.get(position); //returns list item at the specified position
            returnedValue = currentItem.getRouteName();
        }

        return returnedValue;
    }

//    @Override
//    public long getItemId(int position) {  return position; }
//
//    @Override
//    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
//    {
//        return initView(position, convertView, parent);
//    }
//
//    @Override
//    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
//    {
//        return initView(position, convertView, parent);
//    }
//
//    private View initView(int position, View convertView, ViewGroup parent)
//    {
//        ViewHolder viewHolder;
//
//        if (convertView == null) {
//            convertView = LayoutInflater.from(context).inflate(R.layout.layout_spinner_row_item, parent, false);
//            viewHolder = new ViewHolder(convertView);
//            convertView.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }
//
//        SpinnerItem currentItem = (SpinnerItem) getItem(position);
//        viewHolder.itemName.setText(currentItem.getRouteName());
//
//        return convertView;
//    }
//
//    private class ViewHolder
//    {
//        TextView itemName;
//
//        public ViewHolder(View view)
//        {
//            itemName = (TextView)view.findViewById(R.id.routeName);
//        }
//    }
}
