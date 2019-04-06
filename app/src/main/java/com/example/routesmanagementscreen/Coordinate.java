package com.example.routesmanagementscreen;

import java.io.Serializable;

public class Coordinate implements Serializable {
    private static final long serialVersionUID = 1L;

    private long m_ID;
    private String m_latitude;
    private String m_longitude;

    public Coordinate() {
    }

    Coordinate(String i_Latitude, String i_Longitude) {
        this.m_latitude = new String(i_Latitude);
        this.m_longitude = new String(i_Longitude);
    }

    public String getLatitude()
    {
        return m_latitude;
    }

    public String getLongitude()
    {
        return m_longitude;
    }

    @Override
    public String toString() {
        return String.format("%d, %d", this.m_latitude, this.m_longitude);
    }
}
