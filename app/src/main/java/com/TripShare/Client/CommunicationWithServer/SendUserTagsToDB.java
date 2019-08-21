package com.TripShare.Client.CommunicationWithServer;

import android.os.AsyncTask;

// TODO!!!
public class SendUserTagsToDB  extends AsyncTask<String, Integer, String>
{
    private Utils m_utils = new Utils();

    public SendUserTagsToDB()
    {
    }

    protected String doInBackground(String... Args) {
        String output = null;

        try {

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

    }
}
