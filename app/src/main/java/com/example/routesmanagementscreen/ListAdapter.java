package com.example.routesmanagementscreen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ListAdapter extends ArrayAdapter<ListItem>
{
    private Context context; //context
    private ArrayList<ListItem> items; //data source of the list adapter

    //public constructor
    public ListAdapter(Context i_context)
    {
        super(i_context, 0);
        context = i_context;
        items = new ArrayList<>();
    }

   public ArrayList<ListItem> getItems() { return items; }

    @Override
    public int getCount() {
        return items.size(); //returns total of items in the list
    }

    @Override
    public ListItem getItem(int position)
    {
        return items.get(position); //returns list item at the specified position
    }

    public void updateItemInDataSource(String i_newRouteName, int i_indexOfItemToUpdate)
    {
        items.get(i_indexOfItemToUpdate).getRoute().setRouteName(i_newRouteName);
        addAll(getItems());
        notifyDataSetChanged();
    }

    public void add(ListItem i_itemToAdd)
    {
        items.add(i_itemToAdd);
        addAll(getItems());
        notifyDataSetChanged();
    }

    public void remove(int i_indexOfItemToRemove)
    {
        items.remove(i_indexOfItemToRemove);
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_list_view_row_items, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ListItem currentItem = (ListItem) getItem(position);
        viewHolder.itemName.setText(currentItem.getRoute().getRouteName());
        viewHolder.itemDate.setText(currentItem.getRoute().getCreatedDate());

        return convertView;
    }

    private class ViewHolder
    {
        TextView itemName;
        TextView itemDate;

        public ViewHolder(View view)
        {
            itemName = (TextView)view.findViewById(R.id.text_view_item_name);
            itemDate = (TextView)view.findViewById(R.id.Item_Created_Date);
        }
    }
}