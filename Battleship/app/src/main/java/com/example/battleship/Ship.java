package com.example.battleship;

import android.content.Context;
import android.util.AttributeSet;



public class Ship extends android.support.v7.widget.AppCompatImageButton {
    private String direction; //n,s,e,w
    private String type;
    private int length;
    private int health;
    private int[][] positions; //holds positions of each part
    private boolean Sunk;
    private boolean placed;

    public Ship(Context context, AttributeSet attrs) {
        super(context,attrs);
        Sunk = false;
        placed = false;
        direction = "e"; }

    public void setPositions(int x, int y){
        placed = true;
        switch(direction){
            case "n":
                for(int i = 0; i<length; i++){
                    positions[i][0] = x;
                    positions[i][1] = y+i;
                }
                break;
            case "e":
                for(int i = 0; i<length; i++){
                    positions[i][0] = x+i;
                    positions[i][1] = y;
                }
                break;
            default:
                for(int i = 0; i<length; i++){
                    positions[i][0] = -1;
                    positions[i][1] = -1;
                }
        }

    }
    public void setType (String s){ type =s;}
    public void setDirection(String c){ direction = c;}
    public void setLength(int l) {
        length = l;
        health = l;
        positions = new int[length][2];
    }
    public boolean isSunk () { return Sunk;}
    public boolean isPlaced() {return placed;}
    public void hit(int x, int y){
        health--;
        if(health == 0){
            Sunk = true;
        }
    }
    public String getType() { return type;}
    public int getLength(){ return length;}
    public String getDirection() {return direction;}






}
