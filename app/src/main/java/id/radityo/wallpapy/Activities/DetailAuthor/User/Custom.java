package id.radityo.wallpapy.Activities.DetailAuthor.User;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Custom {

    @SerializedName("title")
    @Expose
    public String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
