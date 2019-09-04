package com.TripShare.Client.CommunicationWithServer;

import android.os.AsyncTask;
import com.TripShare.Client.Common.Post;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.utils.URIBuilder;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.util.EntityUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;

public class SendPostToAddToDB extends AsyncTask<String, Integer, String>
{
    private Utils m_utils = new Utils();
    private String postIDFromServer;
    private Post m_postToAdd;

    public SendPostToAddToDB(Post i_postToAdd)
    {
        m_postToAdd = i_postToAdd;
    }

    protected String doInBackground(String... Args)
    {
        String output = null;

        try {
            // This is getting the url from the string we passed in
            URL url = new URL("http://tripshare-env.cqpn2tvmsr.us-east-1.elasticbeanstalk.com/ProfilePostServlet");
            //URL url = new URL("http://10.0.2.2:8080/TripShareProject/ProfilePostServlet");

            // Create the urlConnection
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestMethod("POST");

            // convert the object we want to send to the server
            //  to a json format and create an entity from it
            OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
            String postInJsonFormat = m_utils.convertToJson(m_postToAdd);
            writer.write(postInJsonFormat);
            writer.flush();

            int statusCode = urlConnection.getResponseCode();

            if (statusCode == 200)
            {
                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                postIDFromServer = m_utils.convertInputStreamToString(inputStream);
            }
            else
            {
                // Status code is not 200
                // Do something to handle the error
            }
            urlConnection.disconnect();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return output;
    }
}
