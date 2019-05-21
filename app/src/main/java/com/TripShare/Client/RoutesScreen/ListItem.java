package com.TripShare.Client.RoutesScreen;

import com.TripShare.Client.TripShareObjects.Route;

public class ListItem
{
    private Route m_route;

    public ListItem(Route i_route)
    {
        this.m_route = i_route;
    }

    public Route getRoute() { return m_route; }

    public void setRoute(Route i_route) { m_route = i_route;}
}