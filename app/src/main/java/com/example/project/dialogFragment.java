package com.example.project;

import android.app.Dialog;
import android.content.DialogInterface;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.io.IOException;
import java.util.Objects;

public class dialogFragment extends DialogFragment {
    final String[] dayOfWeek = {"Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье"};
    final boolean[] checkedItemsArray = {false, false, false, false, false, false, false};


    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        return builder.setTitle("Выберите дни недели").setMultiChoiceItems(dayOfWeek, checkedItemsArray, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                checkedItemsArray[i] = b;
            }
        }).setPositiveButton("ОК", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                SQLiteDatabase database;
                DataBaseOpen dataBaseOpen = new DataBaseOpen(getContext());
                try {
                    dataBaseOpen.updateDataBase();
                } catch (IOException mIOException) {
                    throw new Error("UnableToUpdateDatabase");
                }

                try {
                    database = dataBaseOpen.getWritableDatabase();
                } catch (SQLException mSQLException) {
                    throw mSQLException;
                }

                SettingsFragment.isOk(checkedItemsArray, database);

            }

        }) .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SettingsFragment.IsCancel();
            }
        }).create();
    }


    
}
