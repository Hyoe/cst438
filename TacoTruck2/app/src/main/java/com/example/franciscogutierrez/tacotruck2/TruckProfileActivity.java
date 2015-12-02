package com.example.franciscogutierrez.tacotruck2;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TruckProfileActivity extends AppCompatActivity {

    private ProgressDialog pd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_truck_profile);

        this.pd = ProgressDialog.show(this, "Loading Profile...", "Please wait...", true, false);
        try {
            String username = getIntent().getExtras().getString("loggedInUser");
            String myURL = "http://cst438-1139.appspot.com/test?function=getTruckProfile&username=" + username;
            String[] url = new String[]{myURL};
            new GetProfile().execute(url);
        } catch (Exception e) {

        }

        Button submitButton = (Button) findViewById(R.id.truckProfileSubmit);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etName = (EditText) findViewById(R.id.profile_et_truck_name);
                EditText etWebsite = (EditText) findViewById(R.id.profile_et_website);
                EditText etDescription = (EditText) findViewById(R.id.profile_et_description);
                String postURL = "http://cst438-1139.appspot.com/test?function=postTruckProfile&username="
                        + getIntent().getExtras().getString("loggedInUser")
                        + "&truckName=" + etName.getText().toString()
                        + "&website=" + etWebsite.getText().toString()
                        + "&description=" +etDescription.getText().toString();
                String[] url = new String[]{postURL};
                TruckProfileActivity.this.pd = ProgressDialog.show(TruckProfileActivity.this, "Updating Profile...", "Please wait...", true, false);
                new PostProfile().execute(url);
            }
        });


        //http://cst438-1139.appspot.com/test?function=getTruckProfile&username=truck
    }

    private class GetProfile extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            StringBuilder sb = new StringBuilder();
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(params[0]);

            try {
                HttpResponse response = client.execute(httpGet);
                StatusLine sl = response.getStatusLine();
                int sc = sl.getStatusCode();

                if (sc == 200) {
                    //HTTP 200 = OK
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader br = new BufferedReader(new InputStreamReader(content));
                    String line;

                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                }
            } catch (Exception e) {

            }
            return sb.toString();
        }

        @Override
        protected void onPostExecute(String string) {
            super.onPostExecute(string);
            if (TruckProfileActivity.this.pd != null) {
                TruckProfileActivity.this.pd.dismiss();
            }

            TextView statusTv = (TextView) findViewById(R.id.statusTruckProfile);
            statusTv.setText(string);

            EditText etName = (EditText) findViewById(R.id.profile_et_truck_name);
            EditText etWebsite = (EditText) findViewById(R.id.profile_et_website);
            EditText etDescription = (EditText) findViewById(R.id.profile_et_description);

            try {
                JSONObject jObject = new JSONObject(string);
                String status = jObject.getString("status");

                if (status.equals("goodProfile")) {
                    String truckName = jObject.getString("truckName");
                    String website = jObject.getString("website");
                    String description = jObject.getString("description");

                    etName.setText(truckName);
                    etWebsite.setText(website);
                    etDescription.setText(description);
                }

            } catch (Exception e) {

            }


        }
    }

    private class PostProfile extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            StringBuilder sb = new StringBuilder();
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(params[0]);

            try {
                HttpResponse response = client.execute(httpGet);
                StatusLine sl = response.getStatusLine();
                int sc = sl.getStatusCode();

                if (sc == 200) {
                    //HTTP 200 = OK
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader br = new BufferedReader(new InputStreamReader(content));
                    String line;

                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                }
            } catch (Exception e) {

            }
            return sb.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (TruckProfileActivity.this.pd != null) {
                TruckProfileActivity.this.pd.dismiss();
            }
        }
    }
}
