package org.asu.cse535.lookup.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PlacesNearMeResponse implements Serializable {

    @SerializedName("html_attributions")
    @Expose
    private List<String> htmlAttributions;

    @SerializedName("results")
    @Expose
    private List<PlaceNearMe> results;

    public PlacesNearMeResponse(List<String> htmlAttributions, List<PlaceNearMe> results) {
        this.htmlAttributions = htmlAttributions;
        this.results = results;
    }

    public List<String> getHtmlAttributions() {
        return htmlAttributions;
    }

    public void setHtmlAttributions(List<String> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }

    public List<PlaceNearMe> getResults() {
        return results;
    }

    public void setResults(List<PlaceNearMe> results) {
        this.results = results;
    }
}
