package com.TripShare.Client.CommunicationWithServer;

import android.os.AsyncTask;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpDelete;
import cz.msebera.android.httpclient.client.utils.URIBuilder;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.util.EntityUtils;

public class DeleteRouteRequestFromDB extends AsyncTask<String, Integer, String>
{
    String m_routeToDeleteID;
    int m_indexOfListItemToRemove;
    RemoveItemFromListViewListener m_listener;

    public DeleteRouteRequestFromDB(String i_routeToDeleteID, int i_indexOfListItemToRemove, RemoveItemFromListViewListener i_listener)
    {
        m_routeToDeleteID = i_routeToDeleteID;
        m_indexOfListItemToRemove = i_indexOfListItemToRemove;
        m_listener = i_listener;
    }

    protected String doInBackground(String... Args)
    {
        String output = null;

        try
        {
            URIBuilder builder = new URIBuilder("http://tripshare-env.cqpn2tvmsr.us-east-1.elasticbeanstalk.com/RouteServlet");//("http://tripshare-env.cqpn2tvmsr.us-east-1.elasticbeanstalk.com/RouteServlet");
            //URIBuilder builder = new URIBuilder("http://10.0.2.2:8080/TripShareProject/RouteServlet");
            builder.setParameter("m_routeID", m_routeToDeleteID);
            HttpDelete http_delete = new HttpDelete(builder.build());
            http_delete.setHeader("Accept", "application/json");
            http_delete.setHeader("Content-type", "application/json; charset-UTF-8");
            HttpClient httpClient = HttpClientBuilder.create().build();

            // get the response of the server
            HttpResponse httpResponse = httpClient.execute(http_delete);
            HttpEntity httpEntity = httpResponse.getEntity();
            output = EntityUtils.toString(httpEntity);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return output;
    }

    protected void onPostExecute(String result)
    {
        m_listener.removeItemFromListView(m_indexOfListItemToRemove);
    }

    public interface RemoveItemFromListViewListener
    {
        void removeItemFromListView(int i_indexOfListItemToRemove);
    }
}