package com.example.battleship;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Tile[][] buttons = new Tile[10][10];
    private int dim = 10;
    private TextView player;
    private Ship carrier;
    private Ship battleship;
    private Ship cruiser;
    private Button buttonRotate;
    private String type;
    private String direction;
    private int length;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        player = findViewById(R.id.text_view_player);
        for (int i =0; i < 10 ; i++){
            for (int j = 0; j < 10; j ++){
                    String buttonId = "button_"  + i + j;
                    int resId = getResources().getIdentifier(buttonId, "id", getPackageName());
                    buttons[i][j] = findViewById(resId);
                    buttons[i][j].setPosX(j);
                    buttons[i][j].setPosY(i);
                myDragEventListener dragListen = new myDragEventListener();
                    buttons[i][j].setOnDragListener(dragListen);
            }
        }
         carrier= findViewById(R.id.carrier_ship);
        carrier.setType("carrier");
        carrier.setLength(5);
        myOnLongClickListener LongClickListen = new myOnLongClickListener();
        carrier.setOnLongClickListener(LongClickListen);

        battleship = findViewById(R.id.battle_ship);
        battleship.setType("cruiser");
        battleship.setLength(3);
        myOnLongClickListener LongClickListen2 = new myOnLongClickListener();
        battleship.setOnLongClickListener(LongClickListen2);

        cruiser = findViewById(R.id.cruiser_ship);
        cruiser.setType("destroyer");
        cruiser.setLength(2);
        myOnLongClickListener LongClickListen3 = new myOnLongClickListener();
        cruiser.setOnLongClickListener(LongClickListen3);


        buttonRotate = findViewById(R.id.rotate_button);
        buttonRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(carrier.getRotation() == 0) {
                    carrier.setRotation(carrier.getRotation() + 90);
                    carrier.setDirection("n");
                    battleship.setRotation(battleship.getRotation() + 90);
                    battleship.setDirection("n");
                    cruiser.setRotation(cruiser.getRotation() + 90);
                    cruiser.setDirection("n");
                }
                else {
                    carrier.setRotation(carrier.getRotation() - 90);
                    carrier.setDirection("e");
                    battleship.setRotation(battleship.getRotation() - 90);
                    battleship.setDirection("e");
                    cruiser.setRotation(cruiser.getRotation() - 90);
                    cruiser.setDirection("e");
                }
            }
        });
    }
    protected class myOnLongClickListener implements  View.OnLongClickListener {
        @Override
        public boolean onLongClick(final View v) {
            Ship ship = (Ship) v;
            type = ship.getType();
            direction = ship.getDirection();
            length = ship.getLength();
            ClipData dragData =  ClipData.newPlainText("test",ship.getType());
            double rotationRad = Math.toRadians(v.getRotation());
            final int w = (int) (v.getWidth() * v.getScaleX());
            final int h = (int) (v.getHeight() * v.getScaleY());
            double s = Math.abs(Math.sin(rotationRad));
            double c = Math.abs(Math.cos(rotationRad));
            final int width = (int) (w * c + h * s);
            final int height = (int) (w * s + h * c);
            View.DragShadowBuilder myShadow = new View.DragShadowBuilder(v){
                @Override
                public void onDrawShadow(Canvas canvas) {
                    canvas.scale(v.getScaleX(), v.getScaleY(), width,
                            height);
                    canvas.rotate(v.getRotation(), width , height );
                    canvas.translate((width - v.getWidth()) ,
                            (height - v.getHeight()) );
                    super.onDrawShadow(canvas);
                }
            };
            v.startDrag(dragData,myShadow,null,0);

            return true;
        }
    }
    protected class myDragEventListener implements View.OnDragListener{
        private boolean check(int x, int y){
            if(direction.equals("n")){
                for (int i = y; i < y+length ; i++){
                    if(buttons[i][x].getState() == "filled"){
                        return false;
                    }

                }
            }
            else{
                for (int i = x; i < x+length ; i++){
                   if(buttons[y][i].getState() == "filled"){
                       return false;
                   }
                }
            }
            return true;
        }
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        public boolean onDrag(View v, DragEvent event) {
                Tile t = (Tile) v;
                final int action = event.getAction();
                if(t.getState() == "filled"){
                    return false;
                }

            int x = t.getPosX();
            int y = t.getPosY();
                switch(action) {
                    case DragEvent.ACTION_DRAG_STARTED:
                            if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)){
                                v.setBackgroundColor(Color.BLUE);
                                v.invalidate();
                                return true;
                            }
                            return false;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        if(direction.equals("n")){
                            if(t.getPosY() > dim-length){
                                return false;
                            }
                        }
                        else {
                            if(t.getPosX() > dim-length){
                                return false;
                            }
                        }
                                if(direction.equals("n")){
                                    if(!check(x,y)){
                                        return false;
                                    }
                                    for (int i = y; i < y+length ; i++){
                                        buttons[i][x].setBackgroundColor(Color.GREEN);
                                    }
                                }
                                else{
                                    for (int i = x; i < x+length ; i++){
                                        buttons[y][i].setBackgroundColor(Color.GREEN);
                                    }
                                }

                        return true;
                    case DragEvent.ACTION_DRAG_LOCATION:
                        return true;
                    case DragEvent.ACTION_DRAG_EXITED:
                        if(direction.equals("n")){
                            if(t.getPosY() > dim-length){
                                return false;
                            }
                        }
                        else {
                            if(t.getPosX() > dim-length){
                                return false;
                            }
                        }
                        if(!check(x,y)){
                            return false;
                        }
                        if(direction.equals("n")){
                            for (int i = y; i < y+length ; i++){
                                buttons[i][x].setBackgroundColor(Color.BLUE);
                            }
                        }
                        else{
                            for (int i = x; i < x+length ; i++){
                                buttons[y][i].setBackgroundColor(Color.BLUE);
                            }
                        }
                        v.invalidate();
                        return true;
                    case DragEvent.ACTION_DROP:
                        if(direction.equals("n")){
                            if(t.getPosY() > dim-length){
                                return false;
                            }
                        }
                        else {
                            if(t.getPosX() > dim-length){
                                return false;
                            }
                        }
                                if(!check(x,y)){
                                    return false;
                                }
                                if(direction.equals("n")){
                                    for (int i = 0; i < length ; i++){
                                       // buttons[i+y][x].setBackgroundColor(Color.GREEN);
                                        buttons[i+y][x].setState("filled");
                                        buttons[i+y][x].setShip(type);
                                        buttons[i+y][x].setShipPart(i+1);
                                        String drawName = buttons[i+y][x].getShip() +"_" + buttons[i+y][x].getShipPart() +"1";
                                        int resId = getResources().getIdentifier(drawName, "drawable", getPackageName());
                                        Drawable part = getDrawable(resId);
                                        buttons[i+y][x].setBackground(part);

                                    }
                                }
                                else{
                                    for (int i = 0; i < length ; i++){
                                       // buttons[y][x+i].setBackgroundColor(Color.GREEN);
                                        buttons[y][x+i].setState("filled");
                                        buttons[y][x+i].setShip(type);
                                        buttons[y][x+i].setShipPart(i+1);
                                        String drawName = buttons[y][x+i].getShip() +"_" + buttons[y][x+i].getShipPart();
                                        int resId = getResources().getIdentifier(drawName, "drawable", getPackageName());
                                        Drawable part = getDrawable(resId);
                                        buttons[y][x+i].setBackground(part);
                                    }
                                }

                        return true;
                     case DragEvent.ACTION_DRAG_ENDED:
                         v.setBackgroundColor(Color.GRAY);
                         v.invalidate();
                         if (event.getResult()) {
                             Toast.makeText(getApplicationContext(), "The drop was handled.", Toast.LENGTH_LONG).show();

                         }
                         else {
                                Toast.makeText(getApplicationContext(), "The drop didn't work.",Toast.LENGTH_LONG).show();

                         }
                         return true;
                         default:
                             Log.e("DragDrop Example", "Unknown action type received by OnDragListener");
                             break;
                }
                return false;
        }
    }
}
