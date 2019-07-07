package com.TripShare.Client.Common;

import java.io.Serializable;

public class Addition implements Serializable
{
    private static final long serialVersionUID = 1L;

    private long m_ID;
    private String m_note;
    private String m_imageString;

    public String getNote() { return m_note; }

    public void setNote(String i_imageDescription) { m_note = i_imageDescription; }

    public String getImageString() { return m_imageString; }

    public void setImageString(String i_imageString){ m_imageString = i_imageString; }
}
