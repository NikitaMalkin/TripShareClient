package com.TripShare.Client.CommunicationWithServer;

import android.os.AsyncTask;
import com.TripShare.Client.Common.Comment;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.utils.URIBuilder;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.util.EntityUtils;

public class SendCommentToAddToPostInDB extends AsyncTask<String, Integer, String>
{
    private Utils m_utils = new Utils();
    private Comment m_commentToSend;
    private Long m_postID;

    public SendCommentToAddToPostInDB(Comment i_commentToSend, Long i_postID)
    {
        m_commentToSend = i_commentToSend;
        m_postID = i_postID;
    }

    protected String doInBackground(String... Args) {
        String output = null;

        try {
            // convert the object we want to send to the server
            //  to a json format and create an entity from it
            String userRouteInJsonFormat = m_utils.convertToJson(m_commentToSend);
            StringEntity userRouteEntity = new StringEntity(userRouteInJsonFormat);

            // build the post request to send to the server
            URIBuilder builder = new URIBuilder("http://tripshare-env.cqpn2tvmsr.us-east-1.elasticbeanstalk.com/CommentServlet");

            builder.setParameter("m_CommentToAddToPostInDB", userRouteInJsonFormat);
            builder.setParameter("m_PostID", String.valueOf(m_postID));
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