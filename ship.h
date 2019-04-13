#include <iostream>
#include <string>
#include <vector>

using namespace std;

class ship{
	private:
		int hp;
		int start;
		int* coords;
		string name;
		char orient;
	public:
		ship();
		ship(int newHp, string newName, int startX, int startY, char newOrient);
		bool isSunk();
		bool hit(int x, int y);
		int getHealth();

};
