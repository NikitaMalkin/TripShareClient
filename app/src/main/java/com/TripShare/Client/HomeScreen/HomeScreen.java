package com.TripShare.Client.HomeScreen;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.widget.*;
import com.TripShare.Client.Common.*;
import com.TripShare.Client.CommunicationWithServer.GetPostsFromDB;
import com.TripShare.Client.CommunicationWithServer.SendLikeToAddToPostInDB;
import com.TripShare.Client.CommunicationWithServer.SendUserTagsToDB;
import com.TripShare.Client.PostFullScreen.PostFullScreen;
import com.TripShare.Client.R;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeScreen extends ActivityWithNavigationDrawer implements GetPostsFromDB.AddAllItemsToListViewListener, PostsAdapter.shareButtonClickedListener, PostsAdapter.mapButtonClickedListener, PostsAdapter.commentButtonClickedListener, PostsAdapter.likeButtonClickedListener {
    private ArrayList<PostItem> m_posts;
    private PostsAdapter m_PostAdapter;
    private int m_firstPositionToRetrieve;
    private CommentPopUpWindow m_commentWindow;
    private ProgressBar m_progressBar;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        setActivityTitle("Home");

        //Application Drawer initialization
        initializeDrawerLayout();

        RecyclerView Posts = findViewById(R.id.homescreen_recyclerView);
        m_progressBar = findViewById(R.id.homescreen_progressBar);

        m_firstPositionToRetrieve = 0;
        m_posts = new ArrayList<>();
        // Create adapter passing in the sample user data
        PostsAdapter adapter = new PostsAdapter(m_posts, this, this, this, this);
        // Attach the adapter to the recyclerview to populate items
        Posts.setAdapter(adapter);
        m_PostAdapter = adapter;

        // Set layout manager to position the items
        Posts.setLayoutManager(new LinearLayoutManager(this));

        //Posts RecyclerView separator initialization
        DividerItemDecoration decoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        decoration.setDrawable(getDrawable(R.drawable.shadow));
        Posts.addItemDecoration(decoration);


        //FirstLaunchDialog
        if (ApplicationManager.getHomePageFirstLaunch()) {
            DialogFragment firstTimeDialog = new FirstLaunchDialog();
            firstTimeDialog.show(getSupportFragmentManager(), "");
        }

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        try
        {
            new GetPostsFromDB(this, ApplicationManager.getLoggedInUser().getID(), m_firstPositionToRetrieve, true, m_progressBar).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void imageButton_refreshPostsOnClick(View view) {
        // retrieve another 5 posts from DB and get from server.
        new GetPostsFromDB(this, ApplicationManager.getLoggedInUser().getID(), m_firstPositionToRetrieve, true, m_progressBar).execute();
    }

    @Override
    public void addAllItemsToView(final String i_body) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                int howManyPostsAdded = 0;
                if (i_body.equals("[]"))
                    Toast.makeText(getApplicationContext(), "No more posts to load.", Toast.LENGTH_LONG).show();
                else {
                    try {
                        JSONArray jsonArr = new JSONArray(i_body);
                        for (int i = 0; i < jsonArr.length(); i++) {
                            JSONObject jsonObj = jsonArr.getJSONObject(i);
                            Post post = new Gson().fromJson(jsonObj.toString(), Post.class);
                            howManyPostsAdded += addItemToListView(post);
                        }

                        if(howManyPostsAdded == 0)
                            Toast.makeText(getApplicationContext(), "No more posts to load.", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private int addItemToListView(Post i_postToAdd) {
        // set the new route in the list
        if(!postAlreadyAdded(i_postToAdd))
        {
            PostItem itemToAdd = new PostItem(i_postToAdd);
            m_posts.add(itemToAdd);
            m_PostAdapter.notifyDataSetChanged();
            m_firstPositionToRetrieve++;
            return 1;
        }
        else
            return 0;
    }

    private Boolean postAlreadyAdded(Post i_post)
    {
        for (PostItem postItem: m_posts)
        {
            if(i_post.getID() == postItem.getPost().getID())
                return true;
        }
        return false;
    }

    @Override
    public void onShareButtonClick(int i_position, View i_view) {
        Intent postFullScreen = new Intent(HomeScreen.this, PostFullScreen.class);
        postFullScreen.putExtra("Post", gson.toJson(m_posts.get(i_position).getPost()));
        postFullScreen.putExtra("isShowScreenShotButton", true);
        startActivity(postFullScreen);
    }

    @Override
    public void onMapButtonClick(int i_position, View i_view) {
        Intent postFullScreen = new Intent(HomeScreen.this, PostFullScreen.class);
        postFullScreen.putExtra("Post", gson.toJson(m_posts.get(i_position).getPost()));
        postFullScreen.putExtra("isShowScreenShotButton", false);
        startActivity(postFullScreen);
    }

    @Override
    public void onLikeButtonClick(int i_position, View i_view)
    {
        Post post = m_posts.get(i_position).getPost();
        if(!post.checkIfLikedByUser(ApplicationManager.getLoggedInUser().getID()))
        {
            new SendLikeToAddToPostInDB(ApplicationManager.getLoggedInUser().getID(), post.getID()).execute();
            post.addLikedID(ApplicationManager.getLoggedInUser().getID());
            Toast.makeText(getApplicationContext(), "Post Liked!", Toast.LENGTH_LONG).show();

            //paint the imagebutton red
            ImageButton imageButton = i_view.findViewById(R.id.profileItem_imageButtonLike);
            imageButton.setImageDrawable(getDrawable(R.drawable.ic_favorite_black_24dp_red));

            m_PostAdapter.notifyDataSetChanged();
        }
        else
        {
            Toast.makeText(getApplicationContext(), "You have already likes this post.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCommentButtonClick(int i_position, View i_view) {
        m_commentWindow = new CommentPopUpWindow(this, m_posts, m_PostAdapter);
        m_commentWindow.onShowPopup(i_view, i_position);
    }

    public void onEditTagsButtonClick(View i_view)
    {
        //When the edit tags button clicked, open tags dialog
        final DialogFragment editTagsDialog = new TagSelectionDialog();

        editTagsDialog.show(getSupportFragmentManager(), "");
        getSupportFragmentManager().executePendingTransactions();
        ((TagSelectionDialog) editTagsDialog).selectUserPrefferedTags();

        editTagsDialog.getDialog().setOnDismissListener((new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (((TagSelectionDialog) editTagsDialog).getDialogResolvedSuccessfully()) {
                    ApplicationManager.getLoggedInUser().setPreferredTags(((TagSelectionDialog) editTagsDialog).getSelectedTags());
                    SendUserTagsToDB sendToServer = new SendUserTagsToDB(ApplicationManager.getLoggedInUser().getPreferredTags(), ApplicationManager.getLoggedInUser().getID());
                    sendToServer.execute();

                    m_posts.clear();
                    m_firstPositionToRetrieve = 0;
                    m_PostAdapter.notifyDataSetChanged();
                    // retrieve another 5 posts from DB and get from server.
                    new GetPostsFromDB(HomeScreen.this, ApplicationManager.getLoggedInUser().getID(), m_firstPositionToRetrieve, true, m_progressBar).execute();
                }
            }
        }));
    }
}
