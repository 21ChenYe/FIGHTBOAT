package com.example.battleship;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.TextView;

public class MainMenu extends AppCompatActivity {
private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mp = MediaPlayer.create(this, R.raw.ocean_wave);
        mp.start();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);


        TextView button1 = findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.stop();
                MediaPlayer mp2 = MediaPlayer.create(getApplicationContext(), R.raw.steelsword);
                mp2.start();
                startActivity(new Intent(MainMenu.this, MainActivity.class));
            }
        });

        TextView button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.stop();
                MediaPlayer mp2 = MediaPlayer.create(getApplicationContext(), R.raw.steelsword);
                mp2.start();
                startActivity(new Intent(MainMenu.this, Multiplayer.class));
            }
        });
    }

}
