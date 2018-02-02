package manet;

import manet.detection.NeighborProtocolImpl;
import manet.positioning.PositionProtocolImpl;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;
import peersim.config.Configuration;
import peersim.edsim.EDSimulator;

public class Initialisation implements Control{

	public  Initialisation(String prefix) {}

    @Override
    public boolean execute() {
        Node n;
        for(int i = 0; i < Network.size() ; i++){
            int position=Configuration.lookupPid("position");
            int emitter=Configuration.lookupPid("emitter");
            n = Network.get(i);

            PositionProtocolImpl ppi = (PositionProtocolImpl) n.getProtocol(position);
            ppi.initialiseCurrentPosition(n);


            EDSimulator.add(0L, ppi.loop_event, n, position);

            int neighbor=Configuration.lookupPid("neighbor");
            NeighborProtocolImpl npi = (NeighborProtocolImpl) n.getProtocol(neighbor);
            EDSimulator.add(0L, new Message(n.getID(), 0, "HEARTBEAT","HEARTBEAT", neighbor), n, neighbor);
//            EDSimulator.add();

        }
        return false;
    }
}
