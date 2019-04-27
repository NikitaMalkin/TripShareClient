package com.example.routesmanagementscreen;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.methods.HttpDelete;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.utils.URIBuilder;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.util.EntityUtils;

public class PostCreationScreen extends AppCompatActivity {

    private Post m_postToAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_creation_screen);

        initializeViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appbar, menu);
        return true;
    }

    private void initializeViews() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Create new post");
        setSupportActionBar(myToolbar);
    }

    public void OnAddRouteButtonClick(View view)
    {
        Intent routeCreationScreen = new Intent(PostCreationScreen.this, MainActivity.class);
        startActivity(routeCreationScreen);
    }


    public void OnPostButtonClick(View view)
    {
        // TODO: change names of Edit Text to something meaningful.
        TextView postTitle = findViewById(R.id.editText);
        TextView postDescription = findViewById(R.id.editText3);

        m_postToAdd = new Post(0, postTitle.toString(), postDescription.toString());
        new SendPostToAddToDB().execute();
    }

    // Communication with the server and DB

    private class GetPostsFromDB extends AsyncTask<String, Integer, String>
    {
        String m_userID = new String("1"); // TODO !!! change “1” to be the actual user ID
        @Override
        protected String doInBackground(String... Args)
        {
            String output = null;

            try
            {
                HttpClient httpClient = HttpClientBuilder.create().build();

                // Build URI
                URIBuilder builder = new URIBuilder("http://10.0.2.2:8080/SaveRouteToDB/PostServlet");
                builder.setParameter("m_userID", m_userID); // The value is 0 right now but will change in the future to user ID // TODO

                // Send request to server
                HttpGet http_get = new HttpGet(builder.build());
                HttpResponse response = httpClient.execute(http_get);

                // Handle response
                ResponseHandler<String> handler = new BasicResponseHandler();
                String body = handler.handleResponse(response);
                if(response.getStatusLine().getStatusCode() == 200)
                {
                    JSONArray jsonArr = new JSONArray(body);
                    for (int i = 0; i < jsonArr.length(); i++)
                    {
                        JSONObject jsonObj = jsonArr.getJSONObject(i);
                        System.out.print(jsonObj.toString());
                    }
                }
            }
            catch (Exception e)
            {
                Log.e("log_tag", "Error in http connection " + e.toString());
                output = "Error in http connection " + e.toString();
            }
            return output;
        }

        protected void onPostExecute(String result)
        {
            // TODO anythings we want to do after the posts are in the app.
        }
    }

    private class SendPostToAddToDB extends AsyncTask<String, Integer, String> {
                protected String doInBackground(String... Args) {
                String output = null;

                try
                {
                    // convert the object we want to send to the server
                    //  to a json format and create an entity from it
                    String postInJsonFormat = convertPostToJson();
                    StringEntity postRouteEntity = new StringEntity(postInJsonFormat);

                    URIBuilder builder = new URIBuilder("http://10.0.2.2:8080/SaveRouteToDB/PostServlet");
                    builder.setParameter("m_PostToAddToDB", postInJsonFormat);
                    HttpClient httpClient = HttpClientBuilder.create().build();
                    HttpPost http_Post = new HttpPost(builder.build());
                    http_Post.setEntity(postRouteEntity);

                    // set request headers
                    http_Post.setHeader("Accept", "application/json");
                    http_Post.setHeader("Content-type", "application/json; charset-UTF-8");

                    // get the response of the server
                    HttpResponse httpResponse = httpClient.execute(http_Post);
                    HttpEntity httpEntity = httpResponse.getEntity();
                    output = EntityUtils.toString(httpEntity);
                }
                catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }
                catch (ClientProtocolException e)
                {
                    e.printStackTrace();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                catch (URISyntaxException e)
                {
                    e.printStackTrace();
                }
                return output;
            }
    }

    private class DeletePostRequestFromServlet extends AsyncTask<String, Integer, String>
    {
        String m_postToDeleteID; // TODO !!! change “1” to be the actual post ID

        public DeletePostRequestFromServlet(String i_postToDeleteID)
        {
            m_postToDeleteID = i_postToDeleteID;
        }

        protected String doInBackground(String... Args)
        {
            String output = null;

            try
            {

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

            }
            catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }
            catch (ClientProtocolException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            catch (URISyntaxException e)
            {
                e.printStackTrace();
            }
            return output;
        }
    }

    public String convertPostToJson() {
        Gson gson = new Gson();
        return gson.toJson(m_postToAdd);
    }
}
