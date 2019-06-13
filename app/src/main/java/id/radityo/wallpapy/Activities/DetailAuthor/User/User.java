package id.radityo.wallpapy.Activities.DetailAuthor.User;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class User {

    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("updated_at")
    @Expose
    public String updatedAt;
    @SerializedName("username")
    @Expose
    public String username;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("first_name")
    @Expose
    public String firstName;
    @SerializedName("last_name")
    @Expose
    public String lastName;
    @SerializedName("twitter_username")
    @Expose
    public String twitterUsername;
    @SerializedName("portfolio_url")
    @Expose
    public String portfolioUrl;
    @SerializedName("bio")
    @Expose
    public String bio;
    @SerializedName("location")
    @Expose
    public String location;
    @SerializedName("links")
    @Expose
    public UserLinks userLinks;
    @SerializedName("profile_image")
    @Expose
    public ProfileImage profileImage;
    @SerializedName("instagram_username")
    @Expose
    public String instagramUsername;
    @SerializedName("total_collections")
    @Expose
    public Integer totalCollections;
    @SerializedName("total_likes")
    @Expose
    public Integer totalLikes;
    @SerializedName("total_photos")
    @Expose
    public Integer totalPhotos;
    @SerializedName("accepted_tos")
    @Expose
    public Boolean acceptedTos;
    @SerializedName("followed_by_user")
    @Expose
    public Boolean followedByUser;
    @SerializedName("photos")
    @Expose
    public List<Photos> photosList = null;
    @SerializedName("badge")
    @Expose
    public String badge;
    @SerializedName("downloads")
    @Expose
    public Integer downloads;
    @SerializedName("tags")
    @Expose
    public Tags tags;
    @SerializedName("followers_count")
    @Expose
    public Integer followersCount;
    @SerializedName("following_count")
    @Expose
    public Integer followingCount;
    @SerializedName("allow_messages")
    @Expose
    public Boolean allowMessages;
    @SerializedName("numeric_id")
    @Expose
    public Integer numericId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTwitterUsername() {
        return twitterUsername;
    }

    public void setTwitterUsername(String twitterUsername) {
        this.twitterUsername = twitterUsername;
    }

    public String getPortfolioUrl() {
        return portfolioUrl;
    }

    public void setPortfolioUrl(String portfolioUrl) {
        this.portfolioUrl = portfolioUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public UserLinks getUserLinks() {
        return userLinks;
    }

    public void setUserLinks(UserLinks userLinks) {
        this.userLinks = userLinks;
    }

    public ProfileImage getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(ProfileImage profileImage) {
        this.profileImage = profileImage;
    }

    public String getInstagramUsername() {
        return instagramUsername;
    }

    public void setInstagramUsername(String instagramUsername) {
        this.instagramUsername = instagramUsername;
    }

    public Integer getTotalCollections() {
        return totalCollections;
    }

    public void setTotalCollections(Integer totalCollections) {
        this.totalCollections = totalCollections;
    }

    public Integer getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(Integer totalLikes) {
        this.totalLikes = totalLikes;
    }

    public Integer getTotalPhotos() {
        return totalPhotos;
    }

    public void setTotalPhotos(Integer totalPhotos) {
        this.totalPhotos = totalPhotos;
    }

    public Boolean getAcceptedTos() {
        return acceptedTos;
    }

    public void setAcceptedTos(Boolean acceptedTos) {
        this.acceptedTos = acceptedTos;
    }

    public Boolean getFollowedByUser() {
        return followedByUser;
    }

    public void setFollowedByUser(Boolean followedByUser) {
        this.followedByUser = followedByUser;
    }

    public List<Photos> getPhotosList() {
        return photosList;
    }

    public void setPhotosList(List<Photos> photosList) {
        this.photosList = photosList;
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public Integer getDownloads() {
        return downloads;
    }

    public void setDownloads(Integer downloads) {
        this.downloads = downloads;
    }

    public Tags getTags() {
        return tags;
    }

    public void setTags(Tags tags) {
        this.tags = tags;
    }

    public Integer getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(Integer followersCount) {
        this.followersCount = followersCount;
    }

    public Integer getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(Integer followingCount) {
        this.followingCount = followingCount;
    }

    public Boolean getAllowMessages() {
        return allowMessages;
    }

    public void setAllowMessages(Boolean allowMessages) {
        this.allowMessages = allowMessages;
    }

    public Integer getNumericId() {
        return numericId;
    }

    public void setNumericId(Integer numericId) {
        this.numericId = numericId;
    }
}
