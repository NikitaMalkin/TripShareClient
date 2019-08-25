package com.TripShare.Client.Common;

import java.io.Serializable;

public class Comment implements Serializable
{
    private long m_ID;
    private long m_userWhichCommented;
    private String m_userWhichCommentedName;
    private String m_comment;

    public Comment(String i_comment, long i_userID, String i_userName)
    {
        m_comment = i_comment;
        m_userWhichCommented = i_userID;
        m_userWhichCommentedName = i_userName;
    }

    public long getID() { return m_ID; }

    public long getUserWhichCommentedID() { return m_userWhichCommented; }

    public String getComment() { return m_comment; }

    public void setUserWhichCommentedID(long i_userName) { m_userWhichCommented = i_userName; }

    public void setComment( String i_comment ) { m_comment = i_comment; }

    public void setUserName(String i_userName) { m_userWhichCommentedName = i_userName; }

    public String getUserName() { return m_userWhichCommentedName;}
}
