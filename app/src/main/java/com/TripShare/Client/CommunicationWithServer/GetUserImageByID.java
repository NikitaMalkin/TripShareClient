package com.TripShare.Client.CommunicationWithServer;

import android.os.AsyncTask;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.utils.URIBuilder;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;

public class GetUserImageByID extends AsyncTask<String, Integer, String> {
    private Utils m_utils = new Utils();
    private Long m_userID;
    private int m_indexOfCurrentComment;
    private GetUserProfileImageListener m_listener;
    private String m_body;

    public GetUserImageByID(Long i_userID, GetUserProfileImageListener i_listener, int i_indexOfCurrentComment) {
        m_userID = i_userID;
        m_listener = i_listener;
        m_indexOfCurrentComment = i_indexOfCurrentComment;
    }

    protected String doInBackground(String... Args) {
        String output = null;

        try {
            HttpClient httpClient = HttpClientBuilder.create().build();

            // Build URI
            URIBuilder builder = new URIBuilder("http://tripshare-env.cqpn2tvmsr.us-east-1.elasticbeanstalk.com/UserImageByIDServlet");
            //URIBuilder builder = new URIBuilder("http://10.0.2.2:8080/TripShareProject/UserImageByIDServlet");

            builder.setParameter("m_userID", String.valueOf(m_userID));

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
                m_listener.getUserProfileImageString(m_body, m_indexOfCurrentComment);
            }
        }
         catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }

    public interface GetUserProfileImageListener
    {
        void getUserProfileImageString(String i_imageString, int i_indexOfCurrentComment);
    }
}
