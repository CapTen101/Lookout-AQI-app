package com.example.lookout.ui.notifications;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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

public class  NotificationsFragment extends Fragment {

    private int aqi;
    private int temperature;
    private double cityLatitude;
    private double cityLongitude;
    private String myCity;
    private String myState;
    private String myCountry;
    private String weatherIconCode;
    private String category;
    private String[] countryList = new String[96];
    private AutoCompleteTextView countrySearch;
    private AutoCompleteTextView stateSearch;
    private AutoCompleteTextView citySearch;
    private TextView City;
    private TextView State;
    private TextView Country;
    private TextView Temperature;
    private TextView Aqi;
    private TextView Category;
    private ImageView WeatherIcon;
    private ImageView Face;
    private ImageView OtherSideFaceColor;
    private CardView aqiCard;
    private Button map_button;
    private ProgressBar ProgressBar1;
    private ProgressBar ProgressBar2;
    private ProgressBar ProgressBar3;
    ArrayList<String> stateList = new ArrayList<>(1);
    ArrayList<String> cityList = new ArrayList<>(1);
    private final String COUNTRY_LIST_URL = "https://api.airvisual.com/v2/countries?key=9a11661d-a1a4-4629-8030-3669adaade7d";

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        Temperature = root.findViewById(R.id.temperature_value);
        Aqi = root.findViewById(R.id.aqi);
        Category = root.findViewById(R.id.category);
        City = root.findViewById(R.id.city);
        State = root.findViewById(R.id.state);
        Country = root.findViewById(R.id.country);
        WeatherIcon = root.findViewById(R.id.weather_icon);
        Face = root.findViewById(R.id.face);
        OtherSideFaceColor = root.findViewById(R.id.other_side_face_color);
        aqiCard = root.findViewById(R.id.aqi_card);
        ProgressBar1 = root.findViewById(R.id.progressBar1);
        ProgressBar2 = root.findViewById(R.id.progressBar2);
        ProgressBar3 = root.findViewById(R.id.progressBar3);

