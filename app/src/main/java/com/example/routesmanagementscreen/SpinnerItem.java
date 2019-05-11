package com.example.routesmanagementscreen;

public class SpinnerItem
{
    private Route m_route;

    public SpinnerItem(Route i_route)
    {
        m_route = i_route;
    }

    public Route getRoute()
    {
        return m_route;
    }

    public String getRouteName() { return m_route.getRouteName(); }
}
