package manet;

import manet.algorithm.gossip.GossipProtocolImpl;
import manet.communication.EmitterFlooder;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;

import java.util.ArrayList;

public class GossipControler implements Control{

    private static final String PAR_NUMBER = "number";
    private static final String PAR_GOSSIP = "gossipprotocol";
    private static final String PAR_EMITTER = "emitter";
    private static int num;
    private static int it = 0;
    private final int gossip_pid;
    private final int emitter_pid;
    private Node chosen;
    private static ArrayList<Double> allAtt;
    private static ArrayList<Double> allER;

    public GossipControler(String prefix) {
        num = Configuration.getInt(prefix+"."+PAR_NUMBER);
        gossip_pid = Configuration.getPid(prefix+"."+PAR_GOSSIP);
        emitter_pid = Configuration.getPid(prefix+"."+PAR_EMITTER);
        allAtt = new ArrayList<>();
        allER = new ArrayList<>();
    }

    @Override
    public boolean execute() {
        if (it <= num){
            chosen = Network.get(CommonState.r.nextInt(Network.size()));
            GossipProtocolImpl gp = (GossipProtocolImpl) chosen.getProtocol(gossip_pid);
            if(gp.gossipOver(chosen)) {
//                System.out.println("Gossip number "+(it+1));
                for (int i = 0; i < Network.size(); i++) {
                    Node n = Network.get(i);
                    EmitterFlooder ems = (EmitterFlooder) n.getProtocol(emitter_pid);
                    if (it >= 1) {
                        if (i == 0) {
                            double reached = ems.getReached();
                            double rebroad = ems.getRebroad();
                            allAtt.add(it - 1, reached);
                            allER.add(it - 1, ((reached - rebroad) / reached));
                        }
                        ems.reset();
                    }
                }
                gp.initiateGossip(chosen, it, chosen.getID());
                it++;
            }
        }else{
//            EmitterFlooder ems = (EmitterFlooder) Network.get(0).getProtocol(emitter_pid);
//            double reached = ems.getReached();
//            double rebroad = ems.getRebroad();
//            allAtt.add(it - 1, reached);
//            allER.add(it - 1, ((reached - rebroad) / reached));
//            System.out.println();
            double avgAtt = getAvgAtt();
            double avgER = getAvgER();
//            System.out.println("Moyenne de Att "+avgAtt);
            System.out.println("Pourcentage de Att :"+(avgAtt*100/Network.size()));
            System.out.println("Ecart type de Att :"+standardDeviation(allAtt, avgAtt));
            System.out.println("Pourcentage de ER :"+(avgER*100));
            //System.out.println("Pourcent de ER "+(avgER*100/Network.size()));
            System.out.println("Ecart type de ER :"+standardDeviation(allER, avgER));

            System.exit(1);
        }
        return false;
    }

    private double standardDeviation (ArrayList<Double> arr, double avg){
        double standardDeviation = 0;
        for(double i : arr){
            standardDeviation += Math.pow((i-avg),2);
        }
        standardDeviation = standardDeviation/(Network.size()-1);
        standardDeviation = Math.sqrt(standardDeviation);
        return standardDeviation;
    }



    private double getAvgAtt() {
        double avg = 0;
        for(double i: allAtt){
            avg+=i;
        }
        return avg/num;
    }

    private double getAvgER() {
        double avg = 0;
        for(double i: allER){
            avg+=i;
        }
        return avg/num;
    }

}
