package com.example.lookout.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import com.example.lookout.R;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.maps.MapboxMap;

public class DashboardFragment extends FragmentActivity {

    private DashboardViewModel dashboardViewModel;
    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        return root;
    }
//        } else {
//            mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentByTag("com.mapbox.map");
//        }

//        mapView.getMapAsync(new OnMapReadyCallback() {
//            @Override
//            public void onMapReady(@NonNull MapboxMap mapboxMap1) {
//               mapboxMap = mapboxMap1 ;
//
//                mapboxMap.setStyle(Style.OUTDOORS, new Style.OnStyleLoaded() {
//                    @Override
//                    public void onStyleLoaded(@NonNull Style style) {
//
//                    }
//                });
//            }
//        });
//        mapView.getMapAsync(this);

//        mapView = (com.mapbox.mapboxsdk.maps.MapView) root.findViewById(R.id.mapView);
//        mapView.onCreate(savedInstanceState);
//        MapFragment mapFragment = getFragmentManager().findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
//        map = mapView.getMap();

//        mapView.getMapAsync(new OnMapReadyCallback() {
//            @Override
//            public void onMapReady(@NonNull MapboxMap mapboxMap) {
//                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
//                    @Override
//                    public void onStyleLoaded(@NonNull Style style) {
//
//// Map is set up and the style has loaded. Now you can add data or make other map adjustments.
//
//
//                    }
//                });
//            }
//        });

//        map.getUiSettings().setMyLocationButtonEnabled(false);
//        map.setMyLocationEnabled(true);
//        MapsInitializer.initialize(this.getActivity());
//        mapView.getMapAsync(this);
//        MapsInitializer.initialize(this);
//        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(43.1, -87.9), 10);
//        map.animateCamera(cameraUpdate);


//    @SuppressWarnings({"MissingPermission"})
//    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
//// Check if permissions are enabled and if not request
//        if (PermissionsManager.areLocationPermissionsGranted(this)) {
//
//// Get an instance of the LocationComponent.
//            LocationComponent locationComponent = mapboxMap.getLocationComponent();
//
//// Activate the LocationComponent
//            locationComponent.activateLocationComponent(
//                    LocationComponentActivationOptions.builder(this, loadedMapStyle).build());
//
//// Enable the LocationComponent so that it's actually visible on the map
//            locationComponent.setLocationComponentEnabled(true);
//
//// Set the LocationComponent's camera mode
//            locationComponent.setCameraMode(CameraMode.TRACKING);
//
//// Set the LocationComponent's render mode
//            locationComponent.setRenderMode(RenderMode.NORMAL);
//        } else {
//            permissionsManager = new PermissionsManager((PermissionsListener) this);
//            permissionsManager.requestLocationPermissions(this);
//        }
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }
//
//    @Override
//    public void onExplanationNeeded(List<String> permissionsToExplain) {
////        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
//    }
//
//    @Override
//    public void onPermissionResult(boolean granted) {
//        if (granted) {
//            mapboxMap.getStyle(new Style.OnStyleLoaded() {
//                @Override
//                public void onStyleLoaded(@NonNull Style style) {
//                    enableLocationComponent(style);
//                }
//            });
//        } else {
////            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
//            finish();
//        }
//    }

//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        map = googleMap;
//        map.getUiSettings().setZoomControlsEnabled(true);
//        map.getUiSettings().setMyLocationButtonEnabled(false);
//        map.setMyLocationEnabled(true);
//        map.addMarker(new MarkerOptions().position(new LatLng(50, 6)));
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(50, 6), 10));
//    }

//    @Override
//    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
//        DashboardFragment.this.mapboxMap = mapboxMap;

//        mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/mapbox/cjerxnqt3cgvp2rmyuxbeqme7"), new Style.OnStyleLoaded() {
//            @Override
//            public void onStyleLoaded(@NonNull Style style) {
//                enableLocationComponent(style);
//            }
//        });
//    }

//    @Override
//    public void onResume() {
//        mapView.onResume();
//        super.onResume();
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        mapView.onDestroy();
//    }
//
//    @Override
//    public void onLowMemory() {
//        super.onLowMemory();
//        mapView.onLowMemory();
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        mapView.onPause();
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        mapView.onStop();
//    }
//
//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        mapView.onSaveInstanceState(outState);
//    }


//    @SuppressWarnings({"MissingPermission"})
//    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
//// Check if permissions are enabled and if not request
//        if (PermissionsManager.areLocationPermissionsGranted(getContext())) {
//
//// Get an instance of the component
//            LocationComponent locationComponent = mapboxMap.getLocationComponent();
//
//// Activate with options
//            locationComponent.activateLocationComponent(
//                    LocationComponentActivationOptions.builder(getContext(), loadedMapStyle).build());
//
//// Enable to make component visible
//            locationComponent.setLocationComponentEnabled(true);
//
//// Set the component's camera mode
//            locationComponent.setCameraMode(CameraMode.TRACKING);
//
//// Set the component's render mode
//            locationComponent.setRenderMode(RenderMode.COMPASS);
//        } else {
//            permissionsManager = new PermissionsManager(this);
//            permissionsManager.requestLocationPermissions(getActivity());
//        }
//    }

//    @Override
//    public void onExplanationNeeded(List<String> permissionsToExplain) {
//
//    }
//
//    @Override
//    public void onPermissionResult(boolean granted) {
//
//    }
}