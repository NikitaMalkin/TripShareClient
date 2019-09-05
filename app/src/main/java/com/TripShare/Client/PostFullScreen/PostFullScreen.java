package com.TripShare.Client.PostFullScreen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.TripShare.Client.Common.User;
import com.TripShare.Client.CommunicationWithServer.GetRouteByID;
import com.TripShare.Client.PostCreationScreen.ChooseAdditionDialog;
import com.TripShare.Client.R;
import com.TripShare.Client.Common.Coordinate;
import com.TripShare.Client.Common.Post;
import com.TripShare.Client.Common.Route;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.gson.Gson;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PostFullScreen extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.SnapshotReadyCallback, GetRouteByID.SetRetrievedRoute,  GoogleMap.OnMarkerClickListener
{
    private static final PatternItem DOT = new Dot();
    private static final PatternItem GAP = new Gap(20);

    private Post m_postToPresent;
    private Route m_routeToPresent;
    private Button m_saveToGalleryButton;
    private TextView m_postName_textView;
    private TextView m_userName_textView;
    private TextView m_postDescription_textView;
    private TextView m_postLikesCount;
    private TextView m_postCommentCount;
    private ImageView m_tripShareAdImageView;
    private ImageView m_mapScreenShot;
    private GoogleMap m_map;
    private Bitmap m_markerIcon;
    private MapFragment m_mapFragment;
    private PolylineOptions m_polyline;
    private List<PatternItem> m_pattern = Arrays.asList(DOT, GAP);
    private List<Marker> m_markers;
    private View m_viewToScreenShot;
    private boolean m_isShowButton = true;
    private int m_currentCoordinateIndex;
    Gson gson = new Gson();

    public void addImageMarkerAndConvertStringToBitmap(String i_imageString, LatLng i_coordinateToAttachTo) {
        byte[] decodedString = Base64.decode(i_imageString, Base64.DEFAULT);
        Bitmap imageToShow = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        Bitmap smallerMarker = Bitmap.createScaledBitmap(imageToShow, 100, 100, false);
        m_map.addMarker(new MarkerOptions().position(i_coordinateToAttachTo).icon(BitmapDescriptorFactory.fromBitmap(smallerMarker))).setAnchor(1f, 1f);
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
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

    public LatLng getCenterCoordinate() {
        LatLng returnedValue = null;
        List<Coordinate> coordinateList = m_routeToPresent.getRouteCoordinates();
        Coordinate middleCoordinate = m_routeToPresent.getRouteCoordinates().get(coordinateList.size() / 2);
        returnedValue = new LatLng(Double.valueOf(middleCoordinate.getLatitude()), Double.valueOf(middleCoordinate.getLongitude()));

        return returnedValue;
    }

    private void initializeMap() {
        m_polyline = new PolylineOptions();
        m_polyline.pattern(m_pattern);
        m_markers = new ArrayList<>();

        showRouteOnMap();
    }

    private void initializeViews() {
        m_postName_textView.setText(m_postToPresent.getTitle());
        m_postDescription_textView.setText(m_postToPresent.getDescription());
        m_mapScreenShot.setImageDrawable(null);
        // Temp TODO: add the Relevant Info

        // Add the user Info
        m_userName_textView.setText("by " + m_postToPresent.getUserFirstName() + " " + m_postToPresent.getUserLastName());
        m_postLikesCount.setText(String.valueOf(m_postToPresent.getLikeCount()));
        m_postCommentCount.setText(String.valueOf(m_postToPresent.getCommentCount()));

        m_mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        m_mapFragment.getMapAsync(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_post_full_screen);

        // Get the post to present from the last activity
        m_postToPresent = gson.fromJson(getIntent().getStringExtra("Post"), Post.class);
        m_isShowButton = (Boolean)getIntent().getSerializableExtra("isShowScreenShotButton");

        // Get Post Route From DB
        try { new GetRouteByID(this, m_postToPresent.getPostRoute()).execute().get(); }
        catch(Exception e) { e.printStackTrace(); }

        //setTempItem();
        setAllActivityItems();
        initializeViews();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        m_map = map;
        m_map.setOnMarkerClickListener(this);
        m_map.getUiSettings().setZoomControlsEnabled(true);
        initializeMap();
    }

    @Override
    public boolean onMarkerClick(final Marker marker)
    {
        // Getting the pressed coordinate index
        if(m_markers.indexOf(marker) != -1 && m_markers.indexOf(marker) < m_routeToPresent.getRouteCoordinates().size()) {
            m_currentCoordinateIndex = m_markers.indexOf(marker);
            DialogFragment dialog = new ShowCoordinateAdditionsDialog();
            Bundle args = new Bundle();
            args.putString("note", m_routeToPresent.getRouteCoordinates().get(m_currentCoordinateIndex).getNote());
            args.putString("image", m_routeToPresent.getRouteCoordinates().get(m_currentCoordinateIndex).getImageString());
            dialog.setArguments(args);
            dialog.show(getSupportFragmentManager(), "Coordinate Information");
        }
        return true;
    }

    @Override
    public void onSnapshotReady(Bitmap bitmap)
    {
        m_mapScreenShot.setImageBitmap(bitmap);

        Bitmap screenShot = takeScreenShot(m_viewToScreenShot);
        saveToGallery(screenShot);
    }

    private void saveScreenShotToGallery(View v) {
        m_saveToGalleryButton.setVisibility(View.INVISIBLE);
        m_tripShareAdImageView.setVisibility(View.VISIBLE);
        m_viewToScreenShot = v;
        m_map.snapshot(this);
    }

    private void saveToGallery(Bitmap m_screenShotToSave) {
        File sdcard = Environment.getExternalStorageDirectory();
        if (sdcard != null) {
            File mediaDir = new File(sdcard, "DCIM/Camera");
            if (!mediaDir.exists())
            {
                mediaDir.mkdirs();
            }
        }

        MediaStore.Images.Media.insertImage(getContentResolver(), m_screenShotToSave, "screenShotOfPost", "This photo is from TripShare App");
    }

    private void setAllActivityItems() {
        m_saveToGalleryButton = findViewById(R.id.button_save_to_gallery);
        m_postName_textView = findViewById(R.id.TripName_textview);
        m_userName_textView = findViewById(R.id.userName_textview);
        m_postDescription_textView = findViewById(R.id.Trip_description_textview);
        m_postLikesCount = findViewById(R.id.Like_count_textview);
        m_postCommentCount = findViewById(R.id.Comment_count_textView);
        m_tripShareAdImageView = findViewById(R.id.imageView_TripShare_Ad);
        m_mapScreenShot = findViewById(R.id.map_screenshot_imageView);
        m_markerIcon = getBitmapFromVectorDrawable(getApplicationContext(), R.drawable.ic_marker_on_map); //converting image from vector to bitmap

        if (!m_isShowButton) {
            m_saveToGalleryButton.setVisibility(View.INVISIBLE);
        }
        else
        {
            m_saveToGalleryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveScreenShotToGallery(v);
                }
            });
        }
    }

    @Override
    public void setRetrievedRoute(String i_body)
    {
        m_routeToPresent = gson.fromJson(i_body, Route.class);
    }

    private void showRouteOnMap() {
        List<Coordinate> routeCoordinates = m_routeToPresent.getRouteCoordinates();
        LatLng currLatLng;

        for (Coordinate coord : routeCoordinates) {
            currLatLng = new LatLng(Double.valueOf(coord.getLatitude()), Double.valueOf(coord.getLongitude()));
            Marker marker;

            marker = m_map.addMarker(new MarkerOptions()
                    .position(currLatLng)
                    .icon(BitmapDescriptorFactory.fromBitmap(m_markerIcon)));
            marker.setAnchor(0f, 0f);
            m_map.addPolyline(m_polyline
                    .add(currLatLng)
                    .width(30)
                    .color(Color.rgb(100, 186, 105)));
            m_markers.add(marker);

            String coordinateString = coord.getImageString();
            if (coordinateString != null && !coordinateString.isEmpty()) {
                addImageMarkerAndConvertStringToBitmap(coordinateString, marker.getPosition());
            }

            String coordNote = coord.getNote();
            if(coordNote!= null)
                addNoteIconNearMarker(marker.getPosition());
        }

        CameraUpdate center = CameraUpdateFactory.newLatLng(getCenterCoordinate());
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(10);

        m_map.moveCamera(center);
        m_map.animateCamera(zoom);
    }

    private void addNoteIconNearMarker( LatLng i_coordinateToAttachTo)
    {
        Bitmap iconBitmap = getBitmapFromVectorDrawable(getApplicationContext(), R.drawable.ic_note);
        Bitmap smallerMarker = Bitmap.createScaledBitmap(iconBitmap, 50, 50, false);
        m_map.addMarker(new MarkerOptions().position(i_coordinateToAttachTo).icon(BitmapDescriptorFactory.fromBitmap(smallerMarker))).setAnchor(1f, 0.3f);
    }

        private Bitmap takeScreenShot(View i_viewToScreenShot) {
        i_viewToScreenShot = i_viewToScreenShot.getRootView();
        i_viewToScreenShot.setDrawingCacheEnabled(true);
        i_viewToScreenShot.buildDrawingCache(true);
        Bitmap screenShot = Bitmap.createBitmap(i_viewToScreenShot.getDrawingCache());
        i_viewToScreenShot.setDrawingCacheEnabled(true);
        return screenShot;
    }
}