package codevity.collectionsapp;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class NamesActivity extends ListActivity {
    private ArrayAdapter<String> adapter;
    private List<String> list;
    public String[] values = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_names);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       // setSupportActionBar(toolbar);

        try {
            String result = new Names().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        list = new ArrayList<String>();

        Collections.addAll(list, values);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, list);
        setListAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NamesActivity.this, NamesAddActivity.class));
            }
        });
    }

    class Names extends AsyncTask<Void, Void, String> {

        private Exception exception;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(Void... urls) {


            try {
                URL url = new URL(AppConfig.API_URL + "/names");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    String response = stringBuilder.toString();

                    if(response == null) {
                        response = "THERE WAS AN ERROR";
                    }
                    Log.i("INFO", response);

                    try {
                        Log.i("INFO", "TEST!!!");
                        JSONArray object = (JSONArray) new JSONTokener(response).nextValue();
                        Log.i("INFO2",  Integer.toString(object.length()));
                        values = new String[object.length()];
                        for (int i = 0; i < object.length(); i++) {
                            values[i] = object.getJSONObject(i).getString("name");

                        }
                        //       JSONArray values = object.getJSONArray("names");
                        //Log.i("test", object.getJSONArray("names").toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }

            return null;
        }

        protected void onPostExecute(String response) {

        }
    }

}
