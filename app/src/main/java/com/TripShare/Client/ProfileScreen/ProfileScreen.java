package com.TripShare.Client.ProfileScreen;

import android.content.Context;
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
import com.TripShare.Client.CommunicationWithServer.SendCommentToAddToPostInDB;
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
    private PopupWindow m_commentsWindow;
    private ImageView m_profileImageView;
    private TextView m_name_lastname_textView;
    private CommentAdapter m_commentAdapter;
    Gson gson = new Gson();

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
        PostsAdapter adapter = new PostsAdapter(m_posts, this, this, this, this);
        // Attach the adapter to the recyclerview to populate items
        Posts.setAdapter(adapter);
        m_PostAdapter = adapter;

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

        // Initialize contacts
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
        // TODO: change the default picture to an actual picture from the route if exists
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
        postFullScreen.putExtra("Post",gson.toJson(m_posts.get(i_position).getPost()));
        postFullScreen.putExtra("isShowScreenShotButton", false);
        startActivity(postFullScreen);
    }

    @Override
    public void onLikeButtonClick(int i_position, View i_view)
    {
        Post post = m_posts.get(i_position).getPost();
        if(!post.checkIfLikedByUser(Long.valueOf(0))) // TODO: Change to Actual userID !!!!
        {
            new SendLikeToAddToPostInDB(Long.valueOf(0), post.getID()); // TODO: Change to Actual userID !!!!
            post.addLikedID(Long.valueOf(0));  // TODO: Change to Actual userID !!!!
        }
    }

    @Override
    public void onCommentButtonClick(int i_position, View i_view) {
        onShowPopup(i_view, i_position);
    }

    // call this method when required to show popup
    public void onShowPopup(View v, final int i_postPosition) {
        ArrayList<Comment> comments = m_posts.get(i_postPosition).getPost().getCommentsArray();
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // inflate the custom popup layout
        final View inflatedView = layoutInflater.inflate(R.layout.layout_popupwindow_comments, null, false);
        // find the ListView in the popup layout
        ListView commentsListView = (ListView) inflatedView.findViewById(R.id.commentsListView);
        // fill the data to the list items
        setPopUpWindowListView(commentsListView, comments);

        // set height depends on the device size
        m_commentsWindow = new PopupWindow(inflatedView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // set a background drawable with rounders corners
        m_commentsWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popupwindow_background));
        // make it focusable to show the keyboard to enter in `EditText`
        m_commentsWindow.setFocusable(true);
        // make it outside touchable to dismiss the popup window
        m_commentsWindow.setOutsideTouchable(true);

        // show the popup at bottom of the screen and set some margin at bottom ie,
        m_commentsWindow.showAtLocation(v, Gravity.CENTER_VERTICAL, 0, 50);

        final Button submitButton = (Button)inflatedView.findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = ((EditText)inflatedView.findViewById(R.id.writeComment)).getText().toString();
                saveCommentToLocalPost(comment, m_posts.get(i_postPosition).getPost());
                sendCommentToServer(comment, m_posts.get(i_postPosition).getPost());
                ((EditText)inflatedView.findViewById(R.id.writeComment)).setText("");
            }
        });
    }

    void populateList(ArrayList<Comment> i_comments, CommentAdapter i_adapter) {
        for (Comment comment : i_comments) {
            i_adapter.add(new CommentItem(comment));
            i_adapter.notifyDataSetChanged();
        }
    }

    void setPopUpWindowListView(ListView i_listView, ArrayList<Comment> i_comments) {
        m_commentAdapter = new CommentAdapter(ProfileScreen.this);
        populateList(i_comments, m_commentAdapter);
        i_listView.setAdapter(m_commentAdapter);
    }

    // TODO: change to current UserName !!!!!!!!!

    void saveCommentToLocalPost(String i_comment, Post i_postToUpdate)
    {
        m_commentAdapter.add(new CommentItem(new Comment(i_comment, Long.valueOf(0), "Sivan")));
        i_postToUpdate.getCommentsArray().add(new Comment(i_comment, Long.valueOf(0), "Sivan"));
    }

    void sendCommentToServer(String i_comment, Post i_postToUpdate)
    {
        new SendCommentToAddToPostInDB(new Comment(i_comment, Long.valueOf(0), "Sivan"), Long.valueOf(i_postToUpdate.getID())).execute();
    }

    @Override
    public void sendImageToServerAndUpdateView(Bitmap i_imageToAttach)
    {
        m_profileImageView.setImageBitmap(i_imageToAttach);
        // TODO: add the photo to the user object
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        i_imageToAttach.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        String imageString = Base64.encodeToString(byteArray, Base64.DEFAULT);
        ApplicationManager.getLoggedInUser().setImageString(imageString);

        new SendUserProfileImageToDB(ApplicationManager.getLoggedInUser()).execute(); //
    }
}
