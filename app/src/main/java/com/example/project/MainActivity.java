package com.example.project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    enum panelStatus { sport, settings}
    static panelStatus panelStatus = MainActivity.panelStatus.sport;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       Button btn =  findViewById(R.id.sportButton);
       btn.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));


    }

    public void PressButtonSwitcherFragment(View view)
    {
        Button btnSport =  findViewById(R.id.sportButton);
        Button btnSettings =  findViewById(R.id.settingsButton);

        Fragment fragment = null;
        Animation animation = null;
        switch (view.getId())
        {
            case R.id.sportButton:
                if (panelStatus != MainActivity.panelStatus.sport) {
                    btnSport.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                    btnSettings.setTextColor(Color.BLACK);
                    panelStatus = MainActivity.panelStatus.sport;
                    fragment = new sportFragment();
                    animation = AnimationUtils.loadAnimation(this, R.anim.swipe_anim_left);
                }
                break;
            case R.id.settingsButton:
                if (panelStatus != MainActivity.panelStatus.settings) {
                    btnSettings.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                    btnSport.setTextColor(Color.BLACK);
                    panelStatus = MainActivity.panelStatus.settings;
                    fragment = new SettingsFragment();
                    animation = AnimationUtils.loadAnimation(this, R.anim.swipe_anim_right);
                }
                break;
        }
        if(fragment != null)
        {

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fagmensviewer, fragment).commit();
            if(animation != null)
                findViewById(R.id.fagmensviewer).startAnimation(animation);
        }


    }


}