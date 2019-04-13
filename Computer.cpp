#include <iostream>
#include <vector>
#include "map.h"
#include "Computer.h"

using namespace std;

	Computer::Computer(){
		cMap = Map()
		name = "";
	}
	Computer::Computer(int x, int y, string n){
		cMap = Map();
		cMap.setWidth(x);
		cMap.setLength(y);
		cMap.reset();
		name = n;
	}