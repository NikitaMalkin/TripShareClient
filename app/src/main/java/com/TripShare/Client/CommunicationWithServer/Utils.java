package com.TripShare.Client.CommunicationWithServer;

import com.google.gson.Gson;

public class Utils
{
    public String convertToJson(Object i_objectToTranslate)
    {
        Gson gson = new Gson();
        return gson.toJson(i_objectToTranslate);
    }
}
