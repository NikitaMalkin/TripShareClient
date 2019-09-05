package com.TripShare.Client.CommunicationWithServer;

import android.os.AsyncTask;

import java.net.HttpURLConnection;
import java.net.URL;

public class CheckServerStatus extends AsyncTask<String, Integer, String>
{
    private final String url;
    private int code;
    private Boolean m_serverIsOnline;
    private final UpdatedServerStatus m_listener;

    public CheckServerStatus(String i_URL, UpdatedServerStatus i_listener)
    {
        url = i_URL;
        m_listener = i_listener;
    }

    @Override
    protected String doInBackground(String... strings)
    {
        String output;
        try
        {
            URL siteURL = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) siteURL.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3000);
            connection.connect();

            code = connection.getResponseCode();
            if (code == 200)
                m_listener.updateServerStatus(true);
            else
                m_listener.updateServerStatus(false);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        output = url + "\t\tStatus:" + m_serverIsOnline;

        return output;
    }

    public interface UpdatedServerStatus
    {
        void updateServerStatus(Boolean i_isOnline);
    }
}