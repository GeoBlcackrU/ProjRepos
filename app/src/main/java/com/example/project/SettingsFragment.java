package com.example.project;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.BaseKeyListener;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;


public class SettingsFragment extends Fragment {

    static TextInputEditText text;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    //получение ключей с настройками
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    int complexity;
    TextView complexityText;
    @Override
    public void onStart() {
        super.onStart();


        //иницилизация ключей с настройками
        sharedPreferences = getActivity().getSharedPreferences("settingsPreferences", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        complexity =  sharedPreferences.getInt("ComplexityKey", 0);



        complexityText = getView().findViewById(R.id.complexityText);
            complexityText.setText("Вы будете делать " + (int)Math.round(6 + complexity*0.04*6) + " приседаний");

        text = getView().findViewById(R.id.TextInput);

        if(sharedPreferences.contains("Weight"))
        {
            int n = 0;
            n = sharedPreferences.getInt("Weight",0);
            text.setText(String.valueOf(n));
        }
        text.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent)
            {

                if(i == keyEvent.KEYCODE_ENTER)
                {
                    if(text.getText().toString().length() > 0 )
                    {
                        int num = Integer.parseInt(text.getText().toString());

                        if (num > 150 || num < 40)
                        {
                            num = num > 150 ? 150 : 40;
                            text.setText(String.valueOf(num));

                        }
                        editor.putInt("Weight", Integer.parseInt(text.getText().toString()));
                        editor.apply();
                    }
                    else
                    {

                    }

                }

                return false;
            }
        });


        SeekBar sK = getView().findViewById(R.id.complexity);
        sK.setProgress(complexity);
        sK.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b)
            {
                int num = (int)Math.round(6 + i*0.04 * 6);
                complexityText.setText("Вы будете делать " + num + " приседаний");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                editor.putInt("ComplexityKey", seekBar.getProgress());
                editor.apply();

            }
        });
        }

}