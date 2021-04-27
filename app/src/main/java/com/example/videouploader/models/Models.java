package com.example.videouploader.models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;

public class Models {

    public static final class User {
        public static final String USER_SP = "User Shared Preferences";
        public static final String USER_DB = "Users";
        private String username;
        public static final String USERNAME = "username";
        private String emailAddress;
        public static final String EMAIL_ADDRESS = "emailAddress";
        private String uid;
        public static final String UID = "uid";
        private String createdAt;
        public static final String CREATED_AT = "createdAt";
        public static final String CREATION_STATUS = "Creation Status";

        private String profilePic;
        public static final String PROFILE_PIC = "profilePic";
        private String coverImage;
        public static final String COVER_IMAGE = "coverImage";

        public static final int SUCCESS = 1;
        public static final int FAIL = 0;

        public User(String uid) {
            this.uid = uid;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmailAddress() {
            return emailAddress;
        }

        public void setEmailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }


        public String getProfilePic() {
            return profilePic;
        }

        public void setProfilePic(String profilePic) {
            this.profilePic = profilePic;
        }

        public String getCoverImage() {
            return coverImage;
        }

        public void setCoverImage(String coverImage) {
            this.coverImage = coverImage;
        }
    }

    @Entity(tableName = Media.MEDIA_DB)
    public static final class Media implements Serializable {
        public static final String MEDIA_ID = "mediaId";
        public static final String VIDEO_SUR = "VD";
        public static final String IMAGE_SUR = "IMG";
        public static final String VIDEO = "Video";
        public static final String IMAGE = "Image";


        @PrimaryKey
        @NotNull
        private String mediaId;
        public static final String MEDIA_DB = "Media";
        private String mediaUrl;
        public static final String MEDIA_URL = "mediaUrl";
        private String mediaType;
        public static final String MEDIA_TYPE = "mediaType";
        private String createdAt;
        private String lastModifiedAt;
        public static final String LAST_MODIFIED_AT = "lastModifiedAt";
        private String postedByUid;
        public static final String POSTED_BY_UID = "postedByUid";
        private String username;
        public static final String USERNAME = "username";
        private String title;
        public static final String TITLE = "title";
        private String description;
        public static final String DESCRIPTION = "description";
        private String tags;
        public static final String TAGS = "tags";
        public static final String VIEWERS = "viewers";
        private String viewers;
        public static final String SHARES = "shares";
        @Ignore
        private Comment comments;
        public static final String COMMENTS = "comments";
        private String averageRating;
        public static final String AVERAGE_RATING = "averageRating";
        private String mentionList;
        public static final String MENTION_LIST = "mentionList";
        private String shares;

        public static final String NO_IMAGE = "https://louisville.edu/research/handaresearchlab/pi-and-students/photos/nocamera.png/image";
        public static final String DUMMY_VIDEO = "https://archive.org/download/Popeye_forPresident/Popeye_forPresident_512kb.mp4";

        public Media(@NotNull String mediaId) {
            this.mediaId = mediaId;
        }

        public @NotNull String getMediaId() {
            return mediaId;
        }

        public void setMediaId(@NotNull String mediaId) {
            this.mediaId = mediaId;
        }

        public String getMediaUrl() {
            return mediaUrl;
        }

        public void setMediaUrl(String mediaUrl) {
            this.mediaUrl = mediaUrl;
        }

        public String getMediaType() {
            return mediaType;
        }

