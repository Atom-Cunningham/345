import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

class Map{
	/*Author: Adam Cunningham
	 * CSC 345 Spring 2019
	 * Map.java
	 * 
	 * most documentation provided by Dr.McCann.
	 * 
	 * The Map Class: This provides the representation of the game board 
	 * a rectangle of territories with
	 * several holes (invisible, unplayable territories) 
	 * to make the board more interesting.
	 * A Map state consists of the map (Territory[][]),
	 * a reference to a graph data structure that knows
	 * neighboring territories, and
	 * a list of references to the games players
	 * (ArrayList<Player>).
	 */


	//size of the board
	int ROWS;
	int COLUMNS;
	int VICTIMS;	// Number of unused (invisible) territories.
	int NUMTERRITORIES;	// Synonym for ROWS times COLUMNS.
	int OCCUPIED;	// Synonym for NUMTERRITORIES minus VICTIMS.
	int MAXDICE;	//The largest quantity of dice a territory may have
	Graph graph;
	Territory[][] map;
	ArrayList<Player> players;

	

	public Map(ArrayList<Player> players, int rows, int columns,
										int victims, int maxDice){
		/*The arguments are used to set the corresponding constants and
		 *  instance variables. The constructor also declares the map array
		 *  (that is, the game board), and creates territories for the board
		 *  (w/ correct ID#s). It also creates the territory neighbor graph 
		 *  by calling the constructGraph() method and concludes by
		 *  calling the local partitionTerritories() and distributeDice() 
		 *  methods to initialize the game board.*/
		//initialize variables/constants
		ROWS = rows;
		COLUMNS = columns;
		VICTIMS = victims;
		NUMTERRITORIES = rows*columns;
		OCCUPIED = (rows*columns) - victims;
		MAXDICE = maxDice;
		this.players = players;
		
		
		//populateMap makes the game board map with unowned territories
		populateMap();
		//partitionTerritories decides who gets which territories
		//and assigns them creating new territories and replacing the old
		partitionTerritories();
		//assign ids to unowned territories
		//and do sanity checks
		assignIds();
		//construct the graph object
		constructGraph(rows, columns, victims);
		//make sure that there are no floating territories
		distributeDice();
		//fixIslands ensures that the graph is connected
		fixIslands();
		
		
	}
	public void populateMap() {
		/*populateMap() fills the Territory[][] with
		 * new territories that have no owner and -1 dice and id
		 */ 
		map = new Territory[ROWS][COLUMNS];
		for (int m = 0; m < ROWS; m++) {
			for (int n = 0; n < COLUMNS; n++) {
				map[m][n] = new Territory(this);
			}
		}
	}
	public int getTerritoryId (int row, int column) {
		//paramenters: row, column
		//Given the row and column of a territory, 
		//compute and return the territorys ID#.
		//The Territory class supplies getRow() and getCol() methods
		return COLUMNS*row + column;
	}
	
	public void assignIds() {
		/*this function sanity checks the placement of Territories with
		 * randomly generated IDs from partitionTerritories()
		 * by assigning ids iteratively AFTER
		 * all owned territories have been placed
		 * 
		 * if an owned territory does not have the expected ID 
		 * then it has been misplaced (throws AssertionError)
		 * if the territory is unowned and unidentified,
		 *  assign the proper id
		 */
		
		for (int m = 0; m < ROWS; m++) {
			for (int n = 0; n < COLUMNS; n++) {
				Territory curr = map[m][n];
				int id = getTerritoryId(m, n);
				//if no id, check that no owner exists
				if (curr.getIdNum() == -1) {
					assert curr.getOwner() == null;
					curr.setIdNum(id);
				}else { //make sure id is correct for location in map
					assert curr.getIdNum() == id;
				}
			}
		}
	}
	public int countTerritories (Player player) {
		//Determine and return the quantity of territories
		//owned by the given player.
		int numTerritories = 0;
		for (int m = 0; m < map.length; m++) {
			for (int n = 0; n < map[m].length; n++) {
				//compares cell owner with player
				if (playerIsEqual(map[m][n].getOwner(), player)) {
					numTerritories += 1;
				}
			}
		}return numTerritories;
		
	}
	
