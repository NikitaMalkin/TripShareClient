package com.TripShare.Client.CommunicationWithServer;

import android.os.AsyncTask;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.utils.URIBuilder;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.util.EntityUtils;

public class SendLikeToAddToPostInDB extends AsyncTask<String, Integer, String>
{
    private Utils m_utils = new Utils();
    Long m_userIDWhcihLikedToSend;
    Long m_postID;

    public SendLikeToAddToPostInDB(Long i_userIDWhcihLikedToSend, Long i_postID)
    {
        m_userIDWhcihLikedToSend = i_userIDWhcihLikedToSend;
        m_postID = i_postID;
    }

    protected String doInBackground(String... Args) {
        String output = null;

        try {
            // build the post request to send to the server
            URIBuilder builder = new URIBuilder("http://tripshare-env.cqpn2tvmsr.us-east-1.elasticbeanstalk.com/LikeServlet");//("http://tripshare-env.cqpn2tvmsr.us-east-1.elasticbeanstalk.com/LikeServlet");
           // URIBuilder builder = new URIBuilder("http://10.0.2.2:8080/TripShareProject/LikeServlet");

            builder.setParameter("m_LikeToAddToPostInDB",String.valueOf(m_userIDWhcihLikedToSend));
            builder.setParameter("m_PostID", String.valueOf(m_postID));
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost http_Post = new HttpPost(builder.build());

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