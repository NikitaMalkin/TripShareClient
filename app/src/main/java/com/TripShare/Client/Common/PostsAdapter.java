package com.TripShare.Client.Common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
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
    private shareButtonClickedListener m_listenerShare;
    private mapButtonClickedListener m_listenerMap;
    private likeButtonClickedListener m_listenerLike;
    private commentButtonClickedListener m_listenerComment;

    public PostsAdapter(List<PostItem> i_list, shareButtonClickedListener i_listenerShare, mapButtonClickedListener i_listenerMap, likeButtonClickedListener i_listenerLike, commentButtonClickedListener i_listenerComment)
    {
        this.m_list = i_list;
        m_listenerShare = i_listenerShare;
        m_listenerMap = i_listenerMap;
        m_listenerLike = i_listenerLike;
        m_listenerComment = i_listenerComment;
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

        // converting the stored string (if it has one) into bitmap and applying it to the view
        ImageView imageThumbnail = viewHolder.getImageViewImage();
        String imageString = post.getPost().getThumbnailString();
        if (imageString != null)
        {
            byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
            Bitmap image = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imageThumbnail.setImageBitmap(image);
        }

        TextView likesAndCommentsNumber = viewHolder.getTextViewLikesAndCommentsNumber();
        likesAndCommentsNumber.setText(post.getPost().getLikeCount() + " Likes, " + post.getPost().getCommentCount() + " Comments");

        TextView authorName = viewHolder.getTextViewAuthorName();
        authorName.setText("by " + post.getPost().getAuthorFirstName() + " " + post.getPost().getAuthorLastName());

        if (post.getPost().checkIfLikedByUser(ApplicationManager.getLoggedInUser().getID()))
        {
            ImageView likeImage = viewHolder.getImageButtonLikeButton();
            likeImage.setImageDrawable(viewHolder.itemView.getContext().getDrawable(R.drawable.ic_favorite_black_24dp_red));
        }

        if (post.getPost().getIsPrivatePost())
        {
            ImageView privacyIcon = viewHolder.getPrivacyIcon();
            privacyIcon.setImageDrawable(viewHolder.itemView.getContext().getDrawable(R.drawable.ic_lock_black_24dp));
        }
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
        private ImageButton m_shareButton;
        private ImageView m_image;
        private ImageView m_privacyIcon;
        private TextView m_likesAndCommentsNumber;
        private TextView m_authorName;

        ViewHolder(View itemView)
        {
            super(itemView);

            m_postName = itemView.findViewById(R.id.profileItem_TextViewPostName);
            m_postDescription = itemView.findViewById(R.id.profileItem_textViewPostDescription);
            m_likeButton = itemView.findViewById(R.id.profileItem_imageButtonLike);
            m_commentButton = itemView.findViewById(R.id.profileItem_imageButtonComment);
            m_mapButton = itemView.findViewById(R.id.profileItem_imageButtonMap);
            m_shareButton = itemView.findViewById(R.id.profileItem_imageButtonShare);
            m_image = itemView.findViewById(R.id.profileItem_imageView);
            m_likesAndCommentsNumber = itemView.findViewById(R.id.profileItem_textView_numberOfLikesAndComments);
            m_authorName = itemView.findViewById(R.id.profileItem_postAuthor);
            m_privacyIcon = itemView.findViewById(R.id.profileItem_publicity_icon);

            m_shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    m_listenerShare.onShareButtonClick(getAdapterPosition(), v);
                }
            });

            m_mapButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v)
                {
                    m_listenerMap.onMapButtonClick(getAdapterPosition(), v);
                }
            });

            m_likeButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v)
                {
                    m_listenerLike.onLikeButtonClick(getAdapterPosition(), v);
                }
            });

            m_commentButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v)
                {
                    m_listenerComment.onCommentButtonClick(getAdapterPosition(), v);
                }
            });
        }

         ImageView getPrivacyIcon() { return m_privacyIcon; }

         TextView getTextViewPostName()
        {
            return m_postName;
        }

         TextView getTextViewPostDescription()
        {
            return m_postDescription;
        }

         ImageButton getImageButtonLikeButton()
        {
            return m_likeButton;
        }

        public ImageButton getImageButtonCommentButton()
        {
            return m_commentButton;
        }

        public ImageButton getImageButtonShareButton()
        {
            return m_shareButton;
        }

        public ImageButton getImageButtonMapButton()
        {
            return m_mapButton;
        }

         ImageView getImageViewImage()
        {
            return m_image;
        }

         TextView getTextViewLikesAndCommentsNumber()
        {
            return m_likesAndCommentsNumber;
        }

         TextView getTextViewAuthorName() { return m_authorName; }
    }

    public interface shareButtonClickedListener
    {
        void onShareButtonClick(int i_position, View i_view);
    }

    public interface mapButtonClickedListener
    {
        void onMapButtonClick(int i_position, View i_view);
    }

    public interface likeButtonClickedListener
    {
        void onLikeButtonClick(int i_position, View i_view);
    }

    public interface commentButtonClickedListener
    {
        void onCommentButtonClick(int i_position, View i_view);
    }
}
