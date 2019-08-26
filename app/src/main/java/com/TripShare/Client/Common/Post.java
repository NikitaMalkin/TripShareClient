package com.TripShare.Client.Common;
import java.io.Serializable;
import java.util.ArrayList;

public class Post implements Serializable{
    private static final long serialVersionUID = 1L;

    private long m_ID;
    private int m_userID;
    private String m_title;
    private String m_description;
    private long m_routeID;
    private ArrayList<Long> m_usersWhichLikedID;
    private ArrayList<Comment> m_postComments;
    private ArrayList<String> m_relatedTags;

    public Post(int i_userID, String i_title, String i_description)
    {
        m_userID = i_userID;
        setTitle(i_title);
        setDescription(i_description);
        m_usersWhichLikedID = new ArrayList<>();
        m_postComments = new ArrayList<>();
        m_relatedTags = new ArrayList<>();
    }

    public long getID() { return m_ID; }

    public int getUserID() { return m_userID; }
    public void setUserID(int i_userID) { m_userID = i_userID; }

    public String getTitle() { return m_title; }
    public void setTitle(String i_title) { m_title = new String(i_title); }

    public String getDescription() { return m_description; }
    public void setDescription(String i_description) { m_description = new String(i_description); }

    public long getPostRoute() { return m_routeID; }

    public void setRouteID(long i_routeID)
    {
        m_routeID = i_routeID;
    }

    public int getLikeCount()
    {
        return m_usersWhichLikedID.size();
    }

    public int getCommentCount()
    {
        return m_postComments.size();
    }

    public void addLikedID(long i_userIDWhichLiked)
    {
        m_usersWhichLikedID.add(i_userIDWhichLiked);
    }

    public Boolean checkIfLikedByUser(Long i_userID)
    {
        Boolean returnValue = false;

        if(m_usersWhichLikedID.contains(i_userID))
            returnValue = true;

        return returnValue;
    }

    public void addComment(Comment i_comment)
    {
        m_postComments.add(i_comment);
    }

    public ArrayList<Comment> getCommentsArray()
    {
        return m_postComments;
    }

    public void addRelatedTag(String i_relatedTag)
    {
        if(m_relatedTags == null)
            m_relatedTags = new ArrayList<>();

        m_relatedTags.add(i_relatedTag);
    }

    public void intiallizeRelatedTags()
    {
        m_relatedTags = new ArrayList<>();
    }
}