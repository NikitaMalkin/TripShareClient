package com.TripShare.Client.CommunicationWithServer;

import android.os.AsyncTask;
import com.TripShare.Client.Common.Route;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.utils.URIBuilder;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.util.EntityUtils;

public class SendRouteToAddToDB extends AsyncTask<String, Integer, String>
{
    private Utils m_utils = new Utils();
    private String routeIDFromServer;
    Route m_routeToSend;
    AddItemToListViewListener m_listener;

    public SendRouteToAddToDB(Route i_routeToAdd, AddItemToListViewListener i_listener)
    {
        m_routeToSend = i_routeToAdd;
        m_listener = i_listener;
    }

    protected String doInBackground(String... Args) {
        String output = null;

        try {
            // convert the object we want to send to the server
            //  to a json format and create an entity from it
            String userRouteInJsonFormat = m_utils.convertToJson(m_routeToSend);
            StringEntity userRouteEntity = new StringEntity(userRouteInJsonFormat);

            // build the post request to send to the server
            URIBuilder builder = new URIBuilder("http://tripshare-env.cqpn2tvmsr.us-east-1.elasticbeanstalk.com/RouteServlet");//("http://tripshare-env.cqpn2tvmsr.us-east-1.elasticbeanstalk.com/RouteServlet");
            builder.setParameter("m_RouteToAddToDB", userRouteInJsonFormat);
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost http_Post = new HttpPost(builder.build());
            http_Post.setEntity(userRouteEntity);

            // set request headers
            http_Post.setHeader("Accept", "application/json");
            http_Post.setHeader("Content-type", "application/json; charset-UTF-8");

            // get the response of the server
            HttpResponse httpResponse = httpClient.execute(http_Post);
            HttpEntity httpEntity = httpResponse.getEntity();
            output = EntityUtils.toString(httpEntity);
            routeIDFromServer = output;
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
        m_listener.addItemToListView(m_routeToSend);
    }

    public interface AddItemToListViewListener
    {
        void addItemToListView(Route i_route);
    }
}
