package com.TripShare.Client.PostCreationScreen;

import android.content.Context;
import android.widget.ArrayAdapter;

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

    @Override
    public void add(Object object)
    {
        super.add(object);
        items.add((SpinnerItem)object);
        notifyDataSetChanged();
    }

    @Override
    public int getCount()
    {
       return items.size();
    }

    @Override
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

    @Override
    public boolean isEnabled(int position)
    {
        if(position == 0)
        {
            // Disable the first item from Spinner
            // First item will be use for hint
            return false;
        }
        else
        {
            return true;
        }
    }

    public SpinnerItem getItemByName(String i_itemName)
    {
        for (SpinnerItem item: items)
        {
            if(item.getRouteName().equals(i_itemName))
                return item;
        }
        return null;
    }
}
