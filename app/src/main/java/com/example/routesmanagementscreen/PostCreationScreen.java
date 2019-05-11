package com.example.routesmanagementscreen;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.NiceSpinnerAdapter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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

public class PostCreationScreen extends AppCompatActivity
    implements OnMapReadyCallback , GoogleMap.OnMarkerClickListener
{
    private Post m_postToAdd;
    NiceSpinner m_spinner;
    SpinnerAdapter m_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_creation_screen);

        initializeViews();
    }

    @Override
    public void onMapReady(GoogleMap map)
    {
        map.setOnMarkerClickListener(this);

        Bitmap image = getBitmapFromVectorDrawable(getApplicationContext(), R.drawable.ic_adjust_black_24dp); //converting image from vector to bitmap

        map.addMarker(new MarkerOptions().position(new LatLng(31.970, 34.801)).icon(BitmapDescriptorFactory.fromBitmap(image))).setAnchor(0.5f, 0.5f); //every new marker needs a position, an image, an anchor and an associated tag
        map.addMarker(new MarkerOptions().position(new LatLng(32, 35)).icon(BitmapDescriptorFactory.fromBitmap(image))).setAnchor(0.5f, 0.5f);
        map.addMarker(new MarkerOptions().position(new LatLng(31.8, 35.1)).icon(BitmapDescriptorFactory.fromBitmap(image))).setAnchor(0.5f, 0.5f);
        map.addPolyline(new PolylineOptions()
                .add(new LatLng(31.970, 34.801), new LatLng(32, 35), new LatLng(31.8, 35.1), new LatLng(31.970, 34.801))
                .width(5)
                .color(Color.CYAN));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appbar, menu);
        return true;
    }

    private void initializeViews()
    {
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Create new post");
        setSupportActionBar(myToolbar);
        initializeSpinnerWithRoutes();

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void initializeSpinnerWithRoutes()
    {
        m_spinner = (NiceSpinner)findViewById(R.id.spinner);
        m_adapter = new SpinnerAdapter(PostCreationScreen.this);

        settingTheDefaultValueForTheSpinner();
        settingTheHeightOfThePopUp();

        m_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if(position != 0)
                {
                    SpinnerItem clickedItem =(SpinnerItem)m_adapter.getItems().get(position);
                    // TODO: present the route on the map
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        new GetRoutesFromDB().execute();
    }

    public void settingTheDefaultValueForTheSpinner()
    {
        Route defaultValue = new Route(0);
        defaultValue.setRouteName("Choose Route...");
        m_adapter.getItems().add(0, new SpinnerItem(defaultValue));
        m_spinner.setAdapter(m_adapter);
        m_spinner.setSelectedIndex(0);
    }

    public void settingTheHeightOfThePopUp()
    {
        m_spinner.setDropDownListPaddingBottom(160);
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

    private class GetRoutesFromDB extends AsyncTask<String, Integer, String> {
        String body;

        @Override
        protected String doInBackground(String... Args) {
            String output = null;

            try
            {
                HttpClient httpClient = HttpClientBuilder.create().build();

                // Build URI
                URIBuilder builder = new URIBuilder("http://10.0.2.2:8080/SaveRouteToDB/RouteServlet");
                builder.setParameter("m_userID", "0"); // The value is 0 right now but will change in the future to user ID // TODO

                // Send request to server
                HttpGet http_get = new HttpGet(builder.build());
                HttpResponse response = httpClient.execute(http_get);

                // Handle response
                ResponseHandler<String> handler = new BasicResponseHandler();
                body = handler.handleResponse(response);
                int code = response.getStatusLine().getStatusCode();

                // retrieve the list of routes from response need to update the list view in the main thread.
                if (code == 200) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                JSONArray jsonArr = new JSONArray(body);
                                for (int i = 0; i < jsonArr.length(); i++) {
                                    JSONObject jsonObj = jsonArr.getJSONObject(i);
                                    Route route = new Gson().fromJson(jsonObj.toString(), Route.class);
                                    addItemToSpinner(route);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

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
            m_adapter.notifyDataSetChanged();
        }
    }

    private class SendPostToAddToDB extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String... Args) {
            String output = null;

            try {
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

    private class DeletePostRequestFromServlet extends AsyncTask<String, Integer, String> {
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

    public String convertPostToJson()
    {
        Gson gson = new Gson();
        return gson.toJson(m_postToAdd);
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId)
    {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    private void addItemToSpinner(Route i_route)
    {
        m_adapter.add(new SpinnerItem(i_route));
    }

    @Override
    public boolean onMarkerClick(final Marker marker)
    {
        DialogFragment dialog = new ChooseAdditionDialog();
        dialog.show(getSupportFragmentManager(), "Choose Addition");
        return true;
    }
}