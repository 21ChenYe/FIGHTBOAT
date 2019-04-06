#include <iostream>
#include <vector>
#include "map.h"

using namespace std;
	Map::Map(){
		//No-argument constructor, sets name to empty string
		//And populates Table with all 0s
		name = "";
		for (int i = 0; i < length; i++){
			for (int j = 0; j <width; j++){
				Table[i][j] = 0;
			}
		}
	}
	Map::Map(string n){
		//Constructor taking argument of type string. Sets name to string
		//And populates table with 0s;
		name = n;
		for (int i = 0; i < length; i++){
			for (int j = 0; j <width; j++){
				Table[i][j] = 0;
			}
		}
	}
	int Map::Populate(vector<vector<int>> coords){
		//Used to place ships on map. Takes vector of int vectors in the form
		//of [[1,2],[3,4]] which is a list of (x,y) coordinates.
		//If a coordinate is passed to function and that point on the map is not empty,
		// it returns 1. Else, returns 0.
			for (int i = 0; i < coords.size(); i++){
					int x = coords[i][0];
					int y = coords[i][1];
					if (returnState(x,y) != 0){
						return 1;
					}
					else {
						Table[x][y] = 1;
					}
				}
			return 0;
	}
	vector<vector<int>> Map::Attack(vector <vector<int> > attacks){
		//Function used to attack. Arguments: vector of int vectors, same format as Populate
		//Returns a vector of succesful hits
		//Returns an empty vector if there are no hits
		vector<vector<int>> Hits;
		int hCounter = 0;
		for (int i = 0; i < attacks.size(); i ++){
				int x = attacks[i][0];
				int y = attacks[i][1];
				if(returnState(x,y) == 1){
					Table[x][y] = 3;
					Hits.push_back({x,y});
					hCounter++;
				}
				else if (returnState(x,y) == 0 ) {
					Table[x][y] = 2;
				}
		}
		return Hits;
	}

	int Map::returnState(int x, int y){
		//Returns the state of one coordinate on map
		//0 = Empty tile
		//1 = Ship on tile
		//2 = Missed shot
		//3 = A hit tile on ship
		//4 = Sunk ship
		return Table[x][y];
	}
	string Map::getName(){
		//Returns name of map
		return name;
	}
	void Map::setName(string n){
		//Change name of map
		name = n;
	}
	void Map::display(){
		//Displays map, will probably remove this feature later
		for (int i = 0; i < length; i++){
			for(int j = 0; j <width; j++){
				cout << returnState(i,j) << ", ";
			}
			cout <<endl;
		}
	}
	void Map::reset(){
		//Resets map to all 0s
		for (int i = 0; i < length; i++){
			for (int j = 0; j <width; j++){
				Table[i][j] = 0;
			}
		}
	}
	int Map::getWidth(){
		return width;
	}
	int Map::getLength(){
		return length;
	}
	void Map::setWidth(int w){
		width = w;
	}
	void Map::setLength(int l){
		length = l;
	}
	int Map::sinkShip(vector<vector<int>> shipVec){
		//Method to convert a ship whose tiles have all been hit to the sunk state
		//If you try converting a tile to sunk state and it is currently not in the hit
		//state then it returns -1, else returns 0;
		for(int i = 0; i< shipVec.size(); i++){
			int x = shipVec[i][0];
			int y = shipVec[i][1];
			if (returnState(x,y) == 3){
				Table[x][y] = 4;
			}
			else {
				return -1;
			}
		}
		return 0;
	}
	vector<vector<int>> Map::getZeros() {
		//Returns a vector of vector ints of the coordinates of all the zeros on the map
		//Returns an empty vector if there are no zeros
			vector<vector<int>> Zeros;
			for (int i =0; i < length; i++){
				for(int j =0; j < width; j++){
					if (returnState(i,j) == 0){
						Zeros.push_back({i,j});
					}
				}
			}
			return Zeros;
	}
