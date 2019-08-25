package com.TripShare.Client.CommunicationWithServer;

import android.os.AsyncTask;
import com.TripShare.Client.Common.User;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.utils.URIBuilder;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.util.EntityUtils;

public class SendUserToAddToDB extends AsyncTask<String, Integer, String>
{
    Utils m_utils = new Utils();
    User m_userToAdd;
    long userIDFromServer;
    SetUserIDIfNotExistListener m_listener;

    public SendUserToAddToDB(User i_userToAdd, SetUserIDIfNotExistListener i_listener)
    {
        m_userToAdd = i_userToAdd;
        m_listener = i_listener;
    }

    protected String doInBackground(String... Args)
    {
        String output = null;

        try {
            // convert the object we want to send to the server
            //  to a json format and create an entity from it
            String userInfoInJsonFormat = m_utils.convertToJson(m_userToAdd);
            StringEntity userInfoEntity = new StringEntity(userInfoInJsonFormat);

            // build the post request to send to the server
            URIBuilder builder = new URIBuilder("http://10.0.2.2:8080/TripShareProject/UserServlet"); //("http://10.0.2.2:8080/TripShareProject/UserServlet")
            builder.setParameter("m_UserToAddToDB", userInfoInJsonFormat);
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost http_Post = new HttpPost(builder.build());
            http_Post.setEntity(userInfoEntity);

            // set request headers
            http_Post.setHeader("Accept", "application/json");
            http_Post.setHeader("Content-type", "application/json; charset-UTF-8");

            // get the response of the server
            HttpResponse httpResponse = httpClient.execute(http_Post);
            HttpEntity httpEntity = httpResponse.getEntity();
            output = EntityUtils.toString(httpEntity);
            userIDFromServer = Long.valueOf(output);
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
        m_listener.setUserIDIfNotExist(userIDFromServer);
    }

    public interface SetUserIDIfNotExistListener
    {
        void setUserIDIfNotExist(long i_userID);
    }
}
