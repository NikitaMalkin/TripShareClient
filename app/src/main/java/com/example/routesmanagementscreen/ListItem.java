package com.example.routesmanagementscreen;

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