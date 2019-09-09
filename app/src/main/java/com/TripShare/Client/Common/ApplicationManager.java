package com.TripShare.Client.Common;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.TripShare.Client.CommunicationWithServer.GetRoutesFromDB;
import com.TripShare.Client.CommunicationWithServer.SendRouteToAddToDB;
import com.TripShare.Client.LoginScreen.LoginScreen;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;

public final class ApplicationManager {
    private static User m_loggedInUser;
    private static Bitmap m_drawerProfilePicture;
    private static ArrayList<Route> m_userRoutes = new ArrayList<>();
    private static final String m_userLocalInfoFileName = "localInfo.json";
    private static File m_userLocalInfoFile;
    private static final String m_localRoutesFileName = "localRoutes.json";
    private static File m_localRoutesFile;
    private static Boolean m_serverIsOnline = true;

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

    public static void setDrawerProfilePicture(Bitmap i_picture) {
        m_drawerProfilePicture = i_picture;
    }

    public static Bitmap getDrawerProfilePicture() {
        return m_drawerProfilePicture;
    }

    public static void setLoggedInUser(User i_loggedInUser) {
        m_loggedInUser = i_loggedInUser;
    }

    public static User getLoggedInUser() {
        return m_loggedInUser;
    }

    public static File getRoutesFile() {
        return m_localRoutesFile;
    }

    public static String getRoutesFileName() {
        return m_localRoutesFileName;
    }

    public static void setUserRoutes(String i_body) {
        try {
            JSONArray jsonArr = new JSONArray(i_body);
            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject jsonObj = jsonArr.getJSONObject(i);
                Route route = new Gson().fromJson(jsonObj.toString(), Route.class);
                m_userRoutes.add(route);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Route> getUserRoutes() {
        return m_userRoutes;
    }

    public static void addUserRoute(Route i_route) {
        m_userRoutes.add(i_route);
    }

    public static void setIsServerOnline(Boolean i_serverStatus) {
        m_serverIsOnline = i_serverStatus;
    }

    public static Boolean getIsServerOnline() {
        return m_serverIsOnline;
    }

    public static ArrayList<String> getTagList() {
        ArrayList<String> labels = new ArrayList<>();

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

    public static void retrieveUserInfo(Activity i_activity) {
        retrieveApplicationManagerData(i_activity);

        if (!m_serverIsOnline) {
            if (m_loggedInUser != null)
            {
                retrieveUserRoutes(i_activity);
                ((LoginScreen)i_activity).offlineModeConfiguration();
            }
        }
        else if (m_loggedInUser != null && m_loggedInUser.getStringUserName() != null && m_loggedInUser.getPassword() != null) {
            ((LoginScreen) i_activity).validate(m_loggedInUser.getStringUserName(), m_loggedInUser.getPassword(), true);
        }

    }

    private static void retrieveApplicationManagerData(Activity i_activity) {
        m_userLocalInfoFile = new File(i_activity.getFilesDir(), m_userLocalInfoFileName);

        if (m_userLocalInfoFile.exists() && m_userLocalInfoFile.length() != 0) {
            try {
                FileInputStream fileInputStream = i_activity.openFileInput(m_userLocalInfoFileName);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                StringBuilder stringBuilder = new StringBuilder();
                String fileContentLineByLine;

                while ((fileContentLineByLine = reader.readLine()) != null) {
                    stringBuilder.append(fileContentLineByLine);
                }

                JSONArray userInfo = new JSONArray(stringBuilder.toString());
                m_loggedInUser = new User();
                m_loggedInUser.setUserID(Long.valueOf(userInfo.getString(0)));
                m_loggedInUser.setUserName(userInfo.getString(1));
                m_loggedInUser.setPassword(userInfo.getString(2));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void retrieveUserRoutes(Activity i_activity) {
        m_localRoutesFile = new File(i_activity.getFilesDir(), m_localRoutesFileName);

        if (ApplicationManager.getIsServerOnline()) {
            sendLocallySavedRoutesToServer(i_activity);
            new GetRoutesFromDB().execute();
        } else {
            //Toast.makeText(i_activity.getApplicationContext(), "Could not connect to server, unable to fetch existing routes.", Toast.LENGTH_LONG).show();
            loadLocalRoutesIfExist(i_activity);
        }
    }

    private static void sendLocallySavedRoutesToServer(Activity i_activity) {
        if (m_localRoutesFile.exists() && m_localRoutesFile.length() != 0) {
            try {
                FileInputStream fileInputStream = i_activity.openFileInput(m_localRoutesFileName);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                StringBuilder stringBuilder = new StringBuilder();
                String fileContentLineByLine;

                while ((fileContentLineByLine = reader.readLine()) != null) {
                    stringBuilder.append(fileContentLineByLine);
                }

                JSONArray routesFromFile = new JSONArray(stringBuilder.toString());
                for (int i = 0; i < routesFromFile.length(); i++) {
                    JSONObject currentRouteString = routesFromFile.getJSONObject(i);
                    Route currentRoute = new Gson().fromJson(currentRouteString.toString(), Route.class);
                    new SendRouteToAddToDB(currentRoute, null).execute().get();
                }
                m_localRoutesFile.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void loadLocalRoutesIfExist(Activity i_activity) {
        if (m_localRoutesFile.exists()) {
            try {
                FileInputStream fileInputStream = i_activity.openFileInput(m_localRoutesFileName);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                StringBuilder stringBuilder = new StringBuilder();
                String fileContentLineByLine;

                while ((fileContentLineByLine = reader.readLine()) != null) {
                    stringBuilder.append(fileContentLineByLine);
                }


                JSONArray routesFromFile = new JSONArray(stringBuilder.toString());
                for (int i = 0; i < routesFromFile.length(); i++) {
                    Route currentRoute = new Gson().fromJson(routesFromFile.get(i).toString(), Route.class);
                    m_userRoutes.add(currentRoute);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveUserInfoLocally(Activity i_activity) {
        FileOutputStream fileOutputStream = null;
        try {
            m_userLocalInfoFile.delete();
            m_userLocalInfoFile.createNewFile();
            JSONArray userInfo = new JSONArray();
            userInfo.put(m_loggedInUser.getID());
            userInfo.put(m_loggedInUser.getStringUserName());
            userInfo.put(m_loggedInUser.getPassword());

            fileOutputStream = i_activity.openFileOutput(m_userLocalInfoFileName, i_activity.getBaseContext().MODE_PRIVATE);
            fileOutputStream.write(userInfo.toString().getBytes());
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

    public static void DeleteUserSavedInfo(Activity i_activity)
    {
        m_userLocalInfoFile = new File(i_activity.getFilesDir(), m_userLocalInfoFileName);
        m_userLocalInfoFile.delete();
        m_loggedInUser = null;
        m_userRoutes = new ArrayList<>();
        m_drawerProfilePicture = null;
    }
}
