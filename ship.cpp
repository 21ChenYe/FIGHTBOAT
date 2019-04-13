#include <iostream>
#include <string>
#include "ship.h"
using namespace std;

ship::ship(){
  hp = 3;
  name = "ship";
  coords = new int[3];
  orient = 's'; //w = west, s = south, n = north, e = east
}
ship::ship(int newHp, string newName, int startX, int startY, char newOrient){ //startX and startY denote the tip of the ship.
  hp = newHp;
  name = newName;
  orient = newOrient; //n = north, w = west... so on. If I set orient to n, then you only need one X value and then a couple of Y values
  coords = new int[hp]; //we're assuming the graphi is from 0 to 9. the lowest left square being 0 0 and the top right being 9 9
  if(orient == 'n' && (9-startY) >= hp){ //[x][y]
    start = startX; //basically, a north facing ship will have 1 X-value and (insert hp here) amount of Y-values
    for(int i = 0; i < newHp; i++){
      coords[i] = startY + i; //the ship is built from the tip whose coordinate is at (startX, startY)
    } //if the ship is north, then you only need to increment Y values to get the remaining coordinates of the ship
  }//which is why I did startY + i in a loop.
  else if(orient == 'w' && startX >= hp){ //[y][x]
    start = startY; //startX>=hp is required to make sure this doesnt go out of bounds.
    for(int i = 0; i < newHp; i++){
      coords[i] = startX - i;
    }
  }
  else if(orient == 's' && startY >= hp){ //[x][y]
    start = startX;
    for(int i = 0; i < newHp; i++){
      coords[i] = startY - i;
    }
  }
  else if(orient == 'e' && (9-startX) >= hp){ //[y][x]
    start = startY;
    for(int i = 0; i < newHp; i++){
      coords[i] = startX + i;
    }
  }
}

bool ship::isSunk(){ //pretty self explanatory
  if (hp == 0){
    return true;
  }
    else{
  return false;
    }
}

bool ship::hit(int x, int y){ //this one is not however
  if (orient == 'n' && start == x){ //check if the orientation is north. North and south orientations only have one X value
      for(int i = 0; i < hp; i++){ //this loop iterates through the coords array, which only stores the OPPOSITE of the start integer
        if(coords[i] == y){ //the start integer stores an X-value for North and South orients, this means this array is all y values
          return true;
        }
      }

  }
  else if (orient == 'w' && start == y){
      for(int i = 0; i < hp; i++){
        if(coords[i] == x){
          return true;
        }
      }
  }
  else if (orient == 's' && start == x){
    for(int i = 0; i < hp; i++){
      if(coords[i] == y){
        return true;
      }
    }
  }
  else if (orient == 'e' && start == y){
    for(int i = 0; i < hp; i++){
      if(coords[i] == x){
        return true;
      }
    }
  }
  return false;
}
