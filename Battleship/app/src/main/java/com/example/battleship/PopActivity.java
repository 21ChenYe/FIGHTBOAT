package com.example.battleship;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;

public class PopActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop);

        //get display metrics for default screen
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        //set pop up dimensions
        double width = (dm.widthPixels)*.4;
        double height = (dm.heightPixels)*.15;

        getWindow().setLayout((int)width, (int)height);

        //adjust pop up position in screen
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x =  0;
        params.y = -20;

        getWindow().setAttributes(params);






    }
}
