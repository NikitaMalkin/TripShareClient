package com.example.routesmanagementscreen;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;


import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.utils.URIBuilder;
import cz.msebera.android.httpclient.client.methods.HttpDelete;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.util.EntityUtils;

public class MainActivity extends AppCompatActivity
{
    // This value will be set to a route when the server isn't in reach
    private static final long m_defaultID = -1;
    private Route m_routeToAdd;
    private Chronometer m_chronometer;
    private TextView m_longDisplay;
    private TextView m_latDisplay;
    private Handler m_handler;
    private ArrayList<ListItem> m_routesListSource = new ArrayList<>();
    private ListAdapter m_adapter;
    private boolean m_serverIsOnline = false;
    SwipeMenuListView m_swipeListView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        m_serverIsOnline = checkServerStatus();
        initializeViews();
        initializeLocationModules();
        initializeExistingRoutes();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appbar, menu);
        return true;
    }

    private void initializeExistingRoutes()
    {
        initializeRouteList();

        if (m_serverIsOnline)
        {
            new GetRoutesFromDB().execute();
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Could not connect to server, unable to fetch existing routes.", Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkServerStatus()
    {
        //TODO: Need to actually ping the server and see if it is online, currently this function simply disables server interaction and sets the application in offline mode. Maybe instead of saving server status to a variable, call this function everytime we want to see if the server has become online.
        return false;
    }


    private void initializeRouteList()
    {
        m_swipeListView = findViewById(R.id.listView);

        m_adapter = new ListAdapter(MainActivity.this, m_routesListSource);
        m_swipeListView.setAdapter(m_adapter);

        SwipeMenuCreator creator = new SwipeMenuCreator()
        {

            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(217,83,79)));
                // set item width
                deleteItem.setWidth(175);
                // set a icon
                deleteItem.setIcon(R.drawable.delete_ic);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        // set creator
        m_swipeListView.setMenuCreator(creator);

        m_swipeListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index)
            {
                switch (index)
                {
                    case 0:
                        // delete
                        long routeIDToDelete = m_adapter.getItem(position).getItemID();
                        if(m_serverIsOnline)
                            new DeleteRouteRequestFromServlet(String.valueOf(routeIDToDelete), position).execute();
                        else
                            removeItemFromListView(position);
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });
    }

    private void initializeViews() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Route Management");
        m_longDisplay = findViewById(R.id.textviewCurrentLong);
        m_latDisplay = findViewById(R.id.textviewCurrentLat);
        m_chronometer = findViewById(R.id.chronometer);
        setSupportActionBar(myToolbar);

        EditText txtEdit = (EditText) findViewById(R.id.textViewRouteName);

        txtEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    EditText edittext = (EditText) v;
                }
            }
        });
    }

    private void initializeLocationModules() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);

        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                DecimalFormat numberFormat = new DecimalFormat("#.000");

                String latitude = numberFormat.format(location.getLatitude());
                String longitude = numberFormat.format(location.getLongitude());

                m_longDisplay.setText(longitude);
                m_latDisplay.setText(latitude);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };


        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } catch (SecurityException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void buttonRecordRouteClicked(View view) {
        m_routeToAdd = new Route(0);  ///each time a new recording is started, m_routeToAdd is initialized, 0 is a placeholder for UserID

        view.setVisibility(view.GONE);
        View alternateView = findViewById(R.id.view_recording);

        alternateView.setVisibility(View.VISIBLE);

        m_handler = new Handler();  /// initializes m_handler so that every 10 seconds it invokes updateRoute() method
        final int delay = 1000; //milliseconds // CHANGED TO 1s FOR TEST SHOULD CHANGE BACK!!!!!!!!!! //TODO

        m_handler.postDelayed(new Runnable() {
            public void run() {
                updateRoute();
                m_handler.postDelayed(this, delay);
            }
        }, delay);


        m_chronometer = findViewById(R.id.chronometer);

        m_chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer c) {
                m_chronometer = c;
                long t = SystemClock.elapsedRealtime() - c.getBase();
                c.setText(DateFormat.format("kk:mm:ss", t));
            }
        });
        m_chronometer.setBase(SystemClock.elapsedRealtime());
        m_chronometer.start();
    }

    public void buttonStopRecordingRouteClicked(View view) {
        View recordingView = findViewById(R.id.view_recording);
        View savingView = findViewById(R.id.view_saving);
        View buttonCancel = findViewById(R.id.buttonCancel);
        View buttonSave = findViewById(R.id.buttonSave);
        EditText routeName = (EditText) findViewById(R.id.textViewRouteName);

        m_handler.removeCallbacksAndMessages(null);
        recordingView.setVisibility(View.GONE);
        savingView.setVisibility(View.VISIBLE);

        m_chronometer.stop();
    }

    private String getCurrentDateAndTime()
    {
        Calendar currentDate = Calendar.getInstance();
        //String result = currentDate.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH) + currentDate.getDisplayName(Calendar.DAY_OF_MONTH, Calendar.LONG, Locale.ENGLISH) + currentDate.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
        String result = currentDate.getTime().toString();

        return result;
    }

    public void buttonCancelSavingClicked(View view) {
        View savingView = findViewById(R.id.view_saving);
        View recButton = findViewById(R.id.buttonStartRecordingRoute);

        savingView.setVisibility(View.GONE);
        recButton.setVisibility(View.VISIBLE);
    }

    public void buttonSaveClicked(View view) {
        View savingView = findViewById(R.id.view_saving);
        View recButton = findViewById(R.id.buttonStartRecordingRoute);
        EditText routeName = (EditText) findViewById(R.id.textViewRouteName);

        savingView.setVisibility(View.GONE);
        recButton.setVisibility(View.VISIBLE);

        if (routeName.getText().length() == 0) //if route name was left empty, save default route name
        {
            m_routeToAdd.setRouteName(getCurrentDateAndTime());
        }
        else
        {
            m_routeToAdd.setRouteName(routeName.getText().toString());
            routeName.setText(null); //emptying the name for further use
        }

        if (m_serverIsOnline)
        {
            // Send the route to the server to save in the DB
            new SendRequestToServlet().execute();
        }
        else
        {
            addItemToListView(String.valueOf(m_defaultID));
            //TODO: Save the route locally (file?) and once connection to the server is restored, send the route to server.
        }

        hideKeyboardFrom(getApplicationContext(), findViewById(R.id.layout_main));
        Toast.makeText(getApplicationContext(), "Route saved successfully", Toast.LENGTH_LONG).show();
    }

    private void updateRoute() {
        Coordinate newCoord = new Coordinate(m_latDisplay.getText().toString(), m_longDisplay.getText().toString());
        m_routeToAdd.addCoordinateToRoute(newCoord);
        Toast.makeText(getApplicationContext(), "Coordinate added to route", Toast.LENGTH_LONG).show();
    }

    public void textviewRouteNameOnClick(View v) ///clears text on click
    {
        v = (EditText) findViewById(R.id.textViewRouteName);
        ((EditText) v).setText(null);
    }

    // Communication with the server and DB

    private class SendRequestToServlet extends AsyncTask<String, Integer, String>
    {
        private String routeIDFromServer;
        protected String doInBackground(String... Args)
        {
            String output = null;

            try {
                // convert the object we want to send to the server
                //  to a json format and create an entity from it
                String userRouteInJsonFormat = convertToJson();
                StringEntity userRouteEntity = new StringEntity(userRouteInJsonFormat);

                // build the post request to send to the server
                URIBuilder builder = new URIBuilder("http://10.0.2.2:8080/SaveRouteToDB/RouteServlet");
                builder.setParameter("m_RouteToAddToDB", userRouteInJsonFormat);
                HttpClient httpClient = HttpClientBuilder.create().build();
                HttpPost http_Post = new HttpPost(builder.build());
                http_Post.setEntity(userRouteEntity);

                // set request headers
                http_Post.setHeader("Accept", "application/json");
                http_Post.setHeader("Content-type", "application/json; charset-UTF-8");

                // get the response of the server
                HttpResponse httpResponse = httpClient.execute(http_Post);
                HttpEntity httpEntity = httpResponse.getEntity();
                output = EntityUtils.toString(httpEntity);
                routeIDFromServer = output;

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

        @Override
        protected void onPostExecute(String result)
        {
            addItemToListView(String.valueOf(routeIDFromServer));
        }
    }

    private class GetRoutesFromDB extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... Args)
        {
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
                String body = handler.handleResponse(response);
                int code = response.getStatusLine().getStatusCode();

                // retrieve the list of routes from response
                if(code == 200)
                {
                    JSONArray jsonArr = new JSONArray(body);
                    for (int i = 0; i < jsonArr.length(); i++) {
                        JSONObject jsonObj = jsonArr.getJSONObject(i);
                        ListItem itemToAddName = new ListItem(jsonObj.getString("m_routeName"), Long.parseLong(jsonObj.getString("m_ID")));
                        m_routesListSource.add(itemToAddName);
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
            m_adapter.notifyDataSetChanged();
        }
    }

    private class DeleteRouteRequestFromServlet extends AsyncTask<String, Integer, String>
    {
        String m_routeToDeleteID;// TODO !!! change “1” to be the actual route ID
        int m_indexOfListItemToRemove;

        public DeleteRouteRequestFromServlet(String i_routeToDeleteID, int i_indexOfListItemToRemove)
        {
            m_routeToDeleteID = i_routeToDeleteID;
            m_indexOfListItemToRemove = i_indexOfListItemToRemove;
        }

        protected String doInBackground(String... Args)
        {
            String output = null;

            try
            {

                URIBuilder builder = new URIBuilder("http://10.0.2.2:8080/SaveRouteToDB/RouteServlet");
                        builder.setParameter("m_routeID", m_routeToDeleteID);
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

        protected void onPostExecute(String result)
        {
            removeItemFromListView(m_indexOfListItemToRemove);
        }
    }

    public void removeItemFromListView(int i_indexOfListItemToRemove)
    {
        m_routesListSource.remove(i_indexOfListItemToRemove);
        m_adapter.notifyDataSetChanged();
    }

    public void addItemToListView(String i_routeID)
    {
        // set the new route in the list
        ListItem itemToAdd = new ListItem(m_routeToAdd.getRouteName(),Long.parseLong(i_routeID));
        m_routesListSource.add(itemToAdd);
        m_adapter.notifyDataSetChanged();
    }
    public String convertToJson() {
        Gson gson = new Gson();
        return gson.toJson(m_routeToAdd);
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
