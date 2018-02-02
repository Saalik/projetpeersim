package manet.positioning;

import peersim.core.Node;
import peersim.core.Protocol;


public interface PositionProtocol extends Protocol {
	
	/**** Attribut communs à tous les noeuds **/
	
	/*Renvoie la vitesse maximale qu'un noeud peut avoir losque'il est en mouvement*/
	public int getMaxSpeed();
	
	/*Renvoie la vitesse minimale qu'un noeud peut avoir losque'il est en mouvement*/
	public int getMinSpeed();
	
	/*Renvoie l'abscisse maximale (largeur du terrain)*/
	public double getMaxX();
	
	
	/*Renvoie l'ordonnée maximale (largeur du terrain)*/
	public double getMaxY();
	
	/*Renvoie le temps d'immobilité*/
	public int getTimePause();
	
	
	
	
	/**** Attributs propre à chaque noeud **/
	
	
	/*renvoie la position courante, assure que 0 <= x <=MaxX et 0 <= y <=MaxY*/
	public Position getCurrentPosition();
	
	
	/*renvoie la position destination*/
	public Position getCurrentDestination();
	
	/*renvoie la vitesse courante*/
	public int getCurrentSpeed();
	
	/*renvoie vrai si le noeud est en mouvement, faux si le noeud est en pause*/
	public boolean isMoving();
	
	/*Initiatlisation de la position courante*/
	public void initialiseCurrentPosition(Node host);
	
	
}
