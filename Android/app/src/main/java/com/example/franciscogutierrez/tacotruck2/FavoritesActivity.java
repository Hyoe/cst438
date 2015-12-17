//favorites functionality per REQ20
//enable users to see a list of their favorite trucks

package com.example.franciscogutierrez.tacotruck2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

//shows users favorite trucks listing
public class FavoritesActivity extends AppCompatActivity {

    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        lv = (ListView) findViewById(R.id.listView);

        String username = getIntent().getExtras().getString("loggedInUser");

        List<String> arrayList = new ArrayList<String>();
		
		//populates listview with favorite trucks from MySQL database
        try {
            String s = "http://cst438-1139.appspot.com/test?function=getFavorites&username=" + username;
            String[] url = new String[]{s};

            s = new GetData().execute(url).get();

            JSONObject jObject = new JSONObject(s);
            JSONArray jArray = jObject.getJSONArray("favorites");

            for (int i = 0; i < jArray.length(); i++) {
                try {
                    JSONObject oneObject = jArray.getJSONObject(i);
                    String oneObjectsItem = oneObject.getString("truckname");
                    arrayList.add(oneObjectsItem);
                } catch (Exception e) {

                }

            }
			
			//notifies user that no favorites are saved, where applicable
            if (jArray.length() == 0) {
                Toast.makeText(getApplicationContext(), "You do not have any favorites yet.", Toast.LENGTH_LONG).show();
                finish();
            }

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList );

            lv.setAdapter(arrayAdapter);

        } catch (Exception e) {

        }
    }
}
