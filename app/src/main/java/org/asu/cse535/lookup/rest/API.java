package org.asu.cse535.lookup.rest;

import org.asu.cse535.lookup.config.Config;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class API {
    private static <T> T builder(Class<T> endpoint, String URL) {
        return new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(endpoint);
    }



    public static PlacesNearMeEndPoint placesNearMe() {
        return builder(PlacesNearMeEndPoint.class,Config.PLACES_NEAR_ME_API_BASE_URL);
    }


}
