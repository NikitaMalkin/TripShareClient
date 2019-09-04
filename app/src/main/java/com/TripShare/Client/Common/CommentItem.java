package com.TripShare.Client.Common;

import android.graphics.Bitmap;

public class CommentItem
{
    private Comment m_comment;
    private Bitmap m_userProfileImage;

    public CommentItem(Comment i_comment) {  m_comment = i_comment; }

    public String getComment()
    {
        return m_comment.getComment();
    }

    public String getUserName()
    {
        return m_comment.getUserName();
    }

    public Bitmap getUserImage() { return  m_userProfileImage; }

    public void setUserImage(Bitmap i_userProfileImage) { m_userProfileImage = i_userProfileImage; }
}
