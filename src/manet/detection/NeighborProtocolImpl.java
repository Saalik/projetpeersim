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
    //private Set<Long> keys= null;
    //public static final String heartbeat="HEARTBEAT";
    private static final String PAR_PERIOD="period";
    private static final String PAR_TIMER="timer";
    private static final String PAR_NEIGHBORHOODPID="neighborhoodlistener";
    //private static final String PAR_SIZE="size";
    private final int my_pid;
    private final int period;
    private final long timer;
    //private final int nsize;


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
//                  for (Node n = Network.get(i); i < Network.size(); i++) {
//                      if (n.getID() != my_pid) {
//                            int position = Configuration.lookupPid("position");
//                            int emitter = Configuration.lookupPid("emitter");
//                            PositionProtocolImpl ppi = (PositionProtocolImpl) n.getProtocol(position);
//                            if ( ppi.getCurrentPosition().distance( (PositionProtocolImpl) node.getProtocol(position).getCurrentPosition()))
//                                EDSimulator.add(0, "HEYNEIGHBOR", n, emitter);
//                      }
                    /*for(int i= 0; i < timerList.size();i++){
                        timerList.get(i);
                    }*/

//                    Set<Long> keys = timerList.keySet();
//                    for (Long key : keys){
//                        int tmp = (timerList.get(key));
//                        tmp--;
//                        if(tmp>0)
//                            timerList.put(key, tmp);
//                        else {
//                            //keys.remove(key);
//                            neighbors.remove(key);
//                        }
//                    }
                    break;
                case "PROBE":
                    long idSrc = ((Message) event).getIdSrc();
                    //System.out.println("Received from"+((Message) event).getIdSrc()+" "+ ev);
                    //System.out.println(timerList);
                    if(!neighbors.contains(idSrc))
                        neighbors.add(idSrc);

                    if(timerList.containsKey(idSrc)){
                        timerList.put(idSrc,(timerList.get(idSrc)+1));
                    }else {
                        timerList.put(idSrc, 1);
                    }
                    EDSimulator.add(getTimer(), new Message(idSrc, node.getID(), "TIMER","TIMER", pid), node, my_pid);
                    //System.out.println(node.getID()+ " "+timerList);
                    break;
                case "TIMER":
                    //System.out.println("Received from"+((Message) event).getIdSrc()+" "+ ev);

                    idSrc = ((Message) event).getIdSrc();
                    if(neighbors.contains(idSrc)){
                        if(timerList.containsKey(idSrc)){
                            timerList.put(idSrc,(timerList.get(idSrc)-1));
                    //        System.out.println(timerList);
                            if((timerList.get(idSrc)==0)){
                             //   System.out.println("Removed "+idSrc);
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
}

