package com.example.routesmanagementscreen;

import java.util.Date;

public class ListItem
{
    private long m_routeID;
    private String m_routeName;
    private Date m_createdDate;

    public ListItem(String i_routeName, long i_routeID, Date i_createdDate)
    {
        this.m_routeName = i_routeName;
        this.m_routeID = i_routeID;
        this.m_createdDate = i_createdDate;
    }

    public String getItemName() { return this.m_routeName; }

    public long getItemID() { return this.m_routeID; }

    public void setItemName(String i_routeName) { m_routeName = i_routeName; }

    public void setCreatedDate(Date i_createdDate) { m_createdDate = i_createdDate; }

    public Date getCreatedDate() { return m_createdDate; }
}