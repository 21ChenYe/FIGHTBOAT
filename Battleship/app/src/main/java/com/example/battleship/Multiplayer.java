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
    private boolean playerfirst;
    private String initial;
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
                    myOnClickListener clickListen = new myOnClickListener();
                    buttons[i][j].setOnClickListener(clickListen);
                }
            }
            carrier = findViewById(R.id.carrier_ship);
            carrier.setType("carrier");
            carrier.setLength(5);


            battleship = findViewById(R.id.battle_ship);
            battleship.setType("cruiser");
            battleship.setLength(3);


            cruiser = findViewById(R.id.cruiser_ship);
            cruiser.setType("destroyer");
            cruiser.setLength(2);


            sub = findViewById(R.id.sub_ship);
            sub.setType("sub");
            sub.setLength(3);

            Ship[] temp = {battleship, cruiser, sub, carrier};
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
                int check = Character.getNumericValue(value.charAt(0));
                if (check == 0){
                    Log.v("read",value);
                    playerfirst = true;
                    value = "1" + value.substring(1);
                    initial = value;
                    reference.child(id).setValue(value);
                }
                else{
                    playerfirst = false;
                    value = "0" + value.substring(1);
                    initial = value;
                    reference.child(id).setValue(value);
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


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onResume(){
        super.onResume();
        if(AllPlaced) {
            if(playerfirst){
                while(initial == value.substring(1,287)){
                    LoadGame(value.substring(1,287));
                    FlashMap();
                }

            }
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
            if(shiparr[k].isPlaced() == false){
                AllPlaced = false;
            }
        }
        if (AllPlaced){
            String temp = writeToServer();
            if(playerfirst){
                value = value.charAt(0) + temp + value.substring(287);
            }
            else {
                value = value.substring(0,287) + temp;

            }
            initial = temp;
            String test = value;
            reference.child(id).setValue(value);
            player.setText("Your Map");
            buttonRotate.setVisibility(View.GONE);
            while(test.equals(value)){

                }
            Intent change = new Intent(Multiplayer.this, ComputerActivity.class);
            change.putExtra("player",playerfirst);
            startActivity(change);
        }
    }



    protected class myOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Tile t = (Tile) v;
            if (t.getState() == 0) {
                t.setState(4);
                t.setBackgroundColor(Color.BLACK);
                FragmentManager fm = getSupportFragmentManager();
                popFrag editNameDialogFragment = popFrag.newInstance("Yo ho ho! They missed!");
                editNameDialogFragment.show(fm, "fragment_edit_name");
            } else if (t.getState() == 1) {
                t.setState(2);
                String name = t.getShip();
                for (int ship = 0; ship < shiparr.length; ship++) {
                    if (shiparr[ship].getType().equals(name)) {
                        shiparr[ship].hit(t.getPosX(), t.getPosY());
                        if (shiparr[ship].isSunk()) {
                            sink(name);
                            FragmentManager fm = getSupportFragmentManager();
                            popFrag editNameDialogFragment = popFrag.newInstance("Arrg they sunk our " + name);
                            editNameDialogFragment.show(fm, "fragment_edit_name");
                        } else {
                            t.setState(2);
                            t.setBackgroundColor(Color.RED);
                            FragmentManager fm = getSupportFragmentManager();
                            popFrag editNameDialogFragment = popFrag.newInstance("Avast Ye, they hit us!");
                            editNameDialogFragment.show(fm, "fragment_edit_name");
                        }
                    }
                }
            }



        }
    }

    public void sink(String name) {
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                if (buttons[i][j].getShip().equals(name)) {
                    buttons[i][j].setState(3);
                    buttons[i][j].setBackgroundColor(Color.YELLOW);
                }
            }
        }
        boolean AllSunk = true;
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
        private int[] origin = {-1,-1};
        private int[] current = {-1,-1};
        private String directionAttack = "";
        private TextView player;
        private Ship carrier;
        private Ship battleship;
        private Ship cruiser;
        private Ship sub;
        private Vector directions = new Vector();
        private boolean success = true;
        private int x;
        private int y;
        Ship[] shiparr;
        public Computer(){
            carrier = findViewById(R.id.carrier_ship);
            carrier.setType("carrier");
            carrier.setLength(5);


            battleship = findViewById(R.id.battle_ship);
            battleship.setType("cruiser");
            battleship.setLength(3);


            cruiser = findViewById(R.id.cruiser_ship);
            cruiser.setType("destroyer");
            cruiser.setLength(2);


            sub = findViewById(R.id.sub_ship);
            sub.setType("sub");
            sub.setLength(3);

            Ship[] temp = {battleship, cruiser, sub, carrier};
            shiparr = temp;
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
                    temp = temp + ((int) positions.get(k)) + ",";
                }
                int health = shiparr[i].getHealth();
                temp = temp + health + ",";
            }
            return temp;
    }
    private void LoadGame(String s){
        for(int i = 0; i <200; i += 2){
            int a  =i/2;
            int y = a/10;
            int x = a%10;
            buttons[y][x].setState(Character.getNumericValue(s.charAt(i)));
        }
        carrier.setDirection("" + s.charAt(200));
        int w = Character.getNumericValue(s.charAt(202));
        int q = Character.getNumericValue(s.charAt(204));
        carrier.setPositions(q,w);
        for(int j = 202; j <221; j+=4){
            int y =  Character.getNumericValue(s.charAt(j));
            int x = Character.getNumericValue(s.charAt(j+2));
            buttons[y][x].setShip("carrier");
        }
        carrier.setHealth(Character.getNumericValue(s.charAt(222)));
        Log.v("carrier Health", "" + carrier.getHealth());
        battleship.setDirection("" + s.charAt(224));
        w = Character.getNumericValue(s.charAt(226));
        q = Character.getNumericValue(s.charAt(228));
        battleship.setPositions(q,w);
        for(int j = 226; j <237; j+=4){
            int y =  Character.getNumericValue(s.charAt(j));
            int x = Character.getNumericValue(s.charAt(j+2));
            buttons[y][x].setShip("cruiser");
        }
        battleship.setHealth(Character.getNumericValue(s.charAt(238)));

        cruiser.setDirection("" + s.charAt(240));
        w = Character.getNumericValue(s.charAt(242));
        q = Character.getNumericValue(s.charAt(244));
        cruiser.setPositions(q,w);
        for(int j = 242; j <249; j+=4){
            int y =  Character.getNumericValue(s.charAt(j));
            int x = Character.getNumericValue(s.charAt(j+2));
            buttons[y][x].setShip("destroyer");
        }
        cruiser.setHealth(Character.getNumericValue(s.charAt(250)));

        sub.setDirection("" + s.charAt(252));
        w = Character.getNumericValue(s.charAt(254));
        q = Character.getNumericValue(s.charAt(256));
        sub.setPositions(q,w);
        for(int j = 254; j <265; j+=4){
            int y =  Character.getNumericValue(s.charAt(j));
            int x = Character.getNumericValue(s.charAt(j+2));
            buttons[y][x].setShip("sub");
        }
        sub.setHealth(Character.getNumericValue(s.charAt(266)));
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void FlashMap(){
        for(int i = 0; i <10; i++){
            for(int j = 0; j<10; j++){
                int State = buttons[i][j].getState();
                switch (State){
                    case 0:
                    case 1:
                        buttons[i][j].setBackground(getDrawable(R.drawable.ocean_tile));
                        break;
                    case 2:
                        buttons[i][j].setBackgroundColor(Color.RED);
                        break;
                    case 3:
                        buttons[i][j].setBackgroundColor(Color.YELLOW);
                        break;
                    case 4:
                        buttons[i][j].setBackgroundColor(Color.BLACK);
                        break;
                }
            }
        }
    }
}

