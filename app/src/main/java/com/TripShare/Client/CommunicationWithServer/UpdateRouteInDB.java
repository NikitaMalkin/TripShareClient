package com.TripShare.Client.CommunicationWithServer;

import android.os.AsyncTask;
import com.TripShare.Client.Common.Coordinate;
import com.TripShare.Client.Common.Route;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class UpdateRouteInDB extends AsyncTask<String, Integer, String>
{
    private Utils m_utils = new Utils();
    private Route m_updatedRoute;

    public UpdateRouteInDB(Route i_updatedRoute)
    {
        m_updatedRoute = i_updatedRoute;
    }

    protected String doInBackground(String... Args)
    {
        String output = null;
        String coordinateInJsonFormat;

        try
        {
            //Getting the Updated Route to send its coordinates to the server.
            List<Coordinate> routeCoordinates = m_updatedRoute.getRouteCoordinates();

            // Send the post body
            for (Coordinate coord: routeCoordinates)
            {
                if (coord.getAddition() != null)
                {
                    // This is getting the url from the string we passed in
                    URL url = new URL("http://tripshare-env.cqpn2tvmsr.us-east-1.elasticbeanstalk.com/CoordinateUpdateServlet");
                    //URL url = new URL("http://10.0.2.2:8080/TripShareProjetc/CoordinateUpdateServlet");
                    // Create the urlConnection
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    urlConnection.setRequestMethod("POST");

                    // convert the object we want to send to the server
                    //  to a json format and create an entity from it
                    coordinateInJsonFormat = m_utils.convertToJson(coord);
                    OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                    writer.write(coordinateInJsonFormat);
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
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return output;
    }
}