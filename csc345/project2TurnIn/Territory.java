class Territory{
	/*Author: Adam Cunningham
	 * Territory.java
	 * CSC 345 spring 2019
	 * 
	 * most documentation provided by Dr.McCann.
	 * 
	 * The Territory Class: Each of the squares on the game board is represented within the game by a Territory
	 *object. A Territory needs to know the map with which it is associated (type: Map), the quantity of dice
	 *currently assigned to it (int), its identification number (int; see below), and a reference to the player who
	 *owns it (Player)
	 */
	
	public Map map;
	public Player owner;
	public int dice;
	public int idNum;
	
	public Territory(Map map) {
		/*Sets the map reference to the argument, the owner reference to null, and the integers to -1*/
		this.map = map;
		this.owner = null;
		this.dice = -1;
		this.idNum = -1;
		
	}
	public Territory(Map map, Player owner, int dice, int idNum) {
		/*Sets the state variables to the values of the given arguments.*/
		this.map = map;
		this.owner = owner;
		this.dice = dice;
		this.idNum = idNum;
		
	}
	int getRow() {
		//returns the idNum / column of first row in map
		//which is the row of territory (starting at 0, top to bottom)
		return idNum / getMapCols();
	}
	int getCol() {
		//returns the idNum % number of rows in map
		//which is the col of territory (starting at 0, left to right)
		return idNum % getMapCols();
	}
	int getDice() {
		//get dice
		return dice;
	}
	int getIdNum(){
		//get idNum
		return idNum;
		
	}Map getMap(){
		//get map
		return map;
	}
	int getMapCols() {
		return map.getMap()[0].length;
	}
	int getMapRows() {
		return map.getMap().length;
	}
	Player getOwner() {
		//get owner
		return owner;
	}
	
	void setDice (int dice){ 
		//set dice
		this.dice = dice;
	}	
	void setIdNum(int idNum) {
		//set idNum
		this.idNum = idNum;
	}
	void setOwner(Player owner) {
		//set owner
		this.owner = owner;
	}
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Territory other = (Territory) obj;
		if (idNum != other.idNum)
			return false;
		return true;
	}


}