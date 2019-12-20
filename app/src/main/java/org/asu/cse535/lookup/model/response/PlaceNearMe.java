package org.asu.cse535.lookup.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PlaceNearMe implements Serializable {

    @SerializedName("geometry")
    @Expose
    private Geometry geometry;

    @SerializedName("name")
    @Expose
    private String name;


    @SerializedName("icon")
    @Expose
    private String icon;


    @SerializedName("id")
    @Expose
    private String id;


    public PlaceNearMe(Geometry geometry, String name, String icon, String id) {
        this.geometry = geometry;
        this.name = name;
        this.icon = icon;
        this.id = id;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
