package manet.detection;
import manet.Message;
import manet.communication.EmitterImpl;
import peersim.config.Configuration;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import peersim.edsim.EDSimulator;

import java.util.*;


public class NeighborProtocolImpl implements NeighborProtocol, EDProtocol{
    private List<Long> neighbors;
    private Hashtable<Long,Integer> timerList= new Hashtable<>();
    private static final String PAR_PERIOD="period";
    private static final String PAR_TIMER="timer";
    private static final String PAR_NEIGHBORHOODPID="neighborhoodlistener";
    private final int my_pid;
    private final int period;
    private final long timer;
    private int neighborhood_pid;

    public NeighborProtocolImpl(String prefix) {
        String tmp[]=prefix.split("\\.");
        my_pid=Configuration.lookupPid(tmp[tmp.length-1]);
        this.period=Configuration.getInt(prefix+"."+PAR_PERIOD);
        this.timer=Configuration.getInt(prefix+"."+PAR_TIMER);
        this.neighborhood_pid=Configuration.getPid(prefix+"."+PAR_NEIGHBORHOODPID,-1);
        neighbors = new ArrayList<>();
    }

    @Override
    public void processEvent(Node node, int pid, Object event) {

            if (pid != my_pid) {
                throw new RuntimeException("Receive Event for wrong protocol");
            }
            if (event instanceof Message) {
            String ev = (String) ((Message) event).getTag();
            switch (ev) {
                case "HEARTBEAT":
                    EDSimulator.add(getPeriod(), new Message(node.getID(), 0, "HEARTBEAT","HEARTBEAT", pid), node, my_pid);
                    int emitter = Configuration.lookupPid("emitter");
                    EmitterImpl emi = (EmitterImpl) node.getProtocol(emitter);
                    emi.emit(node, new Message(node.getID(), 0, "PROBE", "PROBE", pid));
                    break;
                case "PROBE":
                    long idSrc = ((Message) event).getIdSrc();
                    if(!neighbors.contains(idSrc))
                        neighbors.add(idSrc);
                    if(timerList.containsKey(idSrc)){
                        timerList.put(idSrc,(timerList.get(idSrc)+1));
                    }else {
                        timerList.put(idSrc, 1);
                    }
                    EDSimulator.add(getTimer(), new Message(idSrc, node.getID(), "TIMER","TIMER", pid), node, my_pid);
                    break;
                case "TIMER":
                    idSrc = ((Message) event).getIdSrc();
                    if(neighbors.contains(idSrc)){
                        if(timerList.containsKey(idSrc)){
                            timerList.put(idSrc,(timerList.get(idSrc)-1));
                            if((timerList.get(idSrc)==0)){
                                neighbors.remove(idSrc);
                            }
                        }
                    }

                    break;
                default:
                    //System.out.println("DEFAULT" + ev);
                    break;
            }
            return;
        } else {
            System.out.println(event);
            throw new RuntimeException("Receive unknown Event");
        }
    }

    @Override
    public List<Long> getNeighbors() {
        return neighbors;
    }

    public long getTimer() {
        return timer;
    }

    public int getPeriod() { return period; }

    public int getNeighborhood_pid() { return neighborhood_pid; }

    public int getNumberOfNeighbors(){ return neighbors.size(); }

    @Override
    public Object clone() {
        NeighborProtocolImpl res = null;
        try{
            res = (NeighborProtocolImpl) super.clone();
            res.neighbors = new ArrayList<>();
            res.timerList = new Hashtable<>();
        }catch (CloneNotSupportedException e){
            System.out.println("Error in Neighbor" +
                    "ProtocolImpl cloning");
            e.printStackTrace();
        }
        return res;
    }
}

