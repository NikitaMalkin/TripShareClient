package com.TripShare.Client.CommunicationWithServer;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.utils.URIBuilder;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;

public class GetPostsFromDB extends AsyncTask<String, Integer, String>
{
    private String m_body;
    private Boolean m_isHomePage;
    private AddAllItemsToListViewListener m_listenerAddItems;
    private long m_userID;
    private int m_firstPositionToRetrieve;
    private ProgressBar m_progressBar;

    public GetPostsFromDB(AddAllItemsToListViewListener i_listener, long i_userID, int i_firstPositionToRetrieve, Boolean i_isHomePage, ProgressBar i_progressBarToAnimate)
    {
        m_listenerAddItems = i_listener;
        m_userID = i_userID;
        m_firstPositionToRetrieve = i_firstPositionToRetrieve;
        m_isHomePage = i_isHomePage;
        m_progressBar = i_progressBarToAnimate;
    }

    @Override
    protected void onPreExecute()
    {
        m_progressBar.setVisibility(View.VISIBLE);
    }


    @Override
    protected String doInBackground(String... Args)
    {
        String output = null;


        try
        {
            HttpClient httpClient = HttpClientBuilder.create().build();

            URIBuilder builder;

            // Build URI
            if(m_isHomePage)
            {
                builder = new URIBuilder("http://tripshare-env.cqpn2tvmsr.us-east-1.elasticbeanstalk.com/HomePagePostsServlet");
            }
            else
            {
                builder = new URIBuilder("http://tripshare-env.cqpn2tvmsr.us-east-1.elasticbeanstalk.com/ProfilePostServlet");
            }

            builder.setParameter("m_userID", String.valueOf(m_userID));
            builder.setParameter("m_firstPositionToRetrieve", String.valueOf(m_firstPositionToRetrieve));

            // Send request to server
            HttpGet http_get = new HttpGet(builder.build());
            HttpResponse response = httpClient.execute(http_get);

            // Handle response
            ResponseHandler<String> handler = new BasicResponseHandler();
            m_body = handler.handleResponse(response);
            int code = response.getStatusLine().getStatusCode();

            // retrieve the list of routes from response need to update the list view in the main thread.
            if(code == 200)
            {
                m_listenerAddItems.addAllItemsToView(m_body);
            }
        }
        catch (Exception e)
        {
            Log.e("log_tag", "Error in http connection " + e.toString());
            output = "Error in http connection " + e.toString();
        }

        return output;
    }

    @Override
    protected void onPostExecute(String result)
    {
        m_progressBar.setVisibility(View.GONE);
    }

    public interface AddAllItemsToListViewListener
    {
        void addAllItemsToView(String i_body);
    }
}
