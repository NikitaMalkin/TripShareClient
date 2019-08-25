package com.TripShare.Client.Common;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.TripShare.Client.R;

import java.util.ArrayList;

public class CommentAdapter extends ArrayAdapter<CommentItem>
{
    private Context context; //context
    private ArrayList<CommentItem> items; //data source of the list adapter

    //public constructor
    public CommentAdapter(Context i_context)
    {
        super(i_context, 0);
        context = i_context;
        items = new ArrayList<>();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public ArrayList<CommentItem> getItems() { return items; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_list_item_comment, parent, false);
            convertView.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.comment_list_item_background));
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        CommentItem currentItem = (CommentItem)getItem(position);
        viewHolder.itemName.setText(currentItem.getUserName());
        viewHolder.itemComment.setText(currentItem.getComment());

        return convertView;
    }

    private class ViewHolder
    {
        TextView itemName;
        TextView itemComment;

        public ViewHolder(View view)
        {
            itemName = (TextView)view.findViewById(R.id.username_textView);
            itemComment = (TextView)view.findViewById(R.id.comment_textView);
        }
    }
}
