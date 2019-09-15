package com.TripShare.Client.CommunicationWithServer;

import android.os.AsyncTask;
import com.TripShare.Client.Common.ApplicationManager;
import com.TripShare.Client.Common.Route;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.utils.URIBuilder;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.util.EntityUtils;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class SendRouteToAddToDB extends AsyncTask<String, Integer, String>
{
    private Utils m_utils = new Utils();
    private String routeIDFromServer;
    private Route m_routeToSend;
    private AddItemToListViewListener m_listener;

    public SendRouteToAddToDB(Route i_routeToAdd, AddItemToListViewListener i_listener)
    {
        m_routeToSend = i_routeToAdd;
        m_listener = i_listener;
    }

    protected String doInBackground(String... Args) {
        String output = null;

        try {
            // This is getting the url from the string we passed in
            URL url = new URL("http://tripshare-env.cqpn2tvmsr.us-east-1.elasticbeanstalk.com/RouteServlet");

            // Create the urlConnection
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestMethod("POST");

            // convert the object we want to send to the server
            //  to a json format and create an entity from it
            String userInJsonFormat = m_utils.convertToJson(m_routeToSend);
            OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
            writer.write(userInJsonFormat);
            writer.flush();

            int statusCode = urlConnection.getResponseCode();

            if (statusCode == 200)
            {
                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                String response = m_utils.convertInputStreamToString(inputStream);
                routeIDFromServer = response;
                m_routeToSend.setRouteID(Long.valueOf(routeIDFromServer));
            }
            else
            {
                // Status code is not 200
                // Do something to handle the error
            }
            urlConnection.disconnect();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return output;
    }

    @Override
    protected void onPostExecute(String result)
    {
        if(m_listener != null)
        {
            ApplicationManager.addUserRoute(m_routeToSend);
            m_listener.addItemToListView(m_routeToSend);
        }
    }

    public interface AddItemToListViewListener
    {
        void addItemToListView(Route i_route);
    }
}