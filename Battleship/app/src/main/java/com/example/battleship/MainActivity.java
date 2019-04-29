package com.example.battleship;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.RequiresApi;
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

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {
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
    private Button randButton;
    Ship[] shiparr;

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
            Intent intention = new Intent(getApplicationContext(), PopActivity.class);
            intention.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intention);
            randButton = findViewById(R.id.randButton);
            randButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    comp.RandomHit();
                }
            });
            //Toast.makeText(getApplicationContext(),"Created",Toast.LENGTH_LONG).show();
        }
        File directory = getFilesDir();
        File file = new File(directory, "config.txt");
        file.delete();
    }
    @Override
    protected void onSaveInstanceState(Bundle state){
        Log.v("bundle", "Saved");
        state.putBoolean("check", true);
        super.onSaveInstanceState(state);
    }
    @Override
    protected void onStart(){
        super.onStart();
        Log.v("bundle", "Im here");
    }
    @Override
    protected void onRestoreInstanceState(Bundle state){
        Log.v("bundle", "Fully restored");
        super.onRestoreInstanceState(state);
        boolean check = state.getBoolean("check");
        if(check){
            Log.v("bundle", "we're gucci");
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
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
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
                    if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                        v.setBackgroundColor(Color.BLUE);
                        v.invalidate();
                        return true;
                    }
                    return false;
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
                            buttons[i][x].setBackgroundColor(Color.BLUE);
                        }
                    } else {
                        for (int i = x; i < x + length; i++) {
                            buttons[y][i].setBackgroundColor(Color.BLUE);
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
                    v.setBackgroundColor(Color.GRAY);
                    v.invalidate();
                    if (event.getResult()) {
                        Toast.makeText(getApplicationContext(), "The drop was handled.", Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(getApplicationContext(), "The drop didn't work.", Toast.LENGTH_LONG).show();

                    }
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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
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
        boolean AllPLaced = true;
        for (int k = 0; k < shiparr.length; k++) {
            if (shiparr[k].getType() == type) {
                shiparr[k].setPositions(x, y);
                shiparr[k].setVisibility(View.GONE);
            }
            if(shiparr[k].isPlaced() == false){
                AllPLaced = false;
            }
        }
        if (AllPLaced){
            Intent change = new Intent(MainActivity.this, ComputerActivity.class);
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
                //Add popup message saying missed
            } else if (t.getState() == 1) {
                t.setState(2);
                String name = t.getShip();
                for (int ship = 0; ship < shiparr.length; ship++) {
                    if (shiparr[ship].getType().equals(name)) {
                        shiparr[ship].hit(t.getPosX(), t.getPosY());
                        if (shiparr[ship].isSunk()) {
                            sink(name);
                            //Add popup message saying sunk ...
                        } else {
                            t.setState(2);
                            t.setBackgroundColor(Color.RED);
                            //Add popup message saying hit
                        }
                    }
                }
            }
            Intent goDown = new Intent(MainActivity.this,ComputerActivity.class);
            startActivity(goDown);

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
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        protected void RandomPlace() {
            int xLim;
            int yLim;
            Random rand = new Random();
            for (int i = 0; i < shiparr.length; i++) {
                int randDir = rand.nextInt(2);
                if (randDir == 0) {
                    shiparr[i].setDirection("n");
                } else {
                    shiparr[i].setDirection("e");
                }
                type = shiparr[i].getType();
                length = shiparr[i].getLength();
                direction = shiparr[i].getDirection();
                if (direction.equals("n")) {
                    xLim = dim;
                    yLim = dim - length;
                } else {
                    xLim = dim - length;
                    yLim = dim;
                }
                int x = rand.nextInt(xLim);
                int y = rand.nextInt(yLim);
                while (!check(x, y)) {
                    x = rand.nextInt(xLim);
                    y = rand.nextInt(yLim);
                }
                placeShip(x, y);
            }

        }

        public void RandomHit() {
            Random ran = new Random();
            if(origin[0] == -1) {
                x = ran.nextInt(dim);
                 y = ran.nextInt(dim);
                while (!(buttons[x][y].getState()== 1) && !(buttons[x][y].getState()== 0)) {
                    x = ran.nextInt(dim);
                    y = ran.nextInt(dim);
                }
                if(buttons[x][y].getState()== 1){
                    origin[0] = x;
                    origin[1] = y;
                    current[0] = x;
                    current[1] = y;
                }
            }
            else{
                if (directionAttack.equals("")) {
                    directions.add("n");
                    directions.add("s");
                    directions.add("e");
                    directions.add("w");
                    int choice = ran.nextInt(directions.size());
                    directionAttack = (String) directions.get(choice);
                    while (!checkDirection(directionAttack)){
                        choice = ran.nextInt(directions.size());
                        directionAttack = (String) directions.get(choice);
                    }
                }
                else if (!success){
                    directions.remove(directionAttack);
                    directionAttack = (String) directions.get(0);
                    while (!checkDirection(directionAttack)){
                       int choice = ran.nextInt(directions.size());
                        directionAttack = (String) directions.get(choice);
                    }
                    current[0] = origin[0];
                    current[1] = origin[1];
                }
            }
            if(buttons[x][y].getState()== 1){
                success = true;
            }
            else {
                success = false;
            }
            while (!checkDirection(directionAttack)){
               int choice = ran.nextInt(directions.size());
                directionAttack = (String) directions.get(choice);
            }
            changeCurrent(directionAttack);
            buttons[x][y].performClick();
            if(buttons[x][y].getState() ==3){
                origin[0] = -1;
                directionAttack = "";

            }
        }
        private boolean checkDirection(String d){
            switch (d){
                case "n":
                    if (origin[0] == 0 || (!(buttons[origin[0]-1][origin[1]].getState()== 0) && !(buttons[origin[0]-1][origin[1]].getState()== 1))){
                        directions.remove("n");
                        return false;
                    }
                case "e":
                    if (origin[1] == 10  || (!(buttons[origin[0]][origin[1]+1].getState()== 0) && !(buttons[origin[0]][origin[1]+1].getState()== 1))) {
                        directions.remove("e");
                        return false;
                    }
                case "s":
                    if (origin[0] == 10  || (!(buttons[origin[0]+1][origin[1]].getState()== 0) && !(buttons[origin[0]+1][origin[1]].getState()== 1))) {
                        directions.remove("s");
                        return false;
                    }
                case "w":
                    if (origin[1] == 0 || (!(buttons[origin[0]][origin[1]-1].getState()== 0) && !(buttons[origin[0]][origin[1]-1].getState()== 1))) {
                        directions.remove("w");
                        return false;
                    }
            }
            return true;
        }
        private void changeCurrent(String d){
            switch (d){
                case "e":
                    x = current[0];
                    y = current[1]+1;
                    current[1]++;
                    break;
                case "s":
                    x = current[0]+1;
                    current[0]++;
                    y = current[1];
                    break;
                case "w":
                    x = current[0];
                    y = current[1]-1;
                    current[1]--;
                    break;
                case "n":
                    x = current[0]-1;
                    current[0]--;
                    y = current[1];
                    break;
            }
        }
        }
    }

