package com.TripShare.Client.ProfileScreen;

import java.util.ArrayList;

public class PostItem {
    private String m_postName;
    private String m_postDescription;
    private String m_image;

    PostItem(String i_postName, String i_postDescription, String i_image)
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

    public static ArrayList<PostItem> createSampleData()
    {
        ArrayList<PostItem> list = new ArrayList<PostItem>();

        list.add(new PostItem("First Post", "some random description", "some image?"));
        list.add(new PostItem("Second Post", "some random description", "some image?"));
        list.add(new PostItem("Third Post", "some random description", "some image?"));

        return list;
    }
}