	public int countDice (Player player) {
		//Determine and return the total number of dice currently
		//assigned to this players territories.
		int diceCount = 0;
		for (int m = 0; m < map.length; m++) {
			for (int n = 0; n < map[m].length; n++) {
				if (playerIsEqual(map[m][n].getOwner(), player)) {
					diceCount +=  map[m][n].getDice();
				}
			}
		}return diceCount;

	}
	public ArrayList<Territory> getPropertyOf(Player player){
		//Construct and return a reference to an ArrayList of 
		//Territory object references. The territories referenced
		//by the list are those currently owned by the given player.
		ArrayList<Territory> properties = new ArrayList<>();
		for (int m = 0; m < map.length; m++) {
			for (int n = 0; n < map[m].length; n++) {
				//compares cell owner with player
				if (playerIsEqual( map[m][n].getOwner(), player)) {
					properties.add(map[m][n]);
				}
			}
		}
		System.out.println(player.getId());
		for (Territory prop : properties) {
			System.out.print("  " + prop.getIdNum());
		}System.out.println("");
		return properties;
	}
	
	public ArrayList<Integer> getOwnedIds(){
		//This function uses the global list of Players players
		//it uses the getPropertyOf function to collect territory Ids
		//a list of Ids of all territories owned by players is returned
		
		ArrayList<Integer> ownedIds = new ArrayList<>();
		ArrayList<Territory> ownedByPlayer;
		for (Player player : players) {
			ownedByPlayer = getPropertyOf(player);
			for (Territory territory : ownedByPlayer) {
				//add the id of each property
				//for each property list of each player
				ownedIds.add(territory.getIdNum());
			}
		}
		return ownedIds;
	}
	
	
	public ArrayList<Integer> getPossibleNeighbors(Territory cell) {
		//get Possible Neighbors takes a territory
		//it uses the id of the territory to calculate the ids
		//of the surrounding territories in the map
		//it returns a list of the possible neighbors
		//some possible neighbors may include:
			//territories with ids that are not in the graph
			//territories with ids of -1 (referring to a null owners)
		//getNeighbors does the final checking
		ArrayList<Integer> possible = new ArrayList<>();

		if (cell.getOwner() == null) {
			return possible;
		}
		
		int id = cell.getIdNum();
		
		possible.add(id -COLUMNS);//above node
		possible.add(id +COLUMNS);//below node
		//if cell is on the rim (the far left or right of the map)
		//wraparound would be possible without the following checks
		if (id % COLUMNS != 0) { //then node is not on the left rim
			possible.add(id -1);   //add left node
		}if (id % COLUMNS != (COLUMNS -1)) {//node not on the right rim
			possible.add(id +1);//add right node
		}
		return possible;
	}
	
	public ArrayList<Territory> getNeighbors(Territory cell){
		/*Each territory has at least one adjacent (edge-sharing)
		 * neighboring territory, but no more than four. This method
		 * returns a reference to an ArrayList of references to
		 * the given territorys neighbors. The Graph object
		 * offers a helpful method: isInGraph(). 
		 * isInGraph() takes a territory ID# and returns true if the
		 * territory is participating in the game 
		 * (remember, some territories are invisible and
		 * thus cant be neighbors).
		 */
		
		ArrayList<Territory> neighbors = new ArrayList<>();
		Territory territory;

		ArrayList<Integer> possible = getPossibleNeighbors(cell);
		for (int id : possible) {
			if (graph.isInGraph(id)) {
			territory = getTerritory(id / COLUMNS,
					 				 id % COLUMNS);
				if (territory.getOwner() != null) {
					neighbors.add(territory);
				}
			}
			
		}
		
		return neighbors;
	}
	
	public ArrayList<Territory> getEnemyNeighbors(Territory cell){
		/* Similar to getNeighbors(), above, but the returned list
		 *  contains only references to neighboring territories 
		 *  controlled by another player.
		 */
		ArrayList<Territory> enemyNeighbors = new ArrayList<>();
		for (Territory neighbor : getNeighbors(cell)) {
			 //if not the same owner
			if (!playerIsEqual(cell.getOwner(), neighbor.getOwner())
						   && (neighbor.getOwner() != null)) {
				enemyNeighbors.add(neighbor);
			}
		}return enemyNeighbors;
	}

