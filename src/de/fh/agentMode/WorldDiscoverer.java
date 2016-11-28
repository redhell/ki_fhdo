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
                    if(info.isWall())return false;
                    if(info.canBePit()) return false;
                }
                return true;
            }

            @Override
            public boolean isTarget(Position info) {
                for (DIRECTION dir: DIRECTION.values()) {
                    for (int i = 1; i< 2; i++){
                        int x = info.getX() + dir.xOffset * i;
                        int y = info.getY() + dir.yOffset * i;

                        FieldInfo field = worldInformation.getInfo(x,y);
                        if(field != null && field.isVisited()){
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
