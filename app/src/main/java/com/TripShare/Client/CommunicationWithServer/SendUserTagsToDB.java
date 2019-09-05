package com.TripShare.Client.CommunicationWithServer;

import android.os.AsyncTask;
import com.google.gson.JsonArray;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.utils.URIBuilder;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.util.EntityUtils;

import java.util.ArrayList;

public class SendUserTagsToDB  extends AsyncTask<String, Integer, String>
{
    private Utils m_utils = new Utils();
    private ArrayList<String> m_userPreferredTags;
    private Long m_userID;

    public SendUserTagsToDB(ArrayList<String> i_userPreferredTags, Long i_userID)
    {
        m_userPreferredTags = i_userPreferredTags;
        m_userID = i_userID;
    }

    protected String doInBackground(String... Args) {
        String output = null;

        try
        {
            JsonArray jsonArrayPreferredTags = new JsonArray();
            for(int i=0; i < m_userPreferredTags.size(); i++)
            {
                jsonArrayPreferredTags.add(m_userPreferredTags.get(i));
            }

            URIBuilder builder = new URIBuilder("http://tripshare-env.cqpn2tvmsr.us-east-1.elasticbeanstalk.com/UserTagsServlet");
            //URIBuilder builder = new URIBuilder("http://10.0.2.2:8080/TripShareProject/UserTagsServlet");
            builder.setParameter("m_userPreferredTags", jsonArrayPreferredTags.toString());
            builder.setParameter("m_userID", m_userID.toString());
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost http_Post = new HttpPost(builder.build());

            // set request headers
            http_Post.setHeader("Accept", "application/json");
            http_Post.setHeader("Content-type", "application/json; charset-UTF-8");

            // get the response of the server
            HttpResponse httpResponse = httpClient.execute(http_Post);
            if(httpResponse.getStatusLine().getStatusCode() == 200) {
                HttpEntity httpEntity = httpResponse.getEntity();
                output = EntityUtils.toString(httpEntity);
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

    }
}
