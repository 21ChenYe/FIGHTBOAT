#include <iostream>
#include <string>
#include <vector>

using namespace std;

class Map{
	private:
		int width = 10;
		int length = 10;
		vector<vector<int> > Table = vector<vector<int>>(10,vector<int>(10));
		string name;
	public:
		Map();
		Map(string n);
		int Populate(vector < vector<int> > coords);
		vector<vector<int>> Attack(vector < vector<int> > attacks);
		int returnState(int x, int y);
		string getName();
		void setName(string n);
		void display();
		void reset();
		int getWidth();
		int getLength();
		void setWidth(int w);
		void setLength(int l);
		int sinkShip(vector<vector<int>> shipVec);


};