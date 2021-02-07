/* Author: Adam Cunningham
 * Graph.java
 * CSC 345 2019 Bones battle assignment
 * 
 * majority of documentation provided by Dr.McCann
 * 
 * 
 * The Map class relies on a Graph object to represent
 *  the neighbor relationships between
 * territories. Each territory is represented by a 
 * corresponding vertex within the graph, and neighboring
 * territories are connected by edges. A Graph object needs
 * a representation for the graph, and a way to
 * know which of the vertices are considered to be 
 * inactive by the game. The graph vertices need to be
 * numbered in the same way that the game numbers the territories: 
 * The territory assigned to the upper left corner (row 0, column 0)
 * of the board has ID# 0. The territory to its immediate right has 
 * ID# 1, and the territory just below ID# 0 has ID# 8. In other words, the
 * territories, and thus the vertices, are numbered in row-major order. 
 * Knowing that, the row and column
 * indices can be easily computed from the ID#, and vice-versa.
 */


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;




class Graph{
	HashMap<Integer, HashSet<Integer>> graph;
	Map map;
	
	Graph (int numVertices){
	/*The number of vertices in the graph is supplied.
	 *  The constructor will create a suitable graph representation
	 * that initially includes no edges. Because our application
	 * requires that some of the vertices be inactive within the game,
	 * the graph needs to be able to distinguish the inactive vertices
	 * from the active vertices.
	 * 
	 * The graph representation is an array of linked lists containing integers
	 * */
		graph = new HashMap<>();
		for (int i = 0; i < numVertices; i++) {
			graph.put(i, new HashSet<>());
		}
		
	}
	public List<Integer> getUnusedVertices (){
		/* Returns a list of the ID#s of the inactive vertices
		 * of the graph. If there are no inactive vertices,
		 * a reference to an empty list object is returned.
		 * 
		 * an inactive vertex has no edges
		 */
		List<Integer> unusedVertices = new ArrayList<Integer>();
		for (int vertex : graph.keySet()) {
			if (graph.get(vertex).size() == 0) {
				unusedVertices.add(vertex);
			}
		}return unusedVertices;
	}
	public boolean isEdge (int source, int destination) {
		/* Returns true if the graph possesses an edge
		 * directly connecting the given source and
		 * destination vertices. That is, true is
		 * returned if these vertices are adjacent,
		 * and therefore are included in the HashMap from sources
		 */
		if (graph.containsKey(source) 
				&& (graph.get(source).contains(destination)
				&&  graph.get(destination).contains(source))){
			return true;
		}return false;
	}

	public void addEdge (int source, int destination) {
		/* Ensures that the graph contains an edge
		* connecting the given source and destination vertices.
		*/
		if (graph.containsKey(source)) {
			graph.get(source).add(destination);
		}

	}
	
	public void removeEdge (int source, int destination) {
		/* Parameters: source is used as a key
		 * destination, the element of the value to be removed
		 * Ensures that the graph does not contain
		 * an edge connecting the given source and destination vertices.
		 */
		if (graph.containsKey(source)) {
			graph.get(source).remove(destination);
		}
	}
	
	public boolean isInGraph (int vertex) {
		/*Returns true if the given vertex is active within the graph.
		 * it is active if a territory (including invisible) exists in the map
		 * with the corresponding int Id.
		 * 
		 * if the vertex does not exist return false
		 */
		return graph.containsKey(vertex);
	}
	
	public void removeVertex (int vertex) {
		/* Called to mark a vertex as inactive. Inactive vertices
		* have no neighbors, and thus have a degree of 0.
		* Therefore it replaces the old Hashset(whatever it was)
		* with a new empty one
		*/
		graph.put(vertex, new HashSet<>());
	}
	public List<Integer> getAdjacent (int vertex){
		/* Returns a list of vertex ID#s. A vertex is in the
		* list if it is active and adjacent to the given vertex.
		*/
		List<Integer> adjacent = new ArrayList<Integer>();
		if (graph.containsKey(vertex)) {
			for (int adjacentVertex : graph.get(vertex)) {
				adjacent.add(adjacentVertex);
			}
		}return adjacent;

	}
	public int degree (int vertex) {
		/*Returns the degree of the given vertex.
		 * which is the size of the hashset mapped from vertex
		 * returns -1 if vertex does not exist
		 */
		if (graph.containsKey(vertex)) {
			return (graph.get(vertex).size());
		}return -1;
	}
	
	public boolean connected() {
		/* Returns true if the graph is connected;
		 * that is, if every active vertex
		 * is reachable from every other active vertex.
		 */
		//check for 0 node edge case
		if (graph.size() == 0) {
			return true;
		}
		// create a list of active vertices
		ArrayList<Integer> active = map.getOwnedIds();
		ArrayList<Integer> reached = new ArrayList<>();
		
		//find an active node to begin the dfs
		int startNode = 0;
		while (graph.get(startNode).size() == 0) {
			System.out.println("neighbors of nodes starting at 0: " + graph.get(startNode));
			startNode += 1;
		}
		
		//do a dfs of active vertices to create a reached list
		connectedSearch(reached, startNode);
		
			System.out.println(graph);
			System.out.println("reached:" + reached);
			System.out.println("active:" + active);

        //if: if statement is false, then: the graph is disconnected.
		//or a vital function in Graph is failing
		if (active.size() == reached.size()) {
			//assert that all vertices in reached are in active
			active.removeAll(reached);
			if (active.size() == 0) {
				return true;
			}
		}
		return false;

	}
	
	public void connectedSearch (ArrayList<Integer> reached, int currNode){
		//recursive dfs of graph
		//takes a list of marked nodes, and an id of the current node
		//reached is accessed in the scope of connected, and compared to a
		//list of all known active nodes
		if (!(reached.contains(currNode))) {
			//if currentNode not marked
			reached.add(currNode);
			System.out.println("currNode" + currNode);
			System.out.println("reached" + reached);
			//mark and recurse on currentNode
			for (int neighbor : graph.get(currNode)) {
				assert isEdge(neighbor, currNode);
				connectedSearch(reached, neighbor);
		}
		
		}
	}
	public void setMap(Map map) {
		this.map = map;
	}

}