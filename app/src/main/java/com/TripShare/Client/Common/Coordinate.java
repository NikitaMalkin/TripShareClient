package com.TripShare.Client.Common;

import java.io.Serializable;

public class Coordinate implements Serializable{
    private static final long serialVersionUID = 1L;

    private long m_ID;
    private String m_latitude;
    private String m_longitude;
    private Addition m_addition;

    public Coordinate(String i_Latitude, String i_Longitude)
    {
        this.m_latitude = new String(i_Latitude);
        this.m_longitude = new String(i_Longitude);
        this.m_addition = new Addition();
    }

    public String getLatitude()
    {
        return m_latitude;
    }

    public String getLongitude()
    {
        return m_longitude;
    }

    public void setNote(String i_noteString)
    {
        if(m_addition == null)
            m_addition = new Addition();

        m_addition.setNote(i_noteString);
    }

    public String getNote()
    {
        if(m_addition == null)
            return null;

        return m_addition.getNote();
    }

    public Addition getAddition()
    {
        return m_addition;
    }

    public void setImageString(String i_imageString)
    {
        if(m_addition == null)
            m_addition = new Addition();

        m_addition.setImageString(i_imageString);
    }

    public String getImageString()
    {
        if(m_addition == null)
            return null;

        return m_addition.getImageString();
    }
//
//    @Override
//    public String toString() {
//        return String.format("%d, %d", this.m_latitude, this.m_longitude);
//    }
}
