package de.fh.environment;

import de.fh.connection.wumpus.AgentAction;
import de.fh.util.DIRECTION;
import de.fh.util.Position;

import java.util.HashSet;
import java.util.Iterator;

public class WumpusInfo {
    private HashSet<Position> possiblePositions = new HashSet<>();
    private WorldInformation worldInformation;
    private int id;
    private boolean updatedThisRound = false;


    public WumpusInfo(int id, WorldInformation info, int strength){
        this.id = id;
        worldInformation = info;
        init(strength);

    }

    private void init(int strength){
        Position current = worldInformation.getPosition();
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
        if (worldInformation.getPositionHistory().size() > 0 && !worldInformation.getPositionHistory().peek().equals(AgentAction.GO_FORWARD)) {
            return false;
        }

        DIRECTION currentAgentDirection = worldInformation.getCurrentDirection();
        Position currentPosition = worldInformation.getPosition();
        if(currentAgentDirection.xOffset == 0 && position.getY() == currentPosition.getY()){
            return false;
        }
        if(currentAgentDirection.yOffset == 0 && position.getX() == currentPosition.getX()){
            return false;
        }

        return position.getNewPosition(currentAgentDirection).calculateDistanceTo(currentPosition) < position.calculateDistanceTo(currentPosition);
    }

    private void addField(Position pos){
        if(isFieldPossible(pos))
            possiblePositions.add(pos);
    }

    private boolean isFieldPossible(Position pos){
        if(pos.getX() < 1 || pos.getY() < 1){
            return false;
        }
        FieldInfo info = worldInformation.getInfo(pos);
        if(info == null) return true;

        return !(info.isWall() || info.isPit());
    }

    public int getId(){
        return id;
    }

    public void updateStrength(int strength){
        if(possiblePositions.size() == 0){
            init(strength);
            return;
        }

        Position currentPosition = worldInformation.getPosition();
        Iterator<Position> iter = possiblePositions.iterator();
        while (iter.hasNext()){
            Position pos = iter.next();
            if (currentPosition.calculateDistanceTo(pos) != strength) {
                iter.remove();
            }
        }
        updatedThisRound = true;
    }

    public void expandPossibilities(){
        HashSet<Position> oldPossiblePositions = possiblePositions;
        possiblePositions = new HashSet<>();
        for (Position pos : oldPossiblePositions) {
            for (DIRECTION dir : DIRECTION.values()) {
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
            //if wumpus is not in range remove blocks from possiblePositions
            Position currentPos = worldInformation.getPosition();
            Iterator<Position> iter = possiblePositions.iterator();
            while (iter.hasNext()){
                Position pos = iter.next();
                if (currentPos.calculateDistanceTo(pos) <= 3) {
                    iter.remove();
                }
            }
        }
        updatedThisRound = false;
    }

    public Position[] getPossiblePositions(){
        return possiblePositions.toArray(new Position[possiblePositions.size()]);
    }
}
