package com.example.lookout.Fragment.AQISearch;

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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class AQISearchFragment extends Fragment {

    private int aqi, temperature;
    private double cityLatitude, cityLongitude;
    private String myCity, myState, myCountry, weatherIconCode, category;
    private String[] countryList = new String[96];
    private AutoCompleteTextView countrySearch, stateSearch, citySearch;
    private TextView City, State, Country, Temperature, Aqi, Category;
    private ImageView WeatherIcon, Face, OtherSideFaceColor, searchIconState, searchIconCity;
    private CardView aqiCard;
    private Button map_button, nearestData;
    private ProgressBar ProgressBar1,
            ProgressBar2,
            ProgressBar3;
    ArrayList<String> stateList = new ArrayList<>(1);
    ArrayList<String> cityList = new ArrayList<>(1);

    // Setting the Key and URL for the GET request
    private final String API_KEY = getResources().getString(R.string.API_KEY);
    private final String COUNTRY_LIST_URL = "https://api.airvisual.com/v2/countries?key=" + API_KEY;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        // Inflating all the views with their respective XML layouts
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
        searchIconState = root.findViewById(R.id.state_search_icon);
        searchIconCity = root.findViewById(R.id.city_search_icon);

        // This onClickListener is invoked when the nearestData Button is clicked
        nearestData = root.findViewById(R.id.nearest_data_button);
        nearestData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentNearest = new Intent(getActivity(), NearestCityInfoActivity.class);
                startActivity(intentNearest);
            }
        });

        // Setting up the autocomplete textviews
        countrySearch = root.findViewById(R.id.country_search);
        countrySearch.setHint("Search your Country here");

        stateSearch = root.findViewById(R.id.state_search);
        stateSearch.setHint("Search your State here");

        citySearch = root.findViewById(R.id.city_search);
        citySearch.setHint("Search your City here");

        // Execute the GET Request
        COUNTRYHttpRequest requestCountry = new COUNTRYHttpRequest();
        requestCountry.execute();

        // Setting up the ArrayAdapter for the AutoCompleteTextViews of country
        ArrayAdapter<String> adapterCountry = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, countryList);
        countrySearch.setAdapter(adapterCountry);
        countrySearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myCountry = countrySearch.getText().toString();

                // There was some non-uniformity JSON response specifically in case of United Kingdom
                // Hence setting it manually
                if (countrySearch.getText().toString().equals("United Kingdom")) {
                    myCountry = "UK";
                }

                ProgressBar2.setVisibility(View.VISIBLE);

                // Initiating the State request after getting the country
                STATEHttpRequest requestState = new STATEHttpRequest();
                requestState.execute();

                // Setting up the adapterState
                ArrayAdapter<String> adapterState = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, stateList);
                stateSearch.setAdapter(adapterState);
            }
        });

        // Setting up the ArrayAdapter for the AutoCompleteTextViews of state
        stateSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myState = stateSearch.getText().toString();

                ProgressBar3.setVisibility(View.VISIBLE);

                // Initiating the City request after getting the state
                CITYHttpRequest requestState = new CITYHttpRequest();
                requestState.execute();

                // Setting up the adapterCity
                ArrayAdapter<String> adapterCity = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, cityList);
                citySearch.setAdapter(adapterCity);
            }
        });

        // Setting up the ArrayAdapter for the AutoCompleteTextViews of city
        citySearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myCity = citySearch.getText().toString();

                // Initiating the SpecificCityRequest request
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

    // This ASyncTask handles the asynchronous network request sent to the country search endpoint of the API
    public class COUNTRYHttpRequest extends AsyncTask<URL, String, String> {

        // This method hits the API and gets the response for us.
        // This is executed first before any other below mentioned functions.
        @Override
        protected String doInBackground(URL... urls) {

            URL url;

            // Enclosing the URL Creation in a try-catch
            // for logging any possible exceptions that might occur during runtime
            try {
                url = new URL("https://api.airvisual.com/v2/countries?" +
                                        "key=" + API_KEY);
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
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ProgressBar1.setVisibility(View.GONE);
            countrySearch.setVisibility(View.VISIBLE);
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

            // Defining all JSON Objects for the parsing
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

    // This ASyncTask handles the asynchronous network request sent to the state search endpoint of the API
    public class STATEHttpRequest extends AsyncTask<URL, String, String> {

        @Override
        protected String doInBackground(URL... urls) {

            URL url;
            try {
                final String STATE_LIST_URL = "https://api.airvisual.com/v2/states?country=" + myCountry +
                                                "&key=" + API_KEY;
                url = new URL(STATE_LIST_URL);
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
                Log.e("errorTag", "Error in state request");
            }
            return jsonResponse;
        }

        // This method is executed after the asynchronous request is completed and has sent the response
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ProgressBar2.setVisibility(View.GONE);
            stateSearch.setVisibility(View.VISIBLE);
            searchIconState.setVisibility(View.VISIBLE);
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
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
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

    // This ASyncTask handles the asynchronous network request sent to the city search endpoint of the API
    public class CITYHttpRequest extends AsyncTask<URL, String, String> {

        // This method hits the API and gets the response for us.
        // This is executed first before any other below mentioned functions.
        @Override
        protected String doInBackground(URL... urls) {

            URL url;

            // Enclosing the URL Creation in a try-catch
            // for logging any possible exceptions that might occur during runtime
            try {
                final String CITY_LIST_URL = "https://api.airvisual.com/v2/cities?state=" + myState +
                                                "&country=" + myCountry +
                                                "&key=" + API_KEY;

                url = new URL(CITY_LIST_URL);
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
                Log.e("errorTag", "Error in state request");
            }
            return jsonResponse;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ProgressBar3.setVisibility(View.GONE);
            citySearch.setVisibility(View.VISIBLE);
            searchIconCity.setVisibility(View.VISIBLE);
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

    // This ASyncTask handles the asynchronous network request sent to the specific search endpoint of the API
    public class SpecificCityRequest extends AsyncTask<URL, String, String> {

        @Override
        protected String doInBackground(URL... urls) {

            URL url;

            // Enclosing the URL Creation in a try-catch
            // for logging any possible exceptions that might occur during runtime
            try {

                final String SPECIFIC_CITY_URL = "https://api.airvisual.com/v2/city?city=" + myCity +
                                                    "&state=" + myState +
                                                    "&country=" + myCountry +
                                                    "&key=" + API_KEY;

                url = new URL(SPECIFIC_CITY_URL);
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
                Log.e("errorTag", "Error in request");
            }
            return jsonResponse;
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

            // Defining all JSON Objects for the parsing
            JSONObject parentObject,
                    dataObject,
                    locationObject,
                    weatherObject,
                    pollutionObject,
                    currentObject;

            JSONArray coordinateArray;

            // Parsing the received JSON Objects from the network request and assigning the variables
            // 'output' StringBuilder contains the response here
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

        // This method is executed after the asynchronous request is completed and has sent the response
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

            // Setting up the image views according to the weatherIconCode
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

            // OnClickListener for the Google Map toggle button
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

            // OnClickListener for the specific city AQI Card button
            aqiCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent cardInfo = new Intent(getActivity(), CardInfoActivity.class);
                    cardInfo.putExtra("MY_CITY", myCity);
                    cardInfo.putExtra("MY_STATE", myState);
                    cardInfo.putExtra("MY_COUNTRY", myCountry);
                    startActivity(cardInfo);
                }
            });

        }

    }
}