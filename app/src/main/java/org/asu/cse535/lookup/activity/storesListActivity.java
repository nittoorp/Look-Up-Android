package org.asu.cse535.lookup.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.asu.cse535.lookup.R;
import org.asu.cse535.lookup.adapter.CustomPlacesNearMeAdapter;
import org.asu.cse535.lookup.adapter.ItemClickSupport;
import org.asu.cse535.lookup.config.Config;
import org.asu.cse535.lookup.model.response.PlaceNearMe;
import org.asu.cse535.lookup.model.response.PlacesNearMeResponse;
import org.asu.cse535.lookup.rest.API;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class storesListActivity extends AppCompatActivity {

    private final static String TAG = storesListActivity.class.getSimpleName();

    String selectedItem ;

    private CustomPlacesNearMeAdapter adapter;
    private static RecyclerView recyclerView;
    ProgressDialog progressDialog;
    Location userLocation;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stores_list);

        Intent intent = getIntent();
        selectedItem = intent.getStringExtra("selectedItem");
        userLocation = intent.getParcelableExtra("userLocation");

        progressDialog = new ProgressDialog(storesListActivity.this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        getStoresList();

    }



    public void getStoresList(){

        String temp = (userLocation.getLatitude()+""+
        userLocation.getLongitude()) + " -"+ new Integer(1500).toString()+"-FASHION" + "-"+selectedItem;

        System.out.println(temp);
        //Call<PlacesNearMeResponse> call = API.placesNearMe().searchPlacesNearMe(Config.PLACES_NEAR_ME__API_KEY,(userLocation.getLatitude()+","+
          //      userLocation.getLongitude()),new Integer(1500).toString(),"FASHION","CALVIN KLEIN");

        Call<PlacesNearMeResponse> call = API.placesNearMe().searchPlacesNearMe(Config.PLACES_NEAR_ME__API_KEY,(userLocation.getLatitude()+","+userLocation.getLongitude()),new Integer(15000).toString(),"FASHION",selectedItem);
        call.enqueue(new Callback<PlacesNearMeResponse>() {
            @Override
            public void onResponse(Call<PlacesNearMeResponse> call, Response<PlacesNearMeResponse> response) {
                progressDialog.dismiss();
                generateDataList(response.body());
            }

            @Override
            public void onFailure(Call<PlacesNearMeResponse> call, Throwable t) {
                Log.e(TAG, t.toString());
                progressDialog.dismiss();
                Toast.makeText(storesListActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateDataList(PlacesNearMeResponse body) {
        List<PlaceNearMe> storeList = body.getResults();

        Map<String, PlaceNearMe> map = new HashMap<>();

        for(PlaceNearMe r : storeList){
            map.put(r.getId(),r);
        }

        storeList = new ArrayList<>(map.values());

        if(storeList.size()== 0){
            storeList = new ArrayList<>();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            Toast.makeText(storesListActivity.this, "No content, please try again!", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }

        System.out.println(storeList);
        Collections.shuffle(storeList);
        recyclerView = findViewById(R.id.storeList);

        adapter = new CustomPlacesNearMeAdapter(this,storeList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(storesListActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            private View lastSelectedView = null;

            @SuppressLint("ResourceAsColor")
            public void clearSelection() {
                if(lastSelectedView != null) lastSelectedView.setBackgroundColor(android.R.color.white);
            }
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View view) {
                clearSelection();
                lastSelectedView = view;
                view.setBackgroundDrawable(view.getContext().getResources().getDrawable(R.drawable.blue_color));

                PlaceNearMe selectedStore = adapter.getItem(position);
                System.out.println(selectedStore.getName());

                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("userLat",userLocation.getLatitude());
                intent.putExtra("userLon",userLocation.getLongitude());
                intent.putExtra("storeLat",selectedStore.getGeometry().getLocation().getLat());
                intent.putExtra("storeLon",selectedStore.getGeometry().getLocation().getLon());
                intent.putExtra("title",selectedStore.getName());
                startActivity(intent);
            }
        });
    }

    public void onClickHome(View view){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    public void onClickBack(View view){
        finish();
    }

}


