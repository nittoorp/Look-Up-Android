package org.asu.cse535.lookup.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.asu.cse535.lookup.R;

import java.util.ArrayList;

public class ProductListActivity extends AppCompatActivity {

     ArrayList<String> storesList = null;

    LocationManager locationManager;
    LocationListener locationListener;
    Location userLocation;

    ListView ListView;
    String selected;

    ProgressDialog progressDialog;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        progressDialog = new ProgressDialog(ProductListActivity.this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        initializeUserLocation();
        //listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, GENRES));
        ListView = findViewById(R.id.storesList);

        storesList = (ArrayList<String>) getIntent().getSerializableExtra("storesList");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice, storesList);

        ListView.setAdapter(arrayAdapter);

        ListView.setSelection(0);
        ListView.setItemChecked(0, true);
        ListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        //listView.setItemChecked(2, true);


        if(ListView.getSelectedView() != null) {
            ListView.getSelectedView().setSelected(true);
        }

        ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View arg1, int arg2, long arg3)
            {

                String selected1 = "";



                int cntChoice = ListView.getCount();

                SparseBooleanArray sparseBooleanArray = ListView.getCheckedItemPositions();

                for(int i = 0; i < cntChoice; i++){

                    if(sparseBooleanArray.get(i)) {

                        selected1 += ListView.getItemAtPosition(i).toString() + "\n";



                    }


                }
                selected = selected1;
                System.out.println(selected);

                Toast.makeText(ProductListActivity.this,

                        selected,

                        Toast.LENGTH_LONG).show();

            }

            /*private View lastSelectedView = null;

            @SuppressLint("ResourceAsColor")
            public void clearSelection() {
                if(lastSelectedView != null) lastSelectedView.setBackgroundColor(android.R.color.white);
            }
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(selected!=null){
                selected = ListView.getItemAtPosition(i).toString();

                clearSelection();
                lastSelectedView = view;
                view.setBackgroundDrawable(view.getContext().getResources().getDrawable(R.color.design_default_color_primary_dark));
            }}
        }*/
        });


    }

    public void initializeUserLocation(){
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                userLocation = location;
                progressDialog.dismiss();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) { }

            @Override
            public void onProviderEnabled(String s) { }

            @Override
            public void onProviderDisabled(String s) { }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    public void onClickItem(View view){
        //String url = "http://www.google.com/";
        //String[] strs = selected.split(" ");


        Intent i = new Intent(Intent.ACTION_WEB_SEARCH);
        i.putExtra(SearchManager.QUERY, selected);
        startActivity(i);
        System.out.println("on click whatever");
    }

    public void onClickSearch(View view){
        Intent intent = new Intent(getApplicationContext(), storesListActivity.class);
        System.out.println("Selected item is : " + selected);
        intent.putExtra("selectedItem",selected);
        intent.putExtra("userLocation",userLocation);
        startActivity(intent);
    }

    public void onClickHome(View view){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    public void onClickBack(View view){
        finish();
    }

}
