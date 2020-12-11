package com.example.project;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Objects;


public class sportFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        
        return inflater.inflate(R.layout.fragment_sport, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        onClicker(getView().findViewById(R.id.mornBtn), 0);
        onClicker(getView().findViewById(R.id.warmBtn), 1);
        onClicker(getView().findViewById(R.id.trainBtn), 2);
        onClicker(getView().findViewById(R.id.flexBtn), 3);
    }

    private void onClicker(View view, final int numOfScenario)
    {
        Button btn = (Button)view;
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getActivity(), Fit.class).putExtra("scenario", numOfScenario);
                startActivity(intent);
            }
        });
    }

}