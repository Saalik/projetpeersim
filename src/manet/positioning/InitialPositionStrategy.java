package manet.positioning;

import peersim.core.Node;

public interface InitialPositionStrategy {
	
	
	/*retourne la position initiale du noeud host*/
	public Position getInitialPosition(Node host);
}
