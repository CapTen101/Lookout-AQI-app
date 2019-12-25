package com.example.lookout.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.lookout.R;

public class DashboardFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        Button GoToMap;
        GoToMap = root.findViewById(R.id.map_button);
        GoToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentnearest = new Intent(getActivity(), MapsActivity.class);
                startActivity(intentnearest);
            }
        });
        return root;
    }
}