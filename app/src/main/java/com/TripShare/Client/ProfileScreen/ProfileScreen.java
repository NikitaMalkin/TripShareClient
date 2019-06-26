package com.TripShare.Client.ProfileScreen;

import android.graphics.drawable.VectorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.TripShare.Client.R;

import java.util.ArrayList;

public class ProfileScreen extends AppCompatActivity {

    ArrayList<PostItem> m_posts;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_profle_screen);

        RecyclerView Posts = findViewById(R.id.profileScreen_recyclerView);

        // Initialize contacts
        m_posts = PostItem.createSampleData();
        // Create adapter passing in the sample user data
        PostsAdapter adapter = new PostsAdapter(m_posts);
        // Attach the adapter to the recyclerview to populate items
        Posts.setAdapter(adapter);
        // Set layout manager to position the items
        Posts.setLayoutManager(new LinearLayoutManager(this));

        DividerItemDecoration decoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        decoration.setDrawable(getDrawable(R.drawable.shadow));
        Posts.addItemDecoration(decoration);
    }
}
