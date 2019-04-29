package com.example.battleship;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

import org.w3c.dom.Text;

public class PopActivity extends Activity {
        private TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop);
        Intent in = getIntent();
            Bundle extras = in.getExtras();
            if(in.getExtras() != null){
            String message = extras.getString("message");
            tv = findViewById(R.id.textView2);
            tv.setText(message);
        }
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
