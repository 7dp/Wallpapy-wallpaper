package id.radityo.wallpapy.MyFragment.Collections.Model.CoverPhoto.Author;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CoverPhotoAuthorLinks {

    @SerializedName("self")
    @Expose
    public String self;
    @SerializedName("html")
    @Expose
    public String html;
    @SerializedName("photos")
    @Expose
    public String photos;
    @SerializedName("likes")
    @Expose
    public String likes;
    @SerializedName("portfolio")
    @Expose
    public String portfolio;
    @SerializedName("following")
    @Expose
    public String following;
    @SerializedName("followers")
    @Expose
    public String followers;

    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getPhotos() {
        return photos;
    }

    public void setPhotos(String photos) {
        this.photos = photos;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(String portfolio) {
        this.portfolio = portfolio;
    }

    public String getFollowing() {
        return following;
    }

    public void setFollowing(String following) {
        this.following = following;
    }

    public String getFollowers() {
        return followers;
    }

    public void setFollowers(String followers) {
        this.followers = followers;
    }
}
