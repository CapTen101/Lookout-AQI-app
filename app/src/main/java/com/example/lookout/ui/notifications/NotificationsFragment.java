package com.example.lookout.ui.notifications;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lookout.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    private static final String MY_IP_URL = "https://api.ipify.org?format=json";
    private static final String MY_NEAREST_URL = "https://api.airvisual.com/v2/nearest_city?key=9a11661d-a1a4-4629-8030-3669adaade7d";
    private String myIP;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel = ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
//        final TextView textView = root.findViewById(R.id.text_notifications);
//        notificationsViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return root;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        CircleImageView image;
        TextView temperature;
        TextView city;
        TextView country;
        TextView aqi;
        TextView category;
        ImageView weatherIcon;

        public ViewHolder(View ItemView) {
            super(ItemView);
            cv = ItemView.findViewById(R.id.card_container);
            image = ItemView.findViewById(R.id.list_image);
            temperature = ItemView.findViewById(R.id.temperature);
            city = ItemView.findViewById(R.id.city);
            country = ItemView.findViewById(R.id.country);
            aqi = ItemView.findViewById(R.id.aqi);
            category = ItemView.findViewById(R.id.category);
            weatherIcon = ItemView.findViewById(R.id.weather_icon);
        }

    }

    public class RecyclerAdapter extends RecyclerView.Adapter<ViewHolder> {

        public RecyclerAdapter() {
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
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
//                urlConnection.setRequestProperty("Authorization", "Token 2e9072a007f0fcd23d80fc5537a5c174bee9ff47");
//                urlConnection.setRequestProperty("Content-Type", "application/json");
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

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

}