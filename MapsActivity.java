package com.example.cyclogard;


import android.os.Bundle;

import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import java.util.ArrayList;

import java.util.List;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.InfoWindowAdapter {


    private GoogleMap googleMap;

    private List<LatLng> locationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);





        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                googleMap = map;

                if (googleMap != null) {
                    showMarkers();
                }
            }
        });

        ViewTreeObserver.OnGlobalLayoutListener layoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                zoomToMarkers();


                View mapView = mapFragment.getView();
                if (mapView.getViewTreeObserver().isAlive()) {
                    mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        };




        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {

                googleMap = map;
                googleMap.setInfoWindowAdapter(MapsActivity.this);


                View mapView = mapFragment.getView();
                if (mapView != null) {
                    mapView.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
                }
            }
        });



    }



    private void showMarkers() {



        if (googleMap != null) {



            DataBaseHelper dataBaseHelper = new DataBaseHelper(MapsActivity.this);

            List ListaLat = dataBaseHelper.getLat();
            List ListaLon = dataBaseHelper.getLon();
            List ListaData = dataBaseHelper.getData();


            for (int i = 0; i < ListaLat.size(); i++) {

                LatLng latLng = new LatLng((Double) ListaLat.get(i),(Double) ListaLon.get(i));
                String data = (String) ListaData.get(i);
                String title = "Ocorrência " + (i + 1);
                String snippet = "Coordenadas: " + latLng.latitude + ", " + latLng.longitude + "\n"
                        + "Data e Hora: " + data;



                BitmapDescriptor icon = BitmapHelper.vectorToBitmap(MapsActivity.this, R.drawable.ic_baseline_warning_24, ContextCompat.getColor(MapsActivity.this, R.color.red));
                locationList.add(latLng);

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .title(title)
                        .snippet(snippet)
                        .icon(icon);

                googleMap.addMarker(markerOptions);
            }

        }
    }




    private void zoomToMarkers() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : locationList) {
            builder.include(latLng);
        }

        LatLngBounds bounds = builder.build();
        int padding = 100;

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        googleMap.animateCamera(cameraUpdate);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setInfoWindowAdapter(this);
        showMarkers();

    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {

        View infoWindowView = getLayoutInflater().inflate(R.layout.custom_marker_info, null);

        TextView txtTitle = infoWindowView.findViewById(R.id.txt_title);
        TextView txtSnippet = infoWindowView.findViewById(R.id.txt_snippet);

        txtTitle.setText(marker.getTitle());
        txtSnippet.setText(marker.getSnippet());

        return infoWindowView;
    }

}

