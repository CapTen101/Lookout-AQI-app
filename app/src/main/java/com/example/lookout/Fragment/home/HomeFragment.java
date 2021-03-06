package com.example.lookout.Fragment.home;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import java.util.Random;

public class HomeFragment extends Fragment {

    private HomeFragment homeFragment;
    private TextView mQuoteResult;
    private TextView mAuthorResult;
    private ProgressBar quoteProgress;
    private ProgressBar authorProgress;
    private static final String MY_REQUEST_URL = "https://api.paperquotes.com/quotes/?tags=environment";
    private Random rand = new Random();
    private int randomIndex = rand.nextInt(5);

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        mQuoteResult = root.findViewById(R.id.quote);
        mAuthorResult = root.findViewById(R.id.author);
        quoteProgress = root.findViewById(R.id.quote_progress_bar);
        authorProgress = root.findViewById(R.id.author_progress_bar);

        HttpQuoteRequest requestAPIQuote = new HttpQuoteRequest();
        requestAPIQuote.execute();
        HttpAuthorRequest requestAPIAuthor = new HttpAuthorRequest();
        requestAPIAuthor.execute();

        return root;
    }

    public class HttpQuoteRequest extends AsyncTask<URL, String, String> {

        @Override
        protected String doInBackground(URL... urls) {

            URL url;
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
            mQuoteResult.setVisibility(View.VISIBLE);
            quoteProgress.setVisibility(View.GONE);
            mQuoteResult.setText("\"" + s + "\"");
        }

        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";
            HttpURLConnection urlConnection;
            InputStream inputStream;

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Authorization", "Token 2e9072a007f0fcd23d80fc5537a5c174bee9ff47");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.connect();

            inputStream = urlConnection.getInputStream();
            jsonResponse = readInputStream(inputStream);

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
            JSONObject parentObject;
            JSONArray parentArray;
            JSONObject quoteObject;
            try {
                parentObject = new JSONObject(finalOutput);
                parentArray = parentObject.getJSONArray("results");
                if (randomIndex != 4) {
                    quoteObject = parentArray.getJSONObject(randomIndex);
                    String author = quoteObject.getString("quote");
                    copyOutput = author;
                } else {
                    randomIndex = 2;
                    quoteObject = parentArray.getJSONObject(randomIndex);
                    String author = quoteObject.getString("quote");
                    copyOutput = author;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return copyOutput;
        }
    }

    public class HttpAuthorRequest extends AsyncTask<URL, String, String> {

        @Override
        protected String doInBackground(URL... urls) {

            URL url;
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
            mAuthorResult.setVisibility(View.VISIBLE);
            authorProgress.setVisibility(View.GONE);
            mAuthorResult.setText(s);
        }

        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";
            HttpURLConnection urlConnection;
            InputStream inputStream;

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Authorization", "Token 2e9072a007f0fcd23d80fc5537a5c174bee9ff47");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.connect();

            inputStream = urlConnection.getInputStream();
            jsonResponse = readInputStream(inputStream);

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
            JSONObject parentObject;
            JSONArray parentArray;
            JSONObject authorObject;
            try {
                parentObject = new JSONObject(finalOutput);
                parentArray = parentObject.getJSONArray("results");
                if (randomIndex != 4) {
                    authorObject = parentArray.getJSONObject(randomIndex);
                    String author = authorObject.getString("author");
                    copyOutput = author;
                } else {
                    randomIndex = 2;
                    authorObject = parentArray.getJSONObject(randomIndex);
                    String author = authorObject.getString("author");
                    copyOutput = author;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return copyOutput;
        }
    }
}