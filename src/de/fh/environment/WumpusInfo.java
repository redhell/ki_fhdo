package de.fh.environment;

import de.fh.search.Position;
import de.fh.util.DIRECTION;

import java.util.HashSet;
import java.util.Iterator;

public class WumpusInfo {
    private HashSet<Position> possiblePositions = new HashSet<>();
    private WorldInformation information;
    private int id;
    private boolean updatedThisRound = false;


    public WumpusInfo(int id, WorldInformation info, int strength){
        this.id = id;
        information = info;
        Position current = info.getPosition();
        for(int x = current.getX() - strength; x <= current.getX() + strength; x++){
            for (int y = current.getY() - strength; y <= current.getY() + strength; y++){
                if(current.calculateDistanceTo(new Position(x,y)) == strength){
                    addField(new Position(x,y));
                }
            }
        }
        updatedThisRound = true;
    }

    private void addField(Position pos){
        if(isFieldPossible(pos))
            possiblePositions.add(pos);
    }

    private boolean isFieldPossible(Position pos){
        if(pos.getX() < 1 || pos.getY() < 1){
            return false;
        }
        FieldInfo info = information.getInfo(pos);
        if(info == null) return true;

        if(info.isWall() || info.isPit()){
            return false;
        }

        return true;
    }

    public int getId(){
        return id;
    }

    public void updateStrength(int strength){
        Position current = information.getPosition();
        Iterator<Position> iter = possiblePositions.iterator();
        while (iter.hasNext()){
            Position pos = iter.next();
            if(current.calculateDistanceTo(pos)!= strength){
                iter.remove();
            }
        }
        updatedThisRound = true;
    }

    public void expandPossibilities(){
        HashSet<Position> tmp = possiblePositions;
        possiblePositions = new HashSet<>();
        Iterator<Position> iter = tmp.iterator();
        while (iter.hasNext()){
            Position pos = iter.next();
            for (DIRECTION dir : DIRECTION.values()){
                addField(pos.getNewPosition(dir));
            }
            addField(pos);
        }
    }

    public boolean canBeWumpus(Position position){
        return possiblePositions.contains(position);
    }


    public void finishRound() {
        if(!updatedThisRound){
            Position current = information.getPosition();
            Iterator<Position> iter = possiblePositions.iterator();
            while (iter.hasNext()){
                Position pos = iter.next();
                if(current.calculateDistanceTo(pos)< 3){
                    iter.remove();
                }
            }
        }
        updatedThisRound = false;
    }
}
