package com.example.lookout.Fragment.WorldMap;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class GoToMapButtonFragment extends Fragment {

    ArrayList<String> cityList = new ArrayList<>(1);
    ArrayList<String> aqiList = new ArrayList<>(1);
    double[] LatitudeList = new double[1028];
    double[] LongitudeList = new double[1028];
    private Button map_button;
    private ProgressBar progressBar;

    private String API_TOKEN;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        AllCitiesRequest requestCountry = new AllCitiesRequest();
        requestCountry.execute();

        API_TOKEN = getResources().getString(R.string.API_TOKEN);

        progressBar = root.findViewById(R.id.progress_bar);

        // OnCLickListener for the map_button
        map_button = root.findViewById(R.id.mapbutton);
        map_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openMap = new Intent(getActivity(), AQIWorldMapActivity.class);
                openMap.putExtra("CITY_ARRAYLIST", cityList);
                openMap.putExtra("AQI_ARRAYLIST", aqiList);
                openMap.putExtra("LATITUDE_ARRAY", LatitudeList);
                openMap.putExtra("LONGITUDE_ARRAY", LongitudeList);
                startActivity(openMap);
            }
        });
        return root;
    }

    // This ASyncTask handles the asynchronous network request sent to the API
    public class AllCitiesRequest extends AsyncTask<URL, String, String> {

        @Override
        protected String doInBackground(URL... urls) {

            URL url;

            // Enclosing the URL Creation in a try-catch
            // for logging any possible exceptions that might occur during runtime
            try {
                url = new URL("https://api.waqi.info/map/bounds/?latlng=-85,-180,85,180&token=" + API_TOKEN);
            } catch (MalformedURLException exception) {
                Log.e("errorTag", "Error with creating URL", exception);
                return null;
            }

            // Had to initialise empty here
            // since the compiler was giving error for an uninitialised variable
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                Log.e("errorTag", "Error in country request");
            }
            return jsonResponse;
        }

        // This method is executed after the asynchronous request is completed and has sent the response
        // This method is executed after doInBackground() method
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            JSONObject parentObject;
            JSONArray dataArray;
            try {
                parentObject = new JSONObject(s);
                dataArray = parentObject.getJSONArray("data");
                for (int i = 0; i < 1028; i++) {
                    cityList.add(dataArray.getJSONObject(i).getJSONObject("station").getString("name"));
                    aqiList.add(dataArray.getJSONObject(i).getString("aqi"));
                    LatitudeList[i] = dataArray.getJSONObject(i).getDouble("lat");
                    LongitudeList[i] = dataArray.getJSONObject(i).getDouble("lon");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressBar.setVisibility(View.GONE);
            map_button.setVisibility(View.VISIBLE);
        }

        // This function makes the HTTP request and gets the response
        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse;
            HttpURLConnection urlConnection;
            InputStream inputStream;
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            inputStream = urlConnection.getInputStream();
            jsonResponse = readInputStream(inputStream);
            urlConnection.disconnect();

            return jsonResponse;
        }

        // This function parses the information out of the received response from API
        private String readInputStream(InputStream inputStream) throws IOException {

            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }

            return output.toString();
        }
    }
}