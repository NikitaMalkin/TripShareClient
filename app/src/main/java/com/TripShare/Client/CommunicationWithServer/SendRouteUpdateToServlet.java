package com.TripShare.Client.CommunicationWithServer;

import android.os.AsyncTask;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPut;
import cz.msebera.android.httpclient.client.utils.URIBuilder;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.util.EntityUtils;

public class SendRouteUpdateToServlet extends AsyncTask<String, Integer, String>
{
    private String m_routeIDToUpdate;
    private String m_routeNewName;

    public SendRouteUpdateToServlet(String i_routeID, String i_newRouteName)
    {
        m_routeIDToUpdate = i_routeID;
        m_routeNewName = i_newRouteName;
    }

    protected String doInBackground(String... Args)
    {
        String output = null;

        try {
            // build the post request to send to the server
            URIBuilder builder = new URIBuilder("http://10.0.2.2:8080/SaveRouteToDB/RouteNameUpdateServlet");
            builder.setParameter("m_routeID", m_routeIDToUpdate);
            builder.setParameter("m_newRouteName", m_routeNewName);
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPut http_Put = new HttpPut(builder.build());

            // set request headers
            http_Put.setHeader("Accept", "application/json");
            http_Put.setHeader("Content-type", "application/json; charset-UTF-8");

            // get the response of the server
            HttpResponse httpResponse = httpClient.execute(http_Put);
            HttpEntity httpEntity = httpResponse.getEntity();
            output = EntityUtils.toString(httpEntity);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return output;
    }
}
