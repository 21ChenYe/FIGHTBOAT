package com.example.battleship;

import android.content.Context;
import android.util.AttributeSet;



public class Tile extends android.support.v7.widget.AppCompatButton {
        int posX;
        int posY;
        int shipPart;
        String ship;
        String state;
    public Tile(Context context, AttributeSet attrs){
        super(context,attrs);
    }
    public int getPosX(){
        return posX;
    }
    public void setPosX(int x){
            posX = x;

    }
    public void setPosY(int y){
            posY = y;

    }
    public int getPosY(){
        return posY;
    }
    public int getShipPart(){
            return shipPart;
    }
    public void setShipPart(int part){
            shipPart = part;
    }
    public String getShip(){
        return ship;
    }
    public void setShip(String s){
            ship = s;
    }
    public String getState(){
            return state;
    }
    public void setState(String s){
            state = s;
    }

}
