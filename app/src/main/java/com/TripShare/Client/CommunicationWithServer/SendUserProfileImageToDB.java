package com.TripShare.Client.CommunicationWithServer;

import android.os.AsyncTask;
import com.google.gson.JsonArray;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class SendUserProfileImageToDB extends AsyncTask<String, Integer, String>
{
    private Long m_userIDToUpdate;
    private String m_imageStringToSend;
    private Utils m_utils;

    public SendUserProfileImageToDB(Long i_userIDToUpdate, String i_imageStringToSend)
    {
        m_userIDToUpdate = i_userIDToUpdate;
        m_imageStringToSend = i_imageStringToSend;
        m_utils = new Utils();
    }

    protected String doInBackground(String... Args)
    {
        String output = null;

        try {
            // This is getting the url from the string we passed in
            URL url = new URL("http://10.0.2.2:8080/TripShareProject/UploadProfileImageServlet");//("http://tripshare-env.cqpn2tvmsr.us-east-1.elasticbeanstalk.com/CoordinateUpdateServlet");

            // Create the urlConnection
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestMethod("POST");

            // convert the object we want to send to the server
            //  to a json format and create an entity from it
            JsonArray jsonArray = new JsonArray();
            jsonArray.add(m_userIDToUpdate);
            jsonArray.add(m_imageStringToSend);
            OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
            writer.write(jsonArray.toString());
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

