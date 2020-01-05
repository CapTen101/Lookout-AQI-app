package com.example.lookout.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.example.lookout.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class AQIMap extends FragmentActivity implements OnMapReadyCallback {

    ArrayList<String> cityList = new ArrayList<>();
    ArrayList<String> aqiList = new ArrayList<>();
    double[] LatitudeList = new double[1028];
    double[] LongitudeList = new double[1028];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aqimap);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        GoogleMap mMap;
        mMap = googleMap;

        Intent receivedata = getIntent();
        cityList = receivedata.getStringArrayListExtra("CITY_ARRAYLIST");
        aqiList = receivedata.getStringArrayListExtra("AQI_ARRAYLIST");
        LongitudeList = receivedata.getDoubleArrayExtra("LONGITUDE_ARRAY");
        LatitudeList = receivedata.getDoubleArrayExtra("LATITUDE_ARRAY");

        Log.e("yoyo", "" + cityList.get(675) + " " + aqiList.get(456)+LongitudeList[677]);

        assert LatitudeList != null;
        assert LongitudeList != null;
        for (int i = 0; i < cityList.size(); i++) {
            if (!(aqiList.get(i).equals("-"))) {
                int aqi = Integer.parseInt(aqiList.get(i));

                LatLng coordinates = new LatLng(LatitudeList[i],LongitudeList[i]);

                if ((aqi > 0) && (aqi <= 50)) {
                    mMap.addMarker(new MarkerOptions().position(coordinates).title("" + aqiList.get(i) + " " + R.string.us_aqi).snippet(cityList.get(i)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_green_marker42)));
                } else if ((aqi > 50) && (aqi <= 100)) {
                    mMap.addMarker(new MarkerOptions().position(coordinates).title("" + aqiList.get(i) + " " + R.string.us_aqi).snippet(cityList.get(i)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_yellow_marker42)));
                } else if ((aqi > 100) && (aqi <= 150)) {
                    mMap.addMarker(new MarkerOptions().position(coordinates).title("" + aqiList.get(i) + " " + R.string.us_aqi).snippet(cityList.get(i)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_orange_marker42)));
                } else if ((aqi > 150) && (aqi <= 200)) {
                    mMap.addMarker(new MarkerOptions().position(coordinates).title("" + aqiList.get(i) + " " + R.string.us_aqi).snippet(cityList.get(i)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_red_marker42)));
                } else if ((aqi > 200) && (aqi <= 300)) {
                    mMap.addMarker(new MarkerOptions().position(coordinates).title("" + aqiList.get(i) + " " + R.string.us_aqi).snippet(cityList.get(i)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_maroon_marker42)));
                } else if (aqi > 300) {
                    mMap.addMarker(new MarkerOptions().position(coordinates).title("" + aqiList.get(i) + " " + R.string.us_aqi).snippet(cityList.get(i)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_purple_marker42)));
                }
            }
        }
    }
}
