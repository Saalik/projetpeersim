package manet.positioning;

import peersim.config.Configuration;

public final class PositioningStrategiesFactory {

	private PositioningStrategiesFactory() {}
	
	private static String PAR_INITIAL_POSITION_STRATEGY="initial_position_strategy";
	private static String PAR_NEXT_DESTINATION_STRATEGY="next_destination_strategy";
	
	
	private static final InitialPositionStrategy initial_position_strategy;
	private static final NextDestinationStrategy next_destination_strategy;
	
	static {
		initial_position_strategy=(InitialPositionStrategy)Configuration.getInstance(PAR_INITIAL_POSITION_STRATEGY);
		next_destination_strategy=(NextDestinationStrategy)Configuration.getInstance(PAR_NEXT_DESTINATION_STRATEGY);
	}
	
	public static InitialPositionStrategy getInitialPositionStrategy() {
		return initial_position_strategy;
	}
	
	public static NextDestinationStrategy getNextDestinationStrategy() {
		return next_destination_strategy;
	}
}
