package com.example.irri.patrimonioudec;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SharedPreferences sharedPreferences;
    private DBHelper db;
    private Context context = this;
    private MyLocationListener mll;
    private Circle grande,chico;
    private ArrayList<Marker> markers;
    private boolean listo;
    private LocationManager locationManager;
    private Location last;
    private boolean primero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listo=false;
        last=null;
        primero = true;
        sharedPreferences = getSharedPreferences("preferencias", MODE_PRIVATE);
        db = new DBHelper(this);
        setContentView(R.layout.activity_maps);
        ImageButton ib = findViewById(R.id.imageButton);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context,Opciones.class);
                startActivity(i);
            }
        });

        ImageButton ib2 = findViewById(R.id.imageButton2);
        ib2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(last!=null){
                    LatLng latLng = new LatLng(last.getLatitude(),last.getLongitude());
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(latLng);
                    mMap.animateCamera(cameraUpdate);
                }
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onResume(){
        super.onResume();
        Log.v("alo","resumi");
        if(listo){
            if(sharedPreferences.getBoolean("libre",false)){
                Log.v("alo","true");
                mMap.getUiSettings().setScrollGesturesEnabled(true);
                mMap.getUiSettings().setZoomGesturesEnabled(true);
            }else{
                Log.v("alo","false");
                mMap.getUiSettings().setScrollGesturesEnabled(false);
                mMap.getUiSettings().setZoomGesturesEnabled(false);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            if(sharedPreferences.getBoolean("ahorra",false)){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 7.0f, mll);
            }else{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 3.0f, mll);
            }
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMinZoomPreference(17.0f);
        mMap.setMaxZoomPreference(18.0f);

        if(!sharedPreferences.getBoolean("libre",false)){
            mMap.getUiSettings().setScrollGesturesEnabled(false);
            mMap.getUiSettings().setZoomGesturesEnabled(false);

        }
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.mapa));
        markers = new ArrayList<>();
        db.abre();
        Cursor c = db.getAll();
        c.moveToFirst();
        do{
            String nombre = c.getString(4);
            LatLng latLng = new LatLng(c.getDouble(2),c.getDouble(3));
            int idString = getResources().getIdentifier(nombre,"string",getPackageName());
            Log.v("busco",nombre);
            String titulo = getString(idString);
            Marker m = mMap.addMarker(new MarkerOptions().position(latLng).title(titulo));
            markers.add(m);
            m.setTag(c.getInt(0));

        }while(c.moveToNext());
        db.cierra();
        mMap.setOnMarkerClickListener(new MyMarkerClickListener());

        LatLng latLng = new LatLng(0.0, 0.0);
        grande = mMap.addCircle(new CircleOptions().radius(25.0f).center(latLng)
                .strokeWidth(5.0f).strokeColor(0x770000FF).fillColor(0x3300FFFF));
        chico = mMap.addCircle(new CircleOptions().radius(2.5f).center(latLng)
                .fillColor(0x770000FF).strokeColor(0));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f));
        mll = new MyLocationListener();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        if(sharedPreferences.getBoolean("ahorra",false)){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 7.0f, mll);
        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 3.0f, mll);
        }
        listo=true;

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (requestCode == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return;
            }
            if(sharedPreferences.getBoolean("ahorra",false)){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 7.0f, mll);
            }else{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 3.0f, mll);
            }
            listo=true;
        }
    }
    private class MyLocationListener implements LocationListener{

        @Override
        public void onLocationChanged(Location location) {
            last = location;
            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
            if(!sharedPreferences.getBoolean("libre",false)||primero){
                Log.v("alo","me movi");
                primero = false;
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(latLng);
                mMap.animateCamera(cameraUpdate);
            }
            grande.setCenter(latLng);
            chico.setCenter(latLng);
            for(int i=0;i<markers.size();i++){
                Marker m = markers.get(i);
                Location aux = new Location("asd");
                aux.setLatitude(m.getPosition().latitude);
                aux.setLongitude(m.getPosition().longitude);
                double dist = location.distanceTo(aux);
                if(dist<25.0f){
                    m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                    m.showInfoWindow();
                }else{
                    m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    m.hideInfoWindow();
                }

            }

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }
    private class MyMarkerClickListener implements GoogleMap.OnMarkerClickListener{

        @Override
        public boolean onMarkerClick(Marker marker) {
            if(!sharedPreferences.getBoolean("libre",false)){   //modo libre da lo mismo
                if(last==null){ //aun no se donde estoy
                    return true;
                }
                Location aux = new Location("asd");
                aux.setLatitude(marker.getPosition().latitude);
                aux.setLongitude(marker.getPosition().longitude);
                double dist = last.distanceTo(aux);
                if(dist>25.0f){
                    return true;    //está muy lejos
                }
            }
            int id = (int) marker.getTag();
            Intent i = new Intent(context, MainActivity.class);
            i.putExtra("id",id);
            startActivity(i);
            return true;    //previene movimiento de la camara
        }
    }
}
