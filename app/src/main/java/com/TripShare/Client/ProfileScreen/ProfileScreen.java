package com.TripShare.Client.ProfileScreen;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.*;
import android.widget.*;
import com.TripShare.Client.Common.*;
import com.TripShare.Client.CommunicationWithServer.GetPostsFromDB;
import com.TripShare.Client.CommunicationWithServer.SendLikeToAddToPostInDB;
import com.TripShare.Client.CommunicationWithServer.SendUserProfileImageToDB;
import com.TripShare.Client.PostFullScreen.PostFullScreen;
import com.TripShare.Client.R;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class ProfileScreen extends ActivityWithNavigationDrawer implements GetPostsFromDB.AddAllItemsToListViewListener, PostsAdapter.shareButtonClickedListener, PostsAdapter.mapButtonClickedListener, PostsAdapter.commentButtonClickedListener, PostsAdapter.likeButtonClickedListener, AddPhotoDialog.SendImageToServerAndUpdateProfileViewListener
{
    private ArrayList<PostItem> m_posts;
    private PostsAdapter m_PostAdapter;
    private int m_firstPositionToRetrieve;
    private ImageView m_profileImageView;
    private TextView m_name_lastname_textView;
    private CommentPopUpWindow m_commentWindow;
    private ProgressBar m_progressBar;

    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);
        setActivityTitle("Profile");

        m_name_lastname_textView = findViewById(R.id.profile_name_lastname_textView);
        m_name_lastname_textView.setText(ApplicationManager.getLoggedInUser().getuserRealName() + " " + ApplicationManager.getLoggedInUser().getLastName());
        m_profileImageView = findViewById(R.id.profile_userImage_imageView);

        if(ApplicationManager.getLoggedInUser().getImageString() != null)
        {
            byte[] decodedImageString = Base64.decode(ApplicationManager.getLoggedInUser().getImageString(), Base64.DEFAULT);
            m_profileImageView.setImageBitmap(BitmapFactory.decodeByteArray(decodedImageString, 0, decodedImageString.length));
        }

        m_profileImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                DialogFragment dialog = new AddPhotoDialog();
                Bundle args = new Bundle();
                args.putBoolean("isProfile", true);
                dialog.setArguments(args);
                dialog.show(getSupportFragmentManager(), "Choose Profile Picture");
            }});
                // TODO: add another line that checks if the user has a profile picture already, then set it to the actual photo.

        //Application Drawer initialization
        initializeDrawerLayout();

        // Initialization of posts
        RecyclerView Posts = findViewById(R.id.profileScreen_recyclerView);
        m_firstPositionToRetrieve = 0;
        m_posts = new ArrayList<>();
        // Create adapter passing in the sample user data
        PostsAdapter adapter = new PostsAdapter(m_posts, this, this, this, this);
        // Attach the adapter to the recyclerView to populate items
        Posts.setAdapter(adapter);
        m_PostAdapter = adapter;

        //progressbar initialization
        m_progressBar = findViewById(R.id.profilescreen_progressbar);

        // Set layout manager to position the items
        Posts.setLayoutManager(new LinearLayoutManager(this));

        //Posts RecyclerView separator initialization
        DividerItemDecoration decoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        decoration.setDrawable(getDrawable(R.drawable.shadow));
        Posts.addItemDecoration(decoration);
    }

    @Override
    protected void onResume() {
        super.onResume();

        try
        {
            new GetPostsFromDB(this, ApplicationManager.getLoggedInUser().getID(), m_firstPositionToRetrieve, false, m_progressBar).execute();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void imageButton_refreshPostsOnClick(View view)
    {
        // retrieve another 5 posts from DB and get from server.
       new GetPostsFromDB(this, 0, m_firstPositionToRetrieve, false, m_progressBar).execute();
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
        PostItem itemToAdd = new PostItem(i_postToAdd);
        m_posts.add(itemToAdd);
        m_PostAdapter.notifyDataSetChanged();
    }

    @Override
    public void onShareButtonClick(int i_position, View i_view)
    {
        Intent postFullScreen = new Intent(ProfileScreen.this, PostFullScreen.class);
        postFullScreen.putExtra("Post",gson.toJson(m_posts.get(i_position).getPost()));
        postFullScreen.putExtra("isShowScreenShotButton", true);
        startActivity(postFullScreen);
    }

    @Override
    public void onMapButtonClick(int i_position, View i_view)
    {
        Intent postFullScreen = new Intent(ProfileScreen.this, PostFullScreen.class);
        postFullScreen.putExtra("Post",gson.toJson(m_posts.get(i_position).getPost()));
        postFullScreen.putExtra("isShowScreenShotButton", false);
        startActivity(postFullScreen);
    }

    @Override
    public void onLikeButtonClick(int i_position, View i_view)
    {
        Post post = m_posts.get(i_position).getPost();
        if(!post.checkIfLikedByUser(ApplicationManager.getLoggedInUser().getID()))
        {
            new SendLikeToAddToPostInDB(ApplicationManager.getLoggedInUser().getID(), post.getID());
            post.addLikedID(ApplicationManager.getLoggedInUser().getID());

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

    @Override
    public void sendImageToServerAndUpdateView(Bitmap i_imageToAttach)
    {
        m_profileImageView.setImageBitmap(i_imageToAttach);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        i_imageToAttach.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        String imageString = Base64.encodeToString(byteArray, Base64.DEFAULT);
        ApplicationManager.getLoggedInUser().setImageString(imageString);

        new SendUserProfileImageToDB(ApplicationManager.getLoggedInUser()).execute(); //
    }
}
