package com.TripShare.Client.Common;

import java.io.Serializable;

public class Like implements Serializable {
    private static final long serialVersionUID = 1L;

    private long m_ID;
    private long m_userWhichLikedID;

    public long getID() { return m_ID; }

    public long getUserWhichLikedID() { return m_userWhichLikedID; }

    public void setUserWhichLikedID(long i_ID) { m_userWhichLikedID = i_ID; }
}