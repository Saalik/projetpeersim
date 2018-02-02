package manet.algorithm.gossip;

import peersim.core.Node;
import peersim.core.Protocol;

public interface GossipProtocol extends Protocol {

	/*Permet de déclencher une diffusion identifiée par id, dont le noeud source
	 *  est le noeud id_initiator*/
	public void initiateGossip(Node host, int id, long id_initiator);
}
