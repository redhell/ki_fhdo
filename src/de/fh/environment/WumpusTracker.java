package de.fh.environment;

import de.fh.search.Position;

import java.util.HashMap;

public class WumpusTracker {
    private HashMap<Integer, WumpusInfo> wumpis = new HashMap<>();
    private WorldInformation info;

    public WumpusTracker(WorldInformation info){
        this.info = info;
    }

    public void updateWumpus(int id, int strength){
        if(wumpis.containsKey(id)){
            wumpis.get(id).updateStrength(strength);
        }else{
            wumpis.put(id, new WumpusInfo(id, info, strength));
        }
    }

    public boolean canBeWumpus(Position pos){
        for (WumpusInfo info : wumpis.values()){
            if(info.canBeWumpus(pos)){
                return true;
            }
        }
        return false;
    }

    public void expand(){
        for (WumpusInfo info : wumpis.values()){
            info.expandPossibilities();
        }
    }

    public void finishRound(){
        for (WumpusInfo info : wumpis.values()){
            info.finishRound();
        }
    }
}
