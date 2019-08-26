package com.TripShare.Client.CommunicationWithServer;

import android.os.AsyncTask;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPut;
import cz.msebera.android.httpclient.client.utils.URIBuilder;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.util.EntityUtils;

public class SendUserProfileImageToDB extends AsyncTask<String, Integer, String>
{
    private Long m_userIDToUpdate;
    private String m_imageStringToSend;

    public SendUserProfileImageToDB(Long i_userIDToUpdate, String i_imageStringToSend)
    {
        m_userIDToUpdate = i_userIDToUpdate;
        m_imageStringToSend = i_imageStringToSend;
    }

    protected String doInBackground(String... Args)
    {
        String output = null;

        try {
            // build the post request to send to the server
            URIBuilder builder = new URIBuilder("http://tripshare-env.cqpn2tvmsr.us-east-1.elasticbeanstalk.com/UploadProfileImageServlet");//("http://tripshare-env.cqpn2tvmsr.us-east-1.elasticbeanstalk.com/UploadProfileImageServlet");
            builder.setParameter("m_userID", String.valueOf(m_userIDToUpdate));
            builder.setParameter("m_imageString", m_imageStringToSend);
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPut http_Put = new HttpPut(builder.build());

            // set request headers
            http_Put.setHeader("Accept", "application/json");
            http_Put.setHeader("Content-type", "application/json; charset-UTF-8");

            // get the response of the server
            HttpResponse httpResponse = httpClient.execute(http_Put);
            HttpEntity httpEntity = httpResponse.getEntity();
            output = EntityUtils.toString(httpEntity);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return output;
    }
}

