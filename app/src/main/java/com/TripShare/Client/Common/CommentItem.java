package com.TripShare.Client.Common;

public class CommentItem
{
    private Comment m_comment;

    public CommentItem(Comment i_comment)
    {
        m_comment = i_comment;
    }

    public String getComment()
    {
        return m_comment.getComment();
    }

    public String getUserName()
    {
        return m_comment.getUserName();
    }
}
