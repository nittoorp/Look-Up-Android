package org.asu.cse535.lookup.rest;

import org.asu.cse535.lookup.model.response.PlacesNearMeResponse;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlacesNearMeEndPoint {

    @GET("/maps/api/place/nearbysearch/json")
    Call<PlacesNearMeResponse> searchPlacesNearMe(@Query("key") String key,
                                                     @Query("location") String location,
                                                      @Query("radius") String radius,
                                                      @Query("type") String type,
                                                      @Query("keyword") String keyword);
}
