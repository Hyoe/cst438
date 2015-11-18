package com.includeawesome.loginregister;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button bLogout;
    EditText etFirstName, etLastName, etUserName;
    RadioGroup radio_accountType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etFirstName = (EditText) findViewById(R.id.etFName);
        etLastName = (EditText) findViewById(R.id.etLName);
        etUserName = (EditText) findViewById(R.id.etUserName);
        bLogout = (Button) findViewById(R.id.bLogout);

        bLogout.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.bLogout:
                startActivity(new Intent(this, Login.class));
                break;
        }
    }


}
