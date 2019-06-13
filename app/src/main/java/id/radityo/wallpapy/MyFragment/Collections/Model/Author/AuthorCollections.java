package id.radityo.wallpapy.MyFragment.Collections.Model.Author;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AuthorCollections {
    // Chloe Urban

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
    public Object twitterUsername;
    @SerializedName("portfolio_url")
    @Expose
    public String portfolioUrl;
    @SerializedName("bio")
    @Expose
    public Object bio;
    @SerializedName("location")
    @Expose
    public Object location;
    @SerializedName("links")
    @Expose
    public AuthorLinks authorLinks;
    @SerializedName("profile_image")
    @Expose
    public AuthorProfile authorProfile;
    @SerializedName("instagram_username")
    @Expose
    public Object instagramUsername;
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

    public Object getTwitterUsername() {
        return twitterUsername;
    }

    public void setTwitterUsername(Object twitterUsername) {
        this.twitterUsername = twitterUsername;
    }

    public String getPortfolioUrl() {
        return portfolioUrl;
    }

    public void setPortfolioUrl(String portfolioUrl) {
        this.portfolioUrl = portfolioUrl;
    }

    public Object getBio() {
        return bio;
    }

    public void setBio(Object bio) {
        this.bio = bio;
    }

    public Object getLocation() {
        return location;
    }

    public void setLocation(Object location) {
        this.location = location;
    }

    public AuthorLinks getAuthorLinks() {
        return authorLinks;
    }

    public void setAuthorLinks(AuthorLinks authorLinks) {
        this.authorLinks = authorLinks;
    }

    public AuthorProfile getAuthorProfile() {
        return authorProfile;
    }

    public void setAuthorProfile(AuthorProfile authorProfile) {
        this.authorProfile = authorProfile;
    }

    public Object getInstagramUsername() {
        return instagramUsername;
    }

    public void setInstagramUsername(Object instagramUsername) {
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
}
