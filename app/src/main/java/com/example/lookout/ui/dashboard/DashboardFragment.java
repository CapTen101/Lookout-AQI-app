package com.example.lookout.ui.dashboard;

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
import java.util.ArrayList;

public class DashboardFragment extends Fragment {

    ArrayList<String> cityList = new ArrayList<>(1);
    ArrayList<String> aqiList = new ArrayList<>(1);
    double[] LatitudeList = new double[1028];
    double[] LongitudeList = new double[1028];
    private String COMPLETE_DATA_URL = "https://api.waqi.info/map/bounds/?latlng=-85,-180,85,180&token=58af52459e24f1c64ccc68ce507a40569ddebed6";
    private Button map_button;
    private ProgressBar progressBar;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        AllCitiesRequest requestCountry = new AllCitiesRequest();
        requestCountry.execute();

        progressBar = root.findViewById(R.id.progress_bar);

        map_button = root.findViewById(R.id.mapbutton);
        map_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openMap = new Intent(getActivity(), AQIMap.class);
                openMap.putExtra("CITY_ARRAYLIST", cityList);
                openMap.putExtra("AQI_ARRAYLIST", aqiList);
                openMap.putExtra("LATITUDE_ARRAY", LatitudeList);
                openMap.putExtra("LONGITUDE_ARRAY", LongitudeList);
                startActivity(openMap);
            }
        });
        return root;
    }

    public class AllCitiesRequest extends AsyncTask<URL, String, String> {

        @Override
        protected String doInBackground(URL... urls) {

            URL url;
            try {
                url = new URL(COMPLETE_DATA_URL);
            } catch (MalformedURLException exception) {
                Log.e("errorTag", "Error with creating URL", exception);
                return null;
            }

            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                Log.e("errorTag", "Error in country request");
            }
            return jsonResponse;
        }

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
            Log.e("array ready", cityList.get(2) + " " + LatitudeList[2] + " " + LongitudeList[2]);
            progressBar.setVisibility(View.GONE);
            map_button.setVisibility(View.VISIBLE);
        }

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

        private String readInputStream(InputStream inputStream) throws IOException {

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
            return finalOutput;
        }
    }
}