package id.radityo.wallpapy.MyFragment.New.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Urls {

    @SerializedName("raw")
    @Expose
    public String raw;
    @SerializedName("full")
    @Expose
    public String full;
    @SerializedName("regular")
    @Expose
    public String regular;
    @SerializedName("small")
    @Expose
    public String small;
    @SerializedName("thumb")
    @Expose
    public String thumb;

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public String getFull() {
        return full;
    }

    public void setFull(String full) {
        this.full = full;
    }

    public String getRegular() {
        return regular;
    }

    public void setRegular(String regular) {
        this.regular = regular;
    }

    public String getSmall() {
        return small;
    }

    public void setSmall(String small) {
        this.small = small;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }
}
