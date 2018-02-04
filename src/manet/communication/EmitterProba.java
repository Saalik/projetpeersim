package manet.communication;

import manet.Message;
import manet.positioning.PositionProtocolImpl;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDSimulator;

public class EmitterProba extends EmitterFlooder {
    private static int transitMsgs = 0;
    private static int reached = 0;
    private static int rebroad = 0;
    private boolean arrived = false;
    private static int uselessMsgs = 0;
    private final String PAR_GOSSIPPROTOCOLPID = "gossip";
    private final String PAR_PROBA = "proba";
    private static int gossip_pid;
    private static double proba;

    public EmitterProba(String prefix) {
        super(prefix);
        gossip_pid= Configuration.getPid(prefix+"."+PAR_GOSSIPPROTOCOLPID);
        proba= Configuration.getDouble(prefix+"."+PAR_PROBA);
    }

    @Override
    public void emit(Node host, Message msg) {
        if (!arrived) {
            reached++;
            if(CommonState.r.nextDouble() <getProba() || reached == 1) {
                rebroad++;
                PositionProtocolImpl hostpos = (PositionProtocolImpl) host.getProtocol(getPosition_pid());
                for (int i = 0; i < Network.size(); i++) {
                    Node n = Network.get(i);
                    PositionProtocolImpl postmp = (PositionProtocolImpl) n.getProtocol(getPosition_pid());
                    if (postmp.getCurrentPosition().distance(hostpos.getCurrentPosition()) < getScope() && !(n.equals(host))) {
                        EDSimulator.add(getLatency(), new Message(msg.getIdSrc(),
                                n.getID(), msg.getTag(), msg.getContent(), gossip_pid), n, gossip_pid);
                        incrementTransit();
                    }
                }
            }
            arrived = !arrived;
        }else {
            uselessMsgs++;
        }
        decrementTransit();
    }

    public double getProba() {
        return proba;
    }

    public int getReached() {
        return reached;
    }

    @Override
    public int getRebroad() {
        return rebroad;
    }

    @Override
    public boolean getTransitStatus() {
        return transitMsgs == 0;
    }

    @Override
    public void incrementTransit(){
        transitMsgs++;
    }

    @Override
    public int getUselessMsgs() {
        return uselessMsgs;
    }

    @Override
    public void decrementTransit(){ transitMsgs--; }

    //    Returns true if allowed to reset false if still messages in transit
    @Override
    public boolean reset() {
        if (transitMsgs == 0) {
            arrived = false;
            uselessMsgs = 0;
            reached = 0;
            rebroad = 0;
            return true;
        }else{
            return false;
        }
    }
}
