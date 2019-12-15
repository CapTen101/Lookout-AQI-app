package com.example.lookout.ui.home;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import java.util.Random;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private HomeFragment homeFragment;
    private static TextView mQuoteResult;
    private static TextView mAuthorResult;
    private static final String MY_REQUEST_URL = "https://api.paperquotes.com/quotes/?tags=environment";
    private Random rand = new Random();
    int randomIndex = rand.nextInt(5);

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        mQuoteResult = root.findViewById(R.id.quote);
        mAuthorResult = root.findViewById(R.id.author);

        HttpQuoteRequest requestAPIQuote = new HttpQuoteRequest();
        requestAPIQuote.execute();
        HttpAuthorRequest requestAPIAuthor = new HttpAuthorRequest();
        requestAPIAuthor.execute();

        return root;
    }

    public class HttpQuoteRequest extends AsyncTask<URL, String, String> {

        @Override
        protected String doInBackground(URL... urls) {

            URL url = null;
            try {
                url = new URL(MY_REQUEST_URL);
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

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mQuoteResult.setText("\"" + s + "\"");
        }

        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Authorization", "Token 2e9072a007f0fcd23d80fc5537a5c174bee9ff47");
                urlConnection.setRequestProperty("Content-Type", "application/json");
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
            String copyOutput = null;
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
            JSONArray parentArray = null;
            try {
                parentArray = parentObject.getJSONArray("results");
            } catch (JSONException e) {
                e.printStackTrace();
            }

//            Random rand = new Random();                     //For generating a random quote from a JSONObject
//            int randomIndex = rand.nextInt(5);

            JSONObject quoteObject = null;
            try {
                quoteObject = parentArray.getJSONObject(randomIndex);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                String quote = quoteObject.getString("quote");
                copyOutput = quote;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return copyOutput;
        }
    }

    public class HttpAuthorRequest extends AsyncTask<URL, String, String> {

        @Override
        protected String doInBackground(URL... urls) {

            URL url = null;
            try {
                url = new URL(MY_REQUEST_URL);
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

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mAuthorResult.setText(s);
        }

        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Authorization", "Token 2e9072a007f0fcd23d80fc5537a5c174bee9ff47");
                urlConnection.setRequestProperty("Content-Type", "application/json");
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
            String copyOutput = null;
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
            JSONArray parentArray = null;
            try {
                parentArray = parentObject.getJSONArray("results");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONObject authorObject = null;
            try {
                authorObject = parentArray.getJSONObject(randomIndex);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                String author = authorObject.getString("author");
                copyOutput = author;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return copyOutput;
        }
    }
}