package com.TripShare.Client.CommunicationWithServer;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

 class Utils
{
    public String convertToJson(Object i_objectToTranslate)
    {
        Gson gson = new Gson();
        return gson.toJson(i_objectToTranslate);
    }

    public String convertInputStreamToString(InputStream inputStream)
    {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while((line = bufferedReader.readLine()) != null)
            {
                sb.append(line);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
