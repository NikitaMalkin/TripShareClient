package com.example.routesmanagementscreen;

public class ListItem
{
    private long m_routeID;
    private String m_routeName;

    public ListItem(String i_routeName, long i_routeID)
    {
        this.m_routeName = i_routeName;
        this.m_routeID = i_routeID;
    }

    public String getItemName() {
        return this.m_routeName;
    }
    public long getItemID() { return this.m_routeID; }
    public void setItemName(String i_routeName) { m_routeName = i_routeName; }
}