	private void partitionTerritories() {
		/* This method is called by the constructor after the array of
		 * un-owned territories has been built. This method assigns to
		 * each player the same (or nearly the same) quantity of territories.
		 * Each players territories are to be selected randomly. That is,
		 * do not assign players to territories based on a pattern.
		 * 
		 * this generates ids between 0 (inclusive) and number of
		 * territories (exclusive)
		 * and assigns that territory to players in order*/
		assert OCCUPIED < NUMTERRITORIES;
		assert players.size() > 0;

		int nextId;
		int toBeAssigned = OCCUPIED;
		Random rng = new Random();
		HashSet<Integer> owned = new HashSet<>();
		owned.add(-1);
		
		int playerIdx = 0;
		while (toBeAssigned > 0 && (owned.size() < OCCUPIED)) {
			//assigns the number of occupied territories
			//players take turns
			Player player = players.get(playerIdx);
			
			nextId = -1;
			while (owned.contains(nextId)) {
				nextId = rng.nextInt(NUMTERRITORIES);
			}
			//create a new territory
			//place it in the corresponding map location
			assignTerritory(player, nextId);
			owned.add(nextId); //prevent overlap by marking id
			toBeAssigned--;
			System.out.println("To be assigned " + toBeAssigned);
			
			playerIdx += 1;
			if (playerIdx == players.size()) {
				playerIdx = 0;
			}
		}
	}
	public void assignTerritory(Player player, int idNum) {
		/*creates a new territory, owned by a player, with a
		 * randomly generated idNumber
		 * 
		 * called by partition territories
		 */
		Territory newTerritory = new Territory(this, player, -1, idNum);
		System.out.println(idNum);
		int row = newTerritory.getRow();
		int col = newTerritory.getCol();
		map[row][col] = newTerritory;
	}
	
	private void distributeDice() {
		//TODO fix distribute Dice
		/* This method is called by the constructor after 
		 * partitionTerritories() has been called. Collectively, the
		 * assigned territories of each player have the same quantity
		 * of dice as the territories of every other player: three
		 *  times the players number of territories.
		 *  For example, if there were three players and 15 territories 
		 *  per player, each player would start with 45 dice. This method
		 *  randomly distributes each players dice allotment across his or her
		 *  territories. Each territory must have at least one die, but no
		 *   territory can have more than MAXDICE dice.
		 */
		ArrayList<Territory> properties;
		int diceToPlace;
		Random dicePlacer = new Random();
		int placement;
		Territory luckyTerr;

		for (Player player : players) {
			System.out.println("Curr player to assign dice to" + player.getId());
			//for each player, get their property list
			//calculate how many dice they get
			properties = getPropertyOf(player);
			diceToPlace = properties.size() * 3;
			//put one die in each territory
			for (Territory property : properties) {
				//nothing should have dice yet
				if (property.getDice() == -1) {
					property.setDice(1);
					diceToPlace -= 1;
				}
			}
				//assign remaining dice alotted to current player
				//increment dice in the lucky territory
				while (diceToPlace > 0) {
					placement = dicePlacer.nextInt(properties.size());
					luckyTerr = properties.get(placement);
					if (luckyTerr.getDice() <= MAXDICE) {
						luckyTerr.setDice(luckyTerr.getDice() + 1);
						diceToPlace -= 1;
					}
				}
			
			System.out.println("placed all dice for player" + player.getId());
		}
	}

	public int countConnected (Player player) {
		System.out.println("counting connected");
		/* Returns a count of the number of territories in the
		 * largest connected cluster of territories owned by the given player.
		 */
		//get player owned properties
		ArrayList<Territory> properties = getPropertyOf(player);
		if (properties.size() == 0) {
			return 0;
		}
		ArrayList<Territory> reached;
		int maxCount;
		//dfs of a the graph, testing if each next node is in properties
		//if it is put it in reached
		maxCount = 0;
		while (properties.size() > 0) {
			reached = new ArrayList<>();
			countConnected(properties, reached, properties.get(0));
			maxCount = Integer.max(maxCount, reached.size());//set new max
			
			//removall from arraylist
			properties.removeAll(reached);
			
			System.out.print("reached:");
			for (Territory a : reached) {
				System.out.print(a.getIdNum());
			}System.out.println("");
			System.out.print("properties:");
			for (Territory b : properties) {
				System.out.print(b.getIdNum());
			}System.out.println("");
			break;
		}
		return maxCount;
	}
	
