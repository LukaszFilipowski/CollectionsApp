package codevity.collectionsapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

public class NamesAddActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_names_add);

        final Button button = (Button) findViewById(R.id.addName);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText nameField = (EditText)findViewById(R.id.nameField);
                String name = nameField.getText().toString();

                SharedPreferences settings = getSharedPreferences(AppConfig.PREFS_NAME, 0);
                String token = settings.getString("token", "");
                if (token != "") {

                    JSONObject postData = new JSONObject();
                    try {
                        postData.put("name", name);
                        postData.put("token", token);

                        new SendDeviceDetails().execute(AppConfig.API_URL + "/names", postData.toString());
                        Snackbar.make(v, "Pomyślnie dodano imię", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                        startActivity(new Intent(NamesAddActivity.this, NamesActivity.class));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private class SendDeviceDetails extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            try {
                // http client

                URL murl = new URL(params[0]);
                urlConnection = (HttpURLConnection) murl.openConnection();

                urlConnection.setRequestProperty("Content-Type", "application/json;odata=verbose");
                urlConnection.setRequestProperty("Accept", "application/json;odata=verbose");


                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);


                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(params[1].toString());
                writer.flush();
                writer.close();
                os.close();


                int resCode = urlConnection.getResponseCode();
                Log.i("TAG", "response code=>" + resCode);

                } catch (Exception e) {
                    Log.e("ERR", e.getMessage());
                }

                return "TAK";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("TAG", result); // this is expecting a response code to be sent from your server upon receiving the POST data
        }
    }
}
