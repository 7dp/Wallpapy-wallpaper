package id.radityo.wallpapy.MyFragment.Featured.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Exif {

    @SerializedName("make")
    @Expose
    public String make;
    @SerializedName("model")
    @Expose
    public String model;
    @SerializedName("exposure_time")
    @Expose
    public String exposureTime;
    @SerializedName("aperture")
    @Expose
    public String aperture;
    @SerializedName("focal_length")
    @Expose
    public String focalLength;
    @SerializedName("iso")
    @Expose
    public Integer iso;


    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getExposureTime() {
        return exposureTime;
    }

    public void setExposureTime(String exposureTime) {
        this.exposureTime = exposureTime;
    }

    public String getAperture() {
        return aperture;
    }

    public void setAperture(String aperture) {
        this.aperture = aperture;
    }

    public String getFocalLength() {
        return focalLength;
    }

    public void setFocalLength(String focalLength) {
        this.focalLength = focalLength;
    }

    public Integer getIso() {
        return iso;
    }

    public void setIso(Integer iso) {
        this.iso = iso;
    }
}
