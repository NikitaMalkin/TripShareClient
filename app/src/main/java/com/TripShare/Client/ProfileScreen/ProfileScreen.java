package com.TripShare.Client.ProfileScreen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.TripShare.Client.Common.ActivityWithNavigationDrawer;
import android.widget.Toast;
import com.TripShare.Client.Common.DrawerAdapter;
import com.TripShare.Client.Common.Post;
import com.TripShare.Client.CommunicationWithServer.GetPostsFromDB;
import com.TripShare.Client.PostFullScreen.PostFullScreen;
import com.TripShare.Client.R;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProfileScreen extends ActivityWithNavigationDrawer implements GetPostsFromDB.AddAllItemsToListViewListener, PostsAdapter.shareButtonClickedListener, PostsAdapter.mapButtonClickedListener
{
    private ArrayList<PostItem> m_posts;
    private PostsAdapter m_PostAdapter;
    private DrawerAdapter m_drawerAdapter;
    int m_firstPositionToRetrieve;
    private RecyclerView m_drawerRecyclerView;
    Gson gson = new Gson();
    private RecyclerView.LayoutManager m_layoutManager;
    private ActionBarDrawerToggle m_DrawerToggle;
    private DrawerLayout m_DrawerLayout;
    private String m_ActivityTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);
        setActivityTitle("Profile");

        RecyclerView Posts = findViewById(R.id.profileScreen_recyclerView);

        m_firstPositionToRetrieve = 0;
        m_posts = new ArrayList<>();
        // Create adapter passing in the sample user data
        PostsAdapter adapter = new PostsAdapter(m_posts, this, this);
        // Attach the adapter to the recyclerview to populate items
        Posts.setAdapter(adapter);
        m_PostAdapter = adapter;

        // Initialize contacts
        // m_posts = PostItem.getPosts(); //TODO this method is responsible for gathering the first couple posts
        try
        {
            new GetPostsFromDB(this, 0, m_firstPositionToRetrieve).execute().get();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        // Set layout manager to position the items
        Posts.setLayoutManager(new LinearLayoutManager(this));

        //Posts RecyclerView separator initialization
        DividerItemDecoration decoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        decoration.setDrawable(getDrawable(R.drawable.shadow));
        Posts.addItemDecoration(decoration);

        //Application Drawer initialization
        initializeDrawerLayout();
    }

    public void imageButton_refreshPostsOnClick(View view)
    {
        //PostItem itemToAdd = new PostItem("Some Random Post Name", "Some very random post description but it is longer a little bit... It all began when Sivan and Nikita wanted to make a cool application...", ContextCompat.getDrawable(getApplicationContext(), R.drawable.post_thumbnail_sample));
        //m_posts.add(itemToAdd);
        //m_adapter.notifyDataSetChanged();

        // retrieve another 5 posts from DB and get from server.
        new GetPostsFromDB(this, 0, m_firstPositionToRetrieve).execute();
    }

    @Override
    public void addAllItemsToView(final String i_body)
    {
        runOnUiThread(new Runnable()
        {

            @Override
            public void run()
            {
                if(i_body.equals("[]"))
                    Toast.makeText(getApplicationContext(), "No more posts to load.", Toast.LENGTH_LONG).show();
                else
                {
                    try
                    {
                        JSONArray jsonArr = new JSONArray(i_body);
                        for (int i = 0; i < jsonArr.length(); i++) {
                            JSONObject jsonObj = jsonArr.getJSONObject(i);
                            Post post = new Gson().fromJson(jsonObj.toString(), Post.class);
                            addItemToListView(post);
                            m_firstPositionToRetrieve++;
                        }
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void addItemToListView(Post i_postToAdd)
    {
        // set the new route in the list
        // TODO: change the default picture to an acutal picture from the route if exists
        PostItem itemToAdd = new PostItem(i_postToAdd, ContextCompat.getDrawable(getApplicationContext(), R.drawable.post_thumbnail_sample));
        m_posts.add(itemToAdd);
        m_PostAdapter.notifyDataSetChanged();
    }

    @Override
    public void onShareButtonClick(int i_position, View i_view)
    {
        Intent postFullScreen = new Intent(ProfileScreen.this, PostFullScreen.class);
        // TODO: pass a the clicked post to the next activity
        postFullScreen.putExtra("Post",gson.toJson(m_posts.get(i_position).getPost()));
        postFullScreen.putExtra("isShowScreenShotButton", true);
        startActivity(postFullScreen);
    }

    @Override
    public void onMapButtonClick(int i_position, View i_view)
    {
        Intent postFullScreen = new Intent(ProfileScreen.this, PostFullScreen.class);
        postFullScreen.putExtra("isShowScreenShotButton", false);
        startActivity(postFullScreen);
    }
}
