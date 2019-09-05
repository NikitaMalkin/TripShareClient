package com.TripShare.Client.Common;

import android.graphics.drawable.Drawable;

class DrawerItem {
    private String m_string;
    private Drawable m_image;

    public DrawerItem(String i_string, Drawable i_image)
    {
        this.m_string = i_string;
        this.m_image = i_image;
    }

    public String getString(){
        return m_string;
    }

    public Drawable getImage()
    {
        return m_image;
    }
}
