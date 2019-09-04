package com.TripShare.Client.Common;

import android.graphics.drawable.Drawable;
import com.TripShare.Client.Common.Post;

public class PostItem
{
    private Post m_post;

    public PostItem(Post i_post)
    {
        this.m_post = i_post;
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
}
