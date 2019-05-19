package com.example.routesmanagementscreen;

import android.content.Context;
import android.content.Intent;
import android.graphics.*;
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
import android.widget.TextView;
import android.util.Base64;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.gson.Gson;

import org.angmarch.views.NiceSpinner;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
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
    implements OnMapReadyCallback , GoogleMap.OnMarkerClickListener, AddPhotoDialog.AttachImageToCoordinateListener , AddNoteDialog.AttachNoteToCoordinateListener
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
        initializeViews();
    }

    @Override
    public void onMapReady(GoogleMap map)
    {
        m_map = map;
        m_map.setOnMarkerClickListener(this);
        initializeMap();
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
        m_markerIcon = getBitmapFromVectorDrawable(getApplicationContext(), R.drawable.ic_marker_on_map); //converting image from vector to bitmap

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void initializeSpinnerWithRoutes()
    {
        m_adapter = new SpinnerAdapter(PostCreationScreen.this);
        m_spinner = (NiceSpinner)findViewById(R.id.spinner);
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
        new GetRoutesFromDB().execute();
    }

    private void initializeMap()
    {
        m_polyline = new PolylineOptions();
        m_polyline.pattern(m_pattern);
        m_markers = new ArrayList<>();
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

    public void addImageMarkerAndConvertStringToBitmap(String i_imageString, LatLng i_coordinateToAttachTo)
    {
        byte[] decodedString = Base64.decode(i_imageString, Base64.DEFAULT);
        Bitmap imageToShow = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        Bitmap smallerMarker = Bitmap.createScaledBitmap(imageToShow, 100, 100, false);
        m_map.addMarker(new MarkerOptions().position(i_coordinateToAttachTo).icon(BitmapDescriptorFactory.fromBitmap(smallerMarker))).setAnchor(1f, 1f);
    }

    public void OnAddRouteButtonClick(View view)
    {
        Intent routeCreationScreen = new Intent(PostCreationScreen.this, MainActivity.class);
        startActivity(routeCreationScreen);
    }

    public void OnPostButtonClick(View view)
    {
        TextView postTitle = findViewById(R.id.post_title_editText);
        TextView postDescription = findViewById(R.id.post_description_editText);
        long routeID = m_adapter.getItems().get(m_spinner.getSelectedIndex()).getRoute().getRouteID();

        m_postToAdd = new Post(0, postTitle.getText().toString(), postDescription.getText().toString());
        m_postToAdd.setRouteID(routeID);

        // Send Post to Server and save in DB.
        new SendPostToAddToDB().execute();
        new UpdateRouteInDB().execute();
        // TODO: update the route and coordinates as well
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

    public void showImageNearCoordinate(Bitmap i_imageToAttach)
    {
        LatLng coordinateToAttachTo = m_markers.get(m_currentCoordinateIndex).getPosition();
        Bitmap smallerMarker = Bitmap.createScaledBitmap(i_imageToAttach, 100, 100, false);
        m_map.addMarker(new MarkerOptions().position(coordinateToAttachTo).icon(BitmapDescriptorFactory.fromBitmap(smallerMarker))).setAnchor(1f, 1f);
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
            m_spinner.setAdapter(m_adapter);
            settingTheDefaultValueForTheSpinner();
            m_adapter.notifyDataSetChanged();
        }
    }

    public void settingTheDefaultValueForTheSpinner()
    {
        Route defaultValue = new Route(0);
        defaultValue.setRouteName("Choose Route...");
        m_adapter.getItems().add(0, new SpinnerItem(defaultValue));
        m_spinner.setAdapter(m_adapter);
        m_spinner.setSelectedIndex(0);
    }

    private class SendPostToAddToDB extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String... Args) {
            String output = null;

            try {
                // convert the object we want to send to the server
                //  to a json format and create an entity from it
                String postInJsonFormat = convertToJson(m_postToAdd);
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

    private class UpdateRouteInDB extends AsyncTask<String, Integer, String>
    {
        protected String doInBackground(String... Args)
        {
            String output = null;
            String coordinateInJsonFormat;

            try
            {
                //Getting the Updated Route to send its coordinates to the server.
                SpinnerItem itemSelected = m_adapter.getItems().get(m_spinner.getSelectedIndex());
                Route updatedRoute = itemSelected.getRoute();
                List<Coordinate> routeCoordinates = updatedRoute.getRouteCoordinates();

                // Send the post body
                for (Coordinate coord: routeCoordinates)
                {
                    if (coord.getAddition() != null)
                    {
                        // This is getting the url from the string we passed in
                        URL url = new URL("http://10.0.2.2:8080/SaveRouteToDB/CoordinateUpdateServlet");

                        // Create the urlConnection
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                        urlConnection.setDoInput(true);
                        urlConnection.setDoOutput(true);
                        urlConnection.setRequestProperty("Content-Type", "application/json");
                        urlConnection.setRequestMethod("POST");

                        // convert the object we want to send to the server
                        //  to a json format and create an entity from it
                        coordinateInJsonFormat = convertToJson(coord);
                        OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                        writer.write(coordinateInJsonFormat);
                        writer.flush();

                        int statusCode = urlConnection.getResponseCode();

                        if (statusCode == 200)
                        {
                            InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                            String response = convertInputStreamToString(inputStream);
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

    private String convertInputStreamToString(InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public String convertToJson(Object i_objectToTranslate)
    {
        Gson gson = new Gson();
        return gson.toJson(i_objectToTranslate);
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
        // Getting the pressed coordinate index
        m_currentCoordinateIndex = m_markers.indexOf(marker);
        DialogFragment dialog = new ChooseAdditionDialog();
        dialog.show(getSupportFragmentManager(), "Choose Addition");
        return true;
    }
}