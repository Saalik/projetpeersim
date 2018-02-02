package manet.communication;

import manet.Message;
import peersim.core.Node;

public abstract class EmitterFlooder extends EmitterImpl {

    public EmitterFlooder(String prefix) {
        super(prefix);
    }

    public int getRebroad() {
        return 0;
    }


    public int getReached() {
        return 0;
    }

    public boolean getTransitStatus() {
        return false;
    }

    public void incrementTransit() {
    }

    public void timerUp(){
    }
    public int getUselessMsgs() {
        return 0;
    }

    public void decrementTransit() {
    }

    public boolean reset() {
        return false;
    }

}