package manet;

import manet.detection.NeighborProtocolImpl;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;
import peersim.config.Configuration;
import java.util.ArrayList;

public class DensityControler implements Control{

    private static final String PAR_NEIGHBOR="neighborprotocol";
    private static final String PAR_ITERATIONS="iteration";
    private double actualAverage;
    private double actualStandardDeviation;
    private double accA;
    private double accuSD;
    private int tNum = 0;
    private final int neighbor_pid;
    private static int iteration;
    private ArrayList<Double> allA = new ArrayList();
    private double aa = 0;
    private double asd = 0;

    public DensityControler(String prefix) {
        this.neighbor_pid =Configuration.getPid(prefix+"."+PAR_NEIGHBOR);
        iteration =Configuration.getInt(prefix+"."+PAR_ITERATIONS);
    }

    @Override
    public boolean execute() {
        tNum++;
        double avg = average();
        double sd = standardDeviation();
        aa = averageAverage();
        asd = averageStandardDivision();
        double edt = eDt();
        //itération = 210 pour Simul exo 1 et 330 simul exo 2
        if(tNum == iteration) {
            System.out.print(": " + aa);
            System.out.print(": " + (asd / aa));
            System.out.println(": " + (edt / aa));
        }
        return false;
    }

    private double average (){
        double average = 0;
        for(int i = 0 ; i< Network.size() ; i++){
            Node n = Network.get(i);
            NeighborProtocolImpl nei = (NeighborProtocolImpl) n.getProtocol(neighbor_pid);
            average += nei.getNumberOfNeighbors();
        }
        this.actualAverage = average/Network.size();
        allA.add(actualAverage);
        return actualAverage;
    }

    private double standardDeviation (){
        double standardDeviation = 0;
        for(int i = 0 ; i< Network.size() ; i++){
            Node n = Network.get(i);
            NeighborProtocolImpl nei = (NeighborProtocolImpl) n.getProtocol(neighbor_pid);
            standardDeviation += Math.pow((nei.getNumberOfNeighbors()-actualAverage),2);
        }
        standardDeviation = standardDeviation/(Network.size()-1);
        actualStandardDeviation = Math.sqrt(standardDeviation);
        standardDeviation = actualStandardDeviation;
        return standardDeviation;
    }

    private double averageAverage (){
        accA +=actualAverage;
        aa = accA/tNum;
        return aa;
    }

    private double averageStandardDivision (){
        accuSD +=actualStandardDeviation;
        asd = accuSD/tNum;
        return asd;
    }

    private double eDt(){
        double value = 0;
        for(int i= 0;i < allA.size(); i++){
            value += Math.pow(allA.get(i)-aa,2);
        }
        value =value/tNum;
        return Math.sqrt(value);
    }
}
