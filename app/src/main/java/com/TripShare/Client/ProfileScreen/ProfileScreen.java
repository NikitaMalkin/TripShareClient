package com.TripShare.Client.ProfileScreen;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.TripShare.Client.Common.ActivityWithNavigationDrawer;
import com.TripShare.Client.Common.DrawerAdapter;
import com.TripShare.Client.R;

import java.util.ArrayList;

public class ProfileScreen extends ActivityWithNavigationDrawer {

    private ArrayList<PostItem> m_posts;
    private PostsAdapter m_PostsAdapter;
    private DrawerAdapter m_drawerAdapter;
    private RecyclerView m_drawerRecyclerView;
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

        // Initialize posts
        m_posts = PostItem.getPosts(); //TODO this method is responsible for gathering the first couple posts

        // Create adapter passing in the sample user data
        PostsAdapter adapter = new PostsAdapter(m_posts);
        // Attach the adapter to the recyclerview to populate items
        Posts.setAdapter(adapter);
        m_PostsAdapter = adapter;
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
        PostItem itemToAdd = new PostItem("Trip to the Carmel", "So yesterday we woke up and thought to ourselves what a wonderful world we live in!", ContextCompat.getDrawable(getApplicationContext(), R.drawable.post_thumbnail_sample));
        PostItem itemToAdd2 = new PostItem("Coffee in the middle of the desert", "You know what is better than drinking your morning coffee in your kitchen? Drinking it on a cliff!", ContextCompat.getDrawable(getApplicationContext(), R.drawable.thumbnail2));
        m_posts.add(itemToAdd);
        m_posts.add(itemToAdd2);
        m_PostsAdapter.notifyDataSetChanged();
    }
}
