package com.TripShare.Client.CommunicationWithServer;

import android.os.AsyncTask;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.utils.URIBuilder;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.util.EntityUtils;

import java.util.ArrayList;

// TODO!!!
public class SendUserTagsToDB  extends AsyncTask<String, Integer, String>
{
    private Utils m_utils = new Utils();
    private ArrayList<String> m_userPreferredTags;
    private Long m_userID;

    public SendUserTagsToDB(ArrayList<String> i_userPreferredTags)
    {
        m_userPreferredTags = i_userPreferredTags;
    }

    protected String doInBackground(String... Args) {
        String output = null;

        try
        {
            String userPreferredTagsInJson = m_utils.convertToJson(m_userPreferredTags);
            StringEntity userPreferredTagsEntity = new StringEntity(userPreferredTagsInJson);

            URIBuilder builder = new URIBuilder("http://tripshare-env.cqpn2tvmsr.us-east-1.elasticbeanstalk.com/UserTagsServlet");//("http://tripshare-env.cqpn2tvmsr.us-east-1.elasticbeanstalk.com/UserTagsServlet");
            builder.setParameter("m_userPreferredTags", userPreferredTagsInJson);
            builder.setParameter("m_userID", m_userID.toString());
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost http_Post = new HttpPost(builder.build());
            http_Post.setEntity(userPreferredTagsEntity);

            // set request headers
            http_Post.setHeader("Accept", "application/json");
            http_Post.setHeader("Content-type", "application/json; charset-UTF-8");

            // get the response of the server
            HttpResponse httpResponse = httpClient.execute(http_Post);
            HttpEntity httpEntity = httpResponse.getEntity();
            output = EntityUtils.toString(httpEntity);
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
