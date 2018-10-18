package com.example.user.sqliteapp02;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.user.sqliteapp02.negocio.Cliente;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class cliente_mapa_y_mapaasignar extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener, GoogleMap.OnMapClickListener, View.OnClickListener {

    private double latitud=0, longitud=0;
    GoogleMap mapa;
    Marker marcador;

    private String nombre;

    FloatingActionButton fabMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Remover title bar
        getSupportActionBar().hide();

        setContentView(R.layout.activity_cliente_mapa_y_mapaasignar);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_asignar);
        mapFragment.getMapAsync(this);


        fabMap = (FloatingActionButton)findViewById(R.id.fabmap);
        fabMap.setOnClickListener(this);


        Bundle p = this.getIntent().getExtras();
        int position = p.getInt("position");


        if (position == -1){ //Cliente nuevo
            this.latitud = -6.7719709;
            this.longitud = -79.8374808;
            this.nombre = "Ubicación";
        }else{
            Cliente item = Cliente.listaCli.get(position);
            this.latitud = item.getLatitud();
            this.longitud = item.getLongitud();
            this.nombre = item.getNombre();
        }



        System.out.println("Lat:" + this.latitud);
        System.out.println("Long:" + this.longitud);



    }

    @Override
    public void onMapReady(GoogleMap map) {
        mapa = map;
        map.setOnMarkerDragListener(this);
        map.setOnMapClickListener(this);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.draggable(true);
        markerOptions.position(new LatLng(this.latitud, this.longitud));
        markerOptions.title(this.nombre);
        markerOptions.snippet("Puede arrastrar y ubicar una dirección");

        marcador = map.addMarker(markerOptions);
        marcador.showInfoWindow();


        CameraPosition camPos = new CameraPosition.Builder()
                .target(getCenterCoordinate())
                .zoom(16)
                .build();
        CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);
        //map.animateCamera(camUpd3);
        map.moveCamera(camUpd3);
        //map.setTrafficEnabled(true);


    }

    public LatLng getCenterCoordinate(){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(new LatLng(this.latitud, this.longitud));
        LatLngBounds bounds = builder.build();
        return bounds.getCenter();
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        this.latitud = marker.getPosition().latitude;
        this.longitud = marker.getPosition().longitude;
        mapa.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));

        Toast.makeText(this, "Ubicación, Lat: " + String.valueOf(this.latitud) + "  Long: " + String.valueOf(this.longitud), Toast.LENGTH_LONG).show();

    }

    @Override
    public void onMapClick(LatLng latLng) {
        marcador.setPosition(latLng);
        this.latitud = latLng.latitude;
        this.longitud = latLng.longitude;
        mapa.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        marcador.showInfoWindow();
        Toast.makeText(this, "Ubicación, Lat: " + String.valueOf(this.latitud) + "  Long: " + String.valueOf(this.longitud), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        Bundle p = new Bundle();
        p.putDouble("latitud", this.latitud);
        p.putDouble("longitud", this.longitud);
        intent.putExtras(p);
        //intent.putExtra("DATA", "your string");
        setResult(RESULT_OK, intent);
        finish();
    }
}
