package com.example.franciscogutierrez.tacotruck2;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    String session_username = null;
    String session_firstName = null;
    String session_lastName = null;
    String session_userType = null;

    String[] favArray;

    private GoogleMap mMap;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 222) {
            //returning from register
            if (resultCode == RESULT_OK) {
                session_username = data.getStringExtra("username");
                session_firstName = data.getStringExtra("firstName");
                session_lastName = data.getStringExtra("lastName");
                session_userType = data.getStringExtra("userType");
            }
        }

        if (requestCode == 111) {
            //returning from login
            if (resultCode == RESULT_OK) {
                session_username = data.getStringExtra("username");
                session_firstName = data.getStringExtra("firstName");
                session_lastName = data.getStringExtra("lastName");
                session_userType = data.getStringExtra("userType");
            }
        }

        if (requestCode == 333) {
            //returning from log out
            session_username = null;
            session_firstName = null;
            session_lastName = null;
            session_userType = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.getMenu().clear();
        navigationView.removeHeaderView(navigationView.getHeaderView(0));

        if (session_username == null) {
            //logged out user
            navigationView.inflateHeaderView(R.layout.nav_logged_out);

            navigationView.inflateMenu(R.menu.menu_logged_out);
        } else {
            //logged in user
            navigationView.inflateHeaderView(R.layout.nav_logged_in);

            if (session_userType.equals("0")) {
                //regular user
                navigationView.inflateMenu(R.menu.menu_logged_in_regular_user);

                //get favorites & set array
                try {
                    String myURL = "http://cst438-1139.appspot.com/test?function=getFavorites&username=" + session_username;
                    String[] url = new String[]{myURL};
                    String output = new GetData().execute(url).get();
                    JSONObject jObject = new JSONObject(output);
                    JSONArray jsonArray = jObject.getJSONArray("favorites");

                    favArray = new String[jsonArray.length()];

                    for (int i = 0; i < jsonArray.length(); i++) {
                        favArray[i] = (new JSONObject(jsonArray.getString(i))).getString("truckname");
                    }

                } catch (Exception e) {

                }
            } else {
                //truck user
                navigationView.inflateMenu(R.menu.menu_logged_in_truck_operator);
            }

            TextView tv1 = (TextView) findViewById(R.id.logged_in_fname_lname);
            TextView tv2 = (TextView) findViewById(R.id.logged_in_username);

            tv1.setText(session_username);
            tv2.setText("" + session_firstName + " " + session_lastName);
        }
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open_nav, R.string.close_nav);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //map stuff
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_log_in) {
            Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
            MainActivity.this.startActivityForResult(myIntent, 111);
        } else if (id == R.id.menu_register) {
            Intent myIntent = new Intent(MainActivity.this, RegisterActivity.class);
            MainActivity.this.startActivityForResult(myIntent, 222);
        } else if (id == R.id.menu_loggedin_logout) {
            Intent myIntent = new Intent(MainActivity.this, LogOutActivity.class);
            MainActivity.this.startActivityForResult(myIntent, 333);
        } else if (id == R.id.menu_loggedin_truck_profile) {
            Intent myIntent = new Intent(MainActivity.this, TruckProfileActivity.class);
            myIntent.putExtra("loggedInUser", session_username);
            MainActivity.this.startActivity(myIntent);
        } else if (id == R.id.menu_loggedin_favorites) {
            mMap.addMarker(new MarkerOptions().title("Sydney").snippet("The most populous city in Australia.").position(new LatLng(-34.867, 152.206)));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        //check for acces_fine_location permission
        if ( ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            //ask for permission
        } else {
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (lastKnownLocation != null) {
                mMap.setMyLocationEnabled(true);
                LatLng myLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 13));
            } else {
                mMap.setMyLocationEnabled(true);
                LatLng sydney = new LatLng(33.815, -117.923);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));
            }

        }

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                final View v = getLayoutInflater().inflate(R.layout.marker_layout, null);

                TextView tv = (TextView) v.findViewById(R.id.marker_tv_title);
                tv.setText(marker.getTitle());

                TextView tv2 = (TextView) v.findViewById(R.id.marker_tv_snippet);
                tv2.setText(marker.getSnippet());

                if (session_username != null) {
                    //user is logged in, lets check if truck is in favorites list

                    Boolean flag = false;
                    ImageView iv = (ImageView) v.findViewById(R.id.marker_imageView);

                    for (int i = 0; i < favArray.length; i++) {
                        if (favArray[i].equals(marker.getTitle())) {
                            flag = true;
                        }
                    }

                    if (flag) {
                        iv.setImageResource(R.mipmap.heart_full);
                    } else {
                        iv.setImageResource(R.mipmap.heart_border);
                    }

                } else {
                    ImageView iv = (ImageView) v.findViewById(R.id.marker_imageView);
                    iv.setImageResource(R.mipmap.heart_border);
                }





                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {

                        marker.hideInfoWindow();

                        if (session_username != null) {
                            //user is logged in
                            Boolean flag = false;

                            for (int i = 0; i < favArray.length; i++) {
                                if (favArray[i].equals(marker.getTitle())) {
                                    flag = true;
                                }
                            }

                            if (flag) {
                                Toast.makeText(getApplicationContext(), "Removed from favorites.",  Toast.LENGTH_SHORT).show();
                                //TODO ASYNC TASK TO REMOVE FROM FAVS
                            } else {
                                Toast.makeText(getApplicationContext(), "Added to favorites.",  Toast.LENGTH_SHORT).show();
                                //TODO ASYNC TASK TO ADD TO FAVS
                            }

                        } else {
                            //does nothing
                            //Toast.makeText(getApplicationContext(), "",  Toast.LENGTH_SHORT).show();
                        }


                    }
                });
                return v;
            }
        });

        //get truck gps coords, and post them to the map.
        try {
            String myURL = "http://cst438-1139.appspot.com/test?function=getTrucks";
            String[] url = new String[]{myURL};
            String output = new GetData().execute(url).get();
            JSONObject jObject = new JSONObject(output);
            JSONArray jsonArray = jObject.getJSONArray("truckLocations");

            String truckName;
            Double latitude;
            Double longitude;

            for (int i = 0; i < jsonArray.length(); i++) {

                truckName = (new JSONObject(jsonArray.getString(i))).getString("truckname");
                latitude = (new JSONObject(jsonArray.getString(i))).getDouble("latitude");
                longitude = (new JSONObject(jsonArray.getString(i))).getDouble("longitude");
                Log.i("TAG", truckName + " " + latitude + " " + longitude);

                mMap.addMarker(new MarkerOptions().title(truckName).snippet(" ").position(new LatLng(latitude, longitude)));
                //Toast.makeText(getApplicationContext(), truckName, Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.i("TAG", e.toString());

        }

        /*
        LatLng sydney = new LatLng(-33.867, 151.206);
        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));

        mMap.addMarker(new MarkerOptions().title("Sydney").snippet("The most populous city in Australia.").position(sydney));

        SELECT favorites.truckname, trucklocation.latitude, trucklocation.longitude FROM favorites JOIN trucklocation ON favorites.truckname = trucklocation.truck_name WHERE favorites.username = 'test'
        */

    }
}
