package com.TripShare.Client.Common;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.TripShare.Client.R;

import java.util.ArrayList;

class CommentAdapter extends ArrayAdapter<CommentItem>
{
    private final Context context; //context
    private final ArrayList<CommentItem> items; //data source of the list adapter

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
        if(currentItem.getUserImage() != null)
            viewHolder.itemImage.setImageBitmap(currentItem.getUserImage());

        return convertView;
    }

    private class ViewHolder
    {
        final TextView itemName;
        final TextView itemComment;
        final ImageView itemImage;

        ViewHolder(View view)
        {
            itemName = (TextView)view.findViewById(R.id.username_textView);
            itemComment = (TextView)view.findViewById(R.id.comment_textView);
            itemImage = (ImageView)view.findViewById(R.id.comment_user_image_view);
        }
    }
}
