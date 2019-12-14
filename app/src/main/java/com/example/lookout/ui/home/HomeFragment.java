package com.example.lookout.ui.home;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.lookout.MainActivity;
import com.example.lookout.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private TextView mTextViewResult;

//    private static final String MY_REQUEST_URL = "https://api.paperquotes.com/quotes/?tags=environment";

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
//        final TextView textView = root.findViewById(R.id.text_home);
//        homeViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        mTextViewResult = mTextViewResult.findViewById(R.id.text_view_result);

        OkHttpClient client = new OkHttpClient();
        String url = "https://api.paperquotes.com/quotes/?tags=environment";
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();

                    HomeFragment.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTextViewResult.setText(myResponse);
                        }
                    });
                }
            }
        });

        return root;
    }

//    private void updateUi(Event earthquake) {
//        // Display the earthquake title in the UI
//        TextView titleTextView = (TextView) findViewById(R.id.title);
//        titleTextView.setText(earthquake.title);
//
//        // Display the earthquake date in the UI
//        TextView dateTextView = (TextView) findViewById(R.id.date);
//        dateTextView.setText(getDateString(earthquake.time));
//
//        // Display whether or not there was a tsunami alert in the UI
//        TextView tsunamiTextView = (TextView) findViewById(R.id.tsunami_alert);
//        tsunamiTextView.setText(getTsunamiAlertString(earthquake.tsunamiAlert));
//    }
//
//    public class HttpGetRequest extends AsyncTask<URL, Void, String> {
//
//        @Override
//        protected String doInBackground(URL... urls) {
//
//            URL url = createUrl(MY_REQUEST_URL);
//
//            String jsonResponse = "";
//
//            try {
//                jsonResponse = makeHttpRequest(url);
//            } catch (IOException e) {
//
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String result){
//            if (earthquake == null) {
//                return;
//            }
//
//            updateUi(earthquake);
//
//    }
//
//        private URL createUrl(String stringUrl) {
//            URL url = null;
//            try {
//                url = new URL(stringUrl);
//            } catch (MalformedURLException exception) {
//                Log.e("Tag_error", "Error with creating URL", exception);
//                return null;
//            }
//            return url;
//        }
//
//        private String makeHttpRequest(URL url) throws IOException {
//            String jsonResponse = "";
//            HttpURLConnection urlConnection = null;
//            InputStream inputStream = null;
//            try {
//                urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setRequestMethod("GET");
//                urlConnection.setReadTimeout(10000 /* milliseconds */);
//                urlConnection.setConnectTimeout(15000 /* milliseconds */);
//                urlConnection.connect();
//                inputStream = urlConnection.getInputStream();
//                jsonResponse = readFromStream(inputStream);
//            } catch (IOException e) {
//
//            } finally {
//                if (urlConnection != null) {
//                    urlConnection.disconnect();
//                }
//                if (inputStream != null) {
//                    // function must handle java.io.IOException here
//                    inputStream.close();
//                }
//            }
//            return jsonResponse;
//        }
//
//        private String readFromStream(InputStream inputStream) throws IOException {
//            StringBuilder output = new StringBuilder();
//            if (inputStream != null) {
//                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
//                BufferedReader reader = new BufferedReader(inputStreamReader);
//                String line = reader.readLine();
//                while (line != null) {
//                    output.append(line);
//                    line = reader.readLine();
//                }
//            }
//            return output.toString();
//        }


}