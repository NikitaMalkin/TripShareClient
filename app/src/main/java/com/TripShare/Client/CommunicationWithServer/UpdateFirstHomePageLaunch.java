package com.TripShare.Client.CommunicationWithServer;

import android.os.AsyncTask;
import android.util.Log;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.utils.URIBuilder;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;

public class UpdateFirstHomePageLaunch extends AsyncTask<String, Integer, String> {
    private Long m_userID;

    public UpdateFirstHomePageLaunch(Long i_userID) {
        m_userID = i_userID;
    }

    protected String doInBackground(String... Args) {
        String output = null;
        try
        {
            HttpClient httpClient = HttpClientBuilder.create().build();

            // Build URI
            URIBuilder builder = new URIBuilder("http://tripshare-env.cqpn2tvmsr.us-east-1.elasticbeanstalk.com/UpdateFirstHomePageLaunchServlet");
            //URIBuilder builder = new URIBuilder("http://10.0.2.2:8080/TripShareProject/UpdateFirstHomePageLaunchServlet");

            builder.setParameter("m_userID", String.valueOf(m_userID));

            // Send request to server
            HttpPost http_post = new HttpPost(builder.build());
            HttpResponse response = httpClient.execute(http_post);

            // Handle response
            int code = response.getStatusLine().getStatusCode();

            // retrieve the list of routes from response need to update the list view in the main thread.
            if(code == 200)
            {
                Log.i("Success", "Successfully sent Update!");
            }
        }
        catch (Exception e)
        {
            Log.e("log_tag", "Error in http connection " + e.toString());
            output = "Error in http connection " + e.toString();
        }
        return output;
    }
}