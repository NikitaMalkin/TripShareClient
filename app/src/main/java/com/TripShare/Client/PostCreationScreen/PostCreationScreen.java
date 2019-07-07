package com.TripShare.Client.PostCreationScreen;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import com.TripShare.Client.Common.*;
import com.TripShare.Client.CommunicationWithServer.GetRoutesFromDB;
import com.TripShare.Client.CommunicationWithServer.SendPostToAddToDB;
import com.TripShare.Client.CommunicationWithServer.UpdateRouteInDB;
import com.TripShare.Client.R;
import com.TripShare.Client.RoutesScreen.RoutesScreen;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.gson.Gson;
import org.angmarch.views.NiceSpinner;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PostCreationScreen extends ActivityWithNavigationDrawer
    implements OnMapReadyCallback , GoogleMap.OnMarkerClickListener, AddPhotoDialog.AttachImageToCoordinateListener, AddNoteDialog.AttachNoteToCoordinateListener, GetRoutesFromDB.AddAllItemsToListViewListener, GetRoutesFromDB.UpdateRelevantView
{
    private static final PatternItem DOT = new Dot();
    private static final PatternItem GAP = new Gap(20);

    private Post m_postToAdd;
    private NiceSpinner m_spinner;
    private SpinnerAdapter m_adapter;
    private GoogleMap m_map;
    private PolylineOptions m_polyline;
    private List<PatternItem> m_pattern = Arrays.asList(DOT, GAP);
    private List<Marker> m_markers;
    private Bitmap m_markerIcon;
    private int m_currentCoordinateIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_creation_screen);
        setActivityTitle("Create a new post");
        initializeDrawerLayout();
        initializeViews();
        ApplicationManager.hideKeyboardFrom(getApplicationContext(), findViewById(R.id.post_creation_screen_layout_main));
    }

    private void addItemToSpinner(Route i_route)
    {
        m_adapter.add(new SpinnerItem(i_route));
    }

    public void addAllItemsToView(final String i_body)
    {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                try {
                    JSONArray jsonArr = new JSONArray(i_body);
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

    @Override
    public void attachNoteToRelevantCoordinate(String i_noteToAttach)
    {
        // Attaching the image to the coordinate.
        SpinnerItem itemSelected = m_adapter.getItems().get(m_spinner.getSelectedIndex());
        itemSelected.getRoute().getRouteCoordinates().get(m_currentCoordinateIndex).setNote(i_noteToAttach);
        Marker relevantMarker =  m_markers.get(m_currentCoordinateIndex);
        relevantMarker.setTitle(i_noteToAttach);
        //relevantMarker.showInfoWindow();
    }

    @Override
    public void attachImageToRelevantCoordinate(Bitmap i_imageToAttach)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        i_imageToAttach.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        String imageString = Base64.encodeToString(byteArray, Base64.DEFAULT);

        // Attaching the image to the coordinate.
        SpinnerItem itemSelected = m_adapter.getItems().get(m_spinner.getSelectedIndex());
        itemSelected.getRoute().getRouteCoordinates().get(m_currentCoordinateIndex).setImageString(imageString);

        showImageNearCoordinate(i_imageToAttach);
    }

    public void addImageMarkerAndConvertStringToBitmap(String i_imageString, LatLng i_coordinateToAttachTo)
    {
        byte[] decodedString = Base64.decode(i_imageString, Base64.DEFAULT);
        Bitmap imageToShow = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        Bitmap smallerMarker = Bitmap.createScaledBitmap(imageToShow, 100, 100, false);
        m_map.addMarker(new MarkerOptions().position(i_coordinateToAttachTo).icon(BitmapDescriptorFactory.fromBitmap(smallerMarker))).setAnchor(1f, 1f);
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

    private void initializeViews() //!!!!!!!!!!!!!!!!!!!!  TODO TODO TODO
    {
        initializeSpinnerWithRoutes();
        m_markerIcon = getBitmapFromVectorDrawable(getApplicationContext(), R.drawable.ic_marker_on_map); //converting image from vector to bitmap

        //MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        //mapFragment.getMapAsync(this);

        MapFragment mapFragment = MapFragment.newInstance();
        mapFragment.getMapAsync(this);
    }

    private void initializeSpinnerWithRoutes()
    {
        m_adapter = new SpinnerAdapter(PostCreationScreen.this);
        m_spinner = (NiceSpinner) findViewById(R.id.spinner);
        m_spinner.setDropDownListPaddingBottom(10);

        m_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                m_map.clear();
                initializeMap();

                String spinnerItemString = ((TextView)view).getText().toString();
                if (!((spinnerItemString.equals("Choose Route..."))))
                    showRouteOnMap(spinnerItemString);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        new GetRoutesFromDB(this, this).execute();
    }

    private void initializeMap()
    {
        m_polyline = new PolylineOptions();
        m_polyline.pattern(m_pattern);
        m_markers = new ArrayList<>();
    }

    @Override
    public void onMapReady(GoogleMap map)
    {
        m_map = map;
        m_map.setOnMarkerClickListener(this);
        initializeMap();
    }


    public void OnAddRouteButtonClick(View view)
    {
        Intent routeCreationScreen = new Intent(PostCreationScreen.this, RoutesScreen.class);
        startActivity(routeCreationScreen);
    }

    @Override
    public boolean onMarkerClick(final Marker marker)
    {
        // Getting the pressed coordinate index
        m_currentCoordinateIndex = m_markers.indexOf(marker);
        DialogFragment dialog = new ChooseAdditionDialog();
        dialog.show(getSupportFragmentManager(), "Choose Addition");
        return true;
    }

    public void OnPostButtonClick(View view)
    {
        TextView postTitle = findViewById(R.id.post_title_editText);
        TextView postDescription = findViewById(R.id.post_description_editText);
        long routeID = m_adapter.getItems().get(m_spinner.getSelectedIndex()).getRoute().getRouteID();

        m_postToAdd = new Post(0, postTitle.getText().toString(), postDescription.getText().toString());
        m_postToAdd.setRouteID(routeID);

        // Send Post to Server and save in DB.
        new SendPostToAddToDB(m_postToAdd).execute();

        // Update Relevant Route.
        SpinnerItem itemSelected = m_adapter.getItems().get(m_spinner.getSelectedIndex());
        Route updatedRoute = itemSelected.getRoute();
        new UpdateRouteInDB(updatedRoute).execute();
        // TODO: update the route and coordinates as well
    }

    private void showRouteOnMap(String i_spinnerItemString)
    {
        SpinnerItem clickedItem = (SpinnerItem) m_adapter.getItemByName(i_spinnerItemString);

        List<Coordinate> routeCoordinates = clickedItem.getRoute().getRouteCoordinates();
        LatLng currLatLng;

        for (Coordinate coord : routeCoordinates)
        {
            currLatLng = new LatLng(Double.valueOf(coord.getLatitude()), Double.valueOf(coord.getLongitude()));
            Marker marker;

            marker = m_map.addMarker(new MarkerOptions()
                    .position(currLatLng)
                    .icon(BitmapDescriptorFactory.fromBitmap(m_markerIcon)));
            marker.setAnchor(0.5f, 0.5f);
            m_map.addPolyline(m_polyline
                    .add(currLatLng)
                    .width(24)
                    .color(Color.rgb(100, 186, 105)));
            m_markers.add(marker);

            String coordinateString = coord.getImageString();
            if(coordinateString != null && !coordinateString.isEmpty())
            {
                addImageMarkerAndConvertStringToBitmap(coordinateString, marker.getPosition());
            }

            if(coord.getNote() != null)
            {
                marker.setTitle(coord.getNote());
            }
        }

        CameraUpdate center = CameraUpdateFactory.newLatLng(clickedItem.getCenterCoordinate());
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(13);

        m_map.moveCamera(center);
        m_map.animateCamera(zoom);
    }

    public void showImageNearCoordinate(Bitmap i_imageToAttach)
    {
        LatLng coordinateToAttachTo = m_markers.get(m_currentCoordinateIndex).getPosition();
        Bitmap smallerMarker = Bitmap.createScaledBitmap(i_imageToAttach, 100, 100, false);
        m_map.addMarker(new MarkerOptions().position(coordinateToAttachTo).icon(BitmapDescriptorFactory.fromBitmap(smallerMarker))).setAnchor(1f, 1f);
    }

    public void settingTheDefaultValueForTheSpinner()
    {
        Route defaultValue = new Route(0);
        defaultValue.setRouteName("Choose Route...");
        m_adapter.getItems().add(0, new SpinnerItem(defaultValue));
        m_spinner.setAdapter(m_adapter);
        m_spinner.setSelectedIndex(0);
    }

    public void updateRelevantViewWithSource()
    {
        m_spinner.setAdapter(m_adapter);
        settingTheDefaultValueForTheSpinner();
        m_adapter.notifyDataSetChanged();
    }

}