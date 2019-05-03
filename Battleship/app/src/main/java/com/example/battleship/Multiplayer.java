package com.example.battleship;

import android.annotation.TargetApi;
import android.content.ClipData;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.util.Random;
import java.util.Vector;
//Multiplayer version of main activity
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
    private boolean AllPlaced;
    private String value;
    private boolean update = false;
    private boolean AllSunk;
    Ship[] shiparr;

//Database details to connect to firebase
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    String id = "game";

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            //Same layout
            setContentView(R.layout.activity_main);
            player = findViewById(R.id.text_view_player);
        //Assign drag listeners to all tiles and update them with positional data
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
        //Instantiate the ship objects and assign direction, type and length
        //Direction is either east "e" or north "n"
        //Length signifies how many tiles it takes up
            carrier = findViewById(R.id.carrier_ship);
            carrier.setType("frigate");
            carrier.setDirection("e");
            carrier.setLength(5);


            battleship = findViewById(R.id.battle_ship);
            battleship.setType("caravel");
            battleship.setDirection("e");
            battleship.setLength(3);


            cruiser = findViewById(R.id.cruiser_ship);
            cruiser.setType("dandy");
            cruiser.setDirection("e");
            cruiser.setLength(2);


            sub = findViewById(R.id.sub_ship);
            sub.setType("sloop");
            sub.setDirection("e");
            sub.setLength(3);

            Ship[] temp = {carrier,battleship,cruiser,sub};
            shiparr = temp;
        //Assign onlong click listeners on the ships, allowing them to be dragged
        for (int ship = 0; ship < shiparr.length; ship++) {
                myOnLongClickListener LongClickListen = new myOnLongClickListener();
                shiparr[ship].setOnLongClickListener(LongClickListen);
            }
        //Create rotate button that allows the user to change with direction to place the ships,
        //If it is not placed yet
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
        //Tell the user they can drag their ships
        FragmentManager fm = getSupportFragmentManager();
            popFrag editNameDialogFragment = popFrag.newInstance("Place your Ships");
            editNameDialogFragment.show(fm, "fragment_edit_name");
        //Delete the config file for the enemies map, clearing data from last game

        File directory = getFilesDir();
        File file = new File(directory, "config.txt");
        file.delete();

        //Database connection, if the current string in the database is "a" this means
        //The user is "player 1". This means whereever the user places their ships, the computer
        //for player 2 will place it's ships in those spots
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
    //On returning to this activity if all of the ships are placed, randomly hit a tile
    protected void onResume(){
        super.onResume();
        if(AllPlaced) {
            RandomHit();
        }

    }

    @Override
    //After dismissing a message, if all of the ships are placed, go to the enemy's map
    public void onDismiss(final DialogInterface dialog) {
        if(AllPlaced){
            Intent goDown = new Intent(Multiplayer.this,ComputerActivity.class);
            startActivity(goDown);
        }
    }
    //Assign a custom on click listener to ships so they can be dragged
    protected class myOnLongClickListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(final View v) {
            Ship ship = (Ship) v;
            type = ship.getType();
            direction = ship.getDirection();
            length = ship.getLength();
            //Update the drag shadow to show the correct direction
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
            //Start to drag the ship
            v.startDrag(dragData, myShadow, null, 0);

            return true;
        }
    }
    //Drag listener for the tiles, updates what the tile displays
    protected class myDragEventListener implements View.OnDragListener {
        @RequiresApi(api = Build.VERSION_CODES.M)
        public boolean onDrag(View v, DragEvent event) {
            Tile t = (Tile) v;
            final int action = event.getAction();
            //If a tile already has a ship ignore the drag event
            if (t.getState() == 1) {
                return false;
            }

            int x = t.getPosX();
            int y = t.getPosY();
            //Change the display of the tile depending on what the drag event is doing
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    //If started do nothing
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    //If the ship has entered the tile's region, check if the ship would fit there
                    //If not do nothing
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
                    //If it can fit, change this tile and other tiles to show where the ship will be placed
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
                    //Do nothing
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    //If exited the region of the tile, change the tile back to empty ocean tile if it was white before
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
                    //If dropped, get the ship type and update this tile and other tiles to show the correct
                    //ship part
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
                    //Do nothing
                    return true;
                default:
                    break;
            }
            return false;
        }
    }
    //Method to check if a ship can fit
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
    //Place a ship by updating values to the tiles and images
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
        //Update ship information
        for (int k = 0; k < shiparr.length; k++) {
            if (shiparr[k].getType() == type) {
                shiparr[k].setPositions(x, y);
                shiparr[k].setVisibility(View.GONE);
            }
            if (shiparr[k].isPlaced() == false) {
                AllPlaced = false;
            }
        }
        //If user placed all the ships, set on click listeners to all the tiles
        //Allowing them to be attacked
        if (AllPlaced) {
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    myOnClickListener clickListen = new myOnClickListener();
                    buttons[i][j].setOnClickListener(clickListen);
                }
            }
            //Update the text and get rid of rotate button
            player.setText("Your Map");
            buttonRotate.setVisibility(View.GONE);
            if (update) {
                //If they are player 1, write to the server and go to a regular computer activity
                String s = writeToServer();
                reference.child(id).setValue(s);
                Intent change = new Intent(Multiplayer.this, ComputerActivity.class);
                startActivity(change);
            } else {
                //If player two go to a computer activity that has the positions of player 1
                Intent change = new Intent(Multiplayer.this, ComputerMultiplayer.class);
                startActivity(change);
            }
        }
    }



    protected class myOnClickListener implements View.OnClickListener {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        //If a tile is clicked, play a noise and change it's state and image depending if
        //the tile has a ship part in it or a sunk ship
        public void onClick(View v) {
            MediaPlayer mp2 = MediaPlayer.create(getApplicationContext(), R.raw.cannon_2);
            mp2.start();
            Tile t = (Tile) v;
            //If tile is empty
            if (t.getState() == 0) {
                t.setState(4);
                t.setBackground(getDrawable(R.drawable.ocean_tile_miss));
                FragmentManager fm = getSupportFragmentManager();
                popFrag editNameDialogFragment = popFrag.newInstance("Yo ho ho! They missed!");
                editNameDialogFragment.show(fm, "fragment_edit_name");
            } else if (t.getState() == 1) {
                //If tile has a ship part
                mp2.stop();
                MediaPlayer mp3 = MediaPlayer.create(getApplicationContext(), R.raw.explosion);
                mp3.start();
                t.setState(2);
                String name = t.getShip();
                for (int ship = 0; ship < shiparr.length; ship++) {
                    if (shiparr[ship].getType().equals(name)) {
                        shiparr[ship].hit(t.getPosX(), t.getPosY());
                        //If the ship is sunk update all the tiles with this ship to be sunk image
                        if (shiparr[ship].isSunk()) {
                            sink(name);
                            FragmentManager fm = getSupportFragmentManager();
                            if(!AllSunk) {
                                popFrag editNameDialogFragment = popFrag.newInstance("Arrg they sunk our " + name);
                                editNameDialogFragment.show(fm, "fragment_edit_name");
                            }
                        }
                        //If just hit, update the tile that was hit and the status of the ship object
                        else {
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
    //Sink a ship, update the ship object, and show the correct image
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
        //If all the ships are sunk, show defeat message
        if(AllSunk){
            FragmentManager fm = getSupportFragmentManager();
            finalfrag editNameDialogFragment = finalfrag.newInstance("Defeat!");
            editNameDialogFragment.show(fm, "fragment_edit_name");
        }
    }

    //Randomly hit a tile
    public void RandomHit() {
        int x;
        int y;
            Random ran = new Random();
            x = ran.nextInt(dim);
            y = ran.nextInt(dim);
            while (!(buttons[y][x].getState() == (1)) && !(buttons[y][x].getState() == 0)) {
                x = ran.nextInt(dim);
                y = ran.nextInt(dim);
            }
            buttons[y][x].performClick();
    }


//Method to create string that is sent to the server
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

