package com.TripShare.Client.ProfileScreen;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;


import java.util.ArrayList;

public class PostItem {
    private String m_postName;
    private String m_postDescription;
    private Drawable m_image;

    PostItem(String i_postName, String i_postDescription, Drawable i_image)
    {
        this.m_postName = i_postName;
        this.m_postDescription = i_postDescription;
        this.m_image = i_image;
    }

    public String getPostName()
    {
        return m_postName;
    }

    public String getPostDescription()
    {
        return m_postDescription;
    }

    public Drawable getImage()
    {
        return m_image;
    }

    public static ArrayList<PostItem> getPosts()
    {
        ArrayList<PostItem> result = new ArrayList<PostItem>();

        return result;
    }

}
