package com.example.lookout.ui.notifications;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lookout.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

public class NearestActivity extends AppCompatActivity {

    private String myIP;
    private String myCity;
    private String myState;
    private String myCountry;
    private static final String MY_IP_URL = "https://api.ipify.org?format=json";
    private static final String MY_NEAREST_URL = "https://api.airvisual.com/v2/nearest_city?key=9a11661d-a1a4-4629-8030-3669adaade7d";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearest);
    }

    public class IPHttpRequest extends AsyncTask<URL, String, String> {

        @Override
        protected String doInBackground(URL... urls) {

            URL url = null;
            try {
                url = new URL(MY_IP_URL);
            } catch (MalformedURLException exception) {
                Log.e("errorTag", "Error with creating URL", exception);
                return null;
            }

            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                Log.e("errorTag", "Error in request");
            }
            return jsonResponse;
        }

        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                inputStream = urlConnection.getInputStream();
                jsonResponse = readInputStream(inputStream);

            } catch (IOException e) {

            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return jsonResponse;
        }

        private String readInputStream(InputStream inputStream) throws IOException {
            String copyTheOutput = null;
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }

            String finalOutput = output.toString();
            JSONObject parentObject = null;
            try {
                parentObject = new JSONObject(finalOutput);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                String IP = parentObject.getString("ip");
                copyTheOutput = IP;
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return copyTheOutput;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            myIP = s;
        }
    }

    public class NearestHttpRequest extends AsyncTask<URL, String, String> {

        @Override
        protected String doInBackground(URL... urls) {

            URL url = null;
            String jsonResponse = "";
            try {
                url = new URL(MY_NEAREST_URL);
            } catch (MalformedURLException exception) {
                Log.e("errorTag", "Error with creating URL", exception);
                return null;
            }

            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                Log.e("errorTag", "Error in request");
            }
            return jsonResponse;
        }

        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("x-forwarded-for", myIP);
                urlConnection.connect();

                inputStream = urlConnection.getInputStream();
                jsonResponse = readInputStream(inputStream);

            } catch (IOException e) {

            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return jsonResponse;
        }

        private String readInputStream(InputStream inputStream) throws IOException {
            String copyTheOutput = null;
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }

            String finalOutput = output.toString();
            String dataOutput;
            String weatherOutput;
            String pollutionOutput;
            JSONObject parentObject;
            JSONArray dataArray;
            JSONObject dataObject;
            JSONArray currentArray;
            JSONArray weatherArray;
            JSONArray pollutionArray;
            try {
                parentObject = new JSONObject(finalOutput);
                dataArray = parentObject.getJSONArray("data");
                dataOutput = dataArray.toString();
                dataObject = new JSONObject(dataOutput);
                myCity = dataObject.getString("city");
                myState = dataObject.getString("state");
                myCountry = dataObject.getString("country");
                currentArray = dataObject.getJSONArray("current");
                weatherArray = currentArray.getJSONArray(0);
                weatherOutput = weatherArray.toString();
                pollutionArray = currentArray.getJSONArray(1);
                pollutionOutput = pollutionArray.toString();


            } catch (JSONException e) {
                e.printStackTrace();
            }

            return copyTheOutput;
        }

    }
}
