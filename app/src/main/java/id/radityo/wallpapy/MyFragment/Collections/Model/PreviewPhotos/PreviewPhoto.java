package id.radityo.wallpapy.MyFragment.Collections.Model.PreviewPhotos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PreviewPhoto {

    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("urls")
    @Expose
    public PreviewPhotoUrls urls;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PreviewPhotoUrls getUrls() {
        return urls;
    }

    public void setUrls(PreviewPhotoUrls urls) {
        this.urls = urls;
    }
}
