package com.example.battleship;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Random;
import java.util.Vector;

public class Multiplayer extends AppCompatActivity implements DialogInterface.OnDismissListener {
    private Tile[][] buttons = new Tile[10][10];
    private int dim = 10;
    private TextView player;
    private Ship carrier;
    private Ship battleship;
    private Ship cruiser;
    private Ship sub;
    private Button buttonRotate;
    private String type;
    private String direction;
    private int length;
    private Computer comp;
    private boolean AllPlaced;
    private String value;
    private boolean update = false;
    private boolean AllSunk;
    Ship[] shiparr;


    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    String id = "game";

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) {
            Log.v("bundle", "Recovered");
            boolean see = savedInstanceState.getBoolean("check");
            if (see) {
                Toast.makeText(getApplicationContext(), "We have communication", Toast.LENGTH_LONG).show();
            }
        }
        else {
            setContentView(R.layout.activity_main);
            comp = new Computer();
            player = findViewById(R.id.text_view_player);
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    String buttonId = "button_" + i + j;
                    int resId = getResources().getIdentifier(buttonId, "id", getPackageName());
                    buttons[i][j] = findViewById(resId);
                    buttons[i][j].setPosX(j);
                    buttons[i][j].setPosY(i);
                    buttons[i][j].setState(0);
                    buttons[i][j].setShip("");
                    buttons[i][j].setBackground(getDrawable(R.drawable.ocean_tile));
                    myDragEventListener dragListen = new myDragEventListener();
                    buttons[i][j].setOnDragListener(dragListen);
                }
            }
            carrier = findViewById(R.id.carrier_ship);
            carrier.setType("frigate");
            carrier.setLength(5);


            battleship = findViewById(R.id.battle_ship);
            battleship.setType("caravel");
            battleship.setLength(3);


            cruiser = findViewById(R.id.cruiser_ship);
            cruiser.setType("dandy");
            cruiser.setLength(2);


            sub = findViewById(R.id.sub_ship);
            sub.setType("sloop");
            sub.setLength(3);

            Ship[] temp = {carrier,battleship,cruiser,sub};
            shiparr = temp;
            for (int ship = 0; ship < shiparr.length; ship++) {
                myOnLongClickListener LongClickListen = new myOnLongClickListener();
                shiparr[ship].setOnLongClickListener(LongClickListen);
            }

            buttonRotate = findViewById(R.id.rotate_button);
            buttonRotate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (carrier.getRotation() == 0) {
                        carrier.setRotation(carrier.getRotation() + 90);
                        carrier.setDirection("n");
                        battleship.setRotation(battleship.getRotation() + 90);
                        battleship.setDirection("n");
                        cruiser.setRotation(cruiser.getRotation() + 90);
                        cruiser.setDirection("n");
                        sub.setRotation(sub.getRotation() + 90);
                        sub.setDirection("n");
                    } else {
                        carrier.setRotation(carrier.getRotation() - 90);
                        carrier.setDirection("e");
                        battleship.setRotation(battleship.getRotation() - 90);
                        battleship.setDirection("e");
                        cruiser.setRotation(cruiser.getRotation() - 90);
                        cruiser.setDirection("e");
                        sub.setRotation(sub.getRotation() - 90);
                        sub.setDirection("e");
                    }
                }
            });
            FragmentManager fm = getSupportFragmentManager();
            popFrag editNameDialogFragment = popFrag.newInstance("Place your Ships");
            editNameDialogFragment.show(fm, "fragment_edit_name");
            //Toast.makeText(getApplicationContext(),"Created",Toast.LENGTH_LONG).show();
        }
        File directory = getFilesDir();
        File file = new File(directory, "config.txt");
        file.delete();
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                value = dataSnapshot.getValue(String.class);
                if(value.equals("a")){
                    update = true;
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                value = dataSnapshot.getValue(String.class);
                Log.v("change","changed");

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(AllPlaced) {
            comp.RandomHit();
        }

    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        if(AllPlaced){
            Intent goDown = new Intent(Multiplayer.this,ComputerActivity.class);
            startActivity(goDown);
        }
    }
    protected class myOnLongClickListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(final View v) {
            Ship ship = (Ship) v;
            type = ship.getType();
            direction = ship.getDirection();
            length = ship.getLength();
            ClipData dragData = ClipData.newPlainText("test", ship.getType());
            double rotationRad = Math.toRadians(v.getRotation());
            final int w = (int) (v.getWidth() * v.getScaleX());
            final int h = (int) (v.getHeight() * v.getScaleY());
            double s = Math.abs(Math.sin(rotationRad));
            double c = Math.abs(Math.cos(rotationRad));
            final int width = (int) (w * c + h * s);
            final int height = (int) (w * s + h * c);
            View.DragShadowBuilder myShadow = new View.DragShadowBuilder(v) {
                @Override
                public void onDrawShadow(Canvas canvas) {
                    canvas.scale(v.getScaleX(), v.getScaleY(), width,
                            height);
                    canvas.rotate(v.getRotation(), width, height);
                    canvas.translate((width - v.getWidth()),
                            (height - v.getHeight()));
                    super.onDrawShadow(canvas);
                }
            };
            v.startDrag(dragData, myShadow, null, 0);

            return true;
        }
    }

    protected class myDragEventListener implements View.OnDragListener {
        @RequiresApi(api = Build.VERSION_CODES.M)
        public boolean onDrag(View v, DragEvent event) {
            Tile t = (Tile) v;
            final int action = event.getAction();
            if (t.getState() == 1) {
                return false;
            }

            int x = t.getPosX();
            int y = t.getPosY();
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    if (direction.equals("n")) {
                        if (t.getPosY() > dim - length) {
                            return false;
                        }
                    } else {
                        if (t.getPosX() > dim - length) {
                            return false;
                        }
                    }
                    if (!check(x, y)) {
                        return false;
                    }
                    if (direction.equals("n")) {

                        for (int i = y; i < y + length; i++) {
                            buttons[i][x].setBackgroundColor(Color.GREEN);
                        }
                    } else {
                        for (int i = x; i < x + length; i++) {
                            buttons[y][i].setBackgroundColor(Color.GREEN);
                        }
                    }

                    return true;
                case DragEvent.ACTION_DRAG_LOCATION:
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    if (direction.equals("n")) {
                        if (t.getPosY() > dim - length) {
                            return false;
                        }
                    } else {
                        if (t.getPosX() > dim - length) {
                            return false;
                        }
                    }
                    if (!check(x, y)) {
                        return false;
                    }
                    if (direction.equals("n")) {
                        for (int i = y; i < y + length; i++) {
                            buttons[i][x].setBackground(getDrawable(R.drawable.ocean_tile));
                        }
                    } else {
                        for (int i = x; i < x + length; i++) {
                            buttons[y][i].setBackground(getDrawable(R.drawable.ocean_tile));

                        }
                    }
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DROP:
                    if (direction.equals("n")) {
                        if (t.getPosY() > dim - length) {
                            return false;
                        }
                    } else {
                        if (t.getPosX() > dim - length) {
                            return false;
                        }
                    }
                    if (!check(x, y)) {
                        return false;
                    }
                    placeShip(x, y);
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    return true;
                default:
                    Log.e("DragDrop Example", "Unknown action type received by OnDragListener");
                    break;
            }
            return false;
        }
    }

    private boolean check(int x, int y) {
        if (direction.equals("n")) {
            for (int i = y; i < y + length; i++) {
                if (buttons[i][x].getState() == 1) {
                    return false;
                }

            }
        } else {
            for (int i = x; i < x + length; i++) {
                if (buttons[y][i].getState() == 1) {
                    return false;
                }
            }
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void placeShip(int x, int y) {
        if (direction.equals("n")) {
            for (int i = 0; i < length; i++) {
                buttons[i + y][x].setState(1);
                buttons[i + y][x].setShip(type);
                buttons[i + y][x].setShipPart(i + 1);
                String drawName = buttons[i + y][x].getShip() + "_" + buttons[i + y][x].getShipPart() + "1";
                int resId = getResources().getIdentifier(drawName, "drawable", getPackageName());
                Drawable part = getDrawable(resId);
                buttons[i + y][x].setBackground(part);

            }
        } else {
            for (int i = 0; i < length; i++) {
                buttons[y][x + i].setState(1);
                buttons[y][x + i].setShip(type);
                buttons[y][x + i].setShipPart(i + 1);
                String drawName = buttons[y][x + i].getShip() + "_" + buttons[y][x + i].getShipPart();
                int resId = getResources().getIdentifier(drawName, "drawable", getPackageName());
                Drawable part = getDrawable(resId);
                buttons[y][x + i].setBackground(part);
            }
        }
        AllPlaced = true;
        for (int k = 0; k < shiparr.length; k++) {
            if (shiparr[k].getType() == type) {
                shiparr[k].setPositions(x, y);
                shiparr[k].setVisibility(View.GONE);
            }
            if (shiparr[k].isPlaced() == false) {
                AllPlaced = false;
            }
        }
        if (AllPlaced) {
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    myOnClickListener clickListen = new myOnClickListener();
                    buttons[i][j].setOnClickListener(clickListen);
                }
            }
            player.setText("Your Map");
            buttonRotate.setVisibility(View.GONE);
            if (update) {
                String s = writeToServer();
                reference.child(id).setValue(s);
                Intent change = new Intent(Multiplayer.this, ComputerActivity.class);
                startActivity(change);
            } else {
                Intent change = new Intent(Multiplayer.this, ComputerMultiplayer.class);
                startActivity(change);
            }
        }
    }



    protected class myOnClickListener implements View.OnClickListener {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onClick(View v) {
            MediaPlayer mp2 = MediaPlayer.create(getApplicationContext(), R.raw.cannon_2);
            mp2.start();
            Tile t = (Tile) v;
            if (t.getState() == 0) {
                t.setState(4);
                t.setBackground(getDrawable(R.drawable.ocean_tile_miss));
                FragmentManager fm = getSupportFragmentManager();
                popFrag editNameDialogFragment = popFrag.newInstance("Yo ho ho! They missed!");
                editNameDialogFragment.show(fm, "fragment_edit_name");
            } else if (t.getState() == 1) {
                mp2.stop();
                MediaPlayer mp3 = MediaPlayer.create(getApplicationContext(), R.raw.explosion);
                mp3.start();
                t.setState(2);
                String name = t.getShip();
                for (int ship = 0; ship < shiparr.length; ship++) {
                    if (shiparr[ship].getType().equals(name)) {
                        shiparr[ship].hit(t.getPosX(), t.getPosY());
                        if (shiparr[ship].isSunk()) {
                            sink(name);
                            FragmentManager fm = getSupportFragmentManager();
                            if(!AllSunk) {
                                popFrag editNameDialogFragment = popFrag.newInstance("Arrg they sunk our " + name);
                                editNameDialogFragment.show(fm, "fragment_edit_name");
                            }
                        } else {
                            t.setState(2);
                            if(shiparr[ship].getDirection().equals("n")) {
                                String drawName = t.getShip() + "_" + t.getShipPart()  + "1" +"_x";

                                int resId = getResources().getIdentifier(drawName, "drawable", getPackageName());
                                Drawable part = getDrawable(resId);
                                t.setBackground(part);
                            }
                            else {
                                String drawName = t.getShip() + "_" + t.getShipPart() +  "_x";
                                int resId = getResources().getIdentifier(drawName, "drawable", getPackageName());
                                Drawable part = getDrawable(resId);
                                t.setBackground(part);
                            }
                            FragmentManager fm = getSupportFragmentManager();
                            popFrag editNameDialogFragment = popFrag.newInstance("Avast Ye, they hit us!");
                            editNameDialogFragment.show(fm, "fragment_edit_name");
                        }
                    }
                }
            }



        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void sink(String name) {
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                if (buttons[i][j].getShip().equals(name)) {
                    buttons[i][j].setState(3);
                    buttons[i][j].setBackground(getDrawable(R.drawable.ocean_tile_death));
                }
            }
        }
         AllSunk = true;
        for(int k = 0; k < shiparr.length; k++){
            if(!shiparr[k].isSunk()){
                AllSunk = false;
            }
        }
        if(AllSunk){
            FragmentManager fm = getSupportFragmentManager();
            finalfrag editNameDialogFragment = finalfrag.newInstance("Defeat!");
            editNameDialogFragment.show(fm, "fragment_edit_name");
        }
    }

    protected class Computer{
        private int x;
        private int y;
        public Computer(){

        }

        public void RandomHit() {
            Random ran = new Random();
            x = ran.nextInt(dim);
            y = ran.nextInt(dim);
            while (!(buttons[y][x].getState() == (1)) && !(buttons[y][x].getState() == 0)) {
                x = ran.nextInt(dim);
                y = ran.nextInt(dim);
            }
            Log.v("why", "" + x +" , "+ y);
            buttons[y][x].performClick();
        }
    }



    private String writeToServer() {
           String temp = "";
            for(int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    temp = temp + buttons[i][j].getState() + ",";
                }
            }
            for(int i = 0; i <shiparr.length;i++) {
                temp = temp + shiparr[i].getDirection() + ",";
                Vector positions = shiparr[i].getPositions();
                for (int k = 0; k < positions.size(); k++) {
                    temp = temp + positions.get(k) + ",";
                }
                int health = shiparr[i].getHealth();
                temp = temp + health + ",";
            }
            return temp;
    }

}

