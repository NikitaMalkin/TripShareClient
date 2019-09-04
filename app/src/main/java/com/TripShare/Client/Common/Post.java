package com.TripShare.Client.Common;
import java.io.Serializable;
import java.util.ArrayList;

public class Post implements Serializable{
    private static final long serialVersionUID = 1L;

    private long m_ID;
    private Long m_userID;
    private String m_title;
    private String m_userFirstName;
    private String m_userLastName;
    private String m_postThumbnailString;
    private String m_description;
    private long m_routeID;
    private Boolean m_isPrivatePost;
    private ArrayList<Long> m_usersWhichLikedID;
    private ArrayList<Comment> m_postComments;
    private ArrayList<String> m_relatedTags;

    public Post(Long i_userID, String i_title, String i_description)
    {
        m_userID = i_userID;
        setTitle(i_title);
        setDescription(i_description);
        m_usersWhichLikedID = new ArrayList<>();
        m_postComments = new ArrayList<>();
        m_relatedTags = new ArrayList<>();
    }

    public long getID() { return m_ID; }

    public Long getUserID() { return m_userID; }

    public void setUserID(Long i_userID) { m_userID = i_userID; }

    public String getTitle() { return m_title; }
    public void setTitle(String i_title) { m_title = new String(i_title); }

    public String getDescription() { return m_description; }

    public void setDescription(String i_description) { m_description = new String(i_description); }

    public long getPostRoute() { return m_routeID; }

    public void setRouteID(long i_routeID)
    {
        m_routeID = i_routeID;
    }

    public int getLikeCount()
    {
        return m_usersWhichLikedID.size();
    }

    public int getCommentCount()
    {
        return m_postComments.size();
    }

    public void addLikedID(long i_userIDWhichLiked)
    {
        m_usersWhichLikedID.add(i_userIDWhichLiked);
    }

    public void addComment(Comment i_comment)
    {
        m_postComments.add(i_comment);
    }

    public ArrayList<Comment> getCommentsArray()
    {
        return m_postComments;
    }

    public void setTags(ArrayList<String> i_relatedTags)
    {
        m_relatedTags = i_relatedTags;
    }

    public ArrayList<String> getTags() {  return m_relatedTags; }

    public String getUserFirstName() { return  m_userFirstName; }

    public String getUserLastName() {  return m_userLastName; }

    public void setUserFirstName(String i_userFirstName) {  m_userFirstName = i_userFirstName; }

    public void setUserLastName(String i_userLastName) {  m_userLastName = i_userLastName; }

    public void setThumbnailString(String i_thumbnailString) {  m_postThumbnailString = i_thumbnailString; }

    public String getThumbnailString() {  return m_postThumbnailString; }

    public Boolean checkIfLikedByUser(Long i_userID)
    {
        Boolean returnValue = false;

        if(m_usersWhichLikedID.contains(i_userID))
            returnValue = true;

        return returnValue;
    }

    public String getAuthorFirstName() { return m_userFirstName; }

    public String getAuthorLastName() { return m_userLastName; }

    public Boolean getIsPrivatePost() {return m_isPrivatePost; }

    public void setIsPrivatePost(Boolean i_isPrivatePost) { m_isPrivatePost = i_isPrivatePost; }
}