        Button nearestData;
        nearestData = root.findViewById(R.id.nearest_data_button);
        nearestData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentNearest = new Intent(getActivity(), NearestActivity.class);
                startActivity(intentNearest);
            }
        });

        countrySearch = root.findViewById(R.id.country_search);
        countrySearch.setHint("Search your Country here");
        stateSearch = root.findViewById(R.id.state_search);
        stateSearch.setHint("Search your State here");
        citySearch = root.findViewById(R.id.city_search);
        citySearch.setHint("Search your City here");

        COUNTRYHttpRequest requestCountry = new COUNTRYHttpRequest();
        requestCountry.execute();

        ArrayAdapter<String> adapterCountry = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, countryList);
        countrySearch.setAdapter(adapterCountry);
        countrySearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myCountry = countrySearch.getText().toString();

                if(countrySearch.getText().toString().equals("United Kingdom")){
                    myCountry = "UK";
                }

                ProgressBar2.setVisibility(View.VISIBLE);

                STATEHttpRequest requestState = new STATEHttpRequest();
                requestState.execute();

                ArrayAdapter<String> adapterState = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, stateList);
                stateSearch.setAdapter(adapterState);
            }
        });

        stateSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myState = stateSearch.getText().toString();

                ProgressBar3.setVisibility(View.VISIBLE);

                CITYHttpRequest requestState = new CITYHttpRequest();
                requestState.execute();

                ArrayAdapter<String> adapterCity = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, cityList);
                citySearch.setAdapter(adapterCity);
            }
        });

        citySearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myCity = citySearch.getText().toString();
                SpecificCityRequest requestCity = new SpecificCityRequest();
                requestCity.execute();
            }
        });

        countrySearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stateList.clear();
            }
        });

        stateSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cityList.clear();
            }
        });

        map_button = root.findViewById(R.id.mapgoto);

        return root;
    }


    public class COUNTRYHttpRequest extends AsyncTask<URL, String, String> {

        @Override
        protected String doInBackground(URL... urls) {

            URL url;
            try {
                url = new URL(COUNTRY_LIST_URL);
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
            ProgressBar1.setVisibility(View.GONE);
            countrySearch.setVisibility(View.VISIBLE);
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
            JSONObject parentObject;
            JSONArray dataArray;

            try {
                parentObject = new JSONObject(finalOutput);
                dataArray = parentObject.getJSONArray("data");
                for (int i = 0; i < 96; i++) {
                    countryList[i] = dataArray.getJSONObject(i).getString("country");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return finalOutput;
        }
    }

    public class STATEHttpRequest extends AsyncTask<URL, String, String> {

        @Override
        protected String doInBackground(URL... urls) {

            URL url;
            try {
                final String STATE_LIST_URL = "https://api.airvisual.com/v2/states?country=" + myCountry + "&key=9a11661d-a1a4-4629-8030-3669adaade7d";
                url = new URL(STATE_LIST_URL);
            } catch (MalformedURLException exception) {
                Log.e("errorTag", "Error with creating URL", exception);
                return null;
            }

            String jsonResponse = "";
            try {

                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                Log.e("errorTag", "Error in state request");
            }
            return jsonResponse;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ProgressBar2.setVisibility(View.GONE);
            stateSearch.setVisibility(View.VISIBLE);
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
            JSONObject parentObject;
            JSONArray dataArray;

            try {
                parentObject = new JSONObject(finalOutput);
                dataArray = parentObject.getJSONArray("data");
                for (int i = 0; i < dataArray.length(); i++) {
                    stateList.add(dataArray.getJSONObject(i).getString("state"));
                    Log.e("array", stateList.toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return finalOutput;
        }
    }

    public class CITYHttpRequest extends AsyncTask<URL, String, String> {

        @Override
        protected String doInBackground(URL... urls) {

            URL url;
            try {
                final String CITY_LIST_URL = "https://api.airvisual.com/v2/cities?state=" + myState + "&country=" + myCountry + "&key=9a11661d-a1a4-4629-8030-3669adaade7d";
                url = new URL(CITY_LIST_URL);
            } catch (MalformedURLException exception) {
                Log.e("errorTag", "Error with creating URL", exception);
                return null;
            }

            String jsonResponse = "";
            try {

                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                Log.e("errorTag", "Error in state request");
            }
            return jsonResponse;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ProgressBar3.setVisibility(View.GONE);
            citySearch.setVisibility(View.VISIBLE);
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
            JSONObject parentObject;
            JSONArray dataArray;

            try {
                parentObject = new JSONObject(finalOutput);
                dataArray = parentObject.getJSONArray("data");
                for (int i = 0; i < dataArray.length(); i++) {
                    cityList.add(dataArray.getJSONObject(i).getString("city"));
                    Log.e("array", cityList.toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return finalOutput;
        }
    }

    public class SpecificCityRequest extends AsyncTask<URL, String, String> {

        @Override
        protected String doInBackground(URL... urls) {

            URL url;

            try {
                final String SPECIFIC_CITY_URL = "https://api.airvisual.com/v2/city?city=" + myCity + "&state=" + myState + "&country=" + myCountry + "&key=9a11661d-a1a4-4629-8030-3669adaade7d";
                url = new URL(SPECIFIC_CITY_URL);
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

            JSONObject parentObject;
            JSONObject dataObject;
            JSONObject locationObject;
            JSONArray coordinateArray;
            JSONObject weatherObject;
            JSONObject pollutionObject;
            JSONObject currentObject;
            try {
                parentObject = new JSONObject(output.toString());
                dataObject = parentObject.getJSONObject("data");
                myCity = dataObject.getString("city");
                myState = dataObject.getString("state");
                myCountry = dataObject.getString("country");
                locationObject = dataObject.getJSONObject("location");
                coordinateArray = locationObject.getJSONArray("coordinates");
                cityLongitude = coordinateArray.getDouble(0);
                cityLatitude = coordinateArray.getDouble(1);

                currentObject = dataObject.getJSONObject("current");
                weatherObject = currentObject.getJSONObject("weather");
                temperature = weatherObject.getInt("tp");
                weatherIconCode = weatherObject.getString("ic");

                pollutionObject = currentObject.getJSONObject("pollution");
                aqi = pollutionObject.getInt("aqius");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return output.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if ((aqi > 0) && (aqi <= 50)) {
                category = getString(R.string.good);
                Face.setImageResource(R.drawable.ic_face_green);
                Face.setBackgroundColor(getResources().getColor(R.color.ic_green));
                OtherSideFaceColor.setBackgroundColor(getResources().getColor(R.color.ic_green));
            } else if ((aqi > 50) && (aqi <= 100)) {
                category = getString(R.string.moderate);
                Face.setImageResource(R.drawable.ic_face_yellow);
                Face.setBackgroundColor(getResources().getColor(R.color.ic_yellow));
                OtherSideFaceColor.setBackgroundColor(getResources().getColor(R.color.ic_yellow));
            } else if ((aqi > 100) && (aqi <= 150)) {
                category = getString(R.string.unhealhy_for_sensitive_groups);
                Face.setImageResource(R.drawable.ic_face_orange);
                Face.setBackgroundColor(getResources().getColor(R.color.ic_orange));
                OtherSideFaceColor.setBackgroundColor(getResources().getColor(R.color.ic_orange));
            } else if ((aqi > 150) && (aqi <= 200)) {
                category = getString(R.string.unhealthy);
                Face.setImageResource(R.drawable.ic_face_red);
                Face.setBackgroundColor(getResources().getColor(R.color.ic_red));
                OtherSideFaceColor.setBackgroundColor(getResources().getColor(R.color.ic_red));
            } else if ((aqi > 200) && (aqi <= 300)) {
                category = getString(R.string.very_unhealthy);
                Face.setImageResource(R.drawable.ic_face_maroon);
                Face.setBackgroundColor(getResources().getColor(R.color.ic_maroon));
                OtherSideFaceColor.setBackgroundColor(getResources().getColor(R.color.ic_maroon));
            } else if (aqi > 300) {
                category = getString(R.string.hazardous);
                Face.setImageResource(R.drawable.ic_face_purple);
                Face.setBackgroundColor(getResources().getColor(R.color.ic_purple));
                OtherSideFaceColor.setBackgroundColor(getResources().getColor(R.color.ic_purple));
            }

            switch (weatherIconCode) {
                case "01d":
                    WeatherIcon.setImageResource(R.drawable.ic_01d);
                    break;
                case "01n":
                    WeatherIcon.setImageResource(R.drawable.ic_01n);
                    break;
                case "02d":
                    WeatherIcon.setImageResource(R.drawable.ic_02d);
                    break;
                case "02n":
                    WeatherIcon.setImageResource(R.drawable.ic_02n);
                    break;
                case "03d":
                    WeatherIcon.setImageResource(R.drawable.ic_03d);
                    break;
                case "03n":
                    WeatherIcon.setImageResource(R.drawable.ic_03d);
                    break;
                case "04d":
                    WeatherIcon.setImageResource(R.drawable.ic_04d);
                    break;
                case "04n":
                    WeatherIcon.setImageResource(R.drawable.ic_04d);
                    break;
                case "09d":
                    WeatherIcon.setImageResource(R.drawable.ic_09d);
                    break;
                case "09n":
                    WeatherIcon.setImageResource(R.drawable.ic_09d);
                    break;
                case "10d":
                    WeatherIcon.setImageResource(R.drawable.ic_10d);
                    break;
                case "10n":
                    WeatherIcon.setImageResource(R.drawable.ic_10n);
                    break;
                case "11d":
                    WeatherIcon.setImageResource(R.drawable.ic_11d);
                    break;
                case "11n":
                    WeatherIcon.setImageResource(R.drawable.ic_11d);
                    break;
                case "13d":
                    WeatherIcon.setImageResource(R.drawable.ic_13d);
                    break;
                case "13n":
                    WeatherIcon.setImageResource(R.drawable.ic_13d);
                    break;
                case "50n":
                    WeatherIcon.setImageResource(R.drawable.ic_50d);
                    break;
                case "50d":
                    WeatherIcon.setImageResource(R.drawable.ic_50d);
                    break;
            }

            City.setText("" + myCity);
            State.setText("" + myState);
            Country.setText("" + myCountry);
            Aqi.setText("" + aqi);
            Category.setText("" + category);
            Temperature.setText("" + temperature + "°C");

            map_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent sendDataToMap = new Intent(getActivity(), SpecificCityMapActivity.class);
                    sendDataToMap.putExtra("LONGITUDE", cityLongitude);
                    sendDataToMap.putExtra("LATITUDE", cityLatitude);
                    sendDataToMap.putExtra("COUNTRY", myCountry);
                    sendDataToMap.putExtra("STATE", myState);
                    sendDataToMap.putExtra("CITY", myCity);
                    sendDataToMap.putExtra("AQI", aqi);
                    startActivity(sendDataToMap);
                }
            });

            Log.e("places", "" + cityLatitude + cityLongitude + myCountry + myState + myCity + aqi);

            aqiCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent cardInfo = new Intent(getActivity(), CARDInfo.class);
                    cardInfo.putExtra("MY_CITY", myCity);
                    cardInfo.putExtra("MY_STATE", myState);
                    cardInfo.putExtra("MY_COUNTRY", myCountry);
                    startActivity(cardInfo);
                }
            });

        }

    }
}