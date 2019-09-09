package com.TripShare.Client.PostCreationScreen;

import com.TripShare.Client.Common.Coordinate;
import com.TripShare.Client.Common.Route;
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
        if(coordinateList != null && coordinateList.size() != 0)
        {
            int index = Integer.valueOf(coordinateList.size() / 2);
            Coordinate middleCoordinate = m_route.getRouteCoordinates().get(index);
            returnedValue = new LatLng(Double.valueOf(middleCoordinate.getLatitude()), Double.valueOf(middleCoordinate.getLongitude()));
        }
        return returnedValue;
    }
}
