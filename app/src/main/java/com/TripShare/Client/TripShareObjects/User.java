package com.TripShare.Client.TripShareObjects;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private long m_ID;
    private String m_userName;
    private	String m_password;

    public long getID() { return m_ID; }

    public String getStringUserName() { return m_userName; }

    public String getPassword()
    {
        return m_password;
    }

    public void setUserName(String i_userName) { m_userName = i_userName; }

    public void setPassword(String i_password)
    {
        m_password = i_password;
    }

    public void setUserID(long i_userID)
    {
        m_ID = i_userID;
    }
}