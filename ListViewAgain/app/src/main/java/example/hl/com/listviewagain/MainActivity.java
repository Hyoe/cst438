package example.hl.com.listviewagain;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends ListActivity {

   private ProgressDialog pDialog;

   // URL to get favorites JSON
   private static String url = "http://cst438-1139.appspot.com/test?function=getFavorites&username=test";

   // JSON Node names
   private static final String FAVORITES = "favorites";
   private static final String TRUCK_NAME = "truckname";

   // favorites JSONArray
   JSONArray favorites = null;

   // Hashmap for ListView
   ArrayList<HashMap<String, String>> favoritesList;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      favoritesList = new ArrayList<HashMap<String, String>>();

      ListView lv = getListView();

      // Calling async task to get json
      new GetFavorites().execute();
   }

   /**
    * Async task class to get json by making HTTP call
    * */
   private class GetFavorites extends AsyncTask<Void, Void, Void> {

      @Override
      protected void onPreExecute() {
         super.onPreExecute();
         // Showing progress dialog
         pDialog = new ProgressDialog(MainActivity.this);
         pDialog.setMessage("Please wait...");
         pDialog.setCancelable(false);
         pDialog.show();

      }

      @Override
      protected Void doInBackground(Void... arg0) {
         // Creating service handler class instance
         ServiceHandler sh = new ServiceHandler();

         // Making a request to url and getting response
         String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

         Log.d("Response: ", "> " + jsonStr);

         if (jsonStr != null) {
            try {
               JSONObject jsonObj = new JSONObject(jsonStr);

               // Getting JSON Array node
               favorites = jsonObj.getJSONArray(FAVORITES);

               // looping through All favorites
               for (int i = 0; i < favorites.length(); i++) {
                  JSONObject c = favorites.getJSONObject(i);


                  String name = c.getString(TRUCK_NAME);

                  // tmp hashmap for single favorite
                  HashMap<String, String> favorite = new HashMap<String, String>();

                  // adding each child node to HashMap key => value
                  favorite.put(TRUCK_NAME, name);

                  // adding favorite to favorite list
                  favoritesList.add(favorite);
               }
            } catch (JSONException e) {
               e.printStackTrace();
            }
         } else {
            Log.e("ServiceHandler", "Couldn't get any data from the url");
         }

         return null;
      }

      @Override
      protected void onPostExecute(Void result) {
         super.onPostExecute(result);
         // Dismiss the progress dialog
         if (pDialog.isShowing())
            pDialog.dismiss();
         /**
          * Updating parsed JSON data into ListView
          * */
         ListAdapter adapter = new SimpleAdapter(
            MainActivity.this, favoritesList,
            R.layout.list_item, new String[] {TRUCK_NAME}, new int[] { R.id.name });

         setListAdapter(adapter);
      }

   }

}