	public void countConnected(ArrayList<Territory> properties,
							   ArrayList<Territory> reached, Territory cell){
		//countConnected is an overloaded method
		//which takes three parameters
		//a list of territories all shared by the same player (properties)
		//a list of territories which have already been visited 
		//by this dfs (reached)
		//and a cell, which is a node whos edges will be visited
		//if the adjacent nodes belong to the same player and have not
		//already been reached
		for (Territory neighbor : getNeighbors(cell)) {
			assert (graph.isEdge(cell.getIdNum(), neighbor.getIdNum()));
			//if territory not in reached
			//and it belongs to the same owner as the other properties
			if (!(territoryInList(neighbor, reached)) 
			   && territoryInList(neighbor, properties)) {
				//add to reached (mark it)
				reached.add(neighbor);
				//check its neighbors
				countConnected(properties, reached, neighbor);
			}
		}
		System.out.print("reached:");
		for (Territory a : reached) {
			System.out.print(a.getIdNum());
		}System.out.println("");
	}
	
	public boolean territoryInList(Territory cell, ArrayList<Territory> list) {
		System.out.println("recursive TerritoryInList");
		//for testing
		//this is an iterative search to determine if a territory is contained
		//within a list of territories
		//it takes a territory, cell, and a list of territories, list
		//and returns true or false
		for (Territory territory : list) {
			if (territory.getIdNum() == cell.getIdNum()) {
				return true;
			}
		}return false;
	}

	public Graph constructGraph(int rows, int cols, int victims) {
		/* Builds and returns a reference to a graph representing 
		 * all of the active territories in the game. An acceptable graph
		 *  has the appropriate number of active territories (Map.OCCUPIED),
		 *  scatters the inactive territories among the active
		 *  territories in an unpredictable (pseudo-random) fashion, 
		 *  and ensures that all of the active vertices are connected.
		 *  
		 *  
		 *  use graph functions to get the graph identical to the map
		 *  */
		this.graph = new Graph(NUMTERRITORIES);
		for (int m = 0; m < ROWS; m++) {
			for (int n = 0; n < COLUMNS; n++) {
				Territory curr = map[m][n];
				ArrayList<Territory> currNeighbors = getNeighbors(curr);
				for (Territory neighbor : currNeighbors) {
					graph.addEdge(curr.getIdNum(), neighbor.getIdNum());
					graph.addEdge(neighbor.getIdNum(), curr.getIdNum());
				}
			}
		}return graph;
	}
	public void fixIslands(){
		//fixIslands is step in the constructor method
		//if the graph has isolated nodes,
		//then some steps in main must be repeated
		//when this method is complete,
		//the territory map (and therefore also the graph)
		//will be connected
		graph.setMap(this);
		while (!graph.connected()) {
			populateMap();
			partitionTerritories();
			assignIds();
			constructGraph(ROWS, COLUMNS, VICTIMS);
			distributeDice();
			graph.setMap(this);

		}
	}

	public Territory[][] getMap(){
		return map;
	}
	Graph getGraph(){
		return null;
	}
	Territory getTerritory(int row, int column){
		/* getTerritory simply returns the Territory reference
		 * stored in the game board at location (row,column)
		 */
		return map[row][column];
	}
	Territory getTerritory(Territory cell){
		/* getTerritory simply returns the Territory reference
		 * stored in the game board at location (row,column)
		 */
		return map[cell.getRow()][cell.getCol()];
	}
	public boolean playerIsEqual(Player player, Player other) {
		//returns false if either player is null
		//else gets the players Id and uses those to compare equality
		if ((player == null) || (other == null)) {
			return false;
		}
		return (player.getId() == other.getId());
		
	}
}