        public void setMediaType(String mediaType) {
            this.mediaType = mediaType;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getLastModifiedAt() {
            return lastModifiedAt;
        }

        public void setLastModifiedAt(String lastModifiedAt) {
            this.lastModifiedAt = lastModifiedAt;
        }

        public String getPostedByUid() {
            return postedByUid;
        }

        public void setPostedByUid(String postedByUid) {
            this.postedByUid = postedByUid;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getTags() {
            return tags;
        }

        public void setTags(String tags) {
            this.tags = tags;
        }

        public String getViewers() {
            return viewers;
        }

        public void setViewers(String viewers) {
            this.viewers = viewers;
        }

        public Comment getComments() {
            return comments;
        }

        public void setComments(Comment comments) {
            this.comments = comments;
        }

        public String getAverageRating() {
            return averageRating;
        }

        public void setAverageRating(String averageRating) {
            this.averageRating = averageRating;
        }

        public String getMentionList() {
            return mentionList;
        }

        public void setMentionList(String mentionList) {
            this.mentionList = mentionList;
        }

        public String getShares() {
            return shares;
        }

        public void setShares(String shares) {
            this.shares = shares;
        }
    }

    public static class Comment {
        private String mediaId;
        private String commentId;
        public static final String COMMENT_ID = "commentId";
        private String userCommentedUid;
        public static final String USER_COMMENTED_UID = "userCommentedUid";
        private String replyTo;
        public static final String REPLY_TO = "replyTo";
        private String commentContent;
        public static final String COMMENT_CONTENT = "commentContent";
        private String createdAt;
        private String lastModifiedAt;

        public static final String COMMENT_SUR = "CMT";

        public Comment() {
        }

        public String getReplyTo() {
            return replyTo;
        }

        public void setReplyTo(String replyTo) {
            this.replyTo = replyTo;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getLastModifiedAt() {
            return lastModifiedAt;
        }

        public void setLastModifiedAt(String lastModifiedAt) {
            this.lastModifiedAt = lastModifiedAt;
        }

        public String getMediaId() {
            return mediaId;
        }

        public void setMediaId(String mediaId) {
            this.mediaId = mediaId;
        }

        public String getCommentId() {
            return commentId;
        }

        public void setCommentId(String commentId) {
            this.commentId = commentId;
        }


        public String getUserCommentedUid() {
            return userCommentedUid;
        }

        public void setUserCommentedUid(String userCommentedUid) {
            this.userCommentedUid = userCommentedUid;
        }

        public String getCommentContent() {
            return commentContent;
        }

        public void setCommentContent(String commentContent) {
            this.commentContent = commentContent;
        }

    }

    public static final class Welcome {
        private String title;
        private String imageUrl;
        private String description;
        private ArrayList<Welcome> welcomeList;

        public static final String FAST_IMAGE = "https://www.connectioncafe.com/wp-content/uploads/2020/12/fast-speed-internet-2019.png";
        public static final String SHARE_IMAGE = "https://www.crushpixel.com/big-static18/preview4/friends-group-having-addicted-fun-2986187.jpg";
        public static final String DEV_IMAGE ="https://www.freecodecamp.org/news/content/images/2021/01/tips-for-a-great-developer-1.jpg";



        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public ArrayList<Welcome> getWelcomeList() {
            addWelcomeList();
            return welcomeList;
        }

        public void setWelcomeList(ArrayList<Welcome> welcomeList) {
            this.welcomeList = welcomeList;
        }

        private void addWelcomeList() {
            welcomeList = new ArrayList<>();

            Welcome welcome1 = new Welcome();
            welcome1.setTitle("Experience fast video streaming feed");
            welcome1.setDescription("New Firebase To ensure you are streaming high quality videos at fast rates");
            welcome1.setImageUrl(FAST_IMAGE);


            Welcome welcome2 = new Welcome();
            welcome2.setTitle("Upload your videos without interruption");
            welcome2.setDescription("We have the best developers to ensure you have the best experience with us");
            welcome2.setImageUrl(DEV_IMAGE);


            Welcome welcome3 = new Welcome();
            welcome3.setTitle("Share your videos with your friends");
            welcome3.setDescription("What is the use of sharing videos without friends to see them. Invite your friends");
            welcome3.setImageUrl(SHARE_IMAGE);


            welcomeList.add(welcome1);
            welcomeList.add(welcome2);
            welcomeList.add(welcome3);
            setWelcomeList(welcomeList);
        }
    }

}
