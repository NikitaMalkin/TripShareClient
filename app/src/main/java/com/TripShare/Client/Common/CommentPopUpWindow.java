package com.TripShare.Client.Common;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import com.TripShare.Client.CommunicationWithServer.SendCommentToAddToPostInDB;
import com.TripShare.Client.R;

import java.util.ArrayList;

public class CommentPopUpWindow
{
    private PopupWindow m_commentsWindow;
    private CommentAdapter m_commentAdapter;
    private ArrayList<PostItem> m_posts;
    private Context m_screen;

    public CommentPopUpWindow(Context i_screen,  ArrayList<PostItem> i_posts)
    {
        m_posts = i_posts;
        m_screen = i_screen;
    }

    // call this method when required to show popup
    public void onShowPopup(View v, final int i_postPosition) {
        ArrayList<Comment> comments = m_posts.get(i_postPosition).getPost().getCommentsArray();
        LayoutInflater layoutInflater = (LayoutInflater) m_screen.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // inflate the custom popup layout
        final View inflatedView = layoutInflater.inflate(R.layout.layout_popupwindow_comments, null, false);
        // find the ListView in the popup layout
        ListView commentsListView = (ListView) inflatedView.findViewById(R.id.commentsListView);
        // fill the data to the list items
        setPopUpWindowListView(commentsListView, comments);

        // set height depends on the device size
        m_commentsWindow = new PopupWindow(inflatedView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // set a background drawable with rounders corners
        m_commentsWindow.setBackgroundDrawable(m_screen.getResources().getDrawable(R.drawable.popupwindow_background));
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
        m_commentAdapter = new CommentAdapter(m_screen);
        populateList(i_comments, m_commentAdapter);
        i_listView.setAdapter(m_commentAdapter);
    }

    void saveCommentToLocalPost(String i_comment, Post i_postToUpdate)
    {
        m_commentAdapter.add(new CommentItem(new Comment(i_comment, ApplicationManager.getLoggedInUser().getID(), ApplicationManager.getLoggedInUser().getuserRealName())));
        i_postToUpdate.getCommentsArray().add(new Comment(i_comment,ApplicationManager.getLoggedInUser().getID(), ApplicationManager.getLoggedInUser().getuserRealName()));
    }

    void sendCommentToServer(String i_comment, Post i_postToUpdate)
    {
        new SendCommentToAddToPostInDB(new Comment(i_comment, ApplicationManager.getLoggedInUser().getID(), ApplicationManager.getLoggedInUser().getuserRealName()), Long.valueOf(i_postToUpdate.getID())).execute();
    }
}
