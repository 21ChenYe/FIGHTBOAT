#include <iostream>
#include <vector>
#include "map.h"
#include "ship.h"
using namespace std;

int main() {

		vector<vector<int>> popVect;
		for (int x = 0; x < 10; x++){
			for(int y = 0; y < 10; y += 2){
				popVect.push_back({x,y});
			}
		}
		vector<vector<int>> attackVect = {{1,1}, {2,2}};
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
		int status = m1.sinkShip(attacks);
		cout <<endl;
		cout << status << endl;
		m1.display();
		vector<vector<int>> zeros = m1.getZeros();
		cout << endl;
		for (int h = 0; h < zeros.size(); h++){
			cout << "(" << zeros[h][0] << "," << zeros[h][1] << "), ";
		}
		m1.reset();
		cout << endl;
		cout << endl;
		m1.display();
		cout << endl;
		cout << m1.getWidth() <<endl;
		m1.setWidth(15);
		m1.reset();
		m1.display();


		ship s1 = ship(4,"cruiser", 2,3,'w');




}