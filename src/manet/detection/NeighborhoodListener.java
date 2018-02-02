package manet.detection;

import peersim.core.Node;
import peersim.core.Protocol;

public interface NeighborhoodListener extends Protocol{

	/*appelé lorsque le noeud host détecte un nouveau voisin*/
	public default void newNeighborDetected(Node host, long id_new_neigbor) {}
	
	/*appelé lorsque le noeud host détecte la perte d'un voisin*/
	public default void lostNeighborDetected(Node host, long id_lost_neigbor) {}
}
