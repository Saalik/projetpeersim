package manet.communication;

import manet.Message;
import manet.positioning.PositionProtocolImpl;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDSimulator;

import java.util.ArrayList;

public class EmitterVoisinsNei extends EmitterFlooder {
    private static int transitMsgs = 0;
    private static int timersLaunched = 0;
    private static int reached = 0;
    private static int rebroad = 0;
    private boolean arrived = false;
    private boolean forced = false;
    private boolean broadcastFailed = false;
    private final String PAR_GOSSIPPROTOCOLPID = "gossip";
    private static int gossip_pid;
    private ArrayList<Long> myNeighbors;
    private ArrayList<Long> hisNeighbors;
    private final String PAR_K = "k";
    private Node me;
    private static double k;

    public EmitterVoisinsNei(String prefix) {
        super(prefix);
        gossip_pid= Configuration.getPid(prefix+"."+PAR_GOSSIPPROTOCOLPID);
        myNeighbors = new ArrayList<>();
        hisNeighbors = new ArrayList<>();
        k= Configuration.getDouble(prefix+"."+PAR_K);
    }

    @Override
    public void emit(Node host, Message msg) {
        if (!arrived) {
            reached++;
            myNeighbors = getThemNeighbors(host);
            if(reached == 1 ||CommonState.r.nextDouble()<getProba(host, k)) {
                rebroad++;
                PositionProtocolImpl hostpos = (PositionProtocolImpl) host.getProtocol(getPosition_pid());
                for (int i = 0; i < Network.size(); i++) {
                    Node n = Network.get(i);
                    PositionProtocolImpl postmp = (PositionProtocolImpl) n.getProtocol(getPosition_pid());
                    if (postmp.getCurrentPosition().distance(hostpos.getCurrentPosition()) < getScope() && !(n.equals(host))) {
                        EDSimulator.add(getLatency(), new Message(msg.getIdSrc(),
                                n.getID(), "GOSSIP", myNeighbors, gossip_pid), n, gossip_pid);
                        incrementTransit();
                    }
                }
            }else{
                me =host;
                timersLaunched++;
                broadcastFailed = !broadcastFailed;
                hisNeighbors = (ArrayList<Long>) msg.getContent();
                myNeighbors.removeAll(hisNeighbors);
                long latency = CommonState.r.nextLong(100)+400;
                EDSimulator.add(latency, new Message(msg.getIdSrc(),
                        msg.getIdSrc(), "TIMER", myNeighbors, gossip_pid), host, gossip_pid);
            }
            arrived = !arrived;
        }else if (forced){
            rebroad++;
            PositionProtocolImpl hostpos = (PositionProtocolImpl) host.getProtocol(getPosition_pid());
            for (int i = 0; i < Network.size(); i++) {
                Node n = Network.get(i);
                PositionProtocolImpl postmp = (PositionProtocolImpl) n.getProtocol(getPosition_pid());
                if (postmp.getCurrentPosition().distance(hostpos.getCurrentPosition()) < getScope() && !(n.equals(host))) {
                    EDSimulator.add(getLatency(), new Message(msg.getIdSrc(),
                            n.getID(), "GOSSIP", myNeighbors, gossip_pid), n, gossip_pid);
                    incrementTransit();
                }
            }
            forced = !forced;
        }else {
            if (broadcastFailed) {
                hisNeighbors = (ArrayList<Long>) msg.getContent();
                myNeighbors.removeAll(hisNeighbors);
                myNeighbors.remove(msg.getIdSrc());
            }
        }
        decrementTransit();
    }


    private ArrayList<Long> getThemNeighbors(Node host) {
        ArrayList<Long> tmpNei = new ArrayList<>();
        PositionProtocolImpl hostpos = (PositionProtocolImpl) host.getProtocol(getPosition_pid());
        for(int i = 0; i< Network.size() ; i++) {
            Node n = Network.get(i);
            PositionProtocolImpl postmp = (PositionProtocolImpl) n.getProtocol(getPosition_pid());
            if (postmp.getCurrentPosition().distance(hostpos.getCurrentPosition()) < getScope() && !(n.equals(host))) {
                tmpNei.add(n.getID());
            }
        }
        return tmpNei;
    }

    private double getProba(Node host,double k) {
        double v = 0;
        PositionProtocolImpl hostpos = (PositionProtocolImpl) host.getProtocol(getPosition_pid());
        for(int i = 0; i< Network.size() ; i++) {
            Node n = Network.get(i);
            PositionProtocolImpl postmp = (PositionProtocolImpl) n.getProtocol(getPosition_pid());
            if(postmp.getCurrentPosition().distance(hostpos.getCurrentPosition()) < getScope() && !(n.equals(host)) ) {
                v++;
            }
        }
        return k/v;
    }

    @Override
    public void timerUp(){
        if(!myNeighbors.isEmpty()){
            forced= !forced;
        }
        timersLaunched--;
    }
    @Override
    public Object clone() {
        EmitterVoisinsNei res = null;
        res = (EmitterVoisinsNei) super.clone();
        res.hisNeighbors = new ArrayList<>();
        res.myNeighbors = new ArrayList<>();
        return res;
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
        return transitMsgs == 0 && timersLaunched == 0 ;
    }

    @Override
    public void incrementTransit(){
        transitMsgs++;
    }

    @Override
    public void decrementTransit(){
        transitMsgs--;
    }

    //    Returns true if allowed to reset false if still messages in transit
    @Override
    public boolean reset() {
        if (transitMsgs == 0) {
            arrived = false;
            reached = 0;
            rebroad = 0;
            forced = false;
            myNeighbors = new ArrayList<>();
            broadcastFailed = false;
            timersLaunched = 0;
            return true;
        }else{
            return false;
        }
    }
}
