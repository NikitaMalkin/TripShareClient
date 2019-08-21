package com.TripShare.Client.CommunicationWithServer;

import android.os.AsyncTask;
import android.util.Log;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.utils.URIBuilder;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;

public class GetRouteByID extends AsyncTask<String, Integer, String>
{
    String body;
    SetRetrievedRoute m_listenerAddItems;
    long m_routeID;

    public GetRouteByID(SetRetrievedRoute i_listener, long i_routeID)
    {
        m_listenerAddItems = i_listener;
        m_routeID = i_routeID;
    }
    @Override
    protected String doInBackground(String... Args)
    {
        String output = null;

        try
        {
            HttpClient httpClient = HttpClientBuilder.create().build();

            // Build URI
            URIBuilder builder = new URIBuilder("http://10.0.2.2:8080/TripShareProject/RouteByIDServlet");//("http://tripshare-env.cqpn2tvmsr.us-east-1.elasticbeanstalk.com/RouteByIDServlet");
            // TODO: change "0" to m_userID
            builder.setParameter("m_routeID", String.valueOf(m_routeID));

            // Send request to server
            HttpGet http_get = new HttpGet(builder.build());
            HttpResponse response = httpClient.execute(http_get);

            // Handle response
            ResponseHandler<String> handler = new BasicResponseHandler();
            body = handler.handleResponse(response);
            int code = response.getStatusLine().getStatusCode();

            // retrieve the list of routes from response need to update the list view in the main thread.
            if(code == 200)
            {
                m_listenerAddItems.setRetrievedRoute(body);
            }
        }
        catch (Exception e)
        {
            Log.e("log_tag", "Error in http connection " + e.toString());
            output = "Error in http connection " + e.toString();
        }
        return output;
    }

    public interface SetRetrievedRoute
    {
        void setRetrievedRoute(String i_body);
    }
}
