package manet.communication;

import manet.Message;
import manet.detection.NeighborProtocolImpl;
import manet.positioning.PositionProtocolImpl;
import peersim.config.Configuration;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDSimulator;

public class EmitterImpl implements Emitter{

    private static final String PAR_LATENCY="latency";
    private static final String PAR_SCOPE="scope";
    private static final String PAR_POSITIONPROTOCOLPID="positionprotocol";
    private final int latency;
    private final int scope;
    private final int position_pid;
    private final int my_pid;


    public EmitterImpl(String prefix) {
        String tmp[]=prefix.split("\\.");
        my_pid=Configuration.lookupPid(tmp[tmp.length-1]);
        this.latency=Configuration.getInt(prefix+"."+PAR_LATENCY);
        this.scope=Configuration.getInt(prefix+"."+PAR_SCOPE);
        this.position_pid=Configuration.getPid(prefix+"."+PAR_POSITIONPROTOCOLPID);
    }


    @Override
    public void emit(Node host, Message msg) {
//        System.out.println("");
        int neighbor= Configuration.lookupPid("neighbor");
        //NeighborProtocolImpl npi = (NeighborProtocolImpl) host.getProtocol(neighbor);
        PositionProtocolImpl hostpos = (PositionProtocolImpl) host.getProtocol(position_pid);
        for(int i = 0; i< Network.size() ; i++) {
            Node n = Network.get(i);
            PositionProtocolImpl postmp = (PositionProtocolImpl) n.getProtocol(position_pid);
            if(postmp.getCurrentPosition().distance(hostpos.getCurrentPosition()) < scope && !(n.equals(host)) ) {
                EDSimulator.add(latency, new Message(msg.getIdSrc(), n.getID(), "PROBE","PROBE", neighbor), n, neighbor);
                //System.out.println("Imma emit shit");
            }
        }


    }

    public int getPosition_pid() {
        return position_pid;
    }

    @Override
    public int getLatency() {
        return latency;
    }

    @Override
    public int getScope() {
        return scope;
    }

    @Override
    public Object clone() {
        EmitterImpl res = null;
        try{
            res = (EmitterImpl) super.clone();
        }catch (CloneNotSupportedException e){
            System.out.println("Error in EmitterImpl cloning");
            e.printStackTrace();
        }
        return res;
    }


}
