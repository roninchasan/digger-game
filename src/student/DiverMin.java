package student;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import game.FindState;
import game.FleeState;
import game.Node;
import game.NodeStatus;
import game.SewerDiver;

public class DiverMin extends SewerDiver {

	/** Get to the ring in as few steps as possible. Once you get there, <br>
	 * you must return from this function in order to pick<br>
	 * it up. If you continue to move after finding the ring rather <br>
	 * than returning, it will not count.<br>
	 * If you return from this function while not standing on top of the ring, <br>
	 * it will count as a failure.
	 *
	 * There is no limit to how many steps you can take, but you will receive<br>
	 * a score bonus multiplier for finding the ring in fewer steps.
	 *
	 * At every step, you know only your current tile's ID and the ID of all<br>
	 * open neighbor tiles, as well as the distance to the ring at each of <br>
	 * these tiles (ignoring walls and obstacles).
	 *
	 * In order to get information about the current state, use functions<br>
	 * currentLocation(), neighbors(), and distanceToRing() in state.<br>
	 * You know you are standing on the ring when distanceToRing() is 0.
	 *
	 * Use function moveTo(long id) in state to move to a neighboring<br>
	 * tile by its ID. Doing this will change state to reflect your new position.
	 *
	 * A suggested first implementation that will always find the ring, but <br>
	 * likely won't receive a large bonus multiplier, is a depth-first walk. <br>
	 * Some modification is necessary to make the search better, in general. */
	@Override
	public void find(FindState state) {
		// TODO : Find the ring and return.
		// DO NOT WRITE ALL THE CODE HERE. DO NOT MAKE THIS METHOD RECURSIVE.
		// Instead, write your method elsewhere, with a good specification,
		// and call it from this one.
		findRing(state, new HashSet<Long>());
	}

	/** Use DFS walk algorithm to search map and find ring. <br>
	 * The method has a parameter state of class FindState and a parameter <br>
	 * Visited of type Set<Long>. First, the walker is standing on a node given by FindState state, we
	 * call it c. Second, every node reachable along paths of unvisited nodes from c's neighbors are to
	 * be visited. Third, we must state where the walker will be standing when the method terminates.
	 * The walker will be standing where they started, on node c. Fourth, we have the precondition that
	 * c is unvisited. */
	public void findRing(FindState state, Set<Long> Visited) {
		if (state.distanceToRing() == 0) { return; }
		long c= state.currentLocation();
		Visited.add(c);

		for (NodeStatus n : state.neighbors()) {
			if (!Visited.contains(n.getId())) {
				state.moveTo(n.getId());
				findRing(state, Visited);
				if (state.distanceToRing() == 0) { return; }
				state.moveTo(c);
			}
		}
	}

	/** Flee the sewer system before the steps are all used, trying to <br>
	 * collect as many coins as possible along the way. Your solution must ALWAYS <br>
	 * get out before the steps are all used, and this should be prioritized above<br>
	 * collecting coins.
	 *
	 * You now have access to the entire underlying graph, which can be accessed<br>
	 * through FleeState. currentNode() and getExit() will return Node objects<br>
	 * of interest, and getNodes() will return a collection of all nodes on the graph.
	 *
	 * You have to get out of the sewer system in the number of steps given by<br>
	 * getStepsRemaining(); for each move along an edge, this number is <br>
	 * decremented by the weight of the edge taken.
	 *
	 * Use moveTo(n) to move to a node n that is adjacent to the current node.<br>
	 * When n is moved-to, coins on node n are automatically picked up.
	 *
	 * You must return from this function while standing at the exit. Failing <br>
	 * to do so before steps run out or returning from the wrong node will be<br>
	 * considered a failed run.
	 *
	 * Initially, there are enough steps to get from the starting point to the<br>
	 * exit using the shortest path, although this will not collect many coins.<br>
	 * For this reason, a good starting solution is to use the shortest path to<br>
	 * the exit. */
	@Override
	public void flee(FleeState state) {
		// TODO: Get out of the sewer system before the steps are used up.
		// DO NOT WRITE ALL THE CODE HERE. Instead, write your method elsewhere,
		// with a good specification, and call it from this one.

		fleeSewer(state);
	}

	/** Calls method findCoins to find nearest coins until there are only enough steps remaining to
	 * escape with the shortest path. Method shortest is called to find the shortest path using
	 * Djikstra's algorithm. <br>
	 * The method has a parameter state of class FindState. */
	public void fleeSewer(FleeState state) {
		List<Node> l= Paths.shortest(state.currentNode(), state.getExit());
		HashSet<Node> visited= new HashSet<Node>();
		int size= 12 * l.size();

		findCoins(state, visited, size);

		l= Paths.shortest(state.currentNode(), state.getExit());
		l.remove(0);
		for (Node n : l) {
			state.moveTo(n);
		}
	}

	/** Use altered DFS walk algorithm to search map and find nearby coins. <br>
	 * The method has a parameter state of class FindState, a parameter <br>
	 * Visited of type Set<Long>, and a parameter size of type int (describing the size of the shortest
	 * path from the current location to the exit. */
	public void findCoins(FleeState state, Set<Node> Visited, int size) {
		if (size >= state.stepsLeft()) { return; }
		Node c= state.currentNode();
		Visited.add(c);
		for (Node n : c.getNeighbors()) {
			if ((n.getTile().coins() > 0) && size < state.stepsLeft()) {
				state.moveTo(n);
				size+= c.getEdge(n).length;
				if (!Visited.contains(n) && size < state.stepsLeft()) {
					findCoins(state, Visited, size);
				}
				state.moveTo(c);
				size+= c.getEdge(n).length;

			} else if (!Visited.contains(n) && size < state.stepsLeft()) {
				state.moveTo(n);
				size+= c.getEdge(n).length;
				if (size < state.stepsLeft()) {
					findCoins(state, Visited, size);
				}
				state.moveTo(c);
				size+= c.getEdge(n).length;
			}
		}
		return;
	}
}
