package com.example.lookout.ui.notifications;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

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

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    private String myIP;
    private String myCity;
    private String myState;
    private String myCountry;
    private Button nearestData;
    private SearchView countrySearch;
    private SearchView stateSearch;
    private SearchView citySearch;
    private String[] countryList;
    private String[] stateList;
    private String[] cityList;
    private static final String MY_IP_URL = "https://api.ipify.org?format=json";
    private static final String MY_NEAREST_URL = "https://api.airvisual.com/v2/nearest_city?key=9a11661d-a1a4-4629-8030-3669adaade7d";
//    private final String CITY_LIST_URL = "api.airvisual.com/v2/cities?state=" + myState + "&country=" + myCountry + "&key=9a11661d-a1a4-4629-8030-3669adaade7d";


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel = ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        nearestData = root.findViewById(R.id.nearest_data_button);
        nearestData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentnearest = new Intent(getActivity(), NearestActivity.class);
                startActivity(intentnearest);

                NearestHttpRequest requestNearest = new NearestHttpRequest();
                requestNearest.execute();
            }
        });

        countrySearch = root.findViewById(R.id.country_search);
        stateSearch = root.findViewById(R.id.state_search);
        citySearch = root.findViewById(R.id.city_search);

        IPHttpRequest requestIP = new IPHttpRequest();
        requestIP.execute();

//        final TextView textView = root.findViewById(R.id.text_notifications);
//        notificationsViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

//        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return root;
    }

//    public class ViewHolder extends RecyclerView.ViewHolder {
//
//        CardView cv;
//        CircleImageView image;
//        TextView temperature;
//        TextView city;
//        TextView country;
//        TextView aqi;
//        TextView category;
//        ImageView weatherIcon;
//
//        public ViewHolder(View ItemView) {
//            super(ItemView);
//            cv = ItemView.findViewById(R.id.card_container);
//            image = ItemView.findViewById(R.id.list_image);
//            temperature = ItemView.findViewById(R.id.temperature);
//            city = ItemView.findViewById(R.id.city);
//            country = ItemView.findViewById(R.id.country);
//            aqi = ItemView.findViewById(R.id.aqi);
//            category = ItemView.findViewById(R.id.category);
//            weatherIcon = ItemView.findViewById(R.id.weather_icon);
//        }
//    }

//    public class RecyclerAdapter extends RecyclerView.Adapter<ViewHolder> {
//
//        public RecyclerAdapter() {
//        }
//
//        @NonNull
//        @Override
//        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            return null;
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//
//        }
//
//        @Override
//        public int getItemCount() {
//            return 0;
//        }
//    }

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