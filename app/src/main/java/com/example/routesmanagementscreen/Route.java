package com.example.routesmanagementscreen;

import java.io.Serializable;
import java.util.*;

public class Route implements Serializable{
    private static final long serialVersionUID = 1L;

    private long m_ID;
    private int m_userID;
    private String m_routeName;
    private String m_routeDescription;
    private List<Coordinate> m_routeCoordinates= new ArrayList<>();

    public Route(int i_userID)
    {
        m_userID = i_userID;
    }

    public int getUserID()
    {
        return m_userID;
    }

    public long getRouteID()
    {
        return m_ID;
    }

    public String getRouteName()
    {
        return m_routeName;
    }

    public String getRouteDescription()
    {
        return m_routeDescription;
    }

    public List<Coordinate> getRouteCoordinates()
    {
        return m_routeCoordinates;
    }

    public void addCoordinateToRoute(Coordinate i_newCoordinate)
    {
        m_routeCoordinates.add(i_newCoordinate);
    }

    public void setRouteName(String i_routeName)
    {
        m_routeName = new String(i_routeName);
    }

    public void setRouteDescription(String i_routeDescription)
    {
        m_routeDescription = new String(i_routeDescription);
    }
}

