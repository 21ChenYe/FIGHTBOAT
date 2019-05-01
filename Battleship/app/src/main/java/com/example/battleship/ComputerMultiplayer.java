package com.example.battleship;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Random;
import java.util.Vector;

public class ComputerMultiplayer extends AppCompatActivity implements DialogInterface.OnDismissListener {
    private Tile[][] buttons = new Tile[10][10];
    private int dim = 10;
    private TextView player;
    private Ship carrier;
    private Ship battleship;
    private Ship cruiser;
    private Ship sub;
    private String type;
    private String direction;
    private int length;
    private Computer comp;
    private boolean message = false;
    private boolean AllSunk = false;
    String value;
    Ship[] shiparr;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    String id = "game";
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_computer);
        comp = new Computer();
        player = findViewById(R.id.text_view_player2);
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                String buttonId = "button_" + i + j + "2";
                int resId = getResources().getIdentifier(buttonId, "id", getPackageName());
                buttons[i][j] = findViewById(resId);
                buttons[i][j].setPosX(j);
                buttons[i][j].setPosY(i);
                buttons[i][j].setState(0);
                buttons[i][j].setShip("");
                buttons[i][j].setBackground(getDrawable(R.drawable.ocean_tile));
                myOnClickListener clickListen = new myOnClickListener();
                buttons[i][j].setOnClickListener(clickListen);
            }
        }
        carrier = findViewById(R.id.carrier_ship2);
        carrier.setType("carrier");
        carrier.setLength(5);


        battleship = findViewById(R.id.battle_ship2);
        battleship.setType("cruiser");
        battleship.setLength(3);


        cruiser = findViewById(R.id.cruiser_ship2);
        cruiser.setType("destroyer");
        cruiser.setLength(2);


        sub = findViewById(R.id.sub_ship2);
        sub.setType("sub");
        sub.setLength(3);

        Ship[] temp = {carrier,battleship,cruiser,sub};
        shiparr = temp;
        File directory = getFilesDir();
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                value = dataSnapshot.getValue(String.class);
                if(!value.equals("a"))
                LoadGame(value);
                //reference.child(id).setValue("a");
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
        try {
            Log.v("boop",directory.getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        File file = new File(directory, "config.txt");
        if(file.exists()) {
            String read = readFromFile(getApplicationContext());
            Log.v("length",read);
            LoadGame(read);
            FlashMap();
        }
        else{
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
           // comp.RandomPlace();
        }
        FragmentManager fm = getSupportFragmentManager();
        popFrag editNameDialogFragment = popFrag.newInstance("Choose Your Attack");
        editNameDialogFragment.show(fm, "fragment_edit_name");
    }


    @Override
    public void onDismiss(final DialogInterface dialog) {
        if(message){
            message = false;
            finish();
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
                // String drawName = buttons[i + y][x].getShip() + "_" + buttons[i + y][x].getShipPart() + "1";
                // int resId = getResources().getIdentifier(drawName, "drawable", getPackageName());
                //  Drawable part = getDrawable(resId);
                //  buttons[i + y][x].setBackground(part);

            }
        } else {
            for (int i = 0; i < length; i++) {
                buttons[y][x + i].setState(1);
                buttons[y][x + i].setShip(type);
                buttons[y][x + i].setShipPart(i + 1);
                //  String drawName = buttons[y][x + i].getShip() + "_" + buttons[y][x + i].getShipPart();
                //  int resId = getResources().getIdentifier(drawName, "drawable", getPackageName());
                //  Drawable part = getDrawable(resId);
                // buttons[y][x + i].setBackground(part);
            }
        }
        for (int k = 0; k < shiparr.length; k++) {
            if (shiparr[k].getType() == type) {
                shiparr[k].setPositions(x, y);
                shiparr[k].setVisibility(View.GONE);
            }
        }
    }



    protected class myOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Tile t = (Tile) v;
            if (t.getState() ==0) {
                t.setState(4);
                t.setBackgroundColor(Color.BLACK);
                message =true;
                FragmentManager fm = getSupportFragmentManager();
                popFrag editNameDialogFragment = popFrag.newInstance("Take off ye eye-patch!");
                editNameDialogFragment.show(fm, "fragment_edit_name");
            } else if (t.getState()== 1) {
                t.setState(2);
                String name = t.getShip();
                if(name.equals("")){
                    Log.v("empty","empty ship");
                }
                Log.v("hit",t.getShip());
                for (int ship = 0; ship < shiparr.length; ship++) {
                    if (shiparr[ship].getType().equals(name)) {
                        shiparr[ship].hit(t.getPosX(), t.getPosY());
                        if (shiparr[ship].isSunk()) {
                            sink(name);
                            if(!AllSunk) {
                                message = true;
                                FragmentManager fm = getSupportFragmentManager();
                                popFrag editNameDialogFragment = popFrag.newInstance("Ye've plundered their " + name + "!");
                                editNameDialogFragment.show(fm, "fragment_edit_name");
                            }
                        } else {
                            t.setState(2);
                            t.setBackgroundColor(Color.RED);
                            message = true;
                            FragmentManager fm = getSupportFragmentManager();
                            popFrag editNameDialogFragment = popFrag.newInstance("Aye ye hit 'em!");
                            editNameDialogFragment.show(fm, "fragment_edit_name");
                        }
                    }
                }
            }
            File directory = getFilesDir(); //or getExternalFilesDir(null); for external storage
            File file = new File(directory, "config.txt");
            file.delete();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            writeToFile(getApplicationContext());
            String read = readFromFile(getApplicationContext());
            Log.v("writing",read);
            // finish();

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
        AllSunk = true;
        for(int k = 0; k < shiparr.length; k++){
            if(!shiparr[k].isSunk()){
                AllSunk = false;
            }
        }
        if(AllSunk){
            FragmentManager fm = getSupportFragmentManager();
            finalfrag editNameDialogFragment = finalfrag.newInstance("Victory!");
            editNameDialogFragment.show(fm, "fragment_edit_name");
        }
    }

    protected class Computer{
        public Computer(){

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
    }
    private String readFromFile(Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("config.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("file activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("file activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    private void writeToFile(Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config.txt", Context.MODE_PRIVATE));
            String states = "";
            for(int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    states = states + buttons[i][j].getState() + ",";
                }
            }
            outputStreamWriter.write(states);

            for(int i = 0; i <shiparr.length;i++) {
                String ship = "";
                ship = ship + shiparr[i].getDirection() + ",";
                outputStreamWriter.write(ship);
                Vector positions = shiparr[i].getPositions();
                for (int k = 0; k < positions.size(); k++) {
                    outputStreamWriter.write(((int) positions.get(k)) + ",");
                }
                int health = shiparr[i].getHealth();
                Log.v("fucky wucky", "" + health);
                outputStreamWriter.write(health + ",");
            }
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
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
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
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