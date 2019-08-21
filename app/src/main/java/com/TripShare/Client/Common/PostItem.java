package com.TripShare.Client.Common;

import android.graphics.drawable.Drawable;
import com.TripShare.Client.Common.Post;

public class PostItem
{
    private Post m_post;
    private Drawable m_image;

    public PostItem(Post i_post, Drawable i_image)
    {
        this.m_post = i_post;
        this.m_image = i_image;
    }

    public String getPostName()
    {
        return m_post.getTitle();
    }

    public String getPostDescription()
    {
        return m_post.getDescription();
    }

    public Post getPost() { return m_post; }

    public Drawable getImage()
    {
        return m_image;
    }
}
