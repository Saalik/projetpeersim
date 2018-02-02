package manet.positioning.strategies;

import manet.positioning.InitialPositionStrategy;
import manet.positioning.NextDestinationStrategy;
import manet.positioning.Position;
import manet.positioning.PositionProtocol;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Node;

public class Strategy1InitNext implements InitialPositionStrategy, NextDestinationStrategy {

	private static final String PAR_POSITIONPID = "positionprotocol";
	private final int position_pid;
	
	public Strategy1InitNext(String prefix) {
		position_pid=Configuration.getPid(prefix+"."+PAR_POSITIONPID);
	}
	
	
	@Override
	public Position getInitialPosition(Node host) {
		return getNextDestination(host, 0);
	}


	@Override
	public Position getNextDestination(Node host, int speed) {
		double maxX = ((PositionProtocol)host.getProtocol(position_pid)).getMaxX();
		double maxY = ((PositionProtocol)host.getProtocol(position_pid)).getMaxY();
		return new Position(CommonState.r.nextDouble()*maxX, CommonState.r.nextDouble()*maxY);
	}

}
