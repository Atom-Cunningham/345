/*Author: Adam Cunningham
 * BonesBattle project2
 * csc 345
 * 
 * the computer strategy class makes decisions for npcs
 * it prioritizes attacking spaces which will connect it's
 * territories together
 * secondly, it prioritizes attacking spaces which are
 * more difficult to attack, because they are connected to 
 * less territories
 * third, it attacks whatever happens to be smaller
 * 
 */

import java.util.ArrayList;

class ComputerStrategy implements Strategy{
	Player player;
	Map map;
	Territory attacker;
	Territory defender;
	//TODO attack the thing with the least neighbors first
	
	public void setPlayer(Player whom) {
		//set the player 
		player = whom;		
	}

	
	public boolean willAttack(Map board) {
		// TODO Auto-generated method stub
		map = board;
		ArrayList<Territory> properties = map.getPropertyOf(player);
		for (Territory cell : properties) {
			ArrayList<Territory> enemies = map.getEnemyNeighbors(cell);
			for (Territory enemy : enemies) {
				if (enemy.getDice() < cell.getDice()) {
					//fill in squares first
					if (fillConnect(cell, enemy)) {
						return true;
					}if (controlChokePoints(cell, enemy)) {
						return true;
					}
					attacker = cell;
					defender = enemy;
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean fillConnect(Territory att, Territory def) {
		//fill Connect takes att, def as Territories
		//recognizes at a short range opportunities to consolidate an army
		//if def neighbors (besides att) are friendly, 
		//then set attacker and defender
		int friendlies = 0;
		for (Territory defNeighbor : map.getEnemyNeighbors(def)) {
			//if the enemy of my enemy is my friend
			//flank my enemy and move closer to my allies
			if (   defNeighbor.getOwner() != null
				&& defNeighbor.getOwner().getId() == att.getOwner().getId()) {
				friendlies +=1;
			}
			if (friendlies >= 2) {
				attacker = att;
				defender = def;
				return true;	
			}
			
		}
	return false;
	}
	
	public boolean controlChokePoints(Territory att, Territory def) {
		//controlChokePoints takes two Territories att and def
		//the def neighbors are counted, and if the position is
		//difficult to attack (it has 2 or less neighbors)
		//then set the attacker and defender
		int neighbors = 0;
		for (Territory defNeighbor : map.getEnemyNeighbors(def)) {
			//when outnumbered, seek a stronghold
			neighbors +=1;
			
			if (neighbors <= 2) {
				attacker = att;
				defender = def;
				return true;	
			}
			
		}
	return false;
	}
	
	
	

	
	public Territory getAttacker() {
		//returns the attacker
		return attacker;
	}

	
	public Territory getDefender() {
		//returns the defender
		return defender;
	}
	
}