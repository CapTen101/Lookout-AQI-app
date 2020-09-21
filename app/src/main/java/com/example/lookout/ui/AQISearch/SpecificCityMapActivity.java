package com.example.lookout.ui.AQISearch;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.example.lookout.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class SpecificCityMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Double CityLatitude;
    private Double CityLongitude;
    private int AQI;
    private String CountryName;
    private String StateName;
    private String CityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_city_map);

        Intent ReceiveLatLng = getIntent();
        CityLatitude = ReceiveLatLng.getExtras().getDouble("LATITUDE");
        CityLongitude = ReceiveLatLng.getExtras().getDouble("LONGITUDE");
        AQI = ReceiveLatLng.getExtras().getInt("AQI");
        CountryName = ReceiveLatLng.getStringExtra("COUNTRY");
        StateName = ReceiveLatLng.getStringExtra("STATE");
        CityName = ReceiveLatLng.getStringExtra("CITY");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng cityMarker = new LatLng(CityLatitude, CityLongitude);

        if ((AQI > 0) && (AQI <= 50)) {
            mMap.addMarker(new MarkerOptions().position(cityMarker).title(AQI + " " + getResources().getString(R.string.us_aqi)).snippet(CityName + ", " + StateName + ", " + CountryName).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_green_marker42)));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cityMarker, 7));
        } else if ((AQI > 50) && (AQI <= 100)) {
            mMap.addMarker(new MarkerOptions().position(cityMarker).title(AQI + " " + getResources().getString(R.string.us_aqi)).snippet(CityName + ", " + StateName + ", " + CountryName).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_yellow_marker42)));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cityMarker, 7));
        } else if ((AQI > 100) && (AQI <= 150)) {
            mMap.addMarker(new MarkerOptions().position(cityMarker).title(AQI + " " + getResources().getString(R.string.us_aqi)).snippet(CityName + ", " + StateName + ", " + CountryName).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_orange_marker42)));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cityMarker, 7));
        } else if ((AQI > 150) && (AQI <= 200)) {
            mMap.addMarker(new MarkerOptions().position(cityMarker).title(AQI + " " + getResources().getString(R.string.us_aqi)).snippet(CityName + ", " + StateName + ", " + CountryName).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_red_marker42)));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cityMarker, 7));
        } else if ((AQI > 200) && (AQI <= 300)) {
            mMap.addMarker(new MarkerOptions().position(cityMarker).title(AQI + " " + getResources().getString(R.string.us_aqi)).snippet(CityName + ", " + StateName + ", " + CountryName).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_maroon_marker42)));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cityMarker, 7));
        } else if (AQI > 300) {
            mMap.addMarker(new MarkerOptions().position(cityMarker).title(AQI + " " + getResources().getString(R.string.us_aqi)).snippet(CityName + ", " + StateName + ", " + CountryName).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_purple_marker42)));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cityMarker, 7));
        }

    }
}
