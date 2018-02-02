package manet.algorithm.gossip;

import manet.Message;
import manet.communication.EmitterFlooder;
import manet.communication.EmitterFlooder;
import manet.communication.EmitterSimple;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import peersim.config.Configuration;


public class GossipProtocolImpl implements GossipProtocol, EDProtocol{

    private static final String PAR_EMITTER = "emitter";
    private final int my_pid;
    private final int emitter;
//    private static int test= 0;


    public GossipProtocolImpl(String prefix) {
        String tmp[]=prefix.split("\\.");
        my_pid= peersim.config.Configuration.lookupPid(tmp[tmp.length-1]);
        emitter = Configuration.getPid(prefix+"."+PAR_EMITTER);

    }

    @Override
    public void initiateGossip(Node host, int id, long id_initiator) {
//        System.out.println("Initiating Gossip: "+ id + " with " + host.getID());
        EmitterFlooder emitr = (EmitterFlooder) host.getProtocol(emitter);
        Message msg = new Message( host.getID(), -1, "GOSSIP", "GOSSIP", this.my_pid);
        emitr.incrementTransit();
        emitr.emit(host, msg);
    }

    @Override
    public void processEvent(Node node, int i, Object o) {
        if (i != my_pid) {
            throw new RuntimeException("Receive Event for wrong protocol");
        }
        if (o instanceof Message) {
            String ev = ((Message) o).getTag();
            EmitterFlooder emitr = (EmitterFlooder) node.getProtocol(emitter);
            switch (ev) {
                case "GOSSIP":
                    emitr.emit(node, (Message) o);
                    break;
                case "TIMER":
                    emitr.timerUp();
                default:
                    break;
            }
        }
    }

    public boolean gossipOver(Node host){
        EmitterFlooder emitr = (EmitterFlooder) host.getProtocol(emitter);
        return emitr.getTransitStatus();
    }

    public double getReached(Node host){
        EmitterFlooder emitr = (EmitterFlooder) host.getProtocol(emitter);
        return 0;
    }

    @Override
    public Object clone() {
        GossipProtocolImpl res = null;
        try{
            res = (GossipProtocolImpl) super.clone();
        }catch (CloneNotSupportedException e){
            System.out.println("Error in " +
                    "Gossip cloning");
            e.printStackTrace();
        }
        return res;
    }

}
