package com.TripShare.Client.Common;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.TripShare.Client.R;

import java.util.ArrayList;

public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.DrawerRecyclerViewHolder> {

    private ArrayList<DrawerItem> m_drawerItems;


    public static class DrawerRecyclerViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
         TextView m_textView;
         ImageView m_imageView;

         DrawerRecyclerViewHolder(View view) {
            super(view);
            m_textView = view.findViewById(R.id.drawerItem_text);
            m_imageView = view.findViewById(R.id.drawerItem_image);
        }

         TextView getTextView()
        {
            return m_textView;
        }

        ImageView getImageview()
        {
            return m_imageView;
        }
    }

     DrawerAdapter(ArrayList<DrawerItem> i_list)
    {
        m_drawerItems = i_list;
    }

    @Override
    public DrawerAdapter.DrawerRecyclerViewHolder onCreateViewHolder(ViewGroup parent,
                                                                     int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.layout_recycler_view_drawer_items, parent, false);

        // Return a new holder instance
        DrawerRecyclerViewHolder viewHolder = new DrawerRecyclerViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DrawerRecyclerViewHolder viewHolder, int position) {
        // Get the data model based on position
        DrawerItem item = m_drawerItems.get(position);

        // Set item views based on your views and data model
        TextView textview = viewHolder.getTextView();
        textview.setText(item.getString());

        ImageView imageview = viewHolder.getImageview();
        imageview.setImageDrawable(item.getImage());
    }

    @Override
    public int getItemCount() {
        return m_drawerItems.size();
    }
}
