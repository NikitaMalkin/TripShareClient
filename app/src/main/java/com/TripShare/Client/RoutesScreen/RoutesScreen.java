package com.TripShare.Client.RoutesScreen;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import com.TripShare.Client.CommunicationWithServer.SendRouteToAddToDB;
import com.TripShare.Client.CommunicationWithServer.SendRouteUpdateToDB;
import com.TripShare.Client.Common.ActivityWithNavigationDrawer;
import com.TripShare.Client.PostCreationScreen.PostCreationScreen;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.TripShare.Client.R;
import com.TripShare.Client.Common.Coordinate;
import com.TripShare.Client.Common.Route;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class RoutesScreen extends ActivityWithNavigationDrawer implements EditRouteDialog.EditDialogListener, DeleteRouteRequestFromDB.RemoveItemFromListViewListener, SendRouteToAddToDB.AddItemToListViewListener
{
    private Route m_routeToAdd;
    private Chronometer m_chronometer;
    private TextView m_longDisplay;
    private TextView m_latDisplay;
    private Handler m_handler;
    private ListAdapter m_adapter;
    private int m_currentlyClickedListItem;
    private SwipeMenuListView m_swipeListView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes_screen);
        setActivityTitle("Manage your routes");

        // If server is offline, we want to prevent the user from navigating away from the Routes Screen, since this is the only offline functionality available.
        if (ApplicationManager.getIsServerOnline())
        {
            initializeDrawerLayout();
        }
        else
        {
            initializeOfflineDrawerLayout();
        }

            initializeViews();
            initializeLocationModules();
            initializeRouteList();
            initializeExistingRoutes();

    }

    private void initializeExistingRoutes()
    {
        ArrayList<Route> routes = ApplicationManager.getUserRoutes();

        for (int i = 0; i < routes.size(); i++) {
            addItemToListView(routes.get(i));
        }

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

        if (ApplicationManager.getIsServerOnline())
            new SendRouteToAddToDB(m_routeToAdd, this).execute(); // Send the route to the server to save in the DB
        else
        {
            addItemToListView(m_routeToAdd);
            ApplicationManager.addUserRoute(m_routeToAdd);
        }

        ApplicationManager.hideKeyboardFrom(getApplicationContext(), findViewById(R.id.layout_main));
        Toast.makeText(getApplicationContext(), "Route saved successfully", Toast.LENGTH_LONG).show();
    }

    public void buttonRecordRouteClicked(View view)
    {
        m_routeToAdd = new Route(ApplicationManager.getLoggedInUser().getID());

        view.setVisibility(view.GONE);
        View alternateView = findViewById(R.id.view_recording);

        alternateView.setVisibility(View.VISIBLE);

        m_handler = new Handler();  /// initializes m_handler so that every 10 seconds it invokes updateRoute() method
        final int delay = 10000;

        m_handler.postDelayed(new Runnable() {
            public void run() {
                m_handler.postDelayed(this, delay);
                updateRoute();

            }
        }, delay);


        m_chronometer = findViewById(R.id.chronometer);

        m_chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener(){
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long time = SystemClock.elapsedRealtime() - chronometer.getBase();
                int h   = (int)(time /3600000);
                int m = (int)(time - h*3600000)/60000;
                int s= (int)(time - h*3600000- m*60000)/1000 ;
                String t = (h < 10 ? "0"+h: h)+":"+(m < 10 ? "0"+m: m)+":"+ (s < 10 ? "0"+s: s);
                chronometer.setText(t);
            }
        });
        m_chronometer.setBase(SystemClock.elapsedRealtime());
        m_chronometer.setText("00:00:00");
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

    private void deleteRoute()
    {
        long routeIDToDelete = m_adapter.getItem(m_currentlyClickedListItem).getRoute().getRouteID();
        if(ApplicationManager.getIsServerOnline())
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

                SwipeMenuItem createPost = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                createPost.setBackground(new ColorDrawable(Color.rgb(142, 196, 135)));
                // set item width
                createPost.setWidth(150);
                // set a icon
                createPost.setIcon(R.drawable.ic_publish_black_24dp);
                // add to menu
                menu.addMenuItem(createPost);
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
                    case 2:
                        // createPost
                        takeRouteToPostCreationScreen();
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });
    }

    private void takeRouteToPostCreationScreen()
    {
        Intent intent = new Intent(this, PostCreationScreen.class);
        intent.putExtra("RouteIndexToSelect", m_currentlyClickedListItem);
        startActivity(intent);
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

    @Override
    protected void onStop()
    {
        super.onStop();
        if(!ApplicationManager.getIsServerOnline()) {
            FileOutputStream fileOutputStream = null;
            try {
                ApplicationManager.getRoutesFile().createNewFile();
                JSONArray routesArray = new JSONArray(new Gson().toJson(ApplicationManager.getUserRoutes()));
                fileOutputStream = openFileOutput(ApplicationManager.getRoutesFileName(), getBaseContext().MODE_PRIVATE);
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

    private void openEditRouteDialog(String i_currentRouteName)
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
        ApplicationManager.getUserRoutes().remove(i_indexOfListItemToRemove);
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
        ApplicationManager.getUserRoutes().get(m_currentlyClickedListItem).setRouteName(i_newRouteName);
        m_adapter.updateItemInDataSource(i_newRouteName, m_currentlyClickedListItem);
        if(ApplicationManager.getIsServerOnline())
            new SendRouteUpdateToDB(String.valueOf(selectedItem.getRoute().getRouteID()), i_newRouteName).execute();
    }
}
