package com.TripShare.Client.Common;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.TripShare.Client.HomeScreen.HomeScreen;
import com.TripShare.Client.LoginScreen.LoginScreen;
import com.TripShare.Client.PostCreationScreen.PostCreationScreen;
import com.TripShare.Client.ProfileScreen.ProfileScreen;
import com.TripShare.Client.R;
import com.TripShare.Client.RoutesScreen.RoutesScreen;

import java.util.ArrayList;

public class ActivityWithNavigationDrawer extends AppCompatActivity {

    private DrawerAdapter m_drawerAdapter;
    private RecyclerView m_drawerRecyclerView;
    private RecyclerView.LayoutManager m_layoutManager;
    private ActionBarDrawerToggle m_DrawerToggle;
    private DrawerLayout m_DrawerLayout;
    private String m_ActivityTitle;
    private boolean m_isOfflineMode;

    protected void setActivityTitle(String i_title) //setter for activity title, initializeDrawerLayout() uses the title in order to initialize the action bar
    {
        m_ActivityTitle = i_title;
    }

    protected void initializeDrawerLayout() {
        m_isOfflineMode = false;

        //initialize profile picture, takes much time so it is in another thread
        fetchDrawerProfilePicture();

        //Application Drawer initialization
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        m_DrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        getSupportActionBar().setTitle(m_ActivityTitle);


        m_DrawerToggle = new ActionBarDrawerToggle(this, m_DrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(m_ActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        m_DrawerToggle.setDrawerIndicatorEnabled(true);
        m_DrawerLayout.setDrawerListener(m_DrawerToggle);

        //Drawer RecyclerView initialization
        m_drawerRecyclerView = (RecyclerView) findViewById(R.id.drawer_recyclerview);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        m_drawerRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        m_layoutManager = new LinearLayoutManager(this);
        m_drawerRecyclerView.setLayoutManager(m_layoutManager);

        //initialize user's name and lastname
        TextView text = findViewById(R.id.drawer_textView);
        text.setText(ApplicationManager.getLoggedInUser().getuserRealName() + " " + ApplicationManager.getLoggedInUser().getLastName());

        // specify an adapter
        ArrayList<DrawerItem> list = new ArrayList<DrawerItem>();
        list.add(new DrawerItem("Home", getDrawable(R.drawable.ic_home_black_24dp)));
        list.add(new DrawerItem("Profile", getDrawable(R.drawable.ic_account_box_black_24dp)));
        list.add(new DrawerItem("Create new route", getDrawable(R.drawable.ic_explore_black_24dp)));
        list.add(new DrawerItem("Create new post", getDrawable(R.drawable.ic_public_black_24dp)));
        list.add(new DrawerItem("Logout", getDrawable(R.drawable.ic_exit_to_app_black_24dp)));
        m_drawerAdapter = new DrawerAdapter(list);
        m_drawerRecyclerView.setAdapter(m_drawerAdapter);

    }

    protected void initializeOfflineDrawerLayout()
    {
        m_isOfflineMode = true;

        //Application Drawer initialization
        getSupportActionBar().setIcon(R.drawable.ic_signal_wifi_off_black_24dp);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("   " + m_ActivityTitle + " Offline");
        m_DrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
    }

    private void fetchDrawerProfilePicture()
    {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String imageString = ApplicationManager.getLoggedInUser().getImageString();
                if (imageString != null) //if user actually uploaded a custom picture, load it. otherwise we use the default picture
                {
                        byte[] decodedImageString = Base64.decode(imageString, Base64.DEFAULT);
                        Bitmap bitmapImage = BitmapFactory.decodeByteArray(decodedImageString, 0, decodedImageString.length);
                        ApplicationManager.setDrawerProfilePicture(bitmapImage);
                }
            }
        });
    }

    private void updateDrawerPictureView()
    {
        ImageView imageView = findViewById(R.id.drawer_userImage);

        if (ApplicationManager.getDrawerProfilePicture() == null)
        {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_iconfinder_icon));
        }
        else
        {
            imageView.setImageBitmap(ApplicationManager.getDrawerProfilePicture());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //update drawer profile picture UI
        updateDrawerPictureView();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        // Activate the navigation drawer toggle
        if (m_DrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (!m_isOfflineMode)
        {
            m_DrawerToggle.syncState();
        }
    }

    public void drawerItem_OnClick(View view) //method responsible for handling drawer menu item click events
    {
        TextView textview = view.findViewById(R.id.drawerItem_text);
        DrawerLayout layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        layout.closeDrawers();

        if (textview.getText() == "Logout")
        {
            ApplicationManager.DeleteUserSavedInfo((Activity) view.getContext());
            Intent intent = new Intent(this, LoginScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //this line clears the application activity stack, so that user can't logout and then press 'back' and be logged in again
            startActivity(intent);
        }

        else if (textview.getText() == "Home")
        {
            Intent intent = new Intent(this, HomeScreen.class);
            startActivity(intent);
        }

        else if (textview.getText() == "Create new route")
        {
            Intent intent = new Intent(this, RoutesScreen.class);
            startActivity(intent);
        }

        else if (textview.getText() == "Create new post")
        {
            Intent intent = new Intent(this, PostCreationScreen.class);
            startActivity(intent);
        }

        else if (textview.getText() == "Profile")
        {
            Intent intent = new Intent(this, ProfileScreen.class);
            startActivity(intent);
        }
    }
}
