package com.TripShare.Client.CommunicationWithServer;

import android.os.AsyncTask;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpDelete;
import cz.msebera.android.httpclient.client.utils.URIBuilder;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

// This class is yet used!!!!

public class DeletePostRequestFromServlet extends AsyncTask<String, Integer, String>
{
    String m_postToDeleteID; // TODO !!! change “1” to be the actual post ID

    public DeletePostRequestFromServlet(String i_postToDeleteID) {
        m_postToDeleteID = i_postToDeleteID;
    }

    protected String doInBackground(String... Args) {
        String output = null;

        try {

            URIBuilder builder = new URIBuilder("http://10.0.2.2:8080/SaveRouteToDB/PostServlet");
            builder.setParameter("m_postID", m_postToDeleteID);
            HttpDelete http_delete = new HttpDelete(builder.build());
            http_delete.setHeader("Accept", "application/json");
            http_delete.setHeader("Content-type", "application/json; charset-UTF-8");
            HttpClient httpClient = HttpClientBuilder.create().build();

            // get the response of the server
            HttpResponse httpResponse = httpClient.execute(http_delete);
            HttpEntity httpEntity = httpResponse.getEntity();
            output = EntityUtils.toString(httpEntity);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return output;
    }
}
