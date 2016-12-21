package de.fh.agentMode;

import de.fh.util.DIRECTION;
import de.fh.environment.FieldInfo;
import de.fh.environment.WorldInformation;
import de.fh.connection.wumpus.AgentAction;
import de.fh.search.Position;
import de.fh.search.TargetValidator;
import de.fh.search.UCS;

public class WorldDiscoverer extends AgentMode {
    private WorldInformation worldInformation;

    public WorldDiscoverer(WorldInformation information){
        worldInformation = information;
    }

    /***
     * Returns the next best option to discover all pits.
     * Null if the world has all information to target pits.
     */
    @Override
    public AgentAction nextMove() {
        UCS search = new UCS(worldInformation, new TargetValidator() {
            @Override
            public boolean isVisitable(Position pos) {
                if(pos.getX() == 0 && pos.getY() == 0) return false;
                FieldInfo info = worldInformation.getInfo(pos);
                if(info != null){
                    if(info.isWall()) return false;
                    if(info.canBePit()) return false;
                    if(info.canBeWumpus()) return false;
                }
                return true;
            }

            @Override
            public boolean isTarget(Position info) {
                for (DIRECTION dir: DIRECTION.values()) {
                    int x = info.getX() + dir.xOffset;
                    int y = info.getY() + dir.yOffset;

                    FieldInfo neighbour = worldInformation.getInfo(x,y);
                    if(neighbour != null){
                        if((worldInformation.getInfo(info) == null || !worldInformation.getInfo(info).isVisited())  && neighbour.canBePit() && neighbour.canBeWumpus()){
                            return true;
                        }
                    }
                }
                for (int i = 1; i< 2; i++){
                    for (DIRECTION dir: DIRECTION.values()) {

                        int x = info.getX() + dir.xOffset * i;
                        int y = info.getY() + dir.yOffset * i;
                        FieldInfo neighbour = worldInformation.getInfo(x,y);

                        if(neighbour != null && neighbour.isVisited()){
                            return false;
                        }
                    }
                }
                return true;
            }
        });
        return search.getNextActions().pop();
    }
}
