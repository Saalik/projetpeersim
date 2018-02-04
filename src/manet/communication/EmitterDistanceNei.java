package manet.communication;

import manet.Message;
import manet.positioning.PositionProtocolImpl;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDSimulator;

import java.util.ArrayList;

public class EmitterDistanceNei extends EmitterFlooder {
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

    public EmitterDistanceNei(String prefix) {
        super(prefix);
        gossip_pid= Configuration.getPid(prefix+"."+PAR_GOSSIPPROTOCOLPID);
        myNeighbors = new ArrayList<>();
        hisNeighbors = new ArrayList<>();
    }

    @Override
    public void emit(Node host, Message msg) {
//        System.out.println("Call to EmitterSimple.emit()");
        if (forced){
            rebroad++;
            PositionProtocolImpl hostpos = (PositionProtocolImpl) host.getProtocol(getPosition_pid());
            for (int i = 0; i < Network.size(); i++) {
                Node n = Network.get(i);
                PositionProtocolImpl postmp = (PositionProtocolImpl) n.getProtocol(getPosition_pid());
                if (postmp.getCurrentPosition().distance(hostpos.getCurrentPosition()) < getScope() && !(n.equals(host))) {
                    EDSimulator.add(getLatency(), new Message(msg.getIdSrc(),
                            n.getID(), msg.getTag(), myNeighbors, gossip_pid), n, gossip_pid);
                    incrementTransit();
                }
            }
        }else if (!arrived) {
            reached++;
            //System.out.println("Received Gossip: " + host.getID() + " Transit " + transitMsgs);
            myNeighbors = getThemNeighbors(host);
//            System.out.println(myNeighbors);
            if(reached == 1 || CommonState.r.nextDouble()<getProba(host, msg) ) {
                rebroad++;
                PositionProtocolImpl hostpos = (PositionProtocolImpl) host.getProtocol(getPosition_pid());
                for (int i = 0; i < Network.size(); i++) {
                    Node n = Network.get(i);
                    PositionProtocolImpl postmp = (PositionProtocolImpl) n.getProtocol(getPosition_pid());
                    if (postmp.getCurrentPosition().distance(hostpos.getCurrentPosition()) < getScope() && !(n.equals(host))) {
                        EDSimulator.add(getLatency(), new Message(msg.getIdSrc(),
                                n.getID(), msg.getTag(), myNeighbors, gossip_pid), n, gossip_pid);
                        incrementTransit();
                    }
                }
            }else{
                timersLaunched++;
                broadcastFailed = !broadcastFailed;
                hisNeighbors = (ArrayList<Long>) msg.getContent();
                myNeighbors.removeAll(hisNeighbors);
                long latency = CommonState.r.nextLong(100)+200;
                EDSimulator.add(latency, new Message(msg.getIdSrc(),
                        msg.getIdSrc(), "TIMER", myNeighbors, gossip_pid), host, gossip_pid);
            }
            arrived = !arrived;
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

    private double getProba(Node host,Message msg) {
        int proba = 0;
        double v = 0;
        PositionProtocolImpl hostpos = (PositionProtocolImpl) host.getProtocol(getPosition_pid());
        for(int i = 0; i< Network.size() ; i++) {
            Node n = Network.get(i);
            if (n.getID() == msg.getIdSrc()){
                PositionProtocolImpl postmp = (PositionProtocolImpl) n.getProtocol(getPosition_pid());
                return postmp.getCurrentPosition().distance(hostpos.getCurrentPosition())/getScope();
            }
        }
        return -1;
    }

    @Override
    public void timerUp(){
        timersLaunched--;
//        System.out.println(myNeighbors);
        if(!myNeighbors.isEmpty()){
//            System.out.println("TIMER IS USED");
            forced= !forced;
        }//else {
//            System.out.println("TIMER IS WASTED");
//        }
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
