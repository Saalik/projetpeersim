package manet.positioning.strategies;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import manet.MANETGraph;
import manet.communication.Emitter;
import manet.positioning.NextDestinationStrategy;
import manet.positioning.Position;
import manet.positioning.PositionProtocol;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;
import peersim.graph.Graph;
import peersim.graph.GraphAlgorithms;

public class Strategy4Next implements NextDestinationStrategy {

	private static final String PAR_POSITIONPID = "positionprotocol";
	private static final String PAR_EMITTERPID = "emitter";
	private static final String PAR_DISTANCEMIN = "distance_min";
	private static final String PAR_DISTANCEMAX = "distance_max";
	
	private final int position_pid;
	private final int emitter_pid;
	private final int distance_min;
	private final int distance_max;
	
	private Map<Integer,Set<Node>> initial_connected_component=null;//id_component -> liste d'id de node
	
	
	private Node currentMoving;
	
	public Strategy4Next(String prefix) {
		position_pid=Configuration.getPid(prefix+"."+PAR_POSITIONPID);
		emitter_pid=Configuration.getPid(prefix+"."+PAR_EMITTERPID);
		distance_min=Configuration.getInt(prefix+"."+PAR_DISTANCEMIN,0);
		distance_max=Configuration.getInt(prefix+"."+PAR_DISTANCEMAX, Integer.MAX_VALUE);
	}
	
	
	@Override
	public Position getNextDestination(Node host, int speed) {
		final int scope = ((Emitter)host.getProtocol(emitter_pid)).getScope();
		PositionProtocol pos_proto_host = ((PositionProtocol) host.getProtocol(position_pid));
		
		if(initial_connected_component == null) {
			Map<Long,Position> les_positions =  getPositions();
			Graph g = new MANETGraph(les_positions, scope);
			final GraphAlgorithms ga = new GraphAlgorithms();
			ga.weaklyConnectedClusters(g);
			initial_connected_component = new HashMap<>();
			for(int i=0; i< Network.size();i++) {
				int connected_component = ga.color[i];
				Set<Node> nodes = initial_connected_component.getOrDefault(connected_component, new HashSet<>());
				nodes.add(Network.get(i));
				initial_connected_component.put(connected_component, nodes);
			}	
		}
		
			
		if(currentMoving != null) {
			PositionProtocol pos_proto_cur_moving = (PositionProtocol) currentMoving.getProtocol(position_pid);
			if (pos_proto_cur_moving.isMoving()) {//est-il toujours en mouvement
				return pos_proto_host.getCurrentPosition(); //host n'est pas autorisé à bouger.
			}else {
				currentMoving=null;
			}
		}
		//host peut bouger
		//choisir un voisin
		long id_neigbor = CommonState.r.nextLong(Network.size());
		//choisir un angle
		double angle = CommonState.r.nextDouble()*(Math.PI*2);
		//position du voisin
		Position pos_neigbor = ((PositionProtocol)Network.get((int)id_neigbor).getProtocol(position_pid)).getCurrentPosition();
		//choisir une distance aléatoire du voisin entre distance_init_min et distance_init_max
		double min_distance=Math.min(scope,Math.max(distance_min, 0.0));
		double max_distance=Math.min(distance_max, scope);
		double distance = CommonState.r.nextDouble()*(max_distance-min_distance) + min_distance    ;   //Math.min(scope, CommonState.r.nextDouble()*scope+(scope/3));
		Position new_position = pos_neigbor.getNewPositionWith(distance, angle).bound(0, 0, pos_proto_host.getMaxX(), pos_proto_host.getMaxY());
		Map<Long,Position> les_positions =  getPositions();
		les_positions.put(host.getID(), new_position);
		Graph g = new MANETGraph(les_positions, scope);
		final GraphAlgorithms ga = new GraphAlgorithms();
		Map<?,?> m =ga.weaklyConnectedClusters(g);
		if(m.size() >1) {
			return pos_proto_host.getCurrentPosition();//le mouvement de host entraine un split du reseau
		}
		currentMoving=host;
		return new_position;
	}
	
	
	private Map<Long, Position> getPositions(){
		Map<Long, Position> res = new HashMap<>();
		for(int i=0; i< Network.size();i++) {
			Node n = Network.get(i);
			PositionProtocol pos_proto_n = (PositionProtocol) n.getProtocol(position_pid);
			Position cur = pos_proto_n.getCurrentPosition();
			res.put(n.getID(),cur);
		}
		return res;
	}

}
