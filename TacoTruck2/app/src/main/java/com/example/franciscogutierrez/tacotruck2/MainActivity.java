package com.example.franciscogutierrez.tacotruck2;

import android.content.Intent;
import android.os.Bundle;

import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    String session_username = null;
    String session_firstName = null;
    String session_lastName = null;
    String session_userType = null;

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
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng sydney = new LatLng(-33.867, 151.206);
        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));

        mMap.addMarker(new MarkerOptions().title("Sydney").snippet("The most populous city in Australia.").position(sydney));

    }
}
