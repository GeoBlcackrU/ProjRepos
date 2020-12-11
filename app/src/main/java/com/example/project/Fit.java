package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import static com.example.project.R.color.colorPrimary;
import static com.example.project.R.color.designFifth;
import static com.example.project.R.color.designSecond;
import static com.example.project.R.color.green;
import static com.example.project.R.color.lightGreen;
import static com.example.project.R.color.peach;

public class Fit extends AppCompatActivity {

    int id_of_focused = 0;
    int id_of_doing = 0;
    void ChangerOfActivity()
    {

    }

    void createActivitiesInList(int a)
    {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(20,0,0,0);
        final LinearLayout mainLayout = findViewById(R.id.list_of_activities);
        for(int i = 0; i < a; i++)
        {
            final LinearLayout ln = new LinearLayout(this);
            final int finalI = i;
            ln.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if(id_of_focused > id_of_doing)
                        mainLayout.getChildAt(id_of_focused).setBackgroundColor(0);
                    else if(id_of_focused < id_of_doing)
                    {
                        mainLayout.getChildAt(id_of_focused).setBackgroundColor(getResources().getColor(lightGreen));
                    }

                    id_of_focused = finalI;
                    if(id_of_focused > id_of_doing)
                    ln.setBackgroundColor(getResources().getColor(designFifth));
                    else if(id_of_focused < id_of_doing)
                    ln.setBackgroundColor(getResources().getColor(green));
                }
            });
            ln.setOrientation(LinearLayout.VERTICAL);

            ImageView im = new ImageView(this);
            im.setImageResource(R.drawable.test_pic);
            ln.addView(im);

            TextView tx = new TextView(this);
            tx.setText("test ");
            tx.setTextSize(40);
            ln.addView(tx);
                if(i>0)
                    mainLayout.addView(ln, layoutParams);
                else
                {
                    ln.setBackgroundColor(getResources().getColor(designSecond));
                    mainLayout.addView(ln);
                }


        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_fit);
        int scenario = getIntent().getExtras().getInt("scenario");
         createActivitiesInList(5);

    }

    public void OnClick(View view)
    {

        LinearLayout mainLayout = findViewById(R.id.list_of_activities);
         if (id_of_doing+1 < mainLayout.getChildCount())
         {
             mainLayout.getChildAt(id_of_doing).setBackgroundColor(getResources().getColor(lightGreen));
             id_of_doing++;
              mainLayout.getChildAt(id_of_doing).setBackgroundColor(getResources().getColor(designSecond));

         }
    }

}