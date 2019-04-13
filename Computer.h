#include <iostream>
#include "map.cpp"
#include <vector>

using namespace std;

class Computer{
	private:
		Map cMap;
		vector< vector<int> > hits;
		string name;
	public:
		Computer();
		Computer(int x, int y, string name);
		Fill();
		vector<vector<int>> Attack(vector<vector<int>> zeros);
		vector<vector<int>> Defend(vector<vector<int>> shots);
	};
