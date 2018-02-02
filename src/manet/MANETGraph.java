package manet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import manet.positioning.Position;
import peersim.graph.Graph;

public class MANETGraph implements Graph {

	
	private final Map<Long, Position> positions = new HashMap<>();
	private final Map<Long, List<Integer>> neighbors = new HashMap<>();
	private final int scope;
	public MANETGraph(Map<Long, Position> positions,int scope) {
		this.positions.putAll(positions);
		this.scope=scope;
	}
	
	
	@Override
	public boolean isEdge(int i, int j) {
		return positions.get(new Long(i)).distance(positions.get(new Long(j))) <= scope;
	}

	@Override
	public int size() {
		return positions.size();
	}
	
	

	

	@Override
	public boolean directed() {
		return false;
	}

		
	@Override
	public Collection<Integer> getNeighbours(int i) {
		Long li= new Long(i);
		if(!neighbors.containsKey(li)) {
			List<Integer> neighb = new ArrayList<>();
			for(int j =0 ; j < size() ; j++) {
				if(i!=j && isEdge(i, j)) {
					neighb.add(j);
				}
			}
			neighbors.put(li, neighb);
		}
		return neighbors.get(li);
	}
	
	@Override
	public int degree(int i) {
		return getNeighbours(i).size();
	}
	
	
	
	@Override
	public Object getNode(int i) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getEdge(int i, int j) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean setEdge(int i, int j) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean clearEdge(int i, int j) {
		throw new UnsupportedOperationException();
	}

	
	


}
