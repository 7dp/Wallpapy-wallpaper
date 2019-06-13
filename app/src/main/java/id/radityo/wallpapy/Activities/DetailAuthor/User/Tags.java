package id.radityo.wallpapy.Activities.DetailAuthor.User;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Tags {

    @SerializedName("custom")
    @Expose
    public List<Custom> custom = null;
    @SerializedName("aggregated")
    @Expose
    public List<Aggregated> aggregated = null;

    public List<Custom> getCustom() {
        return custom;
    }

    public void setCustom(List<Custom> custom) {
        this.custom = custom;
    }

    public List<Aggregated> getAggregated() {
        return aggregated;
    }

    public void setAggregated(List<Aggregated> aggregated) {
        this.aggregated = aggregated;
    }
}
