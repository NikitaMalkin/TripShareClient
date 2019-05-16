package com.example.routesmanagementscreen;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

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

    public LatLng getCenterCoordinate()
    {
        LatLng returnedValue = null;
        List<Coordinate> coordinateList = m_route.getRouteCoordinates();
        Coordinate middleCoordinate = m_route.getRouteCoordinates().get(coordinateList.size()/2);
        returnedValue = new LatLng(Double.valueOf(middleCoordinate.getLatitude()), Double.valueOf(middleCoordinate.getLongitude()));

        return returnedValue;
    }
}
