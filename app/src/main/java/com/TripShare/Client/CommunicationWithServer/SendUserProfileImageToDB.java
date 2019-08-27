package com.TripShare.Client.CommunicationWithServer;

import android.os.AsyncTask;
import com.TripShare.Client.Common.User;
import com.google.gson.JsonArray;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class SendUserProfileImageToDB extends AsyncTask<String, Integer, String>
{
    private User m_user;
    private Utils m_utils;

    public SendUserProfileImageToDB(User i_user)
    {
        m_user = i_user;
        m_utils = new Utils();
    }

    protected String doInBackground(String... Args)
    {
        String output = null;

        try {
            // This is getting the url from the string we passed in
            URL url = new URL("http://tripshare-env.cqpn2tvmsr.us-east-1.elasticbeanstalk.com/UploadProfileImageServlet");//("http://tripshare-env.cqpn2tvmsr.us-east-1.elasticbeanstalk.com/UploadProfileImageServlet");

            // Create the urlConnection
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestMethod("POST");

            // convert the object we want to send to the server
            //  to a json format and create an entity from it
            String userInJsonFormat = m_utils.convertToJson(m_user);
            OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
            writer.write(userInJsonFormat);
            writer.flush();

            int statusCode = urlConnection.getResponseCode();

            if (statusCode == 200)
            {
                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                String response = m_utils.convertInputStreamToString(inputStream);
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