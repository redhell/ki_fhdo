package de.fh.environment;

import de.fh.connection.wumpus.AgentAction;
import de.fh.util.DIRECTION;
import de.fh.util.Position;

import java.util.HashSet;
import java.util.Iterator;

public class WumpusInfo {
    private HashSet<Position> possiblePositions = new HashSet<>();
    private WorldInformation information;
    private int id;
    private int lastStrength;
    private boolean updatedThisRound = false;


    public WumpusInfo(int id, WorldInformation info, int strength){
        this.id = id;
        information = info;
        init(strength);

    }

    private void init(int strength){
        lastStrength = strength;
        Position current = information.getPosition();
        for(int x = current.getX() - strength; x <= current.getX() + strength; x++){
            for (int y = current.getY() - strength; y <= current.getY() + strength; y++){
                if(current.calculateDistanceTo(new Position(x,y)) == strength && !isFieldBehindAgent(new Position(x,y))){
                    addField(new Position(x,y));
                }
            }
        }
        updatedThisRound = true;
    }

    private boolean isFieldBehindAgent(Position position){
        if(information.getPositionHistory().size() > 0 && !information.getPositionHistory().peek().equals(AgentAction.GO_FORWARD)){
            return false;
        }

        DIRECTION currentAgentDirection = information.getDir();
        Position currentPosition = information.getPosition();
        if(currentAgentDirection.xOffset == 0 && position.getY() == currentPosition.getY()){
            return false;
        }
        if(currentAgentDirection.yOffset == 0 && position.getX() == currentPosition.getX()){
            return false;
        }

        if(position.getNewPosition(currentAgentDirection).calculateDistanceTo(currentPosition)<position.calculateDistanceTo(currentPosition)){
            return true;
        }
        return false;
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
        lastStrength = strength;
        if(possiblePositions.size() == 0){
            init(strength);
            return;
        }

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

    public Position[] getPossiblePositions(){
        return possiblePositions.toArray(new Position[possiblePositions.size()]);
    }

    public int getLastStrength(){
        return lastStrength;
    }
}
