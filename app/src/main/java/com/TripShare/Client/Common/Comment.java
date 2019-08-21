package com.TripShare.Client.Common;

import java.io.Serializable;

public class Comment implements Serializable
{
    private long m_ID;
    private String m_userWhichCommentedName;
    private String m_comment;

    public Comment(String i_comment, String i_userName)
    {
        m_comment = i_comment;
        m_userWhichCommentedName = i_userName;
    }

    public long getID() { return m_ID; }

    public String getUserWhichCommentedID() { return m_userWhichCommentedName; }

    public String getComment() { return m_comment; }

    public void setUserWhichCommentedID(String i_userName) { m_userWhichCommentedName = i_userName; }

    public void setComment( String i_comment ) { m_comment = i_comment; }
}
