package com.TripShare.Client.ProfileScreen;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.TripShare.Client.R;

import java.util.ArrayList;

public class ProfileScreen extends AppCompatActivity {

    ArrayList<PostItem> m_posts;
    PostsAdapter m_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_profle_screen);

        RecyclerView Posts = findViewById(R.id.profileScreen_recyclerView);

        // Initialize contacts
        m_posts = PostItem.getPosts(); //TODO this method is responsible for gathering the first couple posts

        // Create adapter passing in the sample user data
        PostsAdapter adapter = new PostsAdapter(m_posts);
        // Attach the adapter to the recyclerview to populate items
        Posts.setAdapter(adapter);
        m_adapter = adapter;
        // Set layout manager to position the items
        Posts.setLayoutManager(new LinearLayoutManager(this));

        DividerItemDecoration decoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        decoration.setDrawable(getDrawable(R.drawable.shadow));
        Posts.addItemDecoration(decoration);
    }

    public void imageButton_refreshPostsOnClick(View view)
    {
        PostItem itemToAdd = new PostItem("Some Random Post Name", "Some very random post description but it is longer a little bit... It all began when Sivan and Nikita wanted to make a cool application...", ContextCompat.getDrawable(getApplicationContext(), R.drawable.post_thumbnail_sample));
        m_posts.add(itemToAdd);
        m_adapter.notifyDataSetChanged();
    }
}
