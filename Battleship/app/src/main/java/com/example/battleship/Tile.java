package com.example.battleship;

import android.content.Context;
import android.util.AttributeSet;


/*
    Tile object that makes up the map, holds data on where it is in the map, which ship it its
    holding, which part of the ship it is, and which state it is in
 */
public class Tile extends android.support.v7.widget.AppCompatButton {
        int posX;
        int posY;
        int shipPart;
        String ship;
        int state;
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
    public int getState(){
            return state;
    }
    public void setState(int s){
            state = s;
    }

}
