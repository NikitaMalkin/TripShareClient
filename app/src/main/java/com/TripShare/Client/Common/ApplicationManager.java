package com.TripShare.Client.Common;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;

import java.util.ArrayList;

public final class ApplicationManager {

    private static boolean m_firstHomePageLaunch = true;
    private static User m_loggedInUser;
    private static Bitmap m_drawerProfilePicture;


    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void setDrawerProfilePicture(Bitmap i_picture)
    {
        m_drawerProfilePicture = i_picture;
    }

    public static Bitmap getDrawerProfilePicture()
    {
        return m_drawerProfilePicture;
    }

    public static void setLoggedInUser(User i_loggedInUser)
    {
        m_loggedInUser = i_loggedInUser;
    }

    public static User getLoggedInUser()
    {
        return m_loggedInUser;
    }

    public static void setHomePageFirstTimeAccessed()  //this method is called once the homepage is accessed for the first time
    {
        m_firstHomePageLaunch = false;
    }

    public static boolean getHomePageFirstLaunch()
    {
        return m_firstHomePageLaunch;
    }

    public static ArrayList<String> getTagList()
    {
        ArrayList<String> labels = new ArrayList<>();

        //TODO try to find a better way to get the tag names, maybe get them from the server
        labels.add("Forest");
        labels.add("Desert");
        labels.add("City");
        labels.add("Hills");
        labels.add("Mountains");
        labels.add("Trekking");
        labels.add("Camping");
        labels.add("Lake");
        labels.add("River");
        labels.add("Climbing");

        return labels;
    }
}
