package com.example.franciscogutierrez.tacotruck2;
//Enables view of map, trucks on map per REQ1, REQ9

//The file located at cst438/Android/app/build.gradle defines compatbility with
//Android OS from Jelly Bean (API 16) to Marshmallow (API 23) per REQ14

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
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

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
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

        //when you return from another actiivty, we check if that activity had a request code, so we can set certain session variables.
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

        //sets the navigation view depending on if the user is logged out or in. If they're logged in, it shows the nav differently from a regular user and a truck user.
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

		//navigation drawer per REQ18
		//drawer layout per Google material design rules (REQ12)
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
            Intent myIntent = new Intent(MainActivity.this, FavoritesActivity.class);
            myIntent.putExtra("loggedInUser", session_username);
            MainActivity.this.startActivity(myIntent);
        } else if (id == R.id.menu_loggedin_truck_location) {
            new postGPS().execute();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

	//auto-center map at user's location per REQ10
	//view trucks on map per REQ9
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        //check for access_fine_location permission
        if ( ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            //ask for permission
        } else {
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			//if GPS is permitted by user, auto-center to user's location (REQ10)
            if (lastKnownLocation != null) {
                mMap.setMyLocationEnabled(true);
                LatLng myLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 13));
            } else {
				//if GPS not permitted by user, center user in Sydney, Australia (REQ10)
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

			//show truck markers per REQ16. Facilitate adding to favorites.
            @Override
            public View getInfoContents(Marker marker) {
                final View v = getLayoutInflater().inflate(R.layout.marker_layout, null);

                TextView tv = (TextView) v.findViewById(R.id.marker_tv_title);
                tv.setText(marker.getTitle());

                TextView tv2 = (TextView) v.findViewById(R.id.marker_tv_snippet);
                tv2.setText(marker.getSnippet());

                if (session_username != null && session_userType.equals("0")) {
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

                        if (session_username != null && session_userType.equals("0")) {
                            //user is logged in && user = regular user
                            Boolean flag = false;

                            for (int i = 0; i < favArray.length; i++) {
                                if (favArray[i].equals(marker.getTitle())) {
                                    flag = true;
                                }
                            }

                            if (flag) {
                                String myURL = "http://cst438-1139.appspot.com/test?function=editFavorites&username=" + session_username + "&action=remove&value=" + marker.getTitle();
                                myURL = myURL.replaceAll(" ", "%20");
                                myURL = myURL.replaceAll("\n", "%0A");
                                myURL = myURL.replaceAll("\r", "%0D");
                                String[] url = new String[]{myURL};
                                try {
                                    new editFavorites().execute(url).get();
                                } catch (Exception e) {

                                }
                                Toast.makeText(getApplicationContext(), "Removed from favorites.",  Toast.LENGTH_SHORT).show();
							
							//add truck to favorites per REQ6
                            } else {

                                String myURL = "http://cst438-1139.appspot.com/test?function=editFavorites&username=" + session_username + "&action=add&value=" + marker.getTitle();
                                myURL = myURL.replaceAll(" ", "%20");
                                myURL = myURL.replaceAll("\n", "%0A");
                                myURL = myURL.replaceAll("\r", "%0D");
                                String[] url = new String[]{myURL};
                                try {
                                    new editFavorites().execute(url).get();
                                } catch (Exception e) {

                                }
                                Toast.makeText(getApplicationContext(), "Added to favorites.",  Toast.LENGTH_SHORT).show();
                            }

                            //remake favorites list
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

                            //clear all markers
                            mMap.clear();

                            //readd all markers
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

                                    mMap.addMarker(new MarkerOptions().title(truckName).snippet(" ").position(new LatLng(latitude, longitude)));
                                }

                            } catch (Exception e) {
                                Log.i("TAG", e.toString());
                            }



                        }
                    }
                });
                return v;
            }
        });

		//allow users to view map with trucks per REQ1
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

			//includes trucks on map per REQ1
            for (int i = 0; i < jsonArray.length(); i++) {

                truckName = (new JSONObject(jsonArray.getString(i))).getString("truckname");
                latitude = (new JSONObject(jsonArray.getString(i))).getDouble("latitude");
                longitude = (new JSONObject(jsonArray.getString(i))).getDouble("longitude");
				
                mMap.addMarker(new MarkerOptions().title(truckName).snippet(" ").position(new LatLng(latitude, longitude)));
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

	//enable truck users to check in at current location per REQ23
    private class postGPS extends AsyncTask<String, Void, Object> {

        @Override
        protected Object doInBackground(String... params) {
            //get location
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            Boolean errorFlag = false;
            String latitude = "";
            String longitude = "";

            //check for acces_fine_location permission
            if ( ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                //ask for permission
            } else {
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (lastKnownLocation != null) {
                    latitude = Double.toString(lastKnownLocation.getLatitude());
                    longitude = Double.toString(lastKnownLocation.getLongitude());
                } else {
                    errorFlag = true;
                }
            }

            //if errorflag = true, dont post show error toast
            if (!errorFlag) {
                //post to db
                String myURL = "http://cst438-1139.appspot.com/test?function=postLocation&latitude=" + latitude + "&longitude=" + longitude + "&username=" + session_username;
                HttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(myURL);
                try {
                    HttpResponse response = client.execute(httpGet);
                    StatusLine sl = response.getStatusLine();
                    int sc = sl.getStatusCode();

                } catch (Exception e) {

                }

            } else {

            }
            return null;
        }

    }

    private class editFavorites extends AsyncTask<String, Void, Object> {
        @Override
        protected Object doInBackground(String... params) {
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(params[0]);
            try {
                client.execute(httpGet);
            } catch (Exception e) {

            }

            return null;
        }
    }
}
