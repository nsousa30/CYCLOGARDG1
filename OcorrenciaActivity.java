package com.example.cyclogard;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class OcorrenciaActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.InfoWindowAdapter {


    private GoogleMap googleMap;

    private List<LatLng> locationList = new ArrayList<>();

    private ImageView photoImageView;

    String ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocorr);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);

        photoImageView = findViewById(R.id.photoImageView);



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
                googleMap.setInfoWindowAdapter(OcorrenciaActivity.this);

                Intent intent = getIntent();
                if (intent.hasExtra("ID")) {
                    ID = (String) intent.getSerializableExtra("ID");

                    loadPhoto(ID);


                    if (googleMap != null) {
                        showMarkers(Integer.parseInt(ID)-1);
                    }
                }
                View mapView = mapFragment.getView();
                if (mapView != null) {
                    mapView.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
                }
            }
        });






    }





    private void showMarkers(int ID) {



        if (googleMap != null) {



            DataBaseHelper dataBaseHelper = new DataBaseHelper(OcorrenciaActivity.this);

            List ListaLat = dataBaseHelper.getLat();
            List ListaLon = dataBaseHelper.getLon();
            List ListaData = dataBaseHelper.getData();


            int i = ID;

                LatLng latLng = new LatLng((Double) ListaLat.get(i),(Double) ListaLon.get(i));
                String data = (String) ListaData.get(i);
                String title = "Ocorrência " + (i + 1);
                String snippet = "Coordenadas: " + latLng.latitude + ", " + latLng.longitude + "\n"
                        + "Data e Hora: " + data;



                BitmapDescriptor icon = BitmapHelper.vectorToBitmap(OcorrenciaActivity.this, R.drawable.ic_baseline_warning_24, ContextCompat.getColor(OcorrenciaActivity.this, R.color.red));
                locationList.add(latLng);

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .title(title)
                        .snippet(snippet)
                        .icon(icon);

                googleMap.addMarker(markerOptions);
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

    private void loadPhoto(String ID) {
        String ID2 = ID;
        File directory = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "CYCLOGARD");
        if (directory.exists()) {
            File photoFile = new File(directory, ""+ID2+".jpg");
            if (photoFile.exists()) {

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;

                Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath(), options);

                if (bitmap != null) {
                    photoImageView.setImageBitmap(bitmap);

                } else {
                    Toast.makeText(OcorrenciaActivity.this, "Falha ao carregar a imagem", Toast.LENGTH_SHORT).show();
                }
            } else {

            }
        } else {
            Toast.makeText(OcorrenciaActivity.this, "A pasta CYCLOGARD não existe", Toast.LENGTH_SHORT).show();
        }
    }





}

