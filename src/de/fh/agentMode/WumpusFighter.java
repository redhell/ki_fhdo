package de.fh.agentMode;

import de.fh.connection.wumpus.AgentAction;
import de.fh.environment.FieldInfo;
import de.fh.environment.WorldInformation;
import de.fh.environment.WumpusInfo;
import de.fh.search.TargetValidator;
import de.fh.search.UCS;
import de.fh.util.DIRECTION;
import de.fh.util.Position;

import java.util.HashSet;

public class WumpusFighter extends AgentMode{
    private WorldInformation worldInformation;
    private WumpusInfo currentTarget;

    public WumpusFighter(WorldInformation information){
        this.worldInformation = information;
    }

    public WumpusInfo getCurrentTarget(){
        return currentTarget;
    }

    @Override
    public AgentAction nextMove() {
        if(currentTarget == null){
            currentTarget = worldInformation.getWumpusTracker().getFirstWumpus();
            if(currentTarget == null) return null;
        }
        if(currentTarget.getPossiblePositions().length > 1){
            return optimizeLocation();
        }

        try{
            return fight();
        }catch (Exception e){
            return AgentAction.SHOOT;
        }
    }

    public boolean isCurrentPositionShootable(Position pos, DIRECTION dir){
        Position target = currentTarget.getPossiblePositions()[0];

        if(((dir == DIRECTION.DOWN || dir == DIRECTION.UP)
            && (target.getX() == pos.getX()))){

            for (int y = 1; y < 20; y++){
                Position testPos = new Position(pos.getX(), pos.getY() + y * dir.yOffset);
                if(testPos.equals(target)){
                    return true;
                }

                FieldInfo info = worldInformation.getInfo(testPos);
                if(info != null && info.isWall()){
                    return false;
                }
            }

        }

        if(((dir == DIRECTION.RIGHT || dir == DIRECTION.LEFT)
                && (target.getX() == pos.getX()))){

            for (int x = 1; x < 20; x++){
                Position testPos = new Position(pos.getX() + x * dir.xOffset, pos.getY());
                if(testPos.equals(target)){
                    return true;
                }

                FieldInfo info = worldInformation.getInfo(testPos);
                if(info != null && info.isWall()){
                    return false;
                }
            }
        }
        return false;
    }

    public AgentAction fight(){
        UCS search = new UCS(worldInformation, new TargetValidator() {
            @Override
            public boolean isVisitable(Position pos) {
                if(pos.getX() == 0 && pos.getY() == 0) return false;
                /*for(DIRECTION direction : DIRECTION.values()){
                    Position neighbour = pos.getNewPosition(direction);
                    if(worldInformation.canBeWumpus(neighbour)){
                        return false;
                    }
                }*/
                FieldInfo info = worldInformation.getInfo(pos);
                if(info != null){
                    if(info.isWall()) return false;
                    if(info.canBePit()) return false;
                    if(info.canBeWumpus()) return false;
                }
                return true;
            }

            @Override
            public boolean isTarget(Position info, DIRECTION direction) {
                if(isCurrentPositionShootable(info, direction)){
                    return true;
                }
                return false;
            }
        });
            return search.getNextActions().pop();
    }


    private AgentAction optimizeLocation(){
        UCS optimizer = new UCS(worldInformation, new TargetValidator() {
            @Override
            public boolean isVisitable(Position pos) {
                if(pos.getX() == 0 && pos.getY() == 0) return false;
                /*for(DIRECTION direction : DIRECTION.values()){
                    Position neighbour = pos.getNewPosition(direction);
                    if(worldInformation.canBeWumpus(neighbour)){
                        return false;
                    }
                }*/
                FieldInfo info = worldInformation.getInfo(pos);
                if(info != null){
                    if(info.isWall()) return false;
                    if(info.canBePit()) return false;
                    if(info.canBeWumpus()) return false;
                }
                return true;
            }

            @Override
            public boolean isTarget(Position info, DIRECTION dir) {
                return new LocalisationMove(info).isPossible();
            }
        });


        try {
            return optimizer.getNextActions().peek();
        }catch (Exception e){
            System.out.println("No optimization found.");
            return null;
        }

    }

    public class LocalisationMove{
        private HashSet<Integer> strenthList = new HashSet<>();
        private Position newPos;

        public LocalisationMove(Position newPos){
            this.newPos = newPos;
            Position[] possible = currentTarget.getPossiblePositions();
            for(Position pos : possible){
                strenthList.add(newPos.calculateDistanceTo(pos));
            }
        }

        public boolean isPossible(){
            FieldInfo info = worldInformation.getInfo(newPos);
            if(info != null &&(info.canBeWumpus() || info.isWall() || info.canBePit())){
                return false;
            }

            return strenthList.size()>1;
        }
    }

}
