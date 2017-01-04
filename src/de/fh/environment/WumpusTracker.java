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

    /**
     * expands all possible Positions by 1
     */
    public void expand(){
        for (WumpusInfo info : wumpis.values()){
            info.expandPossibilities();
        }
    }

    /**
     * informs all not updated wumpi, that they weren't in range
     */
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

    /**
     * removes wumpus from list
     */
    public void wumpusKilled(int id){
        wumpis.remove(id);
    }
}
