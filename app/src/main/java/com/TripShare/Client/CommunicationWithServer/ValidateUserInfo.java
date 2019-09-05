package com.TripShare.Client.CommunicationWithServer;

import android.os.AsyncTask;
import com.TripShare.Client.Common.User;
import com.google.gson.Gson;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.utils.URIBuilder;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.util.EntityUtils;
import org.json.JSONArray;

public class ValidateUserInfo extends AsyncTask<String, Integer, String>
{
    private Utils m_utils = new Utils();
    private String m_userName;
    private String m_password;
    private boolean m_isUserNamePasswordValid;
    private NotifyIfValidInfoListener m_listener;

    public ValidateUserInfo(NotifyIfValidInfoListener i_listener, String i_userName, String i_password)
    {
        m_listener = i_listener;
        m_userName = i_userName;
        m_password = i_password;
    }

    protected String doInBackground(String... Args) {
        String output = null;
        String userNameInJsonFormat;
        String passwordInJsonFormat;

        try
        {
            // convert the object we want to send to the server
            //  to a json format and create an entity from it
            userNameInJsonFormat = m_utils.convertToJson(m_userName);
            passwordInJsonFormat = m_utils.convertToJson(m_password);
            HttpClient httpClient = HttpClientBuilder.create().build();

            // Build URI
            URIBuilder builder = new URIBuilder("http://tripshare-env.cqpn2tvmsr.us-east-1.elasticbeanstalk.com/UserInfoValidation");
            //URIBuilder builder = new URIBuilder("http://10.0.2.2:8080/TripShareProject/UserInfoValidation");

            builder.setParameter("m_userName", userNameInJsonFormat);
            builder.setParameter("m_password", passwordInJsonFormat);

            // Send request to server
            HttpGet http_get = new HttpGet(builder.build());
            HttpResponse httpResponse = httpClient.execute(http_get);

            if(httpResponse.getStatusLine().getStatusCode() == 200)
            {
                HttpEntity httpEntity = httpResponse.getEntity();
                output = EntityUtils.toString(httpEntity);
                JSONArray jsonArr = new JSONArray(output);
                User userReceived = new Gson().fromJson(jsonArr.getString(0), User.class);
                m_isUserNamePasswordValid = jsonArr.getBoolean(1);
                m_listener.showAppropriateMessage(m_isUserNamePasswordValid, userReceived);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return output;
    }

    public interface NotifyIfValidInfoListener
    {
        void showAppropriateMessage(boolean i_isValidUsernameAndPassword, User i_user);
    }
}
