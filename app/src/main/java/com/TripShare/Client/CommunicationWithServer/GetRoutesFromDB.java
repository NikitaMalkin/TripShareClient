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

public class GetRoutesFromDB extends AsyncTask<String, Integer, String>
{
    String body;
    AddAllItemsToListViewListener m_listenerAddItems;
    UpdateRelevantView m_listenerUpdateView;

    public GetRoutesFromDB(AddAllItemsToListViewListener i_listener, UpdateRelevantView i_listenerUpdateView)
    {
        m_listenerAddItems = i_listener;
        m_listenerUpdateView = i_listenerUpdateView;
    }
    @Override
    protected String doInBackground(String... Args)
    {
        String output = null;

        try
        {
            HttpClient httpClient = HttpClientBuilder.create().build();

            // Build URI
            URIBuilder builder = new URIBuilder("http://10.0.2.2:8080/SaveRouteToDB/RouteServlet");
            builder.setParameter("m_userID", "0"); // The value is 0 right now but will change in the future to user ID // TODO

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
                m_listenerAddItems.addAllItemsToView(body);
            }
        }
        catch (Exception e)
        {
            Log.e("log_tag", "Error in http connection " + e.toString());
            output = "Error in http connection " + e.toString();
        }
        return output;
    }

    protected void onPostExecute(String result)
    {
        if(m_listenerUpdateView != null)
            m_listenerUpdateView.updateRelevantViewWithSource();
    }

    public interface AddAllItemsToListViewListener
    {
        void addAllItemsToView(String i_body);
    }

    public interface UpdateRelevantView
    {
        void updateRelevantViewWithSource();
    }
}
