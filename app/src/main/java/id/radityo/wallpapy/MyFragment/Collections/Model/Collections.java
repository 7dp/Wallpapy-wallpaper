package id.radityo.wallpapy.MyFragment.Collections.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import id.radityo.wallpapy.MyFragment.Collections.Model.Author.Author;
import id.radityo.wallpapy.MyFragment.Collections.Model.CoverPhoto.CoverPhoto;
import id.radityo.wallpapy.MyFragment.Collections.Model.PreviewPhotos.PreviewPhoto;

public class Collections {

    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("description")
    @Expose
    public String description;
    @SerializedName("published_at")
    @Expose
    public String publishedAt;
    @SerializedName("updated_at")
    @Expose
    public String updatedAt;
    @SerializedName("curated")
    @Expose
    public Boolean curated;
    @SerializedName("featured")
    @Expose
    public Boolean featured;
    @SerializedName("total_photos")
    @Expose
    public Integer totalPhotos;
    @SerializedName("private")
    @Expose
    public Boolean privation;
    @SerializedName("share_key")
    @Expose
    public String shareKey;
    @SerializedName("tags")
    @Expose
    public List<Tags> tags = null;
    @SerializedName("links")
    @Expose
    public Links links;
    @SerializedName("user")
    @Expose
    public Author author;
    @SerializedName("cover_photo")
    @Expose
    public CoverPhoto coverPhoto;
    @SerializedName("preview_photos")
    @Expose
    public List<PreviewPhoto> previewPhotos = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getCurated() {
        return curated;
    }

    public void setCurated(Boolean curated) {
        this.curated = curated;
    }

    public Boolean getFeatured() {
        return featured;
    }

    public void setFeatured(Boolean featured) {
        this.featured = featured;
    }

    public Integer getTotalPhotos() {
        return totalPhotos;
    }

    public void setTotalPhotos(Integer totalPhotos) {
        this.totalPhotos = totalPhotos;
    }

    public Boolean getPrivation() {
        return privation;
    }

    public void setPrivation(Boolean privation) {
        this.privation = privation;
    }

    public String getShareKey() {
        return shareKey;
    }

    public void setShareKey(String shareKey) {
        this.shareKey = shareKey;
    }

    public List<Tags> getTags() {
        return tags;
    }

    public void setTags(List<Tags> tags) {
        this.tags = tags;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public CoverPhoto getCoverPhoto() {
        return coverPhoto;
    }

    public void setCoverPhoto(CoverPhoto coverPhoto) {
        this.coverPhoto = coverPhoto;
    }

    public List<PreviewPhoto> getPreviewPhotos() {
        return previewPhotos;
    }

    public void setPreviewPhotos(List<PreviewPhoto> previewPhotos) {
        this.previewPhotos = previewPhotos;
    }
}
