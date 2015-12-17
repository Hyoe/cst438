package com.example.franciscogutierrez.tacotruck2;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

class GetData extends AsyncTask<String, Integer, String> {

    private static final String TAG = "MYTAG";

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

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
            Log.i(TAG, e.toString());
        }
        return sb.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
