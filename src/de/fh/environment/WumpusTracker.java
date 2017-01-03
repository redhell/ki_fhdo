package de.fh.environment;

import de.fh.util.Position;

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

    public void clear(){
        wumpis.clear();
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

    public WumpusInfo getFirstWumpus(){
        if(wumpis.size()>0){
            return wumpis.values().iterator().next();
        }
        return null;
    }

    public void wumpusKilled(int id){
        wumpis.remove(id);
    }
}
