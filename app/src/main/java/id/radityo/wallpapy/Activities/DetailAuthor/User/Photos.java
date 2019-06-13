package id.radityo.wallpapy.Activities.DetailAuthor.User;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Photos {

    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("urls")
    @Expose
    public Urls urls;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Urls getUrls() {
        return urls;
    }

    public void setUrls(Urls urls) {
        this.urls = urls;
    }
}
