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
            // convert the object we want to send to the server
            //  to a json format and create an entity from it
            String userRouteInJsonFormat = m_utils.convertToJson(m_routeToSend);
            StringEntity userRouteEntity = new StringEntity(userRouteInJsonFormat);

            // build the post request to send to the server
            URIBuilder builder = new URIBuilder("http://tripshare-env.cqpn2tvmsr.us-east-1.elasticbeanstalk.com/RouteServlet");//("http://tripshare-env.cqpn2tvmsr.us-east-1.elasticbeanstalk.com/RouteServlet");
            //URIBuilder builder = new URIBuilder("http://10.0.2.2:8080/TripShareProject/RouteServlet");

            builder.setParameter("m_RouteToAddToDB", userRouteInJsonFormat);
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost http_Post = new HttpPost(builder.build());
            http_Post.setEntity(userRouteEntity);

            // set request headers
            http_Post.setHeader("Accept", "application/json");
            http_Post.setHeader("Content-type", "application/json; charset-UTF-8");

            // get the response of the server
            HttpResponse httpResponse = httpClient.execute(http_Post);
            if(httpResponse.getStatusLine().getStatusCode() == 200) {
                HttpEntity httpEntity = httpResponse.getEntity();
                output = EntityUtils.toString(httpEntity);
                routeIDFromServer = output;
                m_routeToSend.setRouteID(Long.valueOf(routeIDFromServer));
            }
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
