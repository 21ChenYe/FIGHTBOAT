#include <iostream>
#include <vector>
#include "map.h"
using namespace std;

int main() {
		vector<vector<int>> popVect = {{1,1},{2,2},{3,3}};
		vector<vector<int>> attackVect = {{1,1}};
		cout << "check" << endl;
		Map m1 = Map();
		m1.setName("Player 1");
		cout << m1.getName() << endl;
		Map m2 = Map("Computer");
		m1.display();
		cout << endl;
		m1.Populate(popVect);
		m1.display();
		cout << endl;
		vector<vector<int>> attacks = m1.Attack(attackVect);
		m1.display();
		m1.reset();
		cout << endl;
		m1.display();
}