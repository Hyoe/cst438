package com.example.franciscogutierrez.tacotruck2;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LogOutActivity extends AppCompatActivity {

    private ProgressDialog pd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_out);

        this.pd = ProgressDialog.show(this, "Logging Out...", "Please wait...", true, false);
        new LogOut().execute("banana");
    }

    private class LogOut extends AsyncTask<String, Void, Object> {

        @Override
        protected Object doInBackground(String... params) {
            try {
                Thread.sleep(2000);
            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (LogOutActivity.this.pd != null) {
                LogOutActivity.this.pd.dismiss();
                LogOutActivity.this.finish();
            }
        }
    }
}