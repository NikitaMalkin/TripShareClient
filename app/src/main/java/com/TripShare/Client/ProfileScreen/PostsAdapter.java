package com.TripShare.Client.ProfileScreen;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.TripShare.Client.R;

import java.util.Collections;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder>
{
    private List<PostItem> m_list = Collections.emptyList();

    PostsAdapter(List<PostItem> i_list)
    {
        this.m_list = i_list;
    }

    @Override
    public PostsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.layout_list_view_profile_items, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(PostsAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        PostItem post = m_list.get(position);

        // Set item views based on your views and data model
        TextView postName = viewHolder.getTextViewPostName();
        postName.setText(post.getPostName());

        TextView postDescription = viewHolder.getTextViewPostDescription();
        postDescription.setText(post.getPostDescription());

        ImageView imageThumbnail = viewHolder.getImageViewImage();
        imageThumbnail.setImageDrawable(post.getImage());

    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount()
    {
        return m_list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView m_postName;
        private TextView m_postDescription;
        private ImageButton m_likeButton;
        private ImageButton m_commentButton;
        private ImageButton m_mapButton;
        private ImageView m_image;

        public ViewHolder(View itemView)
        {
            super(itemView);

            m_postName = itemView.findViewById(R.id.profileItem_TextViewPostName);
            m_postDescription = itemView.findViewById(R.id.profileItem_textViewPostDescription);
            m_likeButton = itemView.findViewById(R.id.profileItem_imageButtonLike);
            m_commentButton = itemView.findViewById(R.id.profileItem_imageButtonComment);
            m_mapButton = itemView.findViewById(R.id.profileItem_imageButtonMap);
            m_image = itemView.findViewById(R.id.profileItem_imageView);
        }

        public TextView getTextViewPostName()
        {
            return m_postName;
        }

        public TextView getTextViewPostDescription()
        {
            return m_postDescription;
        }

        public ImageButton getImageButtonLikeButton()
        {
            return m_likeButton;
        }

        public ImageButton getImageButtonCommentButton()
        {
            return m_commentButton;
        }

        public ImageButton getImageButtonMapButton()
        {
            return m_mapButton;
        }

        public ImageView getImageViewImage()
        {
            return m_image;
        }
    }

}
