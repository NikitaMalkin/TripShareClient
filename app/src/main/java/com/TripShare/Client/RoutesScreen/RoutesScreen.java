package com.TripShare.Client.RoutesScreen;

import android.Manifest;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.TripShare.Client.Common.ApplicationManager;
import com.TripShare.Client.CommunicationWithServer.DeleteRouteRequestFromDB;
import com.TripShare.Client.CommunicationWithServer.GetRoutesFromDB;
import com.TripShare.Client.CommunicationWithServer.SendRouteToAddToDB;
import com.TripShare.Client.CommunicationWithServer.SendRouteUpdateToDB;
import com.TripShare.Client.Common.ActivityWithNavigationDrawer;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.TripShare.Client.R;
import com.TripShare.Client.Common.Coordinate;
import com.TripShare.Client.Common.Route;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class RoutesScreen extends ActivityWithNavigationDrawer implements EditRouteDialog.EditDialogListener, SendRouteToAddToDB.AddItemToListViewListener, GetRoutesFromDB.AddAllItemsToListViewListener, DeleteRouteRequestFromDB.RemoveItemFromListViewListener
{
    // This value will be set to a route when the server isn't in reach
    private static final long m_defaultID = -1;
    private static final String m_localRoutesFileName = "localRoutes.json";
    private File m_localRoutesFile;
    private Route m_routeToAdd;
    private Chronometer m_chronometer;
    private TextView m_longDisplay;
    private TextView m_latDisplay;
    private Handler m_handler;
    private ListAdapter m_adapter;
    private boolean m_serverIsOnline = true;
    private int m_currentlyClickedListItem;
    SwipeMenuListView m_swipeListView;
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes_screen);
        setActivityTitle("Manage your routes");
        try
        {
            //new checkServerStatus("http://tripshare-env.cqpn2tvmsr.us-east-1.elasticbeanstalk.com").execute().get();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        initializeDrawerLayout();
        initializeViews();
        initializeLocationModules();
        initializeExistingRoutes();
    }

    public void addAllItemsToView(final String i_body)
    {
        runOnUiThread(new Runnable()
        {

            @Override
            public void run()
            {
                try
                {
                    JSONArray jsonArr = new JSONArray(i_body);
                    for (int i = 0; i < jsonArr.length(); i++) {
                        JSONObject jsonObj = jsonArr.getJSONObject(i);
                        Route route = new Gson().fromJson(jsonObj.toString(), Route.class);
                        addItemToListView(route);
                    }
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    public void addItemToListView(Route i_routeToAdd)
    {
        // set the new route in the list
        ListItem itemToAdd = new ListItem(i_routeToAdd);
        m_adapter.add(itemToAdd);
    }

    public void buttonCancelSavingClicked(View view)
    {
        View savingView = findViewById(R.id.view_saving);
        View recButton = findViewById(R.id.buttonStartRecordingRoute);

        ApplicationManager.hideKeyboardFrom(getApplicationContext(), findViewById(R.id.layout_main));

        savingView.setVisibility(View.GONE);
        recButton.setVisibility(View.VISIBLE);
    }

    public void buttonSaveClicked(View view)
    {
        View savingView = findViewById(R.id.view_saving);
        View recButton = findViewById(R.id.buttonStartRecordingRoute);
        EditText routeName = (EditText) findViewById(R.id.textViewRouteName);

        savingView.setVisibility(View.GONE);
        recButton.setVisibility(View.VISIBLE);

        // Set created date of route.
        m_routeToAdd.setCreatedDate(getCurrentDateAndTime(new SimpleDateFormat("dd/MM/yy")));

        if (routeName.getText().length() == 0) //if route name was left empty, save default route name
            m_routeToAdd.setRouteName(getCurrentDateAndTime(new SimpleDateFormat("dd/MM/yy EEE HH:mm:ss")));
        else
        {
            m_routeToAdd.setRouteName(routeName.getText().toString());
            routeName.setText(null); //emptying the name for further use
        }

        if (m_serverIsOnline)
            new SendRouteToAddToDB(m_routeToAdd, this).execute(); // Send the route to the server to save in the DB
        else
        {
            m_routeToAdd.setRouteID(m_defaultID);
            addItemToListView(m_routeToAdd);
        }

        ApplicationManager.hideKeyboardFrom(getApplicationContext(), findViewById(R.id.layout_main));
        Toast.makeText(getApplicationContext(), "Route saved successfully", Toast.LENGTH_LONG).show();
    }

    public void buttonRecordRouteClicked(View view)
    {
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

        m_handler.removeCallbacksAndMessages(null);
        recordingView.setVisibility(View.GONE);
        savingView.setVisibility(View.VISIBLE);

        m_chronometer.stop();
    }

    private class checkServerStatus extends AsyncTask<String, Integer, String>
    {
        String url;

        public checkServerStatus(String i_URL)
        {
            url = i_URL;
        }
        int code;

        @Override
        protected String doInBackground(String... strings)
        {
            String output;
            try
            {
                URL siteURL = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) siteURL.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(3000);
                connection.connect();

                code = connection.getResponseCode();
                if (code == 200)
                    m_serverIsOnline = true;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            output = url + "\t\tStatus:" + m_serverIsOnline;

            return output;
        }
    }

    private void deleteRoute()
    {
        long routeIDToDelete = m_adapter.getItem(m_currentlyClickedListItem).getRoute().getRouteID();
        if(m_serverIsOnline)
            new DeleteRouteRequestFromDB(String.valueOf(routeIDToDelete), m_currentlyClickedListItem, this).execute();
        else
            removeItemFromListView(m_currentlyClickedListItem);
    }

    private void editRoute()
    {
        String currentRouteName = m_adapter.getItems().get(m_currentlyClickedListItem).getRoute().getRouteName();
        openEditRouteDialog(currentRouteName);
    }

    private String getCurrentDateAndTime(SimpleDateFormat i_dateFormat)
    {
        java.util.Date currentDate = Calendar.getInstance().getTime();
        String currentDateString = i_dateFormat.format(currentDate);

        return currentDateString;
    }

    private void initializeExistingRoutes()
    {
        initializeRouteList();
        m_localRoutesFile = new File(getFilesDir(), m_localRoutesFileName);

        if (m_serverIsOnline)
        {
            sendLocallySavedRoutesToServer();
            new GetRoutesFromDB(this, null).execute();
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Could not connect to server, unable to fetch existing routes.", Toast.LENGTH_LONG).show();
            loadLocalRoutesIfExist();
        }
    }

    private void initializeRouteList()
    {
        m_swipeListView = findViewById(R.id.listView);

        m_adapter = new ListAdapter(RoutesScreen.this);
        m_swipeListView.setAdapter(m_adapter);

        SwipeMenuCreator creator = new SwipeMenuCreator()
        {
            @Override
            public void create(SwipeMenu menu)
            {
                //create "Edit" item
                SwipeMenuItem editItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                editItem.setBackground(new ColorDrawable(Color.rgb(180,180,180)));
                // set item width
                editItem.setWidth(150);
                // create "delete" item
                // set a icon
                editItem.setIcon(R.drawable.ic_edit_black_24dp);
                // add to menu
                menu.addMenuItem(editItem);
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(217,83,79)));
                // set item width
                deleteItem.setWidth(150);
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete_black_24dp);
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
                m_currentlyClickedListItem = position;
                switch (index)
                {
                    case 0:
                        //edit
                        editRoute();
                        break;
                    case 1:
                        // delete
                        deleteRoute();
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });
    }

    private void initializeViews()
    {
        m_longDisplay = findViewById(R.id.textviewCurrentLong);
        m_latDisplay = findViewById(R.id.textviewCurrentLat);
        m_chronometer = findViewById(R.id.chronometer);

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

    private void initializeLocationModules()
    {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);

        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location)
            {

                DecimalFormat numberFormat = new DecimalFormat("#.000");

                String latitude = numberFormat.format(location.getLatitude());
                String longitude = numberFormat.format(location.getLongitude());

                m_longDisplay.setText(longitude);
                m_latDisplay.setText(latitude);
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) { }
            @Override
            public void onProviderEnabled(String provider) { }
            @Override
            public void onProviderDisabled(String provider) { }
        };

        try
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
        catch (SecurityException e)
        {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void loadLocalRoutesIfExist()
    {
        if(m_localRoutesFile.exists())
        {
            try
            {
                FileInputStream fileInputStream = openFileInput(m_localRoutesFileName);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                StringBuilder stringBuilder = new StringBuilder();
                String fileContentLineByLine;

                while((fileContentLineByLine = reader.readLine()) != null)
                {
                    stringBuilder.append(fileContentLineByLine);
                }


                JSONArray routesFromFile = new JSONArray(stringBuilder.toString());
                for (int i = 0; i < routesFromFile.length(); i++)
                {
                    JSONObject currentRouteString = routesFromFile.getJSONObject(i);
                    Route currentRoute = gson.fromJson(currentRouteString.get("m_route").toString(), Route.class);
                    m_adapter.add(new ListItem(currentRoute));
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        ArrayList<ListItem> routes_list = m_adapter.getItems();

        if(!m_serverIsOnline) {
            FileOutputStream fileOutputStream = null;
            try {
                m_localRoutesFile.createNewFile();
                JSONArray routesArray = new JSONArray(new Gson().toJson(routes_list));
                fileOutputStream = openFileOutput(m_localRoutesFileName, MODE_PRIVATE);
                fileOutputStream.write(routesArray.toString().getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void openEditRouteDialog(String i_currentRouteName)
    {
        Bundle bundle = new Bundle();
        EditRouteDialog editDialog = new EditRouteDialog();

        bundle.putString("currentRouteName", i_currentRouteName);
        editDialog.setArguments(bundle);

        editDialog.show(getSupportFragmentManager(), "Edit Dialog");
    }


    public void removeItemFromListView(int i_indexOfListItemToRemove)
    {
        m_adapter.remove(i_indexOfListItemToRemove);
    }

    private void sendLocallySavedRoutesToServer()
    {
        if(m_localRoutesFile.exists() && m_localRoutesFile.length() != 0)
        {
            try
            {
                FileInputStream fileInputStream = openFileInput(m_localRoutesFileName);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                StringBuilder stringBuilder = new StringBuilder();
                String fileContentLineByLine;

                while((fileContentLineByLine = reader.readLine()) != null)
                {
                    stringBuilder.append(fileContentLineByLine);
                }

                JSONArray routesFromFile = new JSONArray(stringBuilder.toString());
                for (int i = 0; i < routesFromFile.length(); i++)
                {
                    JSONObject currentRouteString = routesFromFile.getJSONObject(i);
                    Route currentRoute = gson.fromJson(currentRouteString.get("m_route").toString(), Route.class);
                    new SendRouteToAddToDB(currentRoute, this).execute().get();
                }
                m_localRoutesFile.delete();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void textviewRouteNameOnClick(View v) ///clears text on click
    {
        v = (EditText) findViewById(R.id.textViewRouteName);
        ((EditText) v).setText(null);
    }

    private void updateRoute() {
        Coordinate newCoord = new Coordinate(m_latDisplay.getText().toString(), m_longDisplay.getText().toString());
        m_routeToAdd.addCoordinateToRoute(newCoord);
    }

    @Override
    public void updateListItemName(String i_newRouteName)
    {
        ListItem selectedItem =  m_adapter.getItems().get(m_currentlyClickedListItem);
        selectedItem.getRoute().setRouteName(i_newRouteName);
        m_adapter.updateItemInDataSource(i_newRouteName, m_currentlyClickedListItem);
        if(m_serverIsOnline)
            new SendRouteUpdateToDB(String.valueOf(selectedItem.getRoute().getRouteID()), i_newRouteName).execute();
    